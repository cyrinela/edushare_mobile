package iset.dsi.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialisation du DrawerLayout et de la Toolbar
        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configuration du Drawer Navigation
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Chargement du fragment par défaut
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            navigationView.setCheckedItem(R.id.nav_home)
        }

        // Configuration de la Bottom Navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> loadFragment(HomeFragment())
                R.id.navigation_resources -> loadFragment(CategoryFragment())
                R.id.navigation_profile -> loadFragment(ProfileFragment())
                else -> false
            }
        }
    }

    // Fonction pour charger un fragment
    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }

    // Gestion des éléments du menu de navigation
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> loadFragment(HomeFragment())
            R.id.nav_settings -> loadFragment(SettingsFragment())
            R.id.nav_dashboard -> loadFragment(DashboardFragment())
            R.id.nav_addressource -> loadFragment(AddRessourceFragment())
            R.id.nav_addgroups -> loadFragment(AddGroupFragment())
            R.id.nav_groups -> loadFragment(GroupsFragment())
            R.id.nav_myressource -> loadFragment(MyRessourcesFragment())
            R.id.nav_notifications -> loadFragment(NotificationsFragment())
            R.id.nav_about -> loadFragment(AboutFragment())
            R.id.favv -> loadFragment(favv())
            /*R.id.nav_likes -> {
                val intent = Intent(this, FavoriteResourcesActivity::class.java)
                startActivity(intent)
            }*/
            R.id.nav_logout -> {
                logout()  // Appel de la fonction de déconnexion
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Gestion du retour arrière
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun logout() {
        val call = RetrofitClient.apiService.logout()  // Appel de la méthode logout via Retrofit

        call.enqueue(object : Callback<SuccessResponse> {
            override fun onResponse(call: Call<SuccessResponse>, response: Response<SuccessResponse>) {
                if (response.isSuccessful) {
                    // Si la réponse est réussie
                    val message = response.body()?.message ?: "Déconnexion réussie !"
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

                    // Rediriger vers la page de login après la déconnexion
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                    finish()  // Fermer l'activité courante (MainActivity)
                } else {
                    // Si la réponse est une erreur, afficher l'erreur dans un AlertDialog
                    val errorMessage = response.errorBody()?.string() ?: "Erreur inconnue"

                    // Afficher un AlertDialog avec l'erreur
                    val builder = android.app.AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("Erreur de déconnexion")
                    builder.setMessage("Détails de l'erreur : $errorMessage")
                    builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    builder.show()
                }
            }

            override fun onFailure(call: Call<SuccessResponse>, t: Throwable) {
                // En cas d'échec de la requête (problème réseau)
                Toast.makeText(applicationContext, "Erreur de connexion", Toast.LENGTH_SHORT).show()
            }
        })
    }







}
