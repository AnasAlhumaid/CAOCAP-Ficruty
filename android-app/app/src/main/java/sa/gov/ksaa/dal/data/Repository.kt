package sa.gov.ksaa.dal.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import sa.gov.ksaa.dal.data.webservices.newDal.DalWebService
import sa.gov.ksaa.dal.data.webservices.newDal.responses.Favourite_aProjectRes

class Repository {
    val dalWebService = DalWebService.getInstance()
    companion object {
        private var _instance: Repository? = null
        fun getIstance(): Repository {
            if (_instance == null){
                _instance = Repository()
            }
            return _instance!!
        }
    }

    //**********************************************************************************************
    // Auth
    suspend fun isEmailAvailable(querMap: Map<String, String>) = dalWebService.webService.isEmailAvailable(querMap)
    suspend fun login(user: Map<String, String>) = dalWebService.webService.login(user)
    suspend fun getOtp(otpUser: Map<String, String>) = dalWebService.webService.getOtp(otpUser)
    suspend fun forgotPassword(querMap: Map<String, String>) = dalWebService.webService.forgotPassword(querMap)
    //    suspend fun resetPassword(user: User) = dalWebService.webService.resetPassword(user)
//    suspend fun isResetCodeValid(user: User) = dalWebService.webService.isResetCodeValid(user)
//    suspend fun requestPasswordReset(user: User) = dalWebService.webService.requestPasswordReset(user)

    //**********************************************************************************************
    // Clients
    suspend fun create_aClient(querMap: Map<String, String>) = dalWebService.webService.create_aClient(querMap)
    suspend fun getClientsById(querMap: Map<String, String>) = dalWebService.webService.getClientsById(querMap) // clientId=2
    suspend fun updateClient(partMap: Map<String, RequestBody>, profileImage: MultipartBody.Part?) = dalWebService.webService.updateClient(partMap, profileImage)
    suspend fun updateFreelancerExpFiles(partMap: Map<String, RequestBody>, fileForUpdate: MultipartBody.Part?) =
        dalWebService.webService.updateFreelancerExpFiles(partMap, fileForUpdate)
    //**********************************************************************************************
    // Freelancers

    suspend fun getAllFreeLancersFavorateForClient(id: Map<String, String>) = dalWebService.webService.getAllFreeLancersFavorateForClient(id)
    suspend fun getAllFreeLancers() = dalWebService.webService.getAllFreeLancers()
    suspend fun getFreelancersByUserId(id: Map<String, String>) = dalWebService.webService.getFreelancersByUserId(id)
    suspend fun getFilterdFreelancers(id: Map<String, String>) = dalWebService.webService.getFillterdFreelancers(id)
    suspend fun create_aFreelancer(partMap: Map<String, RequestBody>, previousWorkfile0: MultipartBody.Part?,
                                   previousWorkfile1: MultipartBody.Part?, educationCertificate0: MultipartBody.Part?,
                                   educationCertificate1: MultipartBody.Part?) =
        dalWebService.webService.create_aFreelancer(
        partMap, previousWorkfile0, previousWorkfile1, educationCertificate0, educationCertificate1)


    // userId=7, firstName=خالد, lastName=احمد, phone=966536637215, about=نبذة, email=f.test01@gmail.com,
    // username=none, skills=ترجمة, experience=خبرة, profileImage=
    suspend fun update_aFreelancer(partMap: Map<String, RequestBody>,
                                   profileImage: MultipartBody.Part?) = dalWebService.webService.update_aFreelancer(partMap, profileImage)
    // userId=7, firstName=خالد, lastName=احمد, phone=966536637215, about=نبذة, email=f.test01@gmail.com,
    // username=none, skills=ترجمة, experience=خبرة

    //**********************************************************************************************
    // Freelancer Experience Files

    //**********************************************************************************************
    //Services
    suspend fun getAllServices() = dalWebService.webService.getAllServices()

    //**********************************************************************************************
    // Certificatios
    //    suspend fun create_aCertification(certification: Certification) = dalWebService.webService.create_aCertification(certification)

    //**********************************************************************************************
    // FavouriteFreelancers
    suspend fun getFavoriteFreelancer(querMap: Map<String, String>) = dalWebService.webService.getFavouriteFreelancerByUserId(querMap)
    suspend fun favouriteFreelancer(querMap: Map<String, String>) = dalWebService.webService.favouriteFreelancer(querMap)

    //**********************************************************************************************
    // Projects
    suspend fun getAllProjects() = dalWebService.webService.getAllProjects()
    suspend fun getAllProjectsByRequesterUserId(querMap: Map<String, String>) = dalWebService.webService.getAllProjectsByRequesterUserId(querMap)
    suspend fun getSearchProjects(querMap: Map<String, String>) = dalWebService.webService.getSearchProjects(querMap)
    suspend fun deleteProjectForClient(querMap: Map<String, String>) = dalWebService.webService.deleteProjectForClient(querMap)
    suspend fun getProjectsByUserId(querMap: Map<String, String>) = dalWebService.webService.getProjectsByUserId(querMap)
    suspend fun getProjectsByUserID(querMap: Map<String, String>) = dalWebService.webService.getProjectsByUserID(querMap)
    suspend fun getProjectById(querMap: Map<String, String>) = dalWebService.webService.getProjectById(querMap) // projectId=18
    suspend fun addNewProject(partMap: Map<String, RequestBody>, uploadfile: MultipartBody.Part?)= dalWebService.webService.addNewProject(partMap,uploadfile)


