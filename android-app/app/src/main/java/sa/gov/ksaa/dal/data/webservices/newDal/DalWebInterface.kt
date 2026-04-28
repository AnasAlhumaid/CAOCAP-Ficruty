package sa.gov.ksaa.dal.data.webservices.newDal

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.QueryMap
import sa.gov.ksaa.dal.data.webservices.newDal.responses.BiddingInvitation
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ClientItem
import sa.gov.ksaa.dal.data.webservices.newDal.responses.CloseProjectRes
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ClosedProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.EmailAvailabilityResponse
import sa.gov.ksaa.dal.data.webservices.newDal.responses.FavouriteFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.Favourite_aProjectRes
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ForgotPasswordResponse
import sa.gov.ksaa.dal.data.webservices.newDal.responses.GroupedBids
import sa.gov.ksaa.dal.data.webservices.newDal.responses.MessageResponse
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser
import sa.gov.ksaa.dal.data.webservices.newDal.responses.RatingAndReview
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewBid
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewChatMessage
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewCommentOnProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewProject
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ObjectAndComplaint
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewDeliverableFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.Notifcation
import sa.gov.ksaa.dal.data.webservices.newDal.responses.OtpModel
import sa.gov.ksaa.dal.data.webservices.newDal.responses.PreviousFreelancer
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ProjectUnderway
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ReplyTo_aComment
import sa.gov.ksaa.dal.data.webservices.newDal.responses.ServicesModel
import sa.gov.ksaa.dal.data.webservices.newDal.responses.SpamCommint
import sa.gov.ksaa.dal.data.webservices.newDal.responses.TechnicalSupportMail
import sa.gov.ksaa.dal.data.webservices.newDal.responses.TechnicalSupportRequest
import sa.gov.ksaa.dal.data.webservices.newDal.responses.UpdateClient
import sa.gov.ksaa.dal.data.webservices.newDal.responses.UpdateFile
import sa.gov.ksaa.dal.data.webservices.newDal.responses.UpdateFreelancer


interface DalWebInterface {
//    @GET("users/list?sort=desc")

//    @GET("users/{user}/repos")
//    suspend fun listRepos(@Path("user") user: String?): Call<List<Repo?>?>?

//@GET("group/{id}/users")
//suspend fun groupList(@Path("id") groupId: Int): Call<MutableList<User?>?>?

//    @GET("group/{id}/users")
//    suspend fun groupList(@Path("id") groupId: Int, @Query("sort") sort: String?): Call<List<User?>?>?

//    @GET("group/{id}/users")
//    suspend fun groupList(
//        @Path("id") groupId: Int,
//        @QueryMap options: Map<String?, String?>?
//    ): Call<List<User?>?>?

//    @POST("users/new")
//    suspend fun createUser(@Body user: User?): Call<User?>?

//    @FormUrlEncoded
//    @POST("user/edit")
//    suspend fun updateUser(
//        @Field("first_name") first: String?,
//        @Field("last_name") last: String?
//    ): Call<User?>?

//    @Multipart
//    @PUT("user/photo")
//    suspend fun updateUser(
//        @Part("photo") photo: RequestBody?,
//        @Part("description") description: RequestBody?
//    ): Call<User?>?

//    @Headers("Cache-Control: max-age=640000")
//    @GET("widget/list")
//    suspend fun widgetList(): Call<List<Widget?>?>?

//    @Headers("Accept: application/vnd.github.v3.full+json", "User-Agent: Retrofit-Sample-App")
//    @GET("users/{username}")
//    suspend fun getUser(@Path("username") username: String?): Call<User?>?

//    @GET("user")
//    suspend Call<User> getUser(@Header("Authorization") String authorization)

//    @GET("user")
//    suspend Call<User> getUser(@HeaderMap Map<String, String> headers)

//**********************************************************************************************

    // Services

    @GET("languageServicePlatform.getServices")
    suspend fun getAllServices(): Response<List<ServicesModel>>


    // Favourite

    @GET("languageServicePlatform.getFavouriteProjectById")
    // freelancerId=4
    suspend fun getFavoriteProjects(@QueryMap options: Map<String, String>): Response<List<NewProject>>

