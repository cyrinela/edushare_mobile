package iset.dsi.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyResourcesAdapter(
    private val context: Context,
    private val resources: List<ResourceDto>, // Liste des ressources
    private val onEdit: (ResourceDto) -> Unit,  // Action pour éditer
    private val onDelete: (ResourceDto) -> Unit // Action pour supprimer
) : RecyclerView.Adapter<MyResourcesAdapter.ResourceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_my_resource, parent, false)
        return ResourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        val resource = resources[position]
        holder.bind(resource)  // Lier les données de la ressource
    }

    override fun getItemCount(): Int = resources.size

    inner class ResourceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Déclaration des TextViews
        private val resourceNom: TextView = view.findViewById(R.id.resourceNom)
        private val resourceDescription: TextView = view.findViewById(R.id.resourceDescription)
        private val categoryName: TextView = view.findViewById(R.id.categoryName)
        private val fileName: TextView = view.findViewById(R.id.fileName)
        private val editButton: Button = view.findViewById(R.id.editButton)
        private val deleteButton: Button = view.findViewById(R.id.deleteButton)

        // Méthode pour lier les données de la ressource avec les TextViews
        fun bind(resource: ResourceDto) {
            // Lier les titres et les valeurs aux TextViews
            resourceNom.text = "NomRessource : ${resource.resourceName}"
            resourceDescription.text = "Description : ${resource.description}"
            categoryName.text = "Catégorie : ${resource.categoryName}"
            fileName.text = "Nom du fichier : ${resource.fileName}"

            // Actions pour les boutons
            editButton.setOnClickListener { onEdit(resource) }
            deleteButton.setOnClickListener { onDelete(resource) }
        }
    }
}
