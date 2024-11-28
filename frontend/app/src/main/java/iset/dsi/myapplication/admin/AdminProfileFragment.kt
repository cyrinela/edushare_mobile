package iset.dsi.myapplication.admin

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
import iset.dsi.myapplication.LoginActivity
import iset.dsi.myapplication.R
import iset.dsi.myapplication.RetrofitClient
import iset.dsi.myapplication.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProfileFragment : Fragment() {

    private lateinit var fullnameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_profile, container, false)

        // Initialisation des vues
        fullnameTextView = view.findViewById(R.id.adminNameTextView)
        emailTextView = view.findViewById(R.id.adminEmailTextView)
        logoutButton = view.findViewById(R.id.logoutButton)

        // Récupérer l'ID administrateur depuis SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("USER_ID", -1)

        if (userId != -1L) {
            // Charger les informations de l'utilisateur (administrateur inclus)
            fetchUserProfile(userId)
        } else {
            Toast.makeText(requireContext(), "Aucun utilisateur connecté", Toast.LENGTH_LONG).show()
        }

        // Bouton de déconnexion
        logoutButton.setOnClickListener {
            logoutUser()
        }

        return view
    }

    private fun fetchUserProfile(userId: Long) {
        // Utilisation du client Retrofit pour récupérer les données de l'utilisateur (administrateur ou non)
        val apiService = RetrofitClient.apiService
        val call = apiService.getUserById(userId)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        // Afficher les informations de l'utilisateur (administrateur inclus)
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

    private fun logoutUser() {
        // Effacer les données de session
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()  // Efface les données de session de l'utilisateur
        editor.apply()

        // Rediriger vers l'écran de connexion
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()  // Fermer l'activité pour empêcher de revenir
    }
}
