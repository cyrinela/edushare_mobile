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
    private val resources: MutableList<Resource>,  // Utilisez MutableList pour permettre la modification
    private val onEdit: (Resource) -> Unit,
    private val onDelete: (Resource) -> Unit
) : RecyclerView.Adapter<MyResourcesAdapter.ResourceViewHolder>() {

    inner class ResourceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nomTextView: TextView = view.findViewById(R.id.resourceNom)
        val descriptionTextView: TextView = view.findViewById(R.id.resourceDescription)
        val editButton: Button = view.findViewById(R.id.editButton)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_my_resource, parent, false)
        return ResourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        val resource = resources[position]

        // Remplir les vues avec les données
        holder.nomTextView.text = resource.nom
        holder.descriptionTextView.text = resource.description

        // Écouteur pour le bouton "Éditer"
        holder.editButton.setOnClickListener {
            onEdit(resource)
        }
        // Écouteur pour le bouton "Supprimer"
        holder.deleteButton.setOnClickListener {
            onDelete(resource)
        }


    }

    override fun getItemCount(): Int = resources.size
}