    @POST("languageServicePlatform.favouriteProject")
    // freelancerId=2&isFavourite=true&projectId=18
    suspend fun favouriteProject(@QueryMap querMap: Map<String, String>): Response<Favourite_aProjectRes>

    @POST("languageServicePlatform.favouriteFreelancer")
    // clientId=6&isFavourite=true&freelancerId=2
    suspend fun favouriteFreelancer(@QueryMap querMap: Map<String, String>): Response<FavouriteFreelancer>

    @GET("languageServicePlatform.getFavouriteFreelancerById")
    // userId=5
    suspend fun getFavouriteFreelancerByUserId(@QueryMap querMap: Map<String, String>): Response<List<FavouriteFreelancer>>

    //**********************************************************************************************

    // Bids
    @GET("languageServicePlatform.getAllBiddingByFreelancerId")
    // freelancerId=3
    suspend fun getBidsFreelancerId(@QueryMap querMap: Map<String, String>): Response<List<NewBid>>

    @GET("languageServicePlatform.tenderProjectForClient")
    // user_id=5
    suspend fun getBidsByUserId(@QueryMap queryMap: Map<String, String>): Response<List<GroupedBids>>

    @GET("languageServicePlatform.tenderProjectForClient")
    // user_id=5
    suspend fun getBidsByClientUserId(@QueryMap queryMap: Map<String, String>): Response<List<NewBid>>

    @GET("languageServicePlatform.deleteBidding")
    suspend fun deleteBidForFreelancer(@QueryMap queryMap: Map<String, String>): Response<List<GroupedBids>>


    @Multipart
    @POST("languageServicePlatform.submitBid")
    // projectId= 2, freelancerId=4, expectedDuration=3, description= وصف, outputExpected=لايوجد, cost= 300,
    // attachmentDescription= وصف,
    @JvmSuppressWildcards
    suspend fun create_aBid(
        @PartMap partMap: Map<String, RequestBody>, @Part biddingFile: MultipartBody.Part?
    ): Response<NewBid> // , @Part biddingFile: MultipartBody.Part?


    @POST("languageServicePlatform.acceptFreeancerBidByClient")
    // projectId=88&bidId=31
    suspend fun acceptFreelancerBidByClient(@QueryMap queryMap: Map<String, String>): Response<Any>

    // Projects
    @Multipart
    @JvmSuppressWildcards
    @POST("languageServicePlatform.addProject")
    // user_id=31&projectTitle=test&category=Proofreading&aboutProject=test&descriptionofProject=test&
    // projectValue=500&numberOfOffers=20&expectedTime=1-07-2023&freelancerLevel=Active&durationOfOffer=22-06-2023&
    // request=private&freelancerIds=1,2,3

//    addProject?user_id=1&projectTitle=مشروع ترجمة جديد&category=التدقيق&aboutProject=نبذة&
//    descriptionofProject=وصف&projectValue=555&numberOfOffers=7&expectedTime=أسبوع 1&freelancerLevel=نشط&
//    durationOfOffer=2023-10-14&request=general&freelancerIds=

    suspend fun addNewProject(
        @PartMap partMap: Map<String, RequestBody>,
        @Part uploadfile: MultipartBody.Part?
    ): Response<NewProject>





    @POST("languageServicePlatform.editProjectDuration")
    suspend fun editDuration(@QueryMap querMap: Map<String, String>): Response<MessageResponse>

    @GET("languageServicePlatform.searchForProject")
    suspend fun getAllProjects(): Response<List<NewProject>>

    // ?userId=3
    @GET("languageServicePlatform.searchFavouritProjectByUserId")
    suspend fun getAllProjectsByRequesterUserId(@QueryMap querMap: Map<String, String>): Response<List<NewProject>>

    @GET("languageServicePlatform.deleteProject")
    suspend fun deleteProjectForClient(@QueryMap querMap: Map<String, String>): Response<List<NewProject>>
    @GET("languageServicePlatform.searchProjectByKeyword")
    suspend fun getSearchProjects(@QueryMap querMap: Map<String, String>): Response<List<NewProject>>

