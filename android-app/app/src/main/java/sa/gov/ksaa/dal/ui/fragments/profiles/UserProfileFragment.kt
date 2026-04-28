package sa.gov.ksaa.dal.ui.fragments.profiles

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64OutputStream
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.datastore.preferences.protobuf.ByteString
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.lyrebirdstudio.croppylib.Croppy
import com.lyrebirdstudio.croppylib.main.CropRequest
import com.soundcloud.android.crop.Crop
import com.soundcloud.android.crop.CropImageActivity
import com.soundcloud.android.crop.CropImageView
import com.yalantis.ucrop.UCrop
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okio.Path.Companion.toPath
import org.chromium.base.ContentUriUtils
import org.chromium.base.ContextUtils.getApplicationContext
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.TAG
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.MessageResponse
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.ui.MainActivity
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
import sa.gov.ksaa.dal.ui.fragments.CropperActivity
import sa.gov.ksaa.dal.ui.fragments.projects.explore.ProjectsFilterBottomSheetModal
import sa.gov.ksaa.dal.ui.viewModels.BaseVM
import sa.gov.ksaa.dal.ui.viewModels.ClientVM
import sa.gov.ksaa.dal.ui.viewModels.FreelancersVM
import sa.gov.ksaa.dal.ui.viewModels.ReviewsVM
import sa.gov.ksaa.dal.ui.viewModels.UserVM
import sa.gov.ksaa.dal.utils.FilePath.getPath
import sa.gov.ksaa.dal.utils.FilePath.isGooglePhotosUri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Base64
import java.util.Locale
import kotlin.math.min


class UserProfileFragment : BaseFragment(R.layout.fragment_profile_user),ImageChangeBottomSheet.OnChangeImage {



    val vm: UserVM by viewModels()
    val freelancerVm: FreelancersVM by viewModels()
    val clientVm: ClientVM by viewModels()
    val MAX_FILE_SIZE = 2000_000L
    lateinit var nameTV: TextView
    lateinit var userIV: ImageView
    lateinit var editPhotoIV: ImageView
    lateinit var ratingBar: RatingBar
//    lateinit var ratingAvgTV: TextView
    lateinit var reviewNumberTV: TextView
    lateinit var loginOutBtn: ImageButton
    lateinit var addNewProject_btn: MaterialTextView

    //    lateinit var projectInvitations_btn: MaterialTextView
    lateinit var projectsManagement_btn: MaterialTextView
    lateinit var personal_info_edit_btn: MaterialTextView
    lateinit var wallet_btn: MaterialTextView
    lateinit var reviews_btn: MaterialTextView
    lateinit var techSupport_btn: MaterialTextView
    lateinit var favourites_btn: MaterialTextView
    private lateinit var cropIntent:Intent
    lateinit var getContentss : ActivityResultLauncher<String>
     var selectedImage: Uri? = null


    companion object {
        const val EXCEEDING_MAX_FILE_SIZE_MSG = "حجم الملف تجاوز الحد المسوح به (2 م.ب)"
        private const val REQUEST_CODE_PERMISSIONS = 101
        private const val REQUEST_CODE_PICK_IMAGE = 102

    }

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
//        val view = inflater.inflate(R.layout.fragment_profile_user, container, false)
//        val button = view.findViewById<ImageView>(R.id.editPhotoIV)
//
//        button.setOnClickListener {
////                    profilePhotoActivityResultLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
////
////                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
//////                      openFile(null, IMAGES_TYPE, photoAtivityResultLauncher)
////                    }
//            onClickImgeChange()
//
//        }
//        //-----------------------
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val permissions = arrayOf(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            )
//            if (ContextCompat.checkSelfPermission(
//                    requireActivity(), // Use requireActivity() for non-null context
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                ) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(
//                    requireActivity(),
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    permissions,
//                    REQUEST_CODE_PERMISSIONS
//                )
//            }
//        }
//        //------------------------
//
//        return view
//
//    }

//    override fun onResume() {
//        super.onResume()
//        editPhotoIV.setOnClickListener {
////                    profilePhotoActivityResultLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
////
////                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
//////                      openFile(null, IMAGES_TYPE, photoAtivityResultLauncher)
////                    }
//            onClickImgeChange()
//
//        }
//    }



    fun assetUrl(u: Uri): String {
        var path = getFileUrlForUri(requireContext(), u)
        if (path?.startsWith("file://") == true) {
            path = path.replace("file://", "")
        }
        return "asset://localhost$path"
    }
    fun getFileUrlForUri(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return legacyPrimaryPath(split[1])
                } else {
                    val splitIndex = docId.indexOf(':', 1)
                    val tag = docId.substring(0, splitIndex)
                    val path = docId.substring(splitIndex + 1)
                    val nonPrimaryVolume = getPathToNonPrimaryVolume(context, tag)
                    if (nonPrimaryVolume != null) {
                        val result = "$nonPrimaryVolume/$path"
                        val file = File(result)
                        return if (file.exists() && file.canRead()) {
                            result
                        } else null
                    }
                }
            } else if (isDownloadsDocument(uri)) {
                val id: String = DocumentsContract.getDocumentId(uri)
                val contentUri: Uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                if (contentUri != null) {
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private val launchImageCrop =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {
//                val selectedImg: Uri = result.data?.data as Uri
//                uriToFile(selectedImg, this@CreateActivity).also { getFile = it }
//
//                createBinding.ivStory.setImageURI(selectedImg)


                val intent = Intent(requireActivity(), CropperActivity::class.java)
//                intent.putExtra("DATA", "haiiii")
                intent.putExtra("DATA", result.data?.data.toString())
//                Log.d("Teston", result.data?.data as Uri)
                startActivityForResult(intent, 101)
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





        val profilePhotoActivityResultLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->



            val cropRequest = CropRequest.Auto(sourceUri = uri!!, requestCode = 101)
            Croppy.start(mainActivity, cropRequest)


//            getImageFile(uri, object : FileListener {
//                override fun onFileChosen(myFile: MyFile) {
//
//                    Log.w(javaClass.simpleName, "onFileChosen: myFile = ${myFile} ")
//                    if (myFile.size > MAX_FILE_SIZE) {
//
//
//                        activitySnackbar.setText(EXCEEDING_MAX_FILE_SIZE_MSG)
//                            .show()
////                    CoroutineScope(Dispatchers.Default).launch {
////
////                        Log.w(javaClass.simpleName, "onFileChosen: myFile.imageRealPath = ${myFile.imageRealPath} ")
////
////                            val compressedImageFile = Compressor.compress(requireContext(), ) {
////                                size(MAX_FILE_SIZE) // 20 KB
////                            }
////                            if (compressedImageFile.length() > MAX_FILE_SIZE){
////                                activitySnackbar.setText(EXCEEDING_MAX_FILE_SIZE_MSG)
////                                    .show()
////                            } else {
////                                myFile.name = compressedImageFile.name
////                                myFile.size = compressedImageFile.length()
////                                myFile.inputStream = compressedImageFile.inputStream()
////                                myFile.uri = compressedImageFile.toUri()
////
////                                if (_user!!.isClient())
////                                    updateClientPhoto(myFile)
////                                else if (_user!!.isFreelancer())
////                                    updateFreelancerPhoto(myFile)
////                            }
////                    }
//
//                    } else {
//                        if (_user!!.isClient())
//
//                            updateClientPhoto(myFile)
//                        else if (_user!!.isFreelancer())
//                            updateFreelancerPhoto(myFile)
////                        else setProfileImage(myFile.uri, userIV, _user.t)
//                    }
//                }
//
//            })
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val createdView = inflater.inflate(R.layout.fragment_profile_user, container, false)
//        initViews(createdView)
//        animate()
        editPhotoIV =  createdView.findViewById(R.id.editPhotoIV)
//        editPhotoIV.setOnClickListener{
//            onClickImgeChange()
//        }
        return createdView
    }


    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        appBarLayout.visibility = View.GONE
        bottomNavigationView.visibility = View.VISIBLE


        val startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            Log.d("result", "result was reached by contract")
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("result", "result was reached by contract")
                val data: Intent? = result.data
            }
        }



        initViews(createdView)