    suspend fun editDuration(querMap: Map<String, String>) = dalWebService.webService.editDuration(querMap)

    // languageServicePlatform.addProject?user_id=3&projectTitle=مشروع جديد 8&category=تدقيق لغوي&
    // aboutProject=مشروع جديد 8&descriptionofProject=مشروع جديد 8&projectValue=484&numberOfOffers=3&
    // expectedTime=21-10-2023&freelancerLevel=متمرس&durationOfOffer=4&request=private&freelancerIds=4
    suspend fun create_aPrivateProject(querMap: Map<String, String>) = dalWebService.webService.create_aPrivateProject(querMap)

    //     languageServicePlatform.addProject?user_id=3&projectTitle=مشروع جديد 8&category=تدقيق لغوي&
//     aboutProject=مشروع جديد 8&descriptionofProject=مشروع جديد 8&projectValue=484&numberOfOffers=3&
//     expectedTime=21-10-2023&freelancerLevel=متمرس&durationOfOffer=4&request=private&freelancerIds=4
    suspend fun create_aProject(querMap: Map<String, String>) = dalWebService.webService.create_aProject(querMap)

    //    suspend fun editProjectById(id: Int) = dalWebService.webService.editProjectById(id)
//    suspend fun updateProjectById(project: Project) = dalWebService.webService.updateProjectById(project.project_id!!, project)
    //    suspend fun searchProjects(project: Project) = dalWebService.webService.searchProjects(project)

    //**********************************************************************************************
    // FavouriteProjects
    suspend fun favouriteProject(querMap: Map<String, String>) = dalWebService.webService.favouriteProject(querMap)

    suspend fun unFavouriteProject(queryParams: MutableMap<String, String>): Response<Favourite_aProjectRes> {
        queryParams["isFavourite"] = "false"
        return dalWebService.webService.favouriteProject(queryParams)
    }

    suspend fun getFavoriteProjects(freelancerFavoriteProject: Map<String, String>) = dalWebService.webService.getFavoriteProjects(freelancerFavoriteProject)

    //**********************************************************************************************
    // CommentsOnProject
    suspend fun add_aProjectComment(querMap: Map<String, String>) = dalWebService.webService.add_aProjectComment(querMap)
    suspend fun add_aReplyToComment(querMap: Map<String, String>) = dalWebService.webService.add_aReplyToComment(querMap)
    suspend fun getCommentsByProjectId(querMap: Map<String, String>) = dalWebService.webService.getCommentsByProjectId(querMap) // projectId=18



    //**********************************************************************************************
    // Bids
    suspend fun getBidsByFreelancerId(querMap: Map<String, String>) = dalWebService.webService.getBidsFreelancerId(querMap) // freelancerId=3
    suspend fun getBidsByUserId(querMap: Map<String, String>) = dalWebService.webService.getBidsByUserId(querMap)
    suspend fun getBidsByCleintUserId(querMap: Map<String, String>) = dalWebService.webService.getBidsByClientUserId(querMap)// user_id=5
    suspend fun deleteBidForFreelancer(querMap: Map<String, String>) = dalWebService.webService.deleteBidForFreelancer(querMap) // user_id=5


    suspend fun create_aBid(
        partMap: Map<String, RequestBody>,
        _biddingFile: MultipartBody.Part?
    )= dalWebService.webService.create_aBid(partMap, _biddingFile) // , biddingFile: MultipartBody.Part?
    suspend fun accepting_aBid(queryMap: Map<String, String>) = dalWebService.webService.acceptFreelancerBidByClient(queryMap)

    suspend fun reject_aBid(queryMap: Map<String, String>) = dalWebService.webService.reject_aBid(queryMap)
    //    suspend fun getBidById(id: Int) = dalWebService.webService.getBidById(id)
//    suspend fun update_aBid(bid: Bid) = dalWebService.webService.update_aBid(bid.id!!, bid)

    //**********************************************************************************************
    // OngoingProjects
    suspend fun getOngoingProjectsByUserTypeAndUserId(querMap: Map<String, String>?) = dalWebService.webService.getOngoingProjectsByUserTypeAndUserId(querMap)// : Response<List<OngoingProject>>
    suspend fun close_aProject(querMap: Map<String, String>) = dalWebService.webService.close_aProject(querMap) // projectId=79
    //    suspend fun getOngoingProjectById(id: Int)= dalWebService.webService.getOngoingProjectById(id)// : Response<GenericResponse<OngoingProject>>
//    suspend fun update_anOngoingProject(ongoingProject: OngoingProject) = dalWebService.webService.update_anOngoingProject(ongoingProject.id, ongoingProject) // : Response<GenericResponse<OngoingProject>>