    @GET("languageServicePlatform.searchForProjectByUserId")
    // userId=5
    suspend fun getProjectsByUserId(@QueryMap querMap: Map<String, String>): Response<List<NewProject>>

    @GET("languageServicePlatform.getProjectsByUserId")
    // userId=1
    suspend fun getProjectsByUserID(@QueryMap querMap: Map<String, String>): Response<List<NewProject>>

    @GET("languageServicePlatform.getProjectById")
    // projectId=18
    suspend fun getProjectById(@QueryMap querMap: Map<String, String>): Response<NewProject>
//    @GET("/projects")
//    @GET("languageServicePlatform.searchForProject")
//    suspend fun getAllProjects(@QueryMap options: Map<String, String>?): Response<GenericResponse<List<Project>>>
//    suspend fun getAllProjects(@QueryMap options: Map<String, String>?): Response<List<ProjectsItem>>

    // Freelancers
    @GET("languageServicePlatform.searchForFreelancer")
    suspend fun getAllFreeLancers(): Response<List<NewFreelancer>>

    @GET("languageServicePlatform.searchFavouritFreelancerByUserId")
    suspend fun getAllFreeLancersFavorateForClient(@QueryMap querymap: Map<String, String>?): Response<List<NewFreelancer>>
    //    @GET("languageServicePlatform.searchForFreelancer")
//    suspend fun getAllFreeLancers(@QueryMap querymap: Map<String, String>?): Response<List<FreelancersItem>>
    @GET("languageServicePlatform.previousFreelancer")
    // user_id=6
    suspend fun getPreviousFreelancerByUserId(@QueryMap querymap: Map<String, String>?): Response<List<PreviousFreelancer>>

    @GET("languageServicePlatform.searchFreelancerByUserId")
    // userId=5
    suspend fun getFreelancersByUserId(@QueryMap querymap: Map<String, String>): Response<List<NewFreelancer>>

    @GET("languageServicePlatform.filterFreelancer")
    suspend fun getFillterdFreelancers(@QueryMap querymap: Map<String, String>): Response<List<NewFreelancer>>


     //   Notifcation
    @GET("languageServicePlatform.getNotificationsByUserId")
    suspend fun getNotifcation(@QueryMap querymap: Map<String, String>?): Response<List<Notifcation>>


    // ChatMessages
    @GET("languageServicePlatform.getChatForFreelancer") // userId=1
    suspend fun getChatMessagesByUserId(@QueryMap querMap: Map<String, String>): Response<List<NewChatMessage>>

    // OngoingProjects
    @GET("languageServicePlatform.projectUnderway")
    // user_type=&user_id=6
    suspend fun getOngoingProjectsByUserTypeAndUserId(@QueryMap querMap: Map<String, String>?): Response<List<ProjectUnderway>>


    // CompletedProjects
    @GET("languageServicePlatform.getCompletedProjectForClient")
    // getCompletedProjectForClient?user_type=freelancer&user_id=1
    suspend fun getCompletedProjectByUserTypeAndUserId(@QueryMap querMap: Map<String, String>): Response<List<ClosedProject>>

    // Users
    @POST("languageServicePlatform.login")// email=kha@gmail.com&password=123456
    suspend fun login(@QueryMap user: Map<String, String>): Response<NewUser>

    @GET("languageServicePlatform.getOtpResponse")// email=kha@gmail.com&password=123456
    suspend fun getOtp(@QueryMap querMap: Map<String, String>): Response<OtpModel>

    @POST("languageServicePlatform.clientRegister")
    // .clientRegister?firstName=محمد&lastName=أحمد&phone=966536637215&about=نبذة&email=kha01@gmail.com&
    // password=123456&dateOfBirth=22/11/1990&nationalId=1023456789&country=السعودية&userType=client&
    // clientType=Client_individual&gender=ذكر
    suspend fun create_aClient(@QueryMap querMap: Map<String, String>): Response<NewUser>

    @POST("languageServicePlatform.checkEmail")
    //languageServicePlatform.checkEmail?email=c.test01@gmail.com
    suspend fun isEmailAvailable(@QueryMap querMap: Map<String, String>): Response<EmailAvailabilityResponse>

