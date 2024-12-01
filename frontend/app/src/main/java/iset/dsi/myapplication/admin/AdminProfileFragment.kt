package iset.dsi.myapplication.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import iset.dsi.myapplication.R
import iset.dsi.myapplication.RetrofitClient
import iset.dsi.myapplication.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProfileFragment : Fragment() {

    private lateinit var fullnameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var editProfileButton: Button
    private lateinit var avatarImageView: ImageView  // ImageView pour l'avatar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_profile, container, false)

        // Initialisation des vues
        fullnameTextView = view.findViewById(R.id.adminNameTextView)
        emailTextView = view.findViewById(R.id.adminNameValueTextView)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        avatarImageView = view.findViewById(R.id.avatarImageView)  // Initialisation de l'ImageView

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
        editProfileButton.setOnClickListener {
            editProfile()
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
                        fullnameTextView.text = "Nom : ${user.fullname}"
                        emailTextView.text = "Email : ${user.email}"

                        // Charger l'avatar de l'utilisateur avec Glide
                        Glide.with(this@AdminProfileFragment)
                            .load(R.drawable.user12)  // Utiliser l'image dans drawable
                            .circleCrop()
                            .into(avatarImageView)

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
        val intent = Intent(requireActivity(), EditProfileActivity::class.java)
        startActivity(intent)
    }
}
