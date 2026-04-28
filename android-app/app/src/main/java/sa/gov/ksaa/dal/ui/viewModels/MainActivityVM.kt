package sa.gov.ksaa.dal.ui.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import sa.gov.ksaa.dal.data.models.Certification
import sa.gov.ksaa.dal.data.webservices.dal.requests.FreelancersInvitation
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.AddProjectFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.BiddingInvitation
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ClosedProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.FavouriteFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewBid
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewDeliverableFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.OtpModel
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ProjectUnderway
import sa.gov.ksaa.dal.data.webservices.newDal.responses.RatingAndReview
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ServicesModel
import sa.gov.ksaa.dal.data.webservices.newDal.responses.SpamCommint
import sa.gov.ksaa.dal.data.webservices.newDal.responses.TSTicket
import sa.gov.ksaa.dal.data.webservices.newDal.responses.TechnicalSupportRequest

class MainActivityVM : ViewModel(){

    val deliverable = MutableLiveData<NewDeliverableFile?>()
    val certificationMLD = MutableLiveData<Certification?>()
    val addProjectFileMLD = MutableLiveData<AddProjectFile?>()
    val clientMLD = MutableLiveData<NewUser?>()
    val closedProject= MutableLiveData<ClosedProject?>()
    val postRatingAndReview= MutableLiveData<RatingAndReview?>()
    val postReportCommint= MutableLiveData<SpamCommint?>()
    val postReviews= MutableLiveData<RatingAndReview?>()
    val ongoingProjectLD = MutableLiveData<ProjectUnderway?>()
    val bidMLD = MutableLiveData<NewBid?>()
    val projecInvitationtMLD = MutableLiveData<BiddingInvitation?>()
    val userMLD: MutableLiveData<NewUser?> = MutableLiveData()
    val currentFreelancerMLD: MutableLiveData<NewFreelancer?> = MutableLiveData()
    val otpData: MutableLiveData<OtpModel?> = MutableLiveData()
    val freelancerMLD: MutableLiveData<NewFreelancer?> = MutableLiveData()
    val favouriteFreelancerMLD: MutableLiveData<FavouriteFreelancer?> = MutableLiveData()
    val newProjectMLD: MutableLiveData<NewProject?> = MutableLiveData()
    val exceptionMutableLiveData = MutableLiveData<Throwable>()
    val invitedFreelancer = MutableLiveData<FreelancersInvitation>()
    val termsAndConditionAcceptanceLD = MutableLiveData<Boolean>()
    val currentFileMLD = MutableLiveData<MyFile>()
    val ticketMLD : MutableLiveData<TechnicalSupportRequest?> = MutableLiveData()
    val servicessMLD : MutableLiveData<ServicesModel?> = MutableLiveData()

    init {
        _instance = this
    }
    companion object {
        var _instance: MainActivityVM? = null
        fun get_Instance(): MainActivityVM{
            if (_instance == null){
                _instance = MainActivityVM()
            }
            return _instance!!
        }
    }

    fun setUser(user: NewUser?){

        userMLD.value = user
        Log.i(javaClass.simpleName, "setUser: user = $user")
        if (user != null) {
            Firebase.crashlytics.setUserId(user.userId.toString())
        } else {
            Firebase.crashlytics.setUserId("null")
        }
    }




//    val userCreated: MutableLiveData<Resource<User>> = MutableLiveData()

//    fun registerUser(user: User){
//        viewModelScope.launch {
//            userCreated.postValue(Resource.Loading())
//            val response = repository.createUser(user)
//            userCreated.postValue(handleRegisterUserResponse(response))
//        }
//    }
//
//    fun handleRegisterUserResponse(response: Response<User>): Resource<User> {
//        if (response.isSuccessful){
//            response.body()?.let { resultResponse ->
//                return Resource.Success(resultResponse)
//            }
//        }
//        return Resource.Error(response.message())
//    }


}