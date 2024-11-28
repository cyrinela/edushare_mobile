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
    private val resources = mutableListOf<Resource>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myressources, container, false)
        var categoryId: Int = 0
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
        categoryId = arguments?.getInt("CATEGORY_ID") ?: 0
        // Charger les ressources depuis le backend
        fetchResources()

        return view
    }

    // Charger les ressources depuis l'API
    private fun fetchResources() {
        RetrofitInstance.api.getResources().enqueue(object : Callback<List<Resource>> {
            override fun onResponse(
                call: Call<List<Resource>>,
                response: Response<List<Resource>>
            ) {
                if (response.isSuccessful) {
                    resources.clear()
                    response.body()?.let { resources.addAll(it) }
                    myResourcesAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Erreur de chargement des ressources",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Resource>>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    // Ouvrir le DialogFragment pour éditer une ressource
    private fun openEditDialog(resource: Resource) {
        val bundle = Bundle().apply {
            resource.id?.let { putInt("RESOURCE_ID", it) }
            putString("RESOURCE_NAME", resource.nom)
            putString("RESOURCE_DESCRIPTION", resource.description)
            putInt("CATEGORIE_ID", resource.categorie_id)
        }

        val dialog = EditResourceDialogFragment().apply {
            arguments = bundle
            setOnResourceUpdatedListener(object :
                EditResourceDialogFragment.OnResourceUpdatedListener {
                override fun onResourceUpdated() {
                    fetchResources() // Recharge les ressources après l'édition
                }
            })
        }
        dialog.show(parentFragmentManager, "EditResourceDialog")
    }

    // Supprimer une ressource via l'API
    private fun deleteResource(resource: Resource) {
        resource.id?.let { resourceId ->
            RetrofitInstance.api.deleteResource(resourceId).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        resources.remove(resource)
                        myResourcesAdapter.notifyDataSetChanged()
                        Toast.makeText(
                            requireContext(),
                            "Ressource supprimée avec succès",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Erreur lors de la suppression",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Erreur réseau : ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } ?: run {
            Toast.makeText(requireContext(), "ID de la ressource introuvable", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Afficher une boîte de dialogue de confirmation pour la suppression
    private fun showDeleteConfirmationDialog(resource: Resource) {
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