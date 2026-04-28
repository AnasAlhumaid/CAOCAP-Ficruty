package sa.gov.ksaa.dal.ui.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ForgotPasswordResponse
import sa.gov.ksaa.dal.data.webservices.newDal.responses.MessageResponse
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.OtpModel
import sa.gov.ksaa.dal.data.webservices.newDal.responses.UpdateClient
import sa.gov.ksaa.dal.data.webservices.newDal.responses.UpdateFreelancer
import java.io.ByteArrayOutputStream

class UserVM: BaseVM(){


    fun isEmailAvailable(email: String) : MutableLiveData<NewResource<sa.gov.ksaa.dal.data.webservices.newDal.responses.EmailAvailabilityResponse>>{
        val isEmailAvailableLD = MutableLiveData<NewResource<sa.gov.ksaa.dal.data.webservices.newDal.responses.EmailAvailabilityResponse>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            isEmailAvailableLD.postValue(NewResource.Loading())
            isEmailAvailableLD.postValue(newHandleResponse(repository.isEmailAvailable(mapOf("email" to email))))
        }
        return isEmailAvailableLD
    }


    fun login(user: Map<String, String>) : MutableLiveData<NewResource<NewUser>>{
        val logedInUserLD = MutableLiveData<NewResource<NewUser>>()
        viewModelScope.launch(coroutineExceptionHandler){
            logedInUserLD.postValue(NewResource.Loading())
            logedInUserLD.postValue(newHandleResponse(repository.login(user)))
        }
        return logedInUserLD
    }
    fun getOtp(querMap: Map<String, String>) : MutableLiveData<NewResource<OtpModel>>{
        val otpUserLD = MutableLiveData<NewResource<OtpModel>>()
        viewModelScope.launch(coroutineExceptionHandler){

            otpUserLD.postValue(newHandleResponse(repository.getOtp(querMap)))
        }
        return otpUserLD
    }


    fun requestPasswordReset(querMap: Map<String, String>) : MutableLiveData<NewResource<ForgotPasswordResponse>>{
        val requestPasswordResetLD: MutableLiveData<NewResource<ForgotPasswordResponse>> = MutableLiveData()
        viewModelScope.launch(coroutineExceptionHandler) {
            requestPasswordResetLD.postValue(NewResource.Loading())
            requestPasswordResetLD.postValue(newHandleResponse(repository.forgotPassword(querMap)))
        }
        return requestPasswordResetLD
    }


//    fun isResetCodeValid(user: User) : MutableLiveData<Resource<Boolean>>{
//        val isResetCodeValidLD: MutableLiveData<Resource<Boolean>> = MutableLiveData()
//        viewModelScope.launch(coroutineExceptionHandler) {
//            isResetCodeValidLD.postValue(Resource.Loading())
////            isResetCodeValidLD.postValue(handleResponse(repository.isResetCodeValid(user)))
//        }
//        return isResetCodeValidLD
//    }
//
//    fun resetPassword(user: User) : MutableLiveData<Resource<User>>{
//        val mutableLiveData = MutableLiveData<Resource<User>>()
//        viewModelScope.launch(coroutineExceptionHandler) {
//            mutableLiveData.postValue(Resource.Loading())
////            mutableLiveData.postValue(handleResponse(repository.resetPassword(user)))
//        }
//        return mutableLiveData
//    }

    // userId=1&firstName=محمد&lastName=test&phone=966536637215&about=نبذة&email=kha@gmail.com&username=none, profileImage
    fun delete_image_aClaein(
        queryMap: MutableMap<String, String>,
        profileImage: MyFile?
    ): MutableLiveData<NewResource<UpdateClient>> {


        val _profileImage: MultipartBody.Part?
        if (profileImage != null)
            _profileImage = createMultiPartBodyFromFile("profileImage", profileImage)
        else {

            queryMap["profileImage"] = ""
            _profileImage = null

        }

        val partMap = toPartMap(queryMap)
        val freelancerLD = MutableLiveData<NewResource<UpdateClient>>()

        viewModelScope.launch(coroutineExceptionHandler) {
            freelancerLD.postValue(NewResource.Loading())
            freelancerLD.postValue(
                newHandleResponse(
                    repository.updateClient(
                        partMap,
                        _profileImage
                    )
                )
            ) // , _previousWorkfile0,
            // _previousWorkfile1, _educationCertificate0, _educationCertificate1
        }
        return freelancerLD
    }


    fun updateClient(user: MutableMap<String, String>, profileImage: MyFile?,ImageBitMap:Bitmap?): MutableLiveData<NewResource<UpdateClient>> {
          val _profileImage: MultipartBody.Part?
        if (profileImage != null)
            _profileImage = createMultiPartBodyFromFile("profileImage", profileImage)
        else if (ImageBitMap!= null)  {

//
//
//            fileToUpload()


            _profileImage = fileToUpload(ImageBitMap,"profileImage")
        }else{
            val emptyFileData = ByteArray(0) // Empty byte array to represent no image data
            val emptyFilePart = MultipartBody.Part.createFormData(
                "profileImage",
                "", // Empty filename
                emptyFileData.toRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            _profileImage = emptyFilePart
        }
        val partMap = toPartMap(user)
        val updateUserLD = MutableLiveData<NewResource<UpdateClient>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            updateUserLD.postValue(NewResource.Loading())
            updateUserLD.postValue(newHandleResponse(repository.updateClient(partMap, _profileImage)))
        }
        return updateUserLD
    }

    fun create_aClient(querMap: Map<String, String>): MutableLiveData<NewResource<NewUser>>{
        val responseLD = MutableLiveData<NewResource<NewUser>>()
        viewModelScope.launch (coroutineExceptionHandler){
            responseLD.postValue(NewResource.Loading())
            responseLD.postValue(newHandleResponse(repository.create_aClient(querMap)))
        }
        return responseLD
    }

    private fun fileToUpload(image: Bitmap, fileName: String): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val byteArray = stream.toByteArray()
        val fileBody: RequestBody = RequestBody.create(

            ".png".toMediaTypeOrNull(),
            byteArray
        )
        return MultipartBody.Part.createFormData("profileImage", "$fileName.png", fileBody)
    }

}