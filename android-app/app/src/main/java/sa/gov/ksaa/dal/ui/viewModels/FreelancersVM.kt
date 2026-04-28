package sa.gov.ksaa.dal.ui.viewModels

import android.graphics.Bitmap
import android.icu.text.MessagePattern
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.UpdateFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.UpdateFreelancer
import java.io.ByteArrayOutputStream
import kotlin.time.Duration.Companion.parse


class FreelancersVM : BaseVM() {

    val freelancerLD = MutableLiveData<NewResource<List<NewFreelancer>>>()

    fun getFreelancersByUserId(queryMap: Map<String, String>): MutableLiveData<NewResource<List<NewFreelancer>>> {
        val freelancerLD = MutableLiveData<NewResource<List<NewFreelancer>>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            freelancerLD.postValue(NewResource.Loading())
            freelancerLD.postValue(newHandleResponse(repository.getFreelancersByUserId(queryMap)))
        }
        return freelancerLD
    }
    fun getFillterdFreelancers(queryMap: Map<String, String>): MutableLiveData<NewResource<List<NewFreelancer>>> {
        val freelancerLD = MutableLiveData<NewResource<List<NewFreelancer>>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            freelancerLD.postValue(NewResource.Loading())
            freelancerLD.postValue(newHandleResponse(repository.getFilterdFreelancers(queryMap)))
        }
        return freelancerLD
    }

    val freelacersLD = MutableLiveData<NewResource<List<NewFreelancer>>>()
    fun getAllFreeLancers(): MutableLiveData<NewResource<List<NewFreelancer>>> {
        viewModelScope.launch(coroutineExceptionHandler) {
            freelacersLD.postValue(NewResource.Loading())
            freelacersLD.postValue(newHandleResponse(repository.getAllFreeLancers()))
        }
        return freelacersLD
    }
    fun getAllFreeLancersFavorateForClient(queryMap: Map<String, String>): MutableLiveData<NewResource<List<NewFreelancer>>> {
        viewModelScope.launch(coroutineExceptionHandler) {
            freelacersLD.postValue(NewResource.Loading())
            freelacersLD.postValue(newHandleResponse(repository.getAllFreeLancersFavorateForClient(queryMap)))
        }
        return freelacersLD
    }


    fun create_aFreelancer(
        params: MutableMap<String, String>, previousWorkfile0: MyFile?, previousWorkfile1: MyFile?,
        educationCertificate0: MyFile?, educationCertificate1: MyFile?
    ): MutableLiveData<NewResource<NewFreelancer>> {


        val _previousWorkfile0: MultipartBody.Part?
        if (previousWorkfile0 == null) {
            _previousWorkfile0 = null
            params["previousWorkfile0"] = ""
            params["previousWorkDesc0"] = ""
        } else {
            _previousWorkfile0 = createMultiPartBodyFromFile("previousWorkfile0", previousWorkfile0)
        }

        val _previousWorkfile1: MultipartBody.Part?
        if (previousWorkfile1 == null) {
            _previousWorkfile1 = null
            params["previousWorkfile1"] = ""
            params["previousWorkDesc1"] = ""
        } else {
            _previousWorkfile1 = createMultiPartBodyFromFile("previousWorkfile1", previousWorkfile1)
        }

        val _educationCertificate0: MultipartBody.Part?
        if (educationCertificate0 == null) {
            _educationCertificate0 = null
            params["educationCertificate0"] = ""
        } else {
            _educationCertificate0 =
                createMultiPartBodyFromFile("educationCertificate0", educationCertificate0)
        }


        val _educationCertificate1: MultipartBody.Part?
        if (educationCertificate1 == null) {
            _educationCertificate1 = null
            params["educationCertificate1"] = ""
        } else {
            _educationCertificate1 =
                createMultiPartBodyFromFile("educationCertificate1", educationCertificate1)
        }


        val partMap = toPartMap(params)

        val freelancerLD = MutableLiveData<NewResource<NewFreelancer>>()

        viewModelScope.launch(coroutineExceptionHandler) {
            freelancerLD.postValue(NewResource.Loading())
            freelancerLD.postValue(
                newHandleResponse(
                    repository.create_aFreelancer(
                        partMap, _previousWorkfile0,
                        _previousWorkfile1, _educationCertificate0, _educationCertificate1
                    )
                )
            )

        }
        return freelancerLD
    }


    // userId=7, firstName=خالد, lastName=احمد, phone=966536637215, about=نبذة, email=f.test01@gmail.com,
    // username=none, skills=ترجمة, experience=خبرة, profileImage=
    fun update_aFreelancer(
        queryMap: MutableMap<String, String>,
        profileImage: MyFile?,
        byteArray: Bitmap?
    ): MutableLiveData<NewResource<UpdateFreelancer>> {


        val _profileImage: MultipartBody.Part?
        if (profileImage != null)
            _profileImage = createMultiPartBodyFromFile("profileImage", profileImage)
        else if (byteArray!= null)  {

//
//
//            fileToUpload()


             _profileImage = fileToUpload(byteArray,"profileImage")
        }else{
            val emptyFileData = ByteArray(0) // Empty byte array to represent no image data
            val emptyFilePart = MultipartBody.Part.createFormData(
                "profileImage",
                "", // Empty filename
                emptyFileData.toRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            _profileImage = emptyFilePart
        }

        val partMap = toPartMap(queryMap)
        val freelancerLD = MutableLiveData<NewResource<UpdateFreelancer>>()

        viewModelScope.launch(coroutineExceptionHandler) {
            freelancerLD.postValue(NewResource.Loading())
            freelancerLD.postValue(
                newHandleResponse(
                    repository.update_aFreelancer(
                        partMap,
                        _profileImage
                    )
                )
            ) // , _previousWorkfile0,
            // _previousWorkfile1, _educationCertificate0, _educationCertificate1
        }
        return freelancerLD
    }
    fun delete_image_aFreelancer(
    queryMap: MutableMap<String, String>,
    profileImage: MyFile?
    ): MutableLiveData<NewResource<UpdateFreelancer>> {


        val _profileImage: MultipartBody.Part?
        if (profileImage != null)
            _profileImage = createMultiPartBodyFromFile("profileImage", profileImage)
        else {

            queryMap["profileImage"] = ""
            _profileImage = null

        }

        val partMap = toPartMap(queryMap)
        val freelancerLD = MutableLiveData<NewResource<UpdateFreelancer>>()

        viewModelScope.launch(coroutineExceptionHandler) {
            freelancerLD.postValue(NewResource.Loading())
            freelancerLD.postValue(
                newHandleResponse(
                    repository.update_aFreelancer(
                        partMap,
                        _profileImage
                    )
                )
            ) // , _previousWorkfile0,
            // _previousWorkfile1, _educationCertificate0, _educationCertificate1
        }
        return freelancerLD
    }


    // fileId, fileForUpdate, fileDescription
    fun updateFreelancerExpFiles(
        queryMap: MutableMap<String, String>,
        fileForUpdate: MyFile?
    ): MutableLiveData<NewResource<UpdateFile>> {

        val profileImagePart = fileForUpdate?.let {
            createMultiPartBodyFromFile("fileForUpdate", it)
        } ?: MultipartBody.Part.createFormData(
            "fileForUpdate",
            "",
            ByteArray(0).toRequestBody("application/pdf".toMediaTypeOrNull())
        )

        val partMap = toPartMap(queryMap)

        return MutableLiveData<NewResource<UpdateFile>>().apply {
            postValue(NewResource.Loading())
            viewModelScope.launch(coroutineExceptionHandler) {
                postValue(newHandleResponse(repository.updateFreelancerExpFiles(partMap, profileImagePart)))
            }
        }
    }
    fun updateFreelancerExpFilesremove(
        queryMap: MutableMap<String, String>,
        fileForUpdate: MyFile?
    ): MutableLiveData<NewResource<UpdateFile>> {

        val _educationCertificate1: MultipartBody.Part?
        if (fileForUpdate == null) {
            _educationCertificate1 = null
            queryMap["fileForUpdate"] = ""
        } else {
            _educationCertificate1 =
                createMultiPartBodyFromFile("fileForUpdate", fileForUpdate)
        }

        val partMap = toPartMap(queryMap)

        return MutableLiveData<NewResource<UpdateFile>>().apply {
            postValue(NewResource.Loading())
            viewModelScope.launch(coroutineExceptionHandler) {
                postValue(newHandleResponse(repository.updateFreelancerExpFiles(partMap, _educationCertificate1)))
            }
        }
    }

    private fun fileToUpload(image: Bitmap, fileName: String): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val byteArray = stream.toByteArray()
        val fileBody: RequestBody = RequestBody.create(

            ".png".toMediaTypeOrNull(),
            byteArray
        )
        return createFormData("profileImage", "$fileName.png", fileBody)
    }
}