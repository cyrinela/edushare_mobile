package iset.dsi.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AfficheRessourceActivity : AppCompatActivity() {

    private lateinit var resourceRecyclerView: RecyclerView
    private lateinit var resourceAdapter: AfficheResourceAdapter
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_affiche_ressource)

        // Initialisation des vues
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.sidebar_nav)

        // Configure le RecyclerView
        resourceRecyclerView = findViewById(R.id.resourceRecyclerView)
        resourceRecyclerView.layoutManager = LinearLayoutManager(this)

        // Action pour le bouton de recherche
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchResources(query)
            } else {
                Toast.makeText(this, "Entrez une recherche", Toast.LENGTH_SHORT).show()
            }
        }

        // Vérifie si une catégorie a été envoyée et récupère les ressources
        val categoryId = intent.getIntExtra("CATEGORY_ID", -1)
        if (categoryId != -1) {
            fetchResourcesByCategory(categoryId)
        } else {
            showAlert("Erreur", "Catégorie invalide")
        }

        // Configure le toolbar pour afficher l'icône du menu (hamburger)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Affiche l'icône de menu dans la Toolbar
        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_menu) // Remplacez "ic_menu" par l'icône que vous souhaitez utiliser
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Action du menu
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navigationView)  // Ouvre le drawer lorsqu'on clique sur l'icône
        }

        // Configure le NavigationView
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

    private fun fetchResourcesByCategory(categoryId: Int) {
        val call = RetrofitInstance.api.getResourcesByCategory(categoryId)
        call.enqueue(object : Callback<List<Resource>> {
            override fun onResponse(call: Call<List<Resource>>, response: Response<List<Resource>>) {
                if (response.isSuccessful) {
                    val resources = response.body()?.filter { it.status == "accepté" } ?: emptyList()
                    if (resources.isNotEmpty()) {
                        resourceAdapter = AfficheResourceAdapter(resources)
                        resourceRecyclerView.adapter = resourceAdapter
                    } else {
                        showAlert("Aucune ressource trouvée", "Aucune ressource acceptée disponible pour cette catégorie.")
                    }
                } else {
                    val errorMessage = "Erreur ${response.code()} : ${response.errorBody()?.string()}"
                    showAlert("Erreur de chargement", errorMessage)
                }
            }

            override fun onFailure(call: Call<List<Resource>>, t: Throwable) {
                showAlert("Erreur de connexion", "Impossible de récupérer les ressources.")
            }
        })
    }

    private fun searchResources(query: String) {
        val call = RetrofitInstance.api.searchResources(query)
        call.enqueue(object : Callback<List<Resource>> {
            override fun onResponse(call: Call<List<Resource>>, response: Response<List<Resource>>) {
                if (response.isSuccessful) {
                    val resources = response.body() ?: emptyList()
                    if (resources.isNotEmpty()) {
                        resourceAdapter = AfficheResourceAdapter(resources)
                        resourceRecyclerView.adapter = resourceAdapter
                    } else {
                        showAlert("Aucune ressource trouvée", "Aucune ressource ne correspond à cette recherche.")
                    }
                } else {
                    val errorMessage = "Erreur ${response.code()} : ${response.errorBody()?.string()}"
                    showAlert("Erreur de recherche", errorMessage)
                }
            }

            override fun onFailure(call: Call<List<Resource>>, t: Throwable) {
                showAlert("Erreur de connexion", "Impossible de récupérer les ressources.")
            }
        })
    }

    private fun showAlert(title: String, message: String) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun logout() {
        val call = RetrofitClient.apiService.logout()
        call.enqueue(object : Callback<SuccessResponse> {
            override fun onResponse(call: Call<SuccessResponse>, response: Response<SuccessResponse>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "Déconnexion réussie !"
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Erreur inconnue"
                    val builder = android.app.AlertDialog.Builder(this@AfficheRessourceActivity)
                    builder.setTitle("Erreur de déconnexion")
                    builder.setMessage("Détails de l'erreur : $errorMessage")
                    builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    builder.show()
                }
            }

            override fun onFailure(call: Call<SuccessResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Erreur de connexion", Toast.LENGTH_SHORT).show()
            }
        })
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
}
