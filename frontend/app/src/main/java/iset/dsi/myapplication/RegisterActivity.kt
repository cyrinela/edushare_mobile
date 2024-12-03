package iset.dsi.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {

    // URL de base du backend
    private val BASE_URL = "http://192.168.35.34:8085"

    // Déclarations des éléments d'interface
    private lateinit var fullnameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var roleSpinner: Spinner
    private lateinit var registerButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialisation des éléments d'interface
        initUI()

        // Configuration du Spinner pour les rôles
        setupRoleSpinner()

        // Gestion du clic sur le bouton "S'inscrire"
        registerButton.setOnClickListener { handleRegisterClick() }
    }

    // Méthode pour initialiser les éléments d'interface
    private fun initUI() {
        fullnameEditText = findViewById(R.id.fullnameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        roleSpinner = findViewById(R.id.roleSpinner)
        registerButton = findViewById(R.id.registerButton)
    }

    // Méthode pour configurer le Spinner des rôles avec uniquement "STUDENT"
    private fun setupRoleSpinner() {
        val roles = arrayOf("STUDENT")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        // Désactivation du Spinner pour éviter toute interaction avec l'utilisateur
        roleSpinner.isEnabled = false

        // Définir "STUDENT" comme rôle sélectionné par défaut
        roleSpinner.setSelection(0)  // L'index 0 correspond à "STUDENT"
    }

    // Gestion du clic sur le bouton d'inscription
    private fun handleRegisterClick() {
        val fullname = fullnameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val role = roleSpinner.selectedItem.toString()

        // Validation des champs
        if (validateInput(fullname, email, password)) {
            val user = User(fullname = fullname, email = email, password = password, role = role)
            registerUser(user)
        }
    }

    // Méthode pour valider les champs de saisie
    private fun validateInput(fullname: String, email: String, password: String): Boolean {
        return when {
            fullname.isEmpty() -> {
                showToast("Le nom complet est requis.")
                false
            }
            email.isEmpty() -> {
                showToast("L'email est requis.")
                false
            }
            password.isEmpty() -> {
                showToast("Le mot de passe est requis.")
                false
            }
            else -> true
        }
    }

    // Méthode pour enregistrer un utilisateur
    private fun registerUser(user: User) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.register(user)
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null) {
                        showToast(registerResponse.message)
                        navigateToLogin()
                    } else {
                        showToast("Réponse vide du serveur.")
                    }
                } else {
                    handleError(response)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                showToast("Erreur de connexion: ${t.localizedMessage}")
            }
        })
    }

    // Méthode pour gérer les erreurs du serveur
    private fun handleError(response: Response<RegisterResponse>) {
        val errorBody = response.errorBody()?.string()
        showToast("Erreur: $errorBody")
    }

    // Méthode pour afficher un Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // Méthode pour naviguer vers l'écran de connexion
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
