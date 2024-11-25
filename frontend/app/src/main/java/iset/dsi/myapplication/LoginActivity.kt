package iset.dsi.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import iset.dsi.myapplication.admin.AdminDashboardActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private val BASE_URL = "http://10.0.2.2:8085" //10.0.2.2:8085

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupText = findViewById(R.id.signupText)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val userLogin = UserLogin(email, password)
                loginUser(userLogin)
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }

        signupText.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(userLogin: UserLogin) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.login(userLogin)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    // Identifiants valides
                    val email = userLogin.email
                    fetchUserByEmail(email) // Récupérer les détails de l'utilisateur
                } else {
                    Toast.makeText(this@LoginActivity, "Identifiants incorrects", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erreur de connexion: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchUserByEmail(email: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.getUserByEmail(email)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        // Rediriger en fonction du rôle
                        when (user.role) {
                            "ADMIN" -> {
                                val intent = Intent(this@LoginActivity, AdminDashboardActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            "STUDENT" -> {
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                Toast.makeText(this@LoginActivity, "Rôle inconnu", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Utilisateur introuvable", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Erreur: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erreur de connexion: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
