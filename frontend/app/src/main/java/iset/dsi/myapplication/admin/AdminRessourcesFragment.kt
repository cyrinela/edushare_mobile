package iset.dsi.myapplication.admin

import iset.dsi.myapplication.R
import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iset.dsi.myapplication.Resource
import iset.dsi.myapplication.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminRessourcesFragment : Fragment(R.layout.fragment_admin_ressources) {

    private lateinit var adminResourcesRecyclerView: RecyclerView
    private lateinit var adminResourcesAdapter: AdminResourcesAdapter
    private val resources = mutableListOf<Resource>()

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

        fetchResources()
    }

    private fun fetchResources() {
        RetrofitInstance.api.getResources().enqueue(object : Callback<List<Resource>> {
            override fun onResponse(call: Call<List<Resource>>, response: Response<List<Resource>>) {
                if (response.isSuccessful) {
                    resources.clear()
                    response.body()?.let { resources.addAll(it) }
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

    private fun updateResourceStatus(resource: Resource, newStatus: String) {
        val updatedResource = resource.copy(status = newStatus)
        RetrofitInstance.api.updateResource(resource.id!!, updatedResource).enqueue(object : Callback<Resource> {
            override fun onResponse(call: Call<Resource>, response: Response<Resource>) {
                if (response.isSuccessful) {
                    // Mise à jour réussie
                    Toast.makeText(requireContext(), "Statut mis à jour", Toast.LENGTH_SHORT).show()
                } else {
                    // Afficher le code d'erreur et le message
                    Toast.makeText(requireContext(), "Erreur : ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                    response.errorBody()?.let {
                        val errorMessage = it.string()
                        Toast.makeText(requireContext(), "Détails : $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            }


            override fun onFailure(call: Call<Resource>, t: Throwable) {
                // Logs pour les erreurs réseau
                println("Erreur réseau : ${t.message}")
            }
        })
    }


}
