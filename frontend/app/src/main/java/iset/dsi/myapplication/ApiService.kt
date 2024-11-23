package iset.dsi.myapplication

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class LoginResponse(
    val success: Boolean,
    val message: String
)
data class RegisterResponse(
    val message: String
)

interface ApiService {
    @GET("/auth/users/email/{email}")
    fun getUserByEmail(@Path("email") email: String): Call<User>

    // Définir la méthode de connexion (login)
    @POST("auth/login")  // Remplace par ton endpoint API pour la connexion
    fun login(@Body userLogin: UserLogin): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body user: User): Call<RegisterResponse>
}
