package sa.gov.ksaa.dal.ui.fragments.wallet

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import sa.gov.ksaa.dal.R
import sa.gov.ksaa.dal.data.models.WalletTransaction
import sa.gov.ksaa.dal.ui.adapters.WalletTransactionsRVadapter
import sa.gov.ksaa.dal.ui.fragments.BaseFragment
class ClientWalletFragment : BaseFragment(R.layout.fragment_client_wallet) {

    lateinit var walletTransactionsRVadapter: WalletTransactionsRVadapter
    lateinit var operationsCountTV: TextView
    lateinit var completedTV: TextView
    lateinit var suspendedTV: TextView
    lateinit var declinedTV: TextView

//
//    lateinit var availableCreditCard: CardView
//    lateinit var suspendedBalanceCard: CardView
//    lateinit var withdrawnBalanceCard: CardView
    lateinit var operation_details_LL: LinearLayout
    lateinit var recyclerView: RecyclerView
//    lateinit var addWithdrawRequestBtn: Button
    private fun initViews(createdView: View) {
//        availableCreditCard = createdView.findViewById(R.id.availableCreditCard)
//        suspendedBalanceCard = createdView.findViewById(R.id.suspendedBalanceCard)
//        withdrawnBalanceCard = createdView.findViewById(R.id.withdrawnBalanceCard)
        operation_details_LL = createdView.findViewById(R.id.operation_details_LL)
        recyclerView = createdView.findViewById(R.id.recyclerView)
//        addWithdrawRequestBtn = createdView.findViewById(R.id.addWithdrawRequestBtn)
        operationsCountTV = createdView.findViewById(R.id.operationsCountTV)


    completedTV = createdView.findViewById(R.id.completedTV)
    completedTV.text = numberFormat.format(completedTV.text.toString().toLong())
    suspendedTV = createdView.findViewById(R.id.suspendedTV)
    suspendedTV.text = numberFormat.format(suspendedTV.text.toString().toLong())
    declinedTV = createdView.findViewById(R.id.declinedTV)
    declinedTV.text = numberFormat.format(declinedTV.text.toString().toLong())
    }


    override fun onViewCreated(createdView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(createdView, savedInstanceState)

        appBarLayout.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.GONE

        initViews(createdView)

        operationsCountTV.text = numberFormat.format(operationsCountTV.text.toString().toInt())

        walletTransactionsRVadapter = WalletTransactionsRVadapter(mutableListOf(WalletTransaction(), WalletTransaction(), WalletTransaction(),
            WalletTransaction(payment_status = WalletTransaction.REJECTED_STATUS),WalletTransaction(),WalletTransaction(payment_status = WalletTransaction.SUSPENDED_STATUS)), requireContext())

        recyclerView.adapter = walletTransactionsRVadapter
    }


}