package sa.gov.ksaa.dal.data.models

import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import java.util.Date

data class WalletTransaction(
    var payment_status: String? = null,
    var transaction_date: Date? = null,
    var sender: NewUser? = null,
    var reference_number: Int? = null,
    var amount_sar: Double? = null,
): MyModel() {
    companion object {
        const val PAIED_STATUS = "تم الدفع"
        const val REJECTED_STATUS = "مرفوضة"
        const val SUSPENDED_STATUS = "معلقة"
    }
}