    //**********************************************************************************************
    // ProjectDeliverables
    // userId, projectId, uploadfile, attachmentDesc
    suspend fun addProjectDeliverable(partMap: Map<String, RequestBody>, uploadfile: MultipartBody.Part?) =
            dalWebService.webService.addProjectDeliverable(partMap, uploadfile )//: Response<MessageResponse>

    // projectId
    suspend fun getProjectDeliverables(querMap: Map<String, String>) =
    dalWebService.webService.getProjectDeliverables(querMap) // : Response<Any>


    // file id

    suspend fun deleteDraivable(partMap: Map<String, RequestBody>, uploadfile: MultipartBody.Part?) =
        dalWebService.webService.deleteDraivable(partMap, uploadfile )
    //**********************************************************************************************
    // ClosedProjects
    suspend fun getCompletedProjectByUserTypeAndUserId(querMap: Map<String, String>) = dalWebService.webService.getCompletedProjectByUserTypeAndUserId(querMap)// : Response<GenericResponse<List<CompletedProject>>>
    //    suspend fun getClosedProjectById(id: Int) = dalWebService.webService.getClosedProjectById(id) // Response<GenericResponse<CompletedProject>>

    //**********************************************************************************************
    // ProjectsReviewsRating
    //    suspend fun add_aReivew(review: Review) = dalWebService.webService.add_aReivew(review) // : Response<GenericResponse<Review>>
    suspend fun getAllReviewsByUserId(queryMap: Map<String, String>) = dalWebService.webService.getReviewsAndRatingsByUserId(queryMap) // userId=2
    suspend fun rate_aFreelancer(queryMap: Map<String, String>) = dalWebService.webService.rate_aFreelancer(queryMap) // projectId=49&clientId=6&freelancerId=3&rating=4&review=جيدجيد
    suspend fun rate_aClient(queryMap: Map<String, String>) = dalWebService.webService.rate_aClient(queryMap) // projectId=49&clientId=5&freelancerId=2&rating=4&review=ممتاز

    suspend fun reportReviews(queryMap: Map<String, String>) = dalWebService.webService.reportReviews(queryMap)

    //**********************************************************************************************
    // CancelledProjects
    suspend fun getCanceledProjectsByUserTypeAndUserId(queryMap: Map<String, String>) = dalWebService.webService.getCanceledProjectsByUserTypeAndUserId(queryMap)// user_type=freelancer&user_id=6



    //**********************************************************************************************
    // ObjectionOrComplains
    suspend fun create_anObjectionOrComplain(querMap: Map<String, String>) = dalWebService.webService.create_anObjectionOrComplain(querMap)
    suspend fun create_spamCommint(querMap: Map<String, String>) = dalWebService.webService.create_spamCommint(querMap)

    //**********************************************************************************************
    // ChatMessages
    suspend fun getChatMessagesByUserId(querMap: Map<String, String>) = dalWebService.webService.getChatMessagesByUserId(querMap) // userId=1
    suspend fun getChatMessagesForFreelancer(querMap: Map<String, String>) = dalWebService.webService.getChatMessagesForFreelancer(querMap) // userId=1
    suspend fun create_aChatMessage(querMap: Map<String, String>) = dalWebService.webService.create_aChatMessage(querMap) // chat=مرحبا&sendById=5&sendToId=1&projectId=55

//**********************************************************************************************
    // Invitations
suspend fun getPreviousFreelancerByUserId(querMap: Map<String, String>) = dalWebService.webService.getPreviousFreelancerByUserId(querMap) // user_id=6
suspend fun getBiddingInvitations(querMap: Map<String, String>) = dalWebService.webService.getBiddingInvitations(querMap) //user_id=1

    //    suspend fun getFreelancersInvitedProjects(params: Map<String, String>?) = dalWebService.webService.getFreelancersInvitedProjects(params)
//    suspend fun inviteFreelancersTo_aProject(freelancersInvitation: FreelancersInvitation) = dalWebService.webService.inviteFreelancersTo_aProject(freelancersInvitation)
    //    suspend fun declineAnInvitation(queryMap: Map<String, String>?) = dalWebService.webService.delete_an_invitation(queryMap) // : Response<GenericResponse<DeleteItemsRespose>>


    //**********************************************************************************************
    // TechSupport
    suspend fun getTechSupportRequests(querMap: Map<String, String>) = dalWebService.webService.getTechSupportRequests(querMap) // userId=1
    suspend fun getAllMailsByUserType(querMap: Map<String, String>) = dalWebService.webService.getAllMailsByUserType(querMap) // userType=Freelancer

    //*********************************************************************************************************
//    Notifcation

    suspend fun getNotifcation(querymap: Map<String, String>) = dalWebService.webService.getNotifcation(querymap)


}