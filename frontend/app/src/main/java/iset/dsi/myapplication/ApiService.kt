package iset.dsi.myapplication

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginResponse(
    val success: Boolean,
    val message: String
)
data class RegisterResponse(
    val message: String
)

interface ApiService {
    // Définir la méthode de connexion (login)
    @POST("auth/login")  // Remplace par ton endpoint API pour la connexion
    fun login(@Body userLogin: UserLogin): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body user: User): Call<RegisterResponse>
}
