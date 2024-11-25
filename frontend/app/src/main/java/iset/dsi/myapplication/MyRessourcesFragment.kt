package iset.dsi.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class MyRessourcesFragment : Fragment() {

    private lateinit var myResourcesRecyclerView: RecyclerView
    private lateinit var myResourcesAdapter: MyResourcesAdapter
    private val resources = mutableListOf<ResourceDto>() // Utilisation de ResourceDto

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myressources, container, false)

        // Initialisation de la RecyclerView
        myResourcesRecyclerView = view.findViewById(R.id.myResourcesRecyclerView)
        myResourcesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Configuration de l'adaptateur
        myResourcesAdapter = MyResourcesAdapter(
            context = requireContext(),
            resources = resources,
            onEdit = { resource ->
                openEditDialog(resource) // Appeler le DialogFragment pour l'édition
            },
            onDelete = { resource ->
                showDeleteConfirmationDialog(resource) // Afficher la boîte de dialogue de confirmation
            }
        )
        myResourcesRecyclerView.adapter = myResourcesAdapter

        // Charger les ressources depuis le backend
        fetchResources()

        return view
    }

    private fun fetchResources() {
        RetrofitInstance.api.getResources2().enqueue(object : Callback<List<ResourceDto>> {
            override fun onResponse(call: Call<List<ResourceDto>>, response: Response<List<ResourceDto>>) {
                if (response.isSuccessful) {
                    resources.clear()  // Vider la liste actuelle
                    response.body()?.let {
                        // Ajouter chaque élément de ResourceDto à la liste
                        resources.addAll(it)
                    }
                    myResourcesAdapter.notifyDataSetChanged()  // Notifier l'adaptateur pour mettre à jour la RecyclerView
                } else {
                    Toast.makeText(requireContext(), "Erreur de chargement des ressources", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ResourceDto>>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Ouvrir le DialogFragment pour éditer une ressource
    private fun openEditDialog(resource: ResourceDto) {  // Utilisation de ResourceDto ici
        val bundle = Bundle().apply {
            // Utiliser les données de ResourceDto ici
            putString("RESOURCE_NAME", resource.resourceName)
            putString("RESOURCE_DESCRIPTION", resource.description)
            putString("CATEGORY_NAME", resource.categoryName)
            putString("FILE_NAME", resource.fileName)
        }

        val dialog = EditResourceDialogFragment().apply {
            arguments = bundle
        }
        dialog.show(parentFragmentManager, "EditResourceDialog")
    }

    // Supprimer une ressource via l'API
    private fun deleteResource(resource: ResourceDto) {  // Utilisation de ResourceDto ici
      /*  RetrofitInstance.api.deleteResource(resource.resourceName).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    resources.remove(resource)
                    myResourcesAdapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Ressource supprimée avec succès", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Erreur lors de la suppression", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })*/
    }

    // Afficher une boîte de dialogue de confirmation pour la suppression
    private fun showDeleteConfirmationDialog(resource: ResourceDto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Supprimer la ressource")
            .setMessage("Êtes-vous sûr de vouloir supprimer cette ressource ?")
            .setPositiveButton("Oui") { _, _ ->
                deleteResource(resource) // Supprimer la ressource
            }
            .setNegativeButton("Non", null)
            .show()
    }
}
