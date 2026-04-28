package sa.gov.ksaa.dal.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import sa.gov.ksaa.dal.data.models.Project
import sa.gov.ksaa.dal.data.webservices.Resource
import sa.gov.ksaa.dal.data.webservices.newDal.MyFile
import sa.gov.ksaa.dal.data.webservices.newDal.NewResource
import sa.gov.ksaa.dal.data.webservices.newDal.responses.MessageResponse
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewDeliverableFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject

class ProjectsVM : BaseVM(){

//    val projectsLD = MutableLiveData<Resource<List<Project>>>()
    val projectsLD = MutableLiveData<NewResource<List<NewProject>>>()

    fun getAllProjects(): MutableLiveData<NewResource<List<NewProject>>>{

        viewModelScope.launch(coroutineExceptionHandler) {
            projectsLD.postValue(NewResource.Loading())
            projectsLD.postValue(
                newHandleResponse(
                    repository.getAllProjects()
                ))
        }

        return projectsLD
    }

    fun getAllProjectsByRequesterUserId(queryMap: Map<String, String>): MutableLiveData<NewResource<List<NewProject>>>{
        viewModelScope.launch(coroutineExceptionHandler) {


            projectsLD.postValue(NewResource.Loading())
            projectsLD.postValue(newHandleResponse(repository.getAllProjectsByRequesterUserId(queryMap)))
        }
        return projectsLD
    }

    fun deleteProjectForClient(queryMap: Map<String, String>): MutableLiveData<NewResource<List<NewProject>>>{
        viewModelScope.launch(coroutineExceptionHandler) {


            projectsLD.postValue(NewResource.Loading())
            projectsLD.postValue(newHandleResponse(repository.deleteProjectForClient(queryMap)))
        }
        return projectsLD
    }
    fun getSearchProject(queryMap: Map<String, String>): MutableLiveData<NewResource<List<NewProject>>>{
        viewModelScope.launch(coroutineExceptionHandler) {


            projectsLD.postValue(NewResource.Loading())
            projectsLD.postValue(newHandleResponse(repository.getSearchProjects(queryMap)))
        }
        return projectsLD
    }

    fun getProjectsByUserId(userId: Int): MutableLiveData<NewResource<List<sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject>>>{
        viewModelScope.launch(coroutineExceptionHandler) {
            projectsLD.postValue(NewResource.Loading())
            projectsLD.postValue(newHandleResponse(repository.getProjectsByUserId(mapOf("userId" to userId.toString()))))
        }
        return projectsLD
    }

    fun getProjectsByUserID(userId: Int): MutableLiveData<NewResource<List<sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject>>>{
        viewModelScope.launch(coroutineExceptionHandler) {
            projectsLD.postValue(NewResource.Loading())
            projectsLD.postValue(newHandleResponse(repository.getProjectsByUserID(mapOf("userId" to userId.toString()))))
        }
        return projectsLD
    }

    fun getProjectById(projectId: Int): MutableLiveData<NewResource<List<sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject>>>{
        val newProjectLD = MutableLiveData<NewResource<sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            newProjectLD.postValue(NewResource.Loading())
            newProjectLD.postValue(newHandleResponse(repository.getProjectById(mapOf("projectId" to projectId.toString()))))
        }
        return projectsLD
    }




    val projectLD = MutableLiveData<Resource<Project>>()

    fun editProjectById(id: Int): MutableLiveData<Resource<Project>>{
        viewModelScope.launch (coroutineExceptionHandler){
            projectLD.postValue(Resource.Loading())
//            projectLD.postValue(handleResponse(repository.editProjectById(id)))
        }
        return projectLD
    }

//    fun updateProjectById(project: Project): MutableLiveData<Resource<Project>>{
//        viewModelScope.launch(coroutineExceptionHandler) {
//            projectLD.postValue(Resource.Loading())
//            projectLD.postValue(handleResponse(repository.updateProjectById(project)))
//        }
//        return projectLD
//    }

    fun addNewProject(queryMap: MutableMap<String, String>, uploadfile: MyFile?) : MutableLiveData<NewResource<NewProject>>{

        val _uploadfile: MultipartBody.Part?
        if(uploadfile == null){
            queryMap["addFile"] = ""
            _uploadfile = null
        }
        else {
            _uploadfile = createMultiPartBodyFromFile("addFile", uploadfile)
        }
        val partMap = toPartMap(queryMap)
        val projectLD = MutableLiveData<NewResource<NewProject>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            projectLD.postValue(NewResource.Loading())
            projectLD.postValue(newHandleResponse(repository.addNewProject(partMap, _uploadfile)))
        }
        return projectLD
    }



    fun getAprojectById(queryMap: Map<String, String>): MutableLiveData<NewResource<sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject>>{
        val projectsLD = MutableLiveData<NewResource<sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject>>()
        viewModelScope.launch (coroutineExceptionHandler) {
            projectsLD.postValue(NewResource.Loading())
            projectsLD.postValue(newHandleResponse(repository.getProjectById(queryMap)))
        }
        return projectsLD
    }


    fun editeDuration(project: Map<String, String>): MutableLiveData<NewResource<MessageResponse>>{
        val projectLD = MutableLiveData<NewResource<MessageResponse>>()
        viewModelScope.launch(coroutineExceptionHandler) {
            projectLD.postValue(NewResource.Loading())
            projectLD.postValue(newHandleResponse(repository.editDuration(project)))
        }
        return projectLD
    }
}