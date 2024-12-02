package iset.dsi.myapplication
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AfficheRessourceActivity : AppCompatActivity() {

    private lateinit var resourceRecyclerView: RecyclerView
    private lateinit var resourceAdapter: AfficheResourceAdapter
    private lateinit var backIcon: ImageView  // Déclare l'ImageView pour l'icône de retour
    private lateinit var searchEditText: EditText  // Déclare l'EditText pour la recherche
    private lateinit var searchButton: Button  // Déclare le bouton pour la recherche

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_affiche_ressource)

        // Initialisation de l'ImageView pour l'icône de retour
        backIcon = findViewById(R.id.backIcon)
        searchEditText = findViewById(R.id.searchEditText) // Initialisation de l'EditText
        searchButton = findViewById(R.id.searchButton) // Initialisation du bouton de recherche

        // Redirige vers CategoriesActivity, pas un fragment
        backIcon.setOnClickListener {
            val intent = Intent(this, CategoryFragment::class.java)
            startActivity(intent)
            finish() // Facultatif, pour fermer AfficheRessourceActivity
        }


        // Configure le RecyclerView
        resourceRecyclerView = findViewById(R.id.resourceRecyclerView)
        resourceRecyclerView.layoutManager = LinearLayoutManager(this)

        // Action pour le bouton de recherche
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchResources(query)
            } else {
                Toast.makeText(this, "Veuillez entrer une recherche", Toast.LENGTH_SHORT).show()
            }
        }

        // Vérifie si une catégorie a été envoyée et récupère les ressources
        val categoryId = intent.getIntExtra("CATEGORY_ID", -1)
        if (categoryId != -1) {
            fetchResourcesByCategory(categoryId)
        } else {
            showAlert("Erreur", "Catégorie invalide")
        }
    }

    private fun fetchResourcesByCategory(categoryId: Int) {
        val call = RetrofitInstance.api.getResourcesByCategory(categoryId)
        call.enqueue(object : Callback<List<Resource>> {
            override fun onResponse(call: Call<List<Resource>>, response: Response<List<Resource>>) {
                if (response.isSuccessful) {
                    // Filtre les ressources par statut "Accepté"
                    val resources = response.body()?.filter { it.status == "Accepté" } ?: emptyList()
                    if (resources.isNotEmpty()) {
                        resourceAdapter = AfficheResourceAdapter(resources)
                        resourceRecyclerView.adapter = resourceAdapter
                    } else {
                        showAlert(
                            "Aucune ressource trouvée",
                            "Aucune ressource acceptée disponible pour cette catégorie."
                        )
                    }
                } else {
                    val errorMessage =
                        "Erreur ${response.code()} : ${response.errorBody()?.string()}"
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
}
