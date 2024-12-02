package iset.dsi.myapplication

import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import android.util.Log  // Import nécessaire pour les logs
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_affiche_ressource)

        // Initialisation de l'ImageView pour l'icône de retour
        backIcon = findViewById(R.id.backIcon)

        // Ajoute l'action pour l'icône de retour
        backIcon.setOnClickListener {
            // Démarre CategoriesActivity lorsqu'on clique sur l'icône de retour
            val intent = Intent(this, CategoryFragment::class.java)
            startActivity(intent)
            finish() // Facultatif, pour fermer AfficheRessourceActivity
        }

        resourceRecyclerView = findViewById(R.id.resourceRecyclerView)
        resourceRecyclerView.layoutManager = LinearLayoutManager(this)

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
                    // Récupérez les ressources et filtrez par statut "Accepté"
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


    private fun showAlert(title: String, message: String) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}