    // Comments
    @POST("languageServicePlatform.sendComment")
    // userId=2&comments=اهلا&projectId=1
    suspend fun add_aProjectComment(@QueryMap querMap: Map<String, String>): Response<NewCommentOnProject>

    @POST("languageServicePlatform.replyComment")
    // userId=1&comments=مرحبا&commentId=1
    suspend fun add_aReplyToComment(@QueryMap querMap: Map<String, String>): Response<ReplyTo_aComment>

    @GET("languageServicePlatform.getClientDetailsByUserId")
    // clientId=2
    suspend fun getClientsById(@QueryMap querMap: Map<String, String>): Response<List<ClientItem>>

    @GET("languageServicePlatform.getCancalProjectForClient")
    // user_type=freelancer&user_id=6
    suspend fun getCanceledProjectsByUserTypeAndUserId(@QueryMap queryMap: Map<String, String>): Response<Any>


    @GET("languageServicePlatform.getReviewAndRatingByUserId")
    // getReviewAndRatingByUserId?userId=2
    suspend fun getReviewsAndRatingsByUserId(@QueryMap querMap: Map<String, String>): Response<List<RatingAndReview>>

    @GET("languageServicePlatform.getTechnicalSupportRequest")
    // userId=1
    suspend fun getTechSupportRequests(@QueryMap queryMap: Map<String, String>): Response<List<TechnicalSupportRequest>>


    // ObjectionsAndComplaints
    @POST("languageServicePlatform.objectionAndComplaint") // userId=6&objectionTo=2&category=التصنيف&description=test&status
    suspend fun create_anObjectionOrComplain(@QueryMap querymap: Map<String, String>): Response<ObjectAndComplaint>

    @POST("languageServicePlatform.reportComment") // userId=6&objectionTo=2&category=التصنيف&description=test&status
    suspend fun create_spamCommint(@QueryMap querymap: Map<String, String>): Response<SpamCommint>

    @POST("languageServicePlatform.getChatForFreelancer")
    // userId=1
    suspend fun getChatMessagesForFreelancer(@QueryMap queryMap: Map<String, String>): Response<List<ChatMessage>>

    // BiddingInvitations
    @GET("languageServicePlatform.privateProjectFreelancer")
    //user_id=1
    suspend fun getBiddingInvitations(@QueryMap queryMap: Map<String, String>): Response<List<BiddingInvitation>>

    @POST("languageServicePlatform.chat")
    // chat?chat=مرحبا&sendById=5&sendToId=1&projectId=55
    suspend fun create_aChatMessage(@QueryMap queryMap: Map<String, String>): Response<NewChatMessage>

    @POST("languageServicePlatform.giveRatingReviewToFreelancer")
    // projectId=49&clientId=6&freelancerId=3&rating=4&review=جيدجيد
    suspend fun rate_aFreelancer(@QueryMap queryMap: Map<String, String>): Response<RatingAndReview>

    @POST("languageServicePlatform.giveRatingReviewToClient")
    // projectId=49&clientId=5&freelancerId=2&rating=4&review=ممتاز
    suspend fun rate_aClient(@QueryMap queryMap: Map<String, String>): Response<RatingAndReview>


    @POST("languageServicePlatform.reportRating")
    // projectId=49&clientId=5&freelancerId=2&rating=4&review=ممتاز
    suspend fun reportReviews(@QueryMap queryMap: Map<String, String>): Response<RatingAndReview>
    @GET("languageServicePlatform.getListOfComment")
    suspend fun getCommentsByProjectId(): Response<List<NewCommentOnProject>>

    @GET("languageServicePlatform.getListOfCommentByProjectId")
    // projectId=18
    suspend fun getCommentsByProjectId(@QueryMap queryMap: Map<String, String>): Response<List<NewCommentOnProject>>


