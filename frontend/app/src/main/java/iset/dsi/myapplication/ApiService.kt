package iset.dsi.myapplication

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
data class LoginResponse2(
    val message: String,
    val userId: Long
)

data class LoginResponse(
    val success: Boolean,
    val message: String
)
data class RegisterResponse(
    val message: String
)

data class SuccessResponse(
    val message: String
)


interface ApiService {

    @GET("/auth/users/{id}")
    fun getUserById(@Path("id") id: Long): Call<User>

    @GET("/auth/logout")
    fun logout(): Call<SuccessResponse>

    @GET("/auth/users/email/{email}")
    fun getUserByEmail(@Path("email") email: String): Call<User>

    @POST("/auth/login")
    fun login(@Body userLogin: UserLogin): Call<LoginResponse2>

    @POST("/auth/register")
    fun register(@Body user: User): Call<RegisterResponse>


}


