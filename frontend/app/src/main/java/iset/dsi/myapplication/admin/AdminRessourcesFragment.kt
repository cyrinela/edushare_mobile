package iset.dsi.myapplication.admin

import iset.dsi.myapplication.R
import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iset.dsi.myapplication.Resource
import iset.dsi.myapplication.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class AdminRessourcesFragment : Fragment(R.layout.fragment_admin_ressources) {

    private lateinit var adminResourcesRecyclerView: RecyclerView
    private lateinit var adminResourcesAdapter: AdminResourcesAdapter
    private val resources = mutableListOf<Resource>()
    private val allResources = mutableListOf<Resource>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adminResourcesRecyclerView = view.findViewById(R.id.adminResourcesRecyclerView)
        adminResourcesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        adminResourcesAdapter = AdminResourcesAdapter(
            context = requireContext(),
            resources = resources,
            onStatusChange = { resource, newStatus ->
                updateResourceStatus(resource, newStatus)
            }
        )
        adminResourcesRecyclerView.adapter = adminResourcesAdapter

        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val searchButton = view.findViewById<Button>(R.id.searchButton)

        // Action du bouton de recherche
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotEmpty()) {
                Log.d("SearchButton", "Recherche lancée avec la query : $query")
                searchResources(query)
            } else {
                Toast.makeText(requireContext(), "Veuillez entrer un terme de recherche", Toast.LENGTH_SHORT).show()
            }
        }

        fetchResources()
    }

    private fun fetchResources() {
        RetrofitInstance.api.getResources().enqueue(object : Callback<List<Resource>> {
            override fun onResponse(call: Call<List<Resource>>, response: Response<List<Resource>>) {
                if (response.isSuccessful) {
                    resources.clear()
                    response.body()?.let { resources.addAll(it) }
                    allResources.clear()
                    allResources.addAll(resources)
                    adminResourcesAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Erreur de chargement des ressources", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Resource>>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchResources(query: String) {
        Log.d("SearchResources", "Recherche avec le query : $query") // Log pour vérifier
        RetrofitInstance.api.searchResources(query).enqueue(object : Callback<List<Resource>> {
            override fun onResponse(call: Call<List<Resource>>, response: Response<List<Resource>>) {
                if (response.isSuccessful) {
                    val searchResults = response.body() ?: emptyList()
                    resources.clear()
                    resources.addAll(searchResults)
                    adminResourcesAdapter.notifyDataSetChanged()
                    Log.d("SearchResources", "Résultats de recherche : ${searchResults.size} ressources trouvées.")
                } else {
                    Toast.makeText(requireContext(), "Erreur de recherche", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Resource>>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur réseau lors de la recherche", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateResourceStatus(resource: Resource, newStatus: String) {
        // Créer une nouvelle ressource avec le statut mis à jour, mais conserver le categorie_id
        val updatedResource = resource.copy(status = newStatus, categorie_id = resource.categorie_id)

        RetrofitInstance.api.updateResource(resource.id!!, updatedResource).enqueue(object : Callback<Resource> {
            override fun onResponse(call: Call<Resource>, response: Response<Resource>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Statut mis à jour", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Erreur : ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Resource>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