    @Multipart
//    @PUT("user/photo")
//    suspend fun updateUser(
//        @Part("photo") photo: RequestBody?,
//        @Part("description") description: RequestBody?
//    ): Call<User?>?
    @POST("languageServicePlatform.freelancerRegister")
//    firstName=قيس, lastName = احمد, phone=966536637215, about=عن المستقل, email=f.test03@gmail.com
    // password=123456, dateOfBirth=22/11/2000, nationalId= 0123456789, country=السعودية, userType=Freelancer
    // experience=خبرة, typeOfService=ترجمة, nameOfUnivesity= ام القرى, graduationYear=2019, enrollmentYear=2016,
    // typeOfCertificate= بكالوريوس, yearOfExperience= 2, previousWorkDesc0= , previousWorkDesc1 =
    @JvmSuppressWildcards
    suspend fun create_aFreelancer(
        @PartMap partMap: Map<String, RequestBody>,
        @Part previousWorkfile0: MultipartBody.Part?,
        @Part previousWorkfile1: MultipartBody.Part?,
        @Part educationCertificate0: MultipartBody.Part?,
        @Part educationCertificate1: MultipartBody.Part?
    ): Response<NewFreelancer>


//    @Multipart
////    @PUT("user/photo")
////    suspend fun updateUser(
////        @Part("photo") photo: RequestBody?,
////        @Part("description") description: RequestBody?
////    ): Call<User?>?
//    @POST("languageServicePlatform.freelancerRegistration")
////    firstName=قيس, lastName = احمد, phone=966536637215, about=عن المستقل, email=f.test03@gmail.com
//    // password=123456, dateOfBirth=22/11/2000, nationalId= 0123456789, country=السعودية, userType=Freelancer
//    // experience=خبرة, typeOfService=ترجمة, nameOfUnivesity= ام القرى, graduationYear=2019, enrollmentYear=2016,
//    // typeOfCertificate= بكالوريوس, yearOfExperience= 2, previousWorkDesc0= , previousWorkDesc1 =
//    suspend fun update_aFreelancer(
//        @PartMap partMap: Map<String, RequestBody>, @Part previousWorkfile0: MultipartBody.Part,
//        @Part previousWorkfile1: MultipartBody.Part, @Part educationCertificate0: MultipartBody.Part,
//        @Part educationCertificate1: MultipartBody.Part ): Response<NewUser>

    @Multipart
    @POST("languageServicePlatform.editFreelancerProfile")
    @JvmSuppressWildcards
    // userId=7, firstName=خالد, lastName=احمد, phone=966536637215, about=نبذة, email=f.test01@gmail.com,
    // username=none, skills=ترجمة, experience=خبرة, profileImage=
    suspend fun update_aFreelancer(
        @PartMap partMap: Map<String, RequestBody>,
        @Part profileImage: MultipartBody.Part?
    ): Response<UpdateFreelancer>
    // , @Part profileImage: MultipartBody.Part?

    @Multipart
    @POST("languageServicePlatform.editFreelancerFiles")
    @JvmSuppressWildcards
    suspend fun updateFreelancerExpFiles(@PartMap partMap: Map<String, RequestBody>, @Part fileForUpdate: MultipartBody.Part?): Response<UpdateFile>

    @Multipart
    @PUT("languageServicePlatform.editClientProfile")
    @JvmSuppressWildcards
    // userId=1&firstName=محمد&lastName=test&phone=966536637215&about=نبذة&email=kha@gmail.com&username=none,
    suspend fun updateClient(@PartMap partMap: Map<String, RequestBody>, @Part profileImage: MultipartBody.Part?): Response<UpdateClient> // ,

    @PUT("languageServicePlatform.closeProject")
    // projectId=79
    suspend fun close_aProject(@QueryMap querMap: Map<String, String>): Response<CloseProjectRes>


    @POST("languageServicePlatform.addProject")
    // languageServicePlatform.addProject?user_id=3&projectTitle=مشروع جديد 8&category=تدقيق لغوي&
    // aboutProject=مشروع جديد 8&descriptionofProject=مشروع جديد 8&projectValue=484&numberOfOffers=3&
    // expectedTime=21-10-2023&freelancerLevel=متمرس&durationOfOffer=4&request=private&freelancerIds=4
    suspend fun create_aPrivateProject(@QueryMap querMap: Map<String, String>): Response<NewProject>

