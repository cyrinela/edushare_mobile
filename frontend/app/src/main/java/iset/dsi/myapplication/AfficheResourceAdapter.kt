package iset.dsi.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AfficheResourceAdapter(private val resources: List<Resource>) : RecyclerView.Adapter<AfficheResourceAdapter.ResourceViewHolder>() {

    // Crée une nouvelle vue (item_resource.xml)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resource, parent, false)
        return ResourceViewHolder(view)
    }

    // Lie les données à l'interface pour chaque élément
    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        val resource = resources[position]
        holder.bind(resource)
    }

    // Retourne le nombre total d'éléments à afficher
    override fun getItemCount(): Int = resources.size

    // ViewHolder pour lier les vues de l'élément
    inner class ResourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val resourceNameTextView: TextView = itemView.findViewById(R.id.categoryName)
        private val resourceDescriptionTextView: TextView = itemView.findViewById(R.id.categoryDescription)

        // Méthode pour lier les données (nom et description de la ressource) aux vues
        fun bind(resource: Resource) {
            resourceNameTextView.text = "Nom Ressource: ${resource.nom}"  // Affichage du nom avec le préfixe
            resourceDescriptionTextView.text = "Description: ${resource.description}"  // Affichage de la description avec le préfixe
        }
    }
}
