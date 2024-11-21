package iset.dsi.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyRessourcesFragment : Fragment() {

    private lateinit var myResourcesRecyclerView: RecyclerView
    private lateinit var myResourcesAdapter: MyResourcesAdapter
    private val resources = mutableListOf<Resource>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_myressources, container, false)

        // Initialisation de la RecyclerView
        myResourcesRecyclerView = view.findViewById(R.id.myResourcesRecyclerView)
        myResourcesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Exemple de données
        resources.add(Resource(1, "Cours de maths", "Algebra avancée", "Accepté"))
        resources.add(Resource(2, "Guide Python", "Introduction à la programmation", "En attente"))
        resources.add(Resource(3, "Résumé d'histoire", "La révolution française", "Refusé"))

        // Configuration de l'adapter
        myResourcesAdapter = MyResourcesAdapter(requireContext(), resources,
            onEdit = { resource ->
                Toast.makeText(requireContext(), "Éditer : ${resource.title}", Toast.LENGTH_SHORT).show()
                // Ajoutez ici la logique pour éditer
            },
            onDelete = { resource ->
                resources.remove(resource)
                myResourcesAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Supprimé : ${resource.title}", Toast.LENGTH_SHORT).show()
            }
        )

        myResourcesRecyclerView.adapter = myResourcesAdapter

        return view
    }
}
