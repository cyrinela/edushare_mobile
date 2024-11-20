package iset.dsi.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    // URL de l'API de connexion (Change avec ton URL)
    private val BASE_URL = "http://172.20.10.6:8085"

    // Déclarations des éléments du layout
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupText: TextView  // Déclaration pour le TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialisation des éléments du layout
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupText = findViewById(R.id.signupText)  // Récupérer le TextView

        // Gérer le clic sur le bouton de connexion
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Vérification que les champs ne sont pas vides
            if (email.isNotEmpty() && password.isNotEmpty()) {
                val userLogin = UserLogin(email, password)  // Créer un objet UserLogin avec email et password
                loginUser(userLogin)
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }

        // Gérer le clic sur le TextView pour rediriger vers RegisterActivity
        signupText.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // Fonction pour effectuer la connexion de l'utilisateur
    private fun loginUser(userLogin: UserLogin) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.login(userLogin)  // Passe l'objet UserLogin avec email et password

        // Appel API asynchrone
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    // Connexion réussie
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        Toast.makeText(this@LoginActivity, "Connexion réussie: ${loginResponse.message}", Toast.LENGTH_LONG).show()
                        // Rediriger vers la page d'accueil ou le dashboard après une connexion réussie
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)  // Remplace HomeActivity par la page d'accueil
                        startActivity(intent)
                        finish()  // Ferme cette activité
                    } else {
                        Toast.makeText(this@LoginActivity, "Réponse vide du serveur", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Afficher le corps de la réponse en cas d'erreur
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@LoginActivity, "Erreur: $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Afficher le message d'erreur complet en cas d'échec de la connexion
                Toast.makeText(this@LoginActivity, "Erreur de connexion: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
