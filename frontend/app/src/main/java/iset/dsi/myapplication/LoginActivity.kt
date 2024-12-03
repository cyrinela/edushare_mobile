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

    private val BASE_URL = "http://172.20.10.6:8085" //Remplacez par votre URL backend
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
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }

        signupText.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val userLogin = UserLogin(email, password)
        val call = apiService.login(userLogin)

        call.enqueue(object : Callback<LoginResponse2> {
            override fun onResponse(call: Call<LoginResponse2>, response: Response<LoginResponse2>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        Toast.makeText(this@LoginActivity, "Connexion réussie, ID utilisateur : ${loginResponse.userId}", Toast.LENGTH_LONG).show()
                        fetchUserByEmail(email)
                    } else {
                        Toast.makeText(this@LoginActivity, "Réponse vide", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Erreur : ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse2>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erreur de connexion : ${t.localizedMessage}", Toast.LENGTH_LONG).show()
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
                        // Enregistrer l'ID utilisateur dans SharedPreferences
                        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()

                          // Vérification que user.id n'est pas null
                        val userId = user.id ?: -1L // Utilise -1 comme valeur par défaut si l'ID est null
                        editor.putLong("USER_ID", userId) // Enregistrement de l'ID utilisateur
                        editor.apply()


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
                    Toast.makeText(this@LoginActivity, "Erreur : ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erreur de connexion : ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

}
