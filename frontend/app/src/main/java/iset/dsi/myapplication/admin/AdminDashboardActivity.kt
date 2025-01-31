package iset.dsi.myapplication.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import iset.dsi.myapplication.LoginActivity
import iset.dsi.myapplication.R
import iset.dsi.myapplication.RetrofitClient
import iset.dsi.myapplication.SuccessResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.sidebar_nav)
        toolbar = findViewById(R.id.toolbar)

        // Configurer la Toolbar
        setSupportActionBar(toolbar)

        // Afficher uniquement l'icône du menu hamburger personnalisé
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu) // Utilise ton icône personnalisée ici
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Affiche uniquement l'icône hamburger

        // Bouton hamburger pour ouvrir le Drawer
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navigationView)  // Ouvrir le menu latéral
        }

        // Écouteur pour les éléments de navigation du Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null

            when (item.itemId) {
                R.id.nav_home_admin -> selectedFragment = AdminHomeFragment()
                R.id.nav_profile_admin -> selectedFragment = AdminProfileFragment()
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

        // Écouteur pour les éléments du Sidebar (NavigationView)
        navigationView.setNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null

            when (item.itemId) {
                R.id.nav_resources_admin -> selectedFragment = AdminCategoriesFragment()
                R.id.nav_ressources_admin -> selectedFragment = AdminRessourcesFragment()
                R.id.gs_users -> selectedFragment = AdminUsersFragment()
            }

            // Remplacer le fragment actuel par le sélectionné
            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()

                // Fermer le menu de navigation latérale
                drawerLayout.closeDrawers()
            }

            true
        }

        // Par défaut, charger le fragment d'accueil
        bottomNavigationView.selectedItemId = R.id.nav_home_admin
    }

    // Fonction de déconnexion
    private fun logout() {
        val apiService = RetrofitClient.apiService

        val call = apiService.logout()

        call.enqueue(object : Callback<SuccessResponse> {
            override fun onResponse(call: Call<SuccessResponse>, response: Response<SuccessResponse>) {
                if (response.isSuccessful) {
                    val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("USER_ID")
                    editor.apply()

                    val intent = Intent(this@AdminDashboardActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@AdminDashboardActivity, "Erreur lors de la déconnexion", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SuccessResponse>, t: Throwable) {
                Toast.makeText(this@AdminDashboardActivity, "Erreur de connexion: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
