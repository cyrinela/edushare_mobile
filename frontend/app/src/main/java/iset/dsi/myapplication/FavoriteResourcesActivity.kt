package iset.dsi.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteResourcesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var favoritesAdapter: AfficheResourceAdapter
    private lateinit var dbHelper: FavoritesDatabaseHelper
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_resources)

        // Initialisation du DrawerLayout et du NavigationView
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView =
            findViewById(R.id.sidebar_nav)  // Utilisation du bon ID pour NavigationView

        // Configuration de la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configuration de l'ActionBarDrawerToggle pour lier la toolbar au DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Configuration du RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFavorites)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialisation de la base de données et récupération des ressources favorites
        dbHelper = FavoritesDatabaseHelper(this)
        val favoriteResources = dbHelper.getAllFavorites()

        // Configuration de l'adaptateur du RecyclerView
        favoritesAdapter = AfficheResourceAdapter(favoriteResources)
        recyclerView.adapter = favoritesAdapter

        // Configuration du menu de navigation (Drawer)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_dashboard -> {
                    loadFragment(DashboardFragment())
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_addressource -> {
                    loadFragment(AddRessourceFragment())
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_addgroups -> {
                    loadFragment(AddGroupFragment())
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_groups -> {
                    loadFragment(GroupsFragment())
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_myressource -> {
                    loadFragment(MyRessourcesFragment())
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_notifications -> {
                    loadFragment(NotificationsFragment())
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_about -> {
                    loadFragment(AboutFragment())
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_likes -> {
                    val intent = Intent(this, FavoriteResourcesActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_logout -> {
                    logout()  // Appel de la fonction de déconnexion
                    drawerLayout.closeDrawers()
                    true
                }

                else -> false
            }
        }
    }

    // Méthode pour charger un fragment
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.fragment_container,
            fragment
        )  // Utiliser le conteneur où les fragments sont affichés
        transaction.addToBackStack(null)
        transaction.commit()
    }


    private fun logout() {
        val call = RetrofitClient.apiService.logout()  // Appel de la méthode logout via Retrofit

        call.enqueue(object : Callback<SuccessResponse> {
            override fun onResponse(
                call: Call<SuccessResponse>,
                response: Response<SuccessResponse>
            ) {
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
                    val builder = android.app.AlertDialog.Builder(this@FavoriteResourcesActivity)
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
