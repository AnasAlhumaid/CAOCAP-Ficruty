package sa.gov.ksaa.dal.data.webservices.newDal

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class DalWebService(){ // val errorsListenor: ErrorsListenor
    companion object {
        const val LOCAL_HOST_URL = "http://207.127.103.111/api/jsonws/"
        const val PRODUCTION_URL = "https://dal.ksaa.gov.sa/api/jsonws/"
        const val TESTING_URL = "http://193.122.80.121:8080/api/jsonws/"
        private var _instance: DalWebService? = null
        fun getInstance(): DalWebService { // errorsListenor: ErrorsListenor
            if (_instance == null)
                _instance = DalWebService() // errorsListenor
            return _instance!!
        }
    }
    var retrofit: Retrofit
    var webService: DalWebInterface
//    var baseURL: String
    init {
        val gson: Gson = GsonBuilder()
            .setDateFormat("dd-MM-yyyy") // "yyyy-MM-dd'T'HH:mm:ssZ"
            .create()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

//        baseURL=  if (BuildConfig.DEBUG) LOCAL_HOST_URL else TESTING_URL

        retrofit = Retrofit.Builder()
            .baseUrl(PRODUCTION_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor{
                    val newRequest = it.request().newBuilder()
                        .addHeader("Authorization", "Basic dGVzdEBsaWZlcmF5LmNvbTp0ZXN0")
                        .addHeader("p_auth", "QrcYYhQU")
                        .build()
                    it.proceed(newRequest)
                }
//                .addInterceptor(object : Interceptor{
//                    override fun intercept(chain: Interceptor.Chain): Response {
//                        return errorsListenor.onOnIntercept(chain)
//                    }
//                })
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15,TimeUnit.SECONDS)
                .build())
            .build()
        webService = retrofit.create(DalWebInterface::class.java)
    }

//    @Throws(IOException::class)
//    private fun onOnIntercept(chain: Chain): Response {
//        try {
//            val response = chain.proceed(chain.request())
////            var contentType: MediaType?
////            val content: String
////            if (response.isSuccessful){
////                contentType = response.body!!.contentType()
////                content = response.body!!.string()
////            } else {
////                contentType = response.bo
////            }
//            return response.newBuilder().body(ResponseBody.create(response.body!!.contentType(), response.body!!.string()))
//                .build()
//        } catch (exception: SocketTimeoutException) {
//            exception.printStackTrace()
//            if (errorsListenor != null) errorsListenor.onConnectionTimeout()
//        }
//        return chain.proceed(chain.request())
//    }
}