package iset.dsi.myapplication.admin

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import iset.dsi.myapplication.R
import iset.dsi.myapplication.RetrofitClient
import iset.dsi.myapplication.ApiService
import iset.dsi.myapplication.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminHomeFragment : Fragment(R.layout.fragment_admin_home) {

    // Méthode appelée quand la vue est prête
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Appeler la méthode pour récupérer les stats et mettre à jour l'UI
        fetchStats(view)
    }

    private fun fetchStats(view: View) {
        // Lancer la récupération des données en arrière-plan
        lifecycleScope.launch {
            try {
                // Appels API pour récupérer les données
                fetchTotalUsers(view)
                fetchTotalResources(view)
                fetchTotalCategories(view)
            } catch (e: Exception) {
                showErrorToast("Erreur réseau ou serveur.")
            }
        }
    }

    private suspend fun fetchTotalUsers(view: View) {
        val apiService = RetrofitClient.apiService
        apiService.getTotalUsersCount().enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (response.isSuccessful) {
                    val userCount = response.body() ?: 0
                    // Mettre à jour l'UI avec les données récupérées
                    view.findViewById<TextView>(R.id.user_count_text).text = userCount.toString()
                    view.findViewById<CardView>(R.id.card_users_active).visibility = View.VISIBLE
                } else {
                    showErrorToast("Erreur lors de la récupération des utilisateurs.")
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                showErrorToast("Erreur lors de la récupération des utilisateurs.")
            }
        })
    }

    private fun fetchTotalResources(view: View) {
        val resourceApi = RetrofitInstance.api // Accédez à l'API via RetrofitInstance.api
        resourceApi.getTotalResourcesCount().enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (response.isSuccessful) {
                    val resourceCount = response.body() ?: 0
                    // Mettre à jour l'UI avec les données récupérées
                    view.findViewById<TextView>(R.id.resource_count_text).text = resourceCount.toString()
                    view.findViewById<CardView>(R.id.card_resources_available).visibility = View.VISIBLE
                } else {
                    showErrorToast("Erreur lors de la récupération des ressources.")
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                showErrorToast("Erreur lors de la récupération des ressources.")
            }
        })
    }


    private fun fetchTotalCategories(view: View) {
        val resourceApi = RetrofitInstance.api // Utilisation correcte de RetrofitInstance
        resourceApi.getTotalCategoriesCount().enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (response.isSuccessful) {
                    val categoryCount = response.body() ?: 0
                    // Mettre à jour l'UI avec les données récupérées
                    view.findViewById<TextView>(R.id.category_count_text).text = categoryCount.toString()
                    view.findViewById<CardView>(R.id.card_categories_available).visibility = View.VISIBLE
                } else {
                    showErrorToast("Erreur lors de la récupération des catégories.")
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                showErrorToast("Erreur lors de la récupération des catégories.")
            }
        })
    }


    private fun showErrorToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
