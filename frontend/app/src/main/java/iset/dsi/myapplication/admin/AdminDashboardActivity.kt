package iset.dsi.myapplication.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import iset.dsi.myapplication.LoginActivity
import iset.dsi.myapplication.R
import iset.dsi.myapplication.RetrofitClient
import iset.dsi.myapplication.SuccessResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Écouteur pour les éléments de navigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null

            when (item.itemId) {
                R.id.nav_home_admin -> selectedFragment = AdminHomeFragment()
                R.id.nav_resources_admin -> selectedFragment = AdminCategoriesFragment()
                R.id.nav_profile_admin -> selectedFragment = AdminProfileFragment()
                R.id.nav_ressources_admin -> selectedFragment = AdminRessourcesFragment()

                // Cas pour logout
                R.id.nav_logout_admin -> {
                    logout()
                }
            }

            // Remplacer le fragment actuel par le sélectionné
            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }

            true
        }

        // Par défaut, charger le fragment d'accueil
        bottomNavigationView.selectedItemId = R.id.nav_home_admin
    }

    // Fonction de déconnexion
    private fun logout() {
        // Utiliser RetrofitClient pour obtenir l'instance Retrofit et créer le service API
        val apiService = RetrofitClient.apiService

        // Appel de l'API logout
        val call = apiService.logout()

        // Enqueue pour exécuter l'appel de manière asynchrone
        call.enqueue(object : Callback<SuccessResponse> {
            override fun onResponse(call: Call<SuccessResponse>, response: Response<SuccessResponse>) {
                if (response.isSuccessful) {
                    // Si la réponse est réussie, supprimer l'ID utilisateur dans SharedPreferences
                    val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("USER_ID")  // Suppression de l'ID utilisateur
                    editor.apply()

                    // Afficher un message de succès
                    Toast.makeText(this@AdminDashboardActivity, "Déconnexion réussie", Toast.LENGTH_SHORT).show()

                    // Redirection vers l'écran de connexion
                    val intent = Intent(this@AdminDashboardActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()  // Fermer l'activité actuelle pour empêcher de revenir à la page précédente
                } else {
                    // Si la réponse n'est pas réussie
                    Toast.makeText(this@AdminDashboardActivity, "Erreur lors de la déconnexion", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SuccessResponse>, t: Throwable) {
                // Gestion des erreurs de connexion
                Toast.makeText(this@AdminDashboardActivity, "Erreur de connexion: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