//        backBtn.setOnClickListener {
//            findNavController().popBackStack()
//        }



        updateUI(_user)
        activityVM.userMLD.observe(viewLifecycleOwner) {
            updateUI(it)
        }



        loginOutBtn.setOnClickListener {
            mainActivity.bottomNavigationView.visibility = View.GONE
            mainActivity.getPreferences(Context.MODE_PRIVATE)
                .edit()
                .remove("user")
                .apply()
            activityVM.setUser(null)
        }
        projectsManagement_btn.setOnClickListener {
            if (_user!!.isClient())
                findNavController().navigate(R.id.action_userProfileFragment_to_projectsBidsCientFragment)
            else
                findNavController().navigate(R.id.action_userProfileFragment_to_projectsBidsFragment)
        }
        personal_info_edit_btn.setOnClickListener {
            if (_user!!.isClient())
                findNavController().navigate(R.id.action_userProfileFragment_to_personalInfoUpdateFragment)
            else if (_user!!.isFreelancer())
                findNavController().navigate(R.id.action_userProfileFragment_to_freelancerPersonalInfoEditingFragment)

        }

        wallet_btn.setOnClickListener {
//            if (_user!!.isClient())
//                findNavController().navigate(R.id.action_userProfileFragment_to_clientWalletFragment)
//            else findNavController().navigate(R.id.action_userProfileFragment_to_freelancerWalletFragment)
        }

        reviews_btn.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_reviewsFragment)
        }

        techSupport_btn.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_technicalSupportTicketsFragment)
        }

        // This callback is only called when MyFragment is at least started
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner,onBackPressedCallback)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {

                Toast.makeText(
                    requireActivity(),
                    "Permissions granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                Toast.makeText(
                    requireActivity(),
                    "Permissions not granted",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }





    private val  cropactivetyresult = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            TODO("Not yet implemented")


        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            TODO("Not yet implemented")
        }

    }



