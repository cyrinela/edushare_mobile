package iset.dsi.myapplication
import iset.dsi.myapplication.admin.EditProfileActivity
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileFragment : Fragment() {

    private val BASE_URL = "http://172.20.10.6:8085"//"http://172.20.10.6:8085" // Remplacez par votre URL backend

    private lateinit var fullnameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var editProfileButton: Button

    private var userId: Long = -1L // Variable pour stocker l'ID de l'utilisateur

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialisation des vues
        fullnameTextView = view.findViewById(R.id.fullnameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        editProfileButton = view.findViewById(R.id.editButton)

        // Récupérer l'ID utilisateur depuis SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        userId = sharedPreferences.getLong("USER_ID", -1)

        if (userId != -1L) {
            // Charger les informations utilisateur
            fetchUserProfile(userId)
        } else {
            Toast.makeText(requireContext(), "Aucun utilisateur connecté", Toast.LENGTH_LONG).show()
        }

        // Bouton pour l'édition du profil
        editProfileButton.setOnClickListener {
            editProfile()
        }

        return view
    }

    private fun fetchUserProfile(userId: Long) {
        // Configurer Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getUserById(userId)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        // Afficher les informations de l'utilisateur
                        fullnameTextView.text = user.fullname
                        emailTextView.text = user.email
                    } else {
                        Toast.makeText(requireContext(), "Utilisateur introuvable", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Erreur : ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur de connexion : ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun editProfile() {
        // Lancer l'activité d'édition du profil
        val intent = Intent(requireActivity(), EditProfileActivity::class.java)
        startActivityForResult(intent, 100) // Code de demande pour l'activité
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == android.app.Activity.RESULT_OK) {
            // Si la mise à jour a réussi, recharger le profil
            fetchUserProfile(userId)
        }
    }
}
