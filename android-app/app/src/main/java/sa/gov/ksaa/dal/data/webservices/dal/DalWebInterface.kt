package sa.gov.ksaa.dal.data.webservices.dal

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.QueryMap
import sa.gov.ksaa.dal.data.models.Bid
import sa.gov.ksaa.dal.data.models.Certification
import sa.gov.ksaa.dal.data.models.ChatMessage
import sa.gov.ksaa.dal.data.models.ClientFavoriteFreelancer
import sa.gov.ksaa.dal.data.models.CommentOnProject
import sa.gov.ksaa.dal.data.models.CompletedProject
import sa.gov.ksaa.dal.data.models.Enquiry
import sa.gov.ksaa.dal.data.models.Freelancer
import sa.gov.ksaa.dal.data.models.Project
import sa.gov.ksaa.dal.data.models.FreelancerFavoriteProject
import sa.gov.ksaa.dal.data.models.FreelancerInvitedProject
import sa.gov.ksaa.dal.data.models.OngoingProject
import sa.gov.ksaa.dal.data.models.ProjectDeliverable
import sa.gov.ksaa.dal.data.models.Qutation
import sa.gov.ksaa.dal.data.models.Review
import sa.gov.ksaa.dal.data.webservices.dal.requests.FreelancersInvitation
import sa.gov.ksaa.dal.data.webservices.dal.responses.DeleteItemsRespose
import sa.gov.ksaa.dal.data.webservices.dal.responses.EmailAvailabilityResponse
import sa.gov.ksaa.dal.data.webservices.dal.responses.GenericResponse
import sa.gov.ksaa.dal.data.webservices.dal.responses.UnFavorite
import sa.gov.ksaa.dal.data.webservices.newDal.responses.NewUser


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
//    @POST("/user/email")
//    suspend fun isEmailAvailable(@Body user: NewUser?): Response<GenericResponse<EmailAvailabilityResponse>>
//
//    @POST("/user")
//    suspend fun createUser(@Body user: User): Response<GenericResponse<User>>
//
//    @GET("/projects")
//    suspend fun getAllProjects(): Response<GenericResponse<List<Project>>>
//
//    @GET("/projects")
//    suspend fun getAllProjects(@QueryMap options: Map<String, String>?): Response<GenericResponse<List<Project>>>
//
//    @HTTP(method = "GET", path = "/projects", hasBody = true)
//    suspend fun getAllProjects(@Body project: Project): Response<GenericResponse<List<Project>>>
//
////    @GET("/projects/search")
////    @HTTP(method = "GET", path = "/projects/search", hasBody = true)
////    suspend fun searchProjects(@Body project: Project): Response<GenericResponse<List<Project>>>
//
//    @GET("/freelancers")
//    suspend fun getAllFreeLancers(): Response<GenericResponse<List<Freelancer>>>
//
//    @GET("/freelancers")
//    suspend fun getAllFreeLancers(@QueryMap querymap: Map<String, String>?): Response<GenericResponse<List<Freelancer>>>
//
//    @GET("/users")
//    suspend fun getUsers(querymap: Map<String, String>?): Response<GenericResponse<List<User>>>
//
////    @GET("/freelancers/search")
//    @HTTP(method = "GET", path = "/freelancers/search", hasBody = true)
//    suspend fun searchFreelancers(@Body freelancer: Freelancer): Response<GenericResponse<List<Freelancer>>>
//
//    @POST("/user/login")
//    suspend fun login(@Body user: User?): Response<GenericResponse<User>>
//
//    @GET("/freelancers/{id}")
//    suspend fun get_aFreelancerById(@Path("id") id: Int): Response<GenericResponse<Freelancer>>
//
//    @POST("/enquiries")
//    suspend fun create_anEnquiry(@Body enquiry: Enquiry): Response<GenericResponse<Enquiry>>
//
//    @GET("/projects/{id}")
//    suspend fun getAprojectById(@Path("id") id: Int): Response<GenericResponse<Project>>
//
//    @GET("/projects/{id}/edit")
//    suspend fun editProjectById(@Path("id") id: Int): Response<GenericResponse<Project>>
//
//    @PUT("/projects/{id}")
//    suspend fun updateProjectById(@Path("id") id: Int, @Body project: Project): Response<GenericResponse<Project>>
//
//    @POST("/projects")
//    suspend fun addNewProject(@Body project: Project): Response<GenericResponse<Project>>
//
//    @POST("/bids")
//    suspend fun addAQEquation(@Body quotation: Qutation) : Response<GenericResponse<Qutation>>
//
//    @POST("/favorite_projects")
//    suspend fun favouriteProject(@Body freelancerFavoriteProject: FreelancerFavoriteProject): Response<GenericResponse<FreelancerFavoriteProject>>
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
//    @PUT("/user/{id}")
//    suspend fun updateUser(@Path("id") id: Int, @Body user: User): Response<GenericResponse<User>>
//
//    @GET("/chat_line")
//    suspend fun getLatestChatMessages() : Response<GenericResponse<List<ChatMessage>>>
//
//    @POST("/comments_on_projects")
//    suspend fun add_aProjectComment(@Body comment: CommentOnProject): Response<GenericResponse<CommentOnProject>>
//
//    @GET("/bids")
//    suspend fun getNewBids(@QueryMap options: Map<String, String>?): Response<GenericResponse<List<Bid>>>
//
//    @POST("/freelancers")
//    suspend fun create_aFreelancer(@Body freelancer: Freelancer): Response<GenericResponse<Freelancer>>
//
//    @DELETE("/favorite_projects")
//    suspend fun unFavouriteProject(@QueryMap options: Map<String, String>?): Response<GenericResponse<UnFavorite>>
//
//    @GET("/favorite_projects")
//    suspend fun getFavoriteProjects(@QueryMap options: Map<String, String>?): Response<GenericResponse<List<FreelancerFavoriteProject>>>
//
//    @POST("/certifications")
//    suspend fun create_aCertification(@Body certification: Certification): Response<GenericResponse<Certification>>
//
//    @GET("/favorite_freelancers")
//    suspend fun getFavoriteFreelancer(@QueryMap clientFavoriteFreelancer: Map<String, String>?): Response<GenericResponse<List<ClientFavoriteFreelancer>>>
//
//    @DELETE("/favorite_freelancers")
//    suspend fun unFavourite_aFreelancer(@QueryMap clientFavoriteFreelancer: Map<String, String>): Response<GenericResponse<UnFavorite>>
//
//    @POST("/favorite_freelancers")
//    suspend fun favouriteFreelancer(@Body freelancerFavoriteProject: ClientFavoriteFreelancer): Response<GenericResponse<ClientFavoriteFreelancer>>
//
//    @GET("/projects_invitations")
//    suspend fun getFreelancersInvitedProjects(@QueryMap params: Map<String, String>?): Response<GenericResponse<List<FreelancerInvitedProject>>>
//
//    @POST("/projects_invitations/freelancers")
//    suspend fun inviteFreelancersTo_aProject(@Body freelancersInvitation: FreelancersInvitation) : Response<GenericResponse<List<FreelancerInvitedProject>>>
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
//    @GET("/working_project")
//    suspend fun getAllOngoingProjects(@QueryMap querMap: Map<String, String>?): Response<GenericResponse<List<OngoingProject>>>
//
//    @GET("/working_project/{id}")
//    suspend fun getOngoingProjectById(@Path("id") id: Int): Response<GenericResponse<OngoingProject>>
//
//    @PUT("/working_project/{id}")
//    suspend fun update_anOngoingProject(@Path("id") id: Int?, @Body ongoingProject: OngoingProject): Response<GenericResponse<OngoingProject>>
//
//    @GET("/closed_projects")
//    suspend fun getAllCompletedProject(@QueryMap querMap: Map<String, String>): Response<GenericResponse<List<CompletedProject>>>
//
//    @POST("/project_deliverables")
//    suspend fun addProjectDeliverable(@Body projectDeliverable: ProjectDeliverable): Response<GenericResponse<ProjectDeliverable>>
//
//    @DELETE("/projects_invitations")
//    suspend fun delete_an_invitation(@QueryMap queryMap: Map<String, String>?): Response<GenericResponse<DeleteItemsRespose>>
//
//    @POST("/reviews")
//    suspend fun add_aReivew(@Body review: Review): Response<GenericResponse<Review>>
//
//    @GET("/reviews")
//    suspend fun getAllReviews(@QueryMap queryMap: Map<String, String>?): Response<GenericResponse<List<Review>>>
//
//    @GET("/closed_projects/{id}")
//    suspend fun getClosedProjectById(@Path("id") id: Int): Response<GenericResponse<CompletedProject>>
//
//    @GET("/user/{id}")
//    suspend fun getClientById(@Path("id") id: Int) : Response<GenericResponse<User>>


}