    @POST("languageServicePlatform.addProject")
//     languageServicePlatform.addProject?user_id=3&projectTitle=مشروع جديد 8&category=تدقيق لغوي&
//     aboutProject=مشروع جديد 8&descriptionofProject=مشروع جديد 8&projectValue=484&numberOfOffers=3&
//     expectedTime=21-10-2023&freelancerLevel=متمرس&durationOfOffer=4&request=private&freelancerIds=4
    suspend fun create_aProject(@QueryMap querMap: Map<String, String>): Response<NewProject>


//    @GET("/projects/{id}")
//    suspend fun getAprojectById(@Path("id") id: Int): Response<GenericResponse<Project>>
//
//    @PUT("/projects/{id}")
//    suspend fun updateProjectById(@Path("id") id: Int, @Body project: Project): Response<GenericResponse<Project>>
//
//    @POST("/bids")
//    suspend fun addAQEquation(@Body quotation: Qutation) : Response<GenericResponse<Qutation>>
//
//    @POST("/user/reset_pwd")
//    suspend fun resetPassword(@Body user: User): Response<GenericResponse<User>>
//
//    @POST("/user/is_valid_pwd_reset_code")
//    suspend fun isResetCodeValid(@Body user: User): Response<GenericResponse<Boolean>>
//
//    @POST("/user/request_reset_pwd")
//    suspend fun requestPasswordReset(@Body user: User): Response<GenericResponse<User>>
//
//    @POST("/certifications")
//    suspend fun create_aCertification(@Body certification: Certification): Response<GenericResponse<Certification>>
//
//    @GET("/bids/{id}")
//    suspend fun getBidById(@Path("id") id: Int) : Response<GenericResponse<Bid>>
//
//    @PUT("/bids/{id}")
//    suspend fun update_aBid(@Path("id") id: Int,@Body bid: Bid): Response<GenericResponse<Bid>>
//
//    @GET("/bids")
//    suspend fun getBids(@QueryMap querMap: Map<String, String>): Response<GenericResponse<List<Bid>>>
//
//
//    @GET("/working_project/{id}")
//    suspend fun getOngoingProjectById(@Path("id") id: Int): Response<GenericResponse<OngoingProject>>
//
//    @PUT("/working_project/{id}")
//    suspend fun update_anOngoingProject(@Path("id") id: Int?, @Body ongoingProject: OngoingProject): Response<GenericResponse<OngoingProject>>
//
//    @DELETE("/projects_invitations")
//    suspend fun delete_an_invitation(@QueryMap queryMap: Map<String, String>?): Response<GenericResponse<DeleteItemsRespose>>
//
//    @GET("/reviews")
//    suspend fun getAllReviews(@QueryMap queryMap: Map<String, String>?): Response<GenericResponse<List<Review>>>
//
//    @GET("/closed_projects/{id}")
//    suspend fun getClosedProjectById(@Path("id") id: Int): Response<GenericResponse<CompletedProject>>

    @Multipart
    @JvmSuppressWildcards
    @POST("languageServicePlatform.addInitialFile")
    // userId, projectId, uploadfile, attachmentDesc
    suspend fun addProjectDeliverable(
        @PartMap partMap: Map<String, RequestBody>,
        @Part uploadfile: MultipartBody.Part?
    ): Response<NewDeliverableFile>


    @GET("languageServicePlatform.getInitialFilesByProjectId")
    // projectId
    suspend fun getProjectDeliverables(@QueryMap querMap: Map<String, String>): Response<List<NewDeliverableFile>>

    @Multipart
    @JvmSuppressWildcards
    @POST("languageServicePlatform.editFreelancerFiles")
    // projectId
    suspend fun deleteDraivable(
        @PartMap partMap: Map<String, RequestBody>,
        @Part uploadfile: MultipartBody.Part?
    ): Response<NewDeliverableFile>

    // userType=Freelancer
    @GET("languageServicePlatform.getAllMailsByUserType")
    suspend fun getAllMailsByUserType(@QueryMap querMap: Map<String, String>): Response<List<TechnicalSupportMail>>

    // bidId=16
    @POST("languageServicePlatform.rejectFreeancerBidByClient")
    suspend fun reject_aBid(@QueryMap querMap: Map<String, String>): Response<Any>


    // email=freelancer@gmail.com
    @POST("languageServicePlatform.forgotPassword")
    suspend fun forgotPassword(@QueryMap querMap: Map<String, String>): Response<ForgotPasswordResponse>



}