//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
//            if (resultCode == RESULT_OK && data != null) {
//                val imageUri = data.data
//                if (imageUri != null) {
//                    startCrop(imageUri) // Delegate cropping logic to a separate method
//                }
//            }
//        } else if (requestCode == Crop.REQUEST_CROP) {
//            if (resultCode == RESULT_OK) {
//                val croppedImageUri = Crop.getOutput(data)
//                if (croppedImageUri != null) {
//                    try {
//                        val bitmap = MediaStore.Images.Media.getBitmap(
//                            requireActivity().contentResolver,
//                            croppedImageUri
//
//                        )
//                        val myFile = convertBitmapToFile(bitmap,"iMAGE")
//
//
//                        Log.w(javaClass.simpleName, "onFileChosen: myFile = ${croppedImageUri} ")
//                        getImageFile(myFile, object : FileListener {
//                            override fun onFileChosen(myFile: MyFile) {
//
//                                Log.w(javaClass.simpleName, "onFileChosen: myFile = ${myFile} ")
//                                if (myFile.size > MAX_FILE_SIZE) {
//
//
//                                    activitySnackbar.setText(EXCEEDING_MAX_FILE_SIZE_MSG)
//                                        .show()
////                    CoroutineScope(Dispatchers.Default).launch {
////
////                        Log.w(javaClass.simpleName, "onFileChosen: myFile.imageRealPath = ${myFile.imageRealPath} ")
////
////                            val compressedImageFile = Compressor.compress(requireContext(), ) {
////                                size(MAX_FILE_SIZE) // 20 KB
////                            }
////                            if (compressedImageFile.length() > MAX_FILE_SIZE){
////                                activitySnackbar.setText(EXCEEDING_MAX_FILE_SIZE_MSG)
////                                    .show()
////                            } else {
////                                myFile.name = compressedImageFile.name
////                                myFile.size = compressedImageFile.length()
////                                myFile.inputStream = compressedImageFile.inputStream()
////                                myFile.uri = compressedImageFile.toUri()
////
////                                if (_user!!.isClient())
////                                    updateClientPhoto(myFile)
////                                else if (_user!!.isFreelancer())
////                                    updateFreelancerPhoto(myFile)
////                            }
////                    }
//
//                                } else {
//                                    if (_user!!.isClient())
//
//                                        updateClientPhoto(myFile)
//                                    else if (_user!!.isFreelancer())
//                                        updateFreelancerPhoto(myFile)
////                        else setProfileImage(myFile.uri, userIV, _user.t)
//                                }
//                            }
//
//                        })
//                        // Optionally call a separate method to save the image to gallery
//                        // saveImageToGallery(bitmap)
//                    } catch (e: Exception) {
//                        // Handle potential exceptions during image retrieval (e.g., security exceptions)
//                        Log.e("MyFragment", "Error getting cropped image:", e)
//                        Toast.makeText(
//                            requireActivity(),
//                            "Error loading image.",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        }
//    }
    private fun startCrop(imageUri: Uri) {
        // Use a library like Glide or Picasso to handle potential OutOfMemoryExceptions
        // for large images before cropping. Consider using a background service for heavy tasks.
        Crop.of(imageUri, Uri.fromFile(File(requireActivity().cacheDir, "cropped")))
            .asSquare() // Adjust cropping options as needed
            .start(requireActivity(), Crop.REQUEST_CROP) // Pass the request code
    }
    private fun convertBitmapToFile(bitmap: Bitmap, fileName: String): Uri? {
        try {
            val cacheDir = requireActivity().cacheDir
            val file = File(cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Adjust format and quality as needed
            outputStream.close()
            return Uri.fromFile(file)
        } catch (e: Exception) {
            Log.e("MyFragment", "Error saving bitmap to file:", e)
            return null
        }
    }


    fun initViews(createdView: View) {
        nameTV = createdView.findViewById(R.id.nameTV)
        userIV = createdView.findViewById(R.id.userIV)
//        editPhotoIV = createdView.findViewById(R.id.editPhotoIV)
        ratingBar = createdView.findViewById(R.id.ratingBar)
//        ratingAvgTV = createdView.findViewById(R.id.ratingAvgTV)
        reviewNumberTV = createdView.findViewById(R.id.reviewNumberTV)
        loginOutBtn = createdView.findViewById(R.id.login_out_btn)
        addNewProject_btn = createdView.findViewById(R.id.addNewProject_btn)
//        projectInvitations_btn = createdView.findViewById(R.id.projectInvitations_btn)
        projectsManagement_btn = createdView.findViewById(R.id.projectsManagement_btn)
        personal_info_edit_btn = createdView.findViewById(R.id.personal_info_edit_btn)
        wallet_btn = createdView.findViewById(R.id.wallet_btn)
        reviews_btn = createdView.findViewById(R.id.reviews_btn)
        techSupport_btn = createdView.findViewById(R.id.techSupport_btn)
        favourites_btn = createdView.findViewById(R.id.favourites_btn)
//        backBtn = createdView.findViewById(R.id.backBtn)

    }




    private fun startCropActivity(uri: Uri?) {
        val options = UCrop.Options().apply {
            // Customize cropping options as needed
            setCompressionQuality(90)
            setToolbarTitle("Crop Image")
            // Add more options as needed
        }

        // Start UCrop activity
        if (uri != null) {
            UCrop.of(uri, Uri.fromFile(File(requireContext().cacheDir, "cropped_image.jpg")))
                .withOptions(options)
                .start(requireContext(), this)


        }


    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            val uriContent = result.uriContent
            val uriFilePath = result.getUriFilePath(requireContext()) // optional usage

            getImageFile(uriContent, object : FileListener {
                    override fun onFileChosen(myFile: MyFile) {

                        Log.w(javaClass.simpleName, "onFileChosen: myFile = ${myFile} ")
                        if (myFile.size > MAX_FILE_SIZE) {


                            activitySnackbar.setText(EXCEEDING_MAX_FILE_SIZE_MSG)
                                .show()

//

                        } else {
                            if (_user!!.isClient())

                                updateClientPhoto(myFile)
                            else if (_user!!.isFreelancer())
                                updateFreelancerPhoto(myFile)
//                        else setProfileImage(myFile.uri, userIV, _user.t)
                        }
                    }

                })

        } else {
            // An error occurred.
            val exception = result.error
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == -1 && requestCode==101) {
            val result = data?.getStringExtra("RESULT")
            var resultUrl: Uri? = null
//            val selectedImg: Uri = data?.data as Uri
            if (result != null) {
                resultUrl = Uri.parse(result)

               val bitmap = MediaStore.Images.Media.getBitmap(mainActivity.contentResolver, resultUrl);
                userIV.setImageBitmap(bitmap)
                if (_user!!.isFreelancer()) {
                    updateFreelancerPhotoTow(bitmap)
                }else if (_user!!.isClient()){
                    updateClientPhotoTow(bitmap)
                }


//                getImageFile(resultUrl, object : FileListener {
//                    override fun onFileChosen(myFile: MyFile) {
//
//                        Log.w(javaClass.simpleName, "onFileChosen: myFile = ${myFile} ")
//                        if (myFile.size > MAX_FILE_SIZE) {
//
//
//                            activitySnackbar.setText(EXCEEDING_MAX_FILE_SIZE_MSG)
//                                .show()
//
////
//
//                        } else {
//                            if (_user!!.isClient())
//
//                                updateClientPhoto(myFile)
//                            else if (_user!!.isFreelancer())
//                                updateFreelancerPhoto(myFile)
////                        else setProfileImage(myFile.uri, userIV, _user.t)
//                        }
//                    }
//
//                })

            }


        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode == -1 && requestCode==101) {
//            val result = data?.getStringExtra("RESULT")
//            var resultUrl: Uri? = null
//            if (result != null) {
//                resultUrl = Uri.parse(result)
//                Log.e("MyFragment", "Error getting cropped image:${resultCode}")
//
//                val bitmap = MediaStore.Images.Media.getBitmap(mainActivity.contentResolver, resultUrl)
//
//
//                updateFreelancerPhoto(saveImageToGallery(bitmap)!!)
//                selectedImage = resultUrl
//
//                val file = File(resultUrl.path!!) // Assuming path is not null
//
//                if (file.exists()) {
//                    val fileSize = file.length()
//                    Log.d("FileSize", "Size of local file: $fileSize bytes")
//                } else {
//                    Log.w("FileSize", "Local file not found: $resultUrl")
//                }
//
//
//
//
//                val filePath = resultUrl.path
//                val privateFile = File(filePath)
//
//// Ensure you have necessary permissions (WRITE_EXTERNAL_STORAGE)
//                val publicDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) // Or use DIRECTORY_PICTURES
//                val publicFile = File(publicDir, privateFile.name)
//
//                if (publicFile.exists()) {
//                    publicFile.delete() // In case it exists from a previous copy operation
//                }
//
//                privateFile.copyTo(publicFile, true)  // Replace if necessary
//
//                val publicUri = Uri.fromFile(publicFile)
//                val contentResolver = requireActivity().applicationContext.contentResolver
//
//                val selectedImg: Uri = data?.data as Uri
//                getImageFile(selectedImg, object : FileListener {
//                    override fun onFileChosen(myFile: MyFile) {
//
//                        Log.w(javaClass.simpleName, "onFileChosen: myFile = ${myFile} ")
//                        if (myFile.size > MAX_FILE_SIZE) {
//
//
//                            activitySnackbar.setText(EXCEEDING_MAX_FILE_SIZE_MSG)
//                                .show()
////                    CoroutineScope(Dispatchers.Default).launch {
////
////                        Log.w(javaClass.simpleName, "onFileChosen: myFile.imageRealPath = ${myFile.imageRealPath} ")
////
////                            val compressedImageFile = Compressor.compress(requireContext(), ) {
////                                size(MAX_FILE_SIZE) // 20 KB
////                            }
////                            if (compressedImageFile.length() > MAX_FILE_SIZE){
////                                activitySnackbar.setText(EXCEEDING_MAX_FILE_SIZE_MSG)
////                                    .show()
////                            } else {
////                                myFile.name = compressedImageFile.name
////                                myFile.size = compressedImageFile.length()
////                                myFile.inputStream = compressedImageFile.inputStream()
////                                myFile.uri = compressedImageFile.toUri()
////
////                                if (_user!!.isClient())
////                                    updateClientPhoto(myFile)
////                                else if (_user!!.isFreelancer())
////                                    updateFreelancerPhoto(myFile)
////                            }
////                    }
//
//                        } else {
//                            if (_user!!.isClient())
//
//                                updateClientPhoto(myFile)
//                            else if (_user!!.isFreelancer())
//                                updateFreelancerPhoto(myFile)
////                        else setProfileImage(myFile.uri, userIV, _user.t)
//                        }
//                    }
//
//                })
//            }
//
//
//        }
//    }
    private fun saveImageToGallery(bitmap: Bitmap): MyFile? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "Image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
        }

        val contentResolver = mainActivity.contentResolver

        try {
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                contentResolver.openOutputStream(uri).use { outputStream ->
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                        Toast.makeText(mainActivity, "Image saved to gallery", Toast.LENGTH_SHORT).show()

                        // Retrieve additional details
                        val cursor = contentResolver.query(
                            uri,
                            arrayOf(MediaStore.Images.Media.SIZE),
                            null, null, null
                        )

                        if (cursor != null) {
                            try {
                                val sizeIndex = cursor.getColumnIndex(MediaStore.Images.Media.SIZE)
                                cursor.moveToFirst()
                                val size = cursor.getLong(sizeIndex)

                                return MyFile(
                                    name = "Image_${System.currentTimeMillis()}.jpg",
                                    size = size,
                                    inputStream = null, // Not provided in this approach
                                    mimeType = "image/jpeg",
                                    uri = uri,
                                    imageRealPath = null  // Not possible to get real path for content URIs
                                )
                            } finally {
                                cursor.close()
                            }
                        } else {
                            Log.w("SaveImageToGallery", "Failed to get file size for content URI: $uri")
                        }
                    } else {
                        Toast.makeText(mainActivity, "Failed to save image to gallery", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(mainActivity, "Failed to save image to gallery", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("SaveImageToGallery", "Error saving image to gallery", e)
        }

        return null // Indicate failure if no MyFile object is created
    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK){
//            if (requestCode == UserProfileFragment.REQUEST_CODE_PICK_IMAGE && data != null) {
//                val uri = data.data
//
//                if (uri != null) {
//
//
//                    Crop.of(uri, Uri.fromFile(File(mainActivity.cacheDir, "cropped")))
//                        .asSquare()
//
//                        .start(mainActivity)
//
//
//
//
//                }
//            }
//
//            else if (requestCode == Crop.REQUEST_CROP ) {
//
//                Log.e("MyFragment", "Error getting cropped image:${resultCode}")
//                activitySnackbar.setText(UserProfileFragment.EXCEEDING_MAX_FILE_SIZE_MSG)
//                val croppedUri = Crop.getOutput(data)
//                if (croppedUri != null) {
//                    val bitmap =
//                        MediaStore.Images.Media.getBitmap(mainActivity.contentResolver, croppedUri)
//
////                saveImageToGallery(bitmap)
//                    activitySnackbar.setText(UserProfileFragment.EXCEEDING_MAX_FILE_SIZE_MSG)
//
//
//                }
//            }
//        }
//    }

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        getImageFile(uri, object : FileListener {
            override fun onFileChosen(myFile: MyFile) {

                Log.w(javaClass.simpleName, "onFileChosen: myFile = ${myFile} ")
                if (myFile.size > MAX_FILE_SIZE) {


                    activitySnackbar.setText(EXCEEDING_MAX_FILE_SIZE_MSG)
                        .show()
//                    CoroutineScope(Dispatchers.Default).launch {
//
//                        Log.w(javaClass.simpleName, "onFileChosen: myFile.imageRealPath = ${myFile.imageRealPath} ")
//
//                            val compressedImageFile = Compressor.compress(requireContext(), ) {
//                                size(MAX_FILE_SIZE) // 20 KB
//                            }
//                            if (compressedImageFile.length() > MAX_FILE_SIZE){
//                                activitySnackbar.setText(EXCEEDING_MAX_FILE_SIZE_MSG)
//                                    .show()
//                            } else {
//                                myFile.name = compressedImageFile.name
//                                myFile.size = compressedImageFile.length()
//                                myFile.inputStream = compressedImageFile.inputStream()
//                                myFile.uri = compressedImageFile.toUri()
//
//                                if (_user!!.isClient())
//                                    updateClientPhoto(myFile)
//                                else if (_user!!.isFreelancer())
//                                    updateFreelancerPhoto(myFile)
//                            }
//                    }

                } else {
                    if (_user!!.isClient())

                        updateClientPhoto(myFile)
                    else if (_user!!.isFreelancer())
                        updateFreelancerPhoto(myFile)
//                        else setProfileImage(myFile.uri, userIV, _user.t)
                }
            }

        })
    }


    val profilePhotoActivityResultLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->






//        getImageFile(uri, object : FileListener {
//            override fun onFileChosen(myFile: MyFile) {
//
//                Log.w(javaClass.simpleName, "onFileChosen: myFile = ${myFile} ")
//                if (myFile.size > MAX_FILE_SIZE) {
//
//
//                    activitySnackbar.setText(EXCEEDING_MAX_FILE_SIZE_MSG)
//                        .show()
////                    CoroutineScope(Dispatchers.Default).launch {
////
////                        Log.w(javaClass.simpleName, "onFileChosen: myFile.imageRealPath = ${myFile.imageRealPath} ")
////
////                            val compressedImageFile = Compressor.compress(requireContext(), ) {
////                                size(MAX_FILE_SIZE) // 20 KB
////                            }
////                            if (compressedImageFile.length() > MAX_FILE_SIZE){
////                                activitySnackbar.setText(EXCEEDING_MAX_FILE_SIZE_MSG)
////                                    .show()
////                            } else {
////                                myFile.name = compressedImageFile.name
////                                myFile.size = compressedImageFile.length()
////                                myFile.inputStream = compressedImageFile.inputStream()
////                                myFile.uri = compressedImageFile.toUri()
////
////                                if (_user!!.isClient())
////                                    updateClientPhoto(myFile)
////                                else if (_user!!.isFreelancer())
////                                    updateFreelancerPhoto(myFile)
////                            }
////                    }
//
//                } else {
//                    if (_user!!.isClient())
//
//                        updateClientPhoto(myFile)
//                    else if (_user!!.isFreelancer())
//                        updateFreelancerPhoto(myFile)
////                        else setProfileImage(myFile.uri, userIV, _user.t)
//                }
//            }
//
//        })
    }




    val photoAtivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            gettingFile(activityResult, object : FileListener {
                override fun onFileChosen(myFile: MyFile) {
                    Log.w(javaClass.simpleName, "onFileChosen: myFile = ${myFile} ")
                    if (myFile.size > MAX_FILE_SIZE) {

                        lifecycleScope.launch {
                            Log.w(javaClass.simpleName, "onFileChosen: myFile.imageRealPath = ${myFile.imageRealPath} ")
                            val compressedImageFile = Compressor.compress(requireContext(), File(
                                myFile.uri?.path ?: ""
                            )) {
                                size(MAX_FILE_SIZE) // 20 KB
                            }
                            if (compressedImageFile.length() > MAX_FILE_SIZE){
                                activitySnackbar.setText(EXCEEDING_MAX_FILE_SIZE_MSG)
                                    .show()

                            } else {
                                myFile.name = compressedImageFile.name
                                myFile.size = compressedImageFile.length()
                                myFile.inputStream = compressedImageFile.inputStream()
                                myFile.uri = compressedImageFile.toUri()


                                if (_user!!.isClient())
                                    updateClientPhoto(myFile)
                                else if (_user!!.isFreelancer())
                                    updateFreelancerPhoto(myFile)
                            }
                        }

                    } else {
                        if (_user!!.isClient()){
                            updateClientPhoto(myFile)
                             }
                        else if (_user!!.isFreelancer())
                            updateFreelancerPhoto(myFile)
//                        else setProfileImage(myFile.uri, userIV, _user.t)
                    }
                }

            })

        }
    }
    fun NavController.safeNavigateWithArgs(direction: NavDirections, bundle: Bundle?) {
        currentDestination?.getAction(direction.actionId)?.run {
            navigate(direction.actionId, bundle)
        }
    }


    var launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.getResultCode() === RESULT_OK) {
                val uri: Uri? = result.getData()?.getData()
                // Use the uri to load the image
            } else if (result.getResultCode() === ImagePicker.RESULT_ERROR) {
                // Use ImagePicker.Companion.getError(result.getData()) to show an error
            }
        }

    private fun updateUI(user: NewUser?) {
        Log.i(javaClass.simpleName, "updateUI: user = $user")
        if (user == null) {
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToGetStartedToLogin()

            findNavController().safeNavigateWithArgs(action,null)

        } else {
            if (user.userId == null || user.userId == 0) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        findNavController().navigate(R.id.action_userProfileFragment_to_getStartedToLogin)
                    }
                }

            } else {
                nameTV.text = user.getFullName()

                var photoURI: Any? = null
                var userType: String?
                var userGender: String? = null

                Glide.with(this)
                    .load(R.drawable.camer_image_icon)
                    .placeholder(R.drawable.camer_image_icon)
                    .centerCrop()
                    .transform(
                        CenterCrop(),
                        CircleCrop()
                    )
                    .into(editPhotoIV)

//
                editPhotoIV.setOnClickListener {
////                    profilePhotoActivityResultLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
////
////                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
//////                      openFile(null, IMAGES_TYPE, photoAtivityResultLauncher)
////                    }
//            onClickImgeChange()

                    onClickImgeChange()



        }


                if (user.isFreelancer()) {
                    getReviews()
                    if (currentFreelancer == null) {
                        findNavController().navigate(R.id.action_userProfileFragment_to_getStartedToLogin)
                    } else {
                        freelancerVM.getFreelancersByUserId(mapOf("userId" to user.userId.toString()))
                            .observe(viewLifecycleOwner) {
                                newHandleSuccessOrErrorResponse(it, { freelancersItems ->
                                    if (freelancersItems.isNotEmpty()) {
                                        val freelancer = freelancersItems[0]
                                        _user?.image = freelancer.image
                                        setProfileImage(freelancer.image, userIV,_user!!.userType ,userGender )
                                        currentFreelancer = freelancer

                                    }
                                })
                            }

                        userGender = currentFreelancer!!.gender

                        val rating = currentFreelancer!!.rating ?: 0.0f
//                        ratingAvgTV.text = formatArabicNumber(rating) // numberFormat.format(currentFreelancer!!.getAverageRating())
                        ratingBar.rating = currentFreelancer!!.getAverageRating()
                        reviewNumberTV.text = numberFormat.format(currentFreelancer!!.listOfRating?.size ?:0)

                        addNewProject_btn.visibility = View.GONE
//                    projectInvitations_btn.setOnClickListener {
//                        findNavController().navigate(R.id.action_userProfileFragment_to_projectInvitationsFragment)
//                    }
                        favourites_btn.setOnClickListener {
                            findNavController().navigate(R.id.action_userProfileFragment_to_favouriteProjectsFragment)
                        }
                    }

                } else if (user.isClient()) {
                    getReviews()

                    clientVm.getClientsById(user.userId!!).observe(viewLifecycleOwner) {
                        newHandleSuccessOrErrorResponse(it, { clenitItems ->
                            if (clenitItems.isNotEmpty()) {
                                val clenit = clenitItems[0]

                                setProfileImage(clenit.imageUrl, userIV,_user!!.userType ,userGender )

                            }
                        })
                    }

                    photoURI = _user?.image
                    userGender = _user!!.gender
//                    projectInvitations_btn.visibility = View.GONE
                    addNewProject_btn.setOnClickListener {
                        findNavController().navigate(R.id.action_userProfileFragment_to_addEditProjectFragment)
                    }
                    favourites_btn.setOnClickListener {
                        findNavController().navigate(R.id.action_userProfileFragment_to_favouriteFreelancersFragment)
                    }


                }


            }
        }

        Log.w(javaClass.simpleName, "onViewCreated: params----------- = ${_user?.image}")
    }


    val reviewsVM: ReviewsVM by viewModels()
    fun getReviews(){
        // userId=2
        reviewsVM.getAllReviewsByUserId(mapOf("userId" to _user!!.userId.toString()))
            .observe(viewLifecycleOwner){res ->
                newHandleSuccessOrErrorResponse(res, {
                    var sum = 0.0f
                    val avg: Float
                    val count: Int
                    if (it.isEmpty()) {
                        avg = 0.0f
                        count = 0
                    } else {
                        it.forEach {review ->
                            sum += review.userRating!!
                        }
                        avg = sum / it.size
                    }

//                    ratingAvgTV.text = String.format(arabicLocale, "%.1f", avg) // numberFormat.format(avg)
                    ratingBar.rating = avg
                    reviewNumberTV.text = numberFormat.format(it.size)
                })
            }
    }

    fun updateFreelancerPhoto(photoFile: MyFile){
        val params = mutableMapOf<String, String>(
            "userId" to _user!!.userId.toString(),
            "firstName" to _user!!.firstName!!,
            "lastName" to _user!!.lastName!!,
            "phone" to _user!!.phone!!,
            "about" to currentFreelancer!!.about!!,
            "email" to _user!!.email!!,
            "username" to "username",
            "skills" to   "[\"${currentFreelancer!!.listOfServices!![0].typeOfServices!!}\"]",
            "experience" to currentFreelancer!!.experience!!,
            "gender" to currentFreelancer!!.gender!!,
            "yearOfExperience" to (currentFreelancer!!.yearOfExperience?: 5).toString(),
            "typeOfCertificate" to currentFreelancer!!.typeOfCertificate!!,
            "graduationYear" to (currentFreelancer!!.graduationYear?: "2020"),
            "enrollmentYear" to (currentFreelancer!!.enrollmentYear?: "2015"),
            "nameOfUnivesity" to (currentFreelancer!!.universityName?: "ام القرى"),
            "password" to _user!!.password!!
        )
        Log.w(javaClass.simpleName, "onViewCreated: params = $params")
        // userId=7, firstName=خالد, lastName=احمد, phone=966536637215, about=نبذة, email=f.test01@gmail.com,
        // username=none, skills=ترجمة, experience=خبرة, profileImage=
        freelancerVM.update_aFreelancer(params, photoFile,null)
            .observe(viewLifecycleOwner) { res ->
                newHandleSuccessOrErrorResponse(res, { msgResponse ->
                    if (msgResponse.message == MessageResponse.UPDATE_PROFILE_SUCCESS_MSG) {
                        currentFreelancer!!.image = msgResponse.imageUrl?:currentFreelancer!!.image
                        setProfileImage(currentFreelancer!!.image, userIV , _user!!.userType, currentFreelancer!!.gender)
                    }
                }){errors ->
                    activitySnackbar
                        .setText(R.string.something_went_wrong_please_try_again_later)
                        .show()

                }

            }
    }

    fun updateFreelancerPhotoTow(photoFile: Bitmap){

        val bitmap = photoFile

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,90,outputStream)
        val byteArray = outputStream.toByteArray()
//        val byteString = ByteString.copyFrom(byteArray)
//        val base64ImageString = byteString.toStringUtf8()



        val params = mutableMapOf<String, String>(
            "userId" to _user!!.userId.toString(),
            "firstName" to _user!!.firstName!!,
            "lastName" to _user!!.lastName!!,
            "phone" to _user!!.phone!!,
            "about" to currentFreelancer!!.about!!,
            "email" to _user!!.email!!,
            "username" to "username",
            "skills" to   "[\"${currentFreelancer!!.listOfServices!![0].typeOfServices!!}\"]",
            "experience" to currentFreelancer!!.experience!!,
            "gender" to currentFreelancer!!.gender!!,
            "yearOfExperience" to (currentFreelancer!!.yearOfExperience?: 5).toString(),
            "typeOfCertificate" to currentFreelancer!!.typeOfCertificate!!,
            "graduationYear" to (currentFreelancer!!.graduationYear?: "2020"),
            "enrollmentYear" to (currentFreelancer!!.enrollmentYear?: "2015"),
            "nameOfUnivesity" to (currentFreelancer!!.universityName?: "ام القرى"),
            "password" to _user!!.password!!,



        )
        Log.w(javaClass.simpleName, "onViewCreated: params = $params")
        // userId=7, firstName=خالد, lastName=احمد, phone=966536637215, about=نبذة, email=f.test01@gmail.com,
        // username=none, skills=ترجمة, experience=خبرة, profileImage=
        freelancerVM.update_aFreelancer(params, null,photoFile)
            .observe(viewLifecycleOwner) { res ->
                newHandleSuccessOrErrorResponse(res, { msgResponse ->
                    if (msgResponse.message == MessageResponse.UPDATE_PROFILE_SUCCESS_MSG) {
                        currentFreelancer!!.image = msgResponse.imageUrl?:currentFreelancer!!.image
                        setProfileImage(currentFreelancer!!.image, userIV , _user!!.userType, currentFreelancer!!.gender)
                    }
                }){errors ->
                    activitySnackbar
                        .setText(R.string.something_went_wrong_please_try_again_later)
                        .show()

                }

            }
    }
    fun deleteFreelancerPhoto(){
        val params = mutableMapOf<String, String>(
            "userId" to _user!!.userId.toString(),
            "firstName" to _user!!.firstName!!,
            "lastName" to _user!!.lastName!!,
            "phone" to _user!!.phone!!,
            "about" to currentFreelancer!!.about!!,
            "email" to _user!!.email!!,
            "username" to "username",
            "skills" to   "[\"${currentFreelancer!!.listOfServices!![0].typeOfServices!!}\"]",
            "experience" to currentFreelancer!!.experience!!,
            "gender" to currentFreelancer!!.gender!!,
            "yearOfExperience" to (currentFreelancer!!.yearOfExperience?: 5).toString(),
            "typeOfCertificate" to currentFreelancer!!.typeOfCertificate!!,
            "graduationYear" to (currentFreelancer!!.graduationYear?: "2020"),
            "enrollmentYear" to (currentFreelancer!!.enrollmentYear?: "2015"),
            "nameOfUnivesity" to (currentFreelancer!!.universityName?: "ام القرى"),
            "password" to _user!!.password!!
        )
        Log.w(javaClass.simpleName, "onViewCreated: params = $params")
        // userId=7, firstName=خالد, lastName=احمد, phone=966536637215, about=نبذة, email=f.test01@gmail.com,
        // username=none, skills=ترجمة, experience=خبرة, profileImage=
        freelancerVM.delete_image_aFreelancer(params, null)
            .observe(viewLifecycleOwner) { res ->
                newHandleSuccessOrErrorResponse(res, { msgResponse ->
                    if (msgResponse.message == MessageResponse.UPDATE_PROFILE_SUCCESS_MSG) {
                        currentFreelancer!!.image = msgResponse.imageUrl?:currentFreelancer!!.image
                        setProfileImage(currentFreelancer!!.image, userIV , _user!!.userType, currentFreelancer!!.gender)
                    }
                }){errors ->
                    activitySnackbar
                        .setText(R.string.something_went_wrong_please_try_again_later)
                        .show()

                }

            }
    }
    fun formatArabicNumber(number: Float): String {
        val arabicLocale = Locale("ar", "SA")

        val decimalFormat = NumberFormat.getInstance(arabicLocale) as DecimalFormat
//        val symbols = decimalFormat.decimalFormatSymbols
//        symbols.zeroDigit = '٠' // Set the zero digit to Arabic-Indic zero

        return decimalFormat.format(number)
    }
    fun updateClientPhoto(profileImage: MyFile){
        val params = mutableMapOf<String, String>(
            "userId" to _user!!.userId.toString(),
            "firstName" to _user!!.firstName!!,
            "lastName" to _user!!.lastName!!,
            "phone" to _user!!.phone!!,
            "about" to _user!!.about!!,
            "email" to _user!!.email!!,
            "username" to "username",
            "gender" to _user!!.gender!!,
            "password" to _user!!.password!!,
        )
        Log.w(javaClass.simpleName, "updateClientPhoto: params = $params")
        userVM.updateClient(params, profileImage,null)
            .observe(viewLifecycleOwner){ res ->
                newHandleSuccessOrErrorResponse(res, { respo ->
                    _user!!.imageUrl = respo.imageUrl?:_user!!.imageUrl
                    setProfileImage(_user!!.imageUrl, userIV , _user!!.userType, _user!!.gender)
                }) {errors ->
                    activitySnackbar
                        .setText(R.string.something_went_wrong_please_try_again_later)
                        .show()

                }
            }
    }
    fun updateClientPhotoTow(profileImage: Bitmap){
        val params = mutableMapOf<String, String>(
            "userId" to _user!!.userId.toString(),
            "firstName" to _user!!.firstName!!,
            "lastName" to _user!!.lastName!!,
            "phone" to _user!!.phone!!,
            "about" to _user!!.about!!,
            "email" to _user!!.email!!,
            "username" to "username",
            "gender" to _user!!.gender!!,
            "password" to _user!!.password!!,
        )
        Log.w(javaClass.simpleName, "updateClientPhoto: params = $params")
        userVM.updateClient(params, null,profileImage)
            .observe(viewLifecycleOwner){ res ->
                newHandleSuccessOrErrorResponse(res, { respo ->
                    _user!!.imageUrl = respo.imageUrl?:_user!!.imageUrl
                    setProfileImage(_user!!.imageUrl, userIV , _user!!.userType, _user!!.gender)
                }) {errors ->
                    activitySnackbar
                        .setText(R.string.something_went_wrong_please_try_again_later)
                        .show()

                }
            }
    }
    fun deleteClientPhoto(){
        val params = mutableMapOf<String, String>(
            "userId" to _user!!.userId.toString(),
            "firstName" to _user!!.firstName!!,
            "lastName" to _user!!.lastName!!,
            "phone" to _user!!.phone!!,
            "about" to _user!!.about!!,
            "email" to _user!!.email!!,
            "username" to "username",
            "gender" to _user!!.gender!!,
            "password" to _user!!.password!!,
        )
        Log.w(javaClass.simpleName, "updateClientPhoto: params = $params")
        userVM.delete_image_aClaein(params, null)
            .observe(viewLifecycleOwner){ res ->
                newHandleSuccessOrErrorResponse(res, { respo ->
                    _user!!.imageUrl = respo.imageUrl?:_user!!.imageUrl
                    setProfileImage(_user!!.imageUrl, userIV , _user!!.userType, _user!!.gender)
                }) {errors ->
                    activitySnackbar
                        .setText(R.string.something_went_wrong_please_try_again_later)
                        .show()

                }
            }
    }

    private val cropLauncher = registerForActivityResult(CropImageContract()) { croppedUri ->
        if (croppedUri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(mainActivity.contentResolver, croppedUri.uriContent)
            // Handle the bitmap here (e.g., display or save)
        } else {
            // Handle failed cropping (e.g., show error message)
            activitySnackbar.setText(UserProfileFragment.EXCEEDING_MAX_FILE_SIZE_MSG)
        }
    }

    override fun onClickImgeChange() {
        val imageChangeBottomSheetModel = ImageChangeBottomSheet(this)
        mainActivity = requireActivity() as MainActivity

        imageChangeBottomSheetModel.show(
            mainActivity.supportFragmentManager,
            ProjectsFilterBottomSheetModal.TAG
        )
    }

    override fun onImageChange() {

//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        profilePhotoActivityResultLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//
//                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
////                      openFile(null, IMAGES_TYPE, photoAtivityResultLauncher)
//                    }

//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//
//        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)

        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent. createChooser(intent, "Choose Picture")
        launchImageCrop.launch(chooser)


    }

    override fun deleteImageProfile() {
        if (_user?.isFreelancer() == true){
            deleteFreelancerPhoto()
        }else{
            deleteClientPhoto()
        }


    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var path: String? = null
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                path = cursor.getString(index)
            }
        } catch (ex: IllegalArgumentException) {
            return getCopyFilePath(uri, context)
        } finally {
            cursor?.close()
        }
        return path ?: getCopyFilePath(uri, context)
    }
    private fun getCopyFilePath(uri: Uri, context: Context): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        val name = cursor.getString(nameIndex)
        val file = File(context.filesDir, name)
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read: Int
            val maxBufferSize = 1024 * 1024
            val bufferSize = min(inputStream!!.available(), maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            return null
        } finally {
            cursor.close()
        }
        return file.path
    }

    private fun legacyPrimaryPath(pathPart: String): String {
        return Environment.getExternalStorageDirectory().toString() + "/" + pathPart
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }
    private fun getPathToNonPrimaryVolume(context: Context, tag: String): String? {
        val volumes = context.externalCacheDirs
        if (volumes != null) {
            for (volume in volumes) {
                if (volume != null) {
                    val path = volume.absolutePath
                    val index = path.indexOf(tag)
                    if (index != -1) {
                        return path.substring(0, index) + tag
                    }
                }
            }
        }
        return null
    }


}
fun NavController.safeNavigateWithArgs(direction: NavDirections, bundle: Bundle?) {
    currentDestination?.getAction(direction.actionId)?.run {
        navigate(direction.actionId, bundle)
    }



}

class CropImageContract : ActivityResultContract<Unit, Uri?>() {
    override fun createIntent(context: Context, params: Unit): Intent {
        // Logic to create an intent to launch the image picking activity goes here
        // (similar to what you might have done before in onActivityResult)
        return Intent() // Replace with your actual intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode == Activity.RESULT_OK && intent != null) {
            return Crop.getOutput(intent)
        }
        return null
    }
}
