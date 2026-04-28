package sa.gov.ksaa.dal.ui.fragments.bids

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.Qutation
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.ui.adapters.RV_Adapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.viewModels.BidsVM

class AddEditQuotationFragment : // (val projectId: Int, val freelancer: Freelancer, val onSendQuotationListener: OnSendQuotationListener)
    BaseFragment(R.layout.fragment_send_quotation) {

    val MAX_FILE_SIZE = 2_000_000
    lateinit var estimatedDurationET: EditText
    lateinit var costET: EditText
    lateinit var aboutET: EditText
    lateinit var outputsET: EditText
    lateinit var attachmentDescET: EditText
    lateinit var fileNameTV: TextView
    lateinit var uploadBtn: Button
    lateinit var submitBtn: Button

    lateinit var aboutUserChar: TextView
    lateinit var aboutUserCharMax: TextView

    lateinit var aboutUserChar2: TextView
    lateinit var aboutUserCharMax2: TextView
    var projectId: Int? = 0

    companion object {
        const val projectIdArgKey = "projectId"
        const val TAG = "SendAQuotation"
    }


    val vm: BidsVM by viewModels()
    lateinit var qutation: Qutation
    var bidFile: MyFile? = null

    val expFile0AtivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            gettingFile(activityResult, object : FileListener{
                override fun onFileChosen(myFile: MyFile) {

                    if (myFile.size > MAX_FILE_SIZE) {
                        fileNameTV.setTextColor(DARK_RED_COLOR_STATE_LIST)
                        fileNameTV.text = getString(R.string.exeeding_max_file_size)
                    } else {
                        fileNameTV.setTextColor(BLACK_COLOR_STATE_LIST)
                        fileNameTV.text = myFile.name
                        bidFile = myFile
                        fileNameTV.setOnClickListener {
                            val browserIntent = Intent(Intent.ACTION_VIEW, bidFile!!.uri)
                                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            startActivity(browserIntent)
                        }
                    }
                }

            })

        }
    }

    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        appBarLayout.visibility = View.VISIBLE
        initViews(createdView)
        estimatedDurationET.setArabicNumberFormatter()
        costET.setArabicNumberFormatter()
        qutation = Qutation()


        projectId = arguments?.getInt(projectIdArgKey, 0)

        uploadBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                openFile(null, All_MIME_TYPES, expFile0AtivityResultLauncher)
            }
        }

        submitBtn.setOnClickListener {
            if (validateQutation()){
                // projectId= 2, freelancerId=4, expectedDuration=3, description= وصف, outputExpected=لايوجد, cost= 300,
                // attachmentDescription= وصف,
                val params = mutableMapOf<String, String>(
                    "projectId" to activityVM.newProjectMLD.value!!.id.toString(),
                    "freelancerId" to (currentFreelancer!!.userId).toString(),
                    "expectedDuration" to convertEnglishNumbersToString(qutation.duration!!),
                    "description" to qutation.description!!,
                    "outputExpected" to qutation.about_the_work_to_be_done.toString(),
                    "cost" to convertEnglishNumbersToString(qutation.price!!) ,
                    "attachmentDescription" to qutation.attachmentDesc!!,
                )
                Log.w(TAG, "onViewCreated: params = $params")
                vm.create_aBid(params, bidFile)
                    .observe(viewLifecycleOwner) {
                        newHandleSuccessOrErrorResponse(it, {quotation ->
                            activitySnackbar.setText("تم ارسال عرض السعر بنجاح")
                                .show()
                            findNavController().popBackStack()
//                            onSendQuotationListener.submitQuotation(quotation)
//                            this@AddEditQuotationFragment.dismiss()
                        })
                    }
            }

        }
        aboutUserCharMax.text = RV_Adapter.numberFormat.format(250)
        aboutUserChar.text = RV_Adapter.numberFormat.format(0)
        outputsET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                aboutUserChar.text = p0?.length.toString()



                val noChars = p0?.length?:0


                aboutUserChar.text = RV_Adapter.numberFormat.format(noChars)
                if (noChars >=250){
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                    outputsET.text?.delete(250, noChars)
                } else {
                    aboutUserChar.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))



                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        aboutUserCharMax2.text = RV_Adapter.numberFormat.format(250)
        aboutUserChar2.text = RV_Adapter.numberFormat.format(0)

        aboutET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                aboutUserChar2.text = p0?.length.toString()



                val noChars = p0?.length?:0


                aboutUserChar2.text = RV_Adapter.numberFormat.format(noChars)
                if (noChars >=250){
                    aboutUserChar2.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                    aboutET.text?.delete(250, noChars)
                } else {
                    aboutUserChar2.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))



                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    lateinit var input: String
    private fun validateQutation(): Boolean {

        qutation.project_id = projectId
        input = estimatedDurationET.text.toString().trim()
        if (input.isEmpty()){
            estimatedDurationET.error = getString(R.string.this_field_is_required)
            estimatedDurationET.requestFocus()
            return false
        }
        qutation.duration = input

        input = costET.text.toString().trim()
        if (input.isEmpty()){
            costET.error = getString(R.string.this_field_is_required)
            costET.requestFocus()
            return false
        }
        qutation.price = input
        input = outputsET.text.toString().trim()
        if (input.isEmpty()){
            outputsET.error = getString(R.string.this_field_is_required)
            outputsET.requestFocus()
            return false
        }
        qutation.about_the_work_to_be_done = input

        input = aboutET.text.toString().trim()
        if (input.isEmpty()){
            aboutET.error = getString(R.string.this_field_is_required)
            aboutET.requestFocus()
            return false
        }
        qutation.description = input

        input = attachmentDescET.text.toString().trim()
        if (bidFile != null && input.isEmpty()){
            attachmentDescET.error = getString(R.string.this_field_is_required)
            attachmentDescET.requestFocus()
            return false
        }
        qutation.attachmentDesc = input

        qutation.freelance_id = activityVM.currentFreelancerMLD.value!!.id

        Log.i(TAG, "validateQutation: qutation = $qutation")
        return true
    }

    private fun initViews(createdView: View) {
        estimatedDurationET = createdView.findViewById(R.id.estimatedDurationET)
        costET = createdView.findViewById(R.id.costET)
        aboutET = createdView.findViewById(R.id.aboutET)
        outputsET = createdView.findViewById(R.id.outputsET)
        attachmentDescET = createdView.findViewById(R.id.attachmentDescET)
        fileNameTV = createdView.findViewById(R.id.fileNameTV)
        uploadBtn = createdView.findViewById(R.id.uploadBtn)
        submitBtn = createdView.findViewById(R.id.submitBtn)

        aboutUserChar = createdView.findViewById(R.id.aboutUser_char_TV)
        aboutUserCharMax = createdView.findViewById(R.id.aboutUserMax)

        aboutUserChar2 = createdView.findViewById(R.id.aboutUser_char_TV2)
        aboutUserCharMax2 = createdView.findViewById(R.id.aboutUserMax2)
    }

    interface OnSendQuotationListener {
        fun submitQuotation(qutation: Qutation)
    }
}