package iset.dsi.myapplication.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import iset.dsi.myapplication.R
import iset.dsi.myapplication.RetrofitClient
import iset.dsi.myapplication.User
import iset.dsi.myapplication.UserWithoutRole
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    private lateinit var fullnameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var mdpEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var progressBar: ProgressBar
    private var userId: Long = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Initialiser les vues
        fullnameEditText = findViewById(R.id.fullnameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        mdpEditText = findViewById(R.id.mdpEditText)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        progressBar = findViewById(R.id.progressBar)

        // Récupérer l'ID utilisateur depuis SharedPreferences
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        userId = sharedPreferences.getLong("USER_ID", -1)

        // Charger les informations actuelles de l'utilisateur pour l'édition
        if (userId != -1L) {
            fetchUserProfile(userId)
        }

        // Sauvegarder les modifications
        saveButton.setOnClickListener {
            saveProfile()
        }

        // Annuler les modifications
        cancelButton.setOnClickListener {
            finish() // Ferme l'activité sans enregistrer les changements
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun validateInput(): Boolean {
        val fullname = fullnameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = mdpEditText.text.toString()

        if (fullname.isEmpty()) {
            fullnameEditText.error = "Le nom complet est obligatoire"
            return false
        }
        if (email.isEmpty()) {
            emailEditText.error = "L'email est obligatoire"
            return false
        }
        if (password.isEmpty()) {
            mdpEditText.error = "Le mot de passe est obligatoire"
            return false
        }

        return true
    }

    private fun fetchUserProfile(userId: Long) {
        val apiService = RetrofitClient.apiService
        val call = apiService.getUserById(userId)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        // Afficher les informations actuelles pour l'édition
                        fullnameEditText.setText(user.fullname)
                        emailEditText.setText(user.email)
                        mdpEditText.setText(user.password)
                    } else {
                        Toast.makeText(this@EditProfileActivity, "Utilisateur introuvable", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@EditProfileActivity, "Erreur : ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@EditProfileActivity, "Erreur de connexion : ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun saveProfile() {
        if (!validateInput()) {
            return
        }

        showLoading()

        // Récupérer les nouvelles valeurs des champs
        val updatedFullname = fullnameEditText.text.toString()
        val updatedEmail = emailEditText.text.toString()
        val updatedPassword = mdpEditText.text.toString()

        // Créer un objet UserWithoutRole avec les nouvelles valeurs
        val updatedUser = UserWithoutRole(
            fullname = updatedFullname,
            email = updatedEmail,
            password = updatedPassword
        )

        // Envoyer la demande de mise à jour des informations de l'utilisateur
        val apiService = RetrofitClient.apiService
        val call = apiService.updateUserProfile(userId, updatedUser)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                hideLoading()
                if (response.isSuccessful) {
                    Toast.makeText(this@EditProfileActivity, "Profil mis à jour", Toast.LENGTH_LONG).show()

                    // Rafraîchir les données de l'utilisateur
                    fetchUserProfile(userId)

                    // Optionnellement, vous pouvez terminer l'activité si vous voulez revenir en arrière
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API Error", "Erreur : $errorBody")

                    val builder = AlertDialog.Builder(this@EditProfileActivity)
                    builder.setTitle("Erreur de mise à jour")
                        .setMessage(errorBody ?: "Une erreur inconnue est survenue.")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()

                    Toast.makeText(this@EditProfileActivity, "Erreur de mise à jour: $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                hideLoading()
                Toast.makeText(this@EditProfileActivity, "Erreur de connexion : ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
