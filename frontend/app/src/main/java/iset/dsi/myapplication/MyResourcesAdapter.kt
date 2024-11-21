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
    private val resources: List<Resource>,
    private val onEdit: (Resource) -> Unit,
    private val onDelete: (Resource) -> Unit
) : RecyclerView.Adapter<MyResourcesAdapter.ResourceViewHolder>() {

    inner class ResourceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.resourceTitle)
        val descriptionTextView: TextView = view.findViewById(R.id.resourceDescription)
        val statusTextView: TextView = view.findViewById(R.id.resourceStatus)
        val editButton: Button = view.findViewById(R.id.editButton)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_my_resource, parent, false)
        return ResourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        val resource = resources[position]
        holder.titleTextView.text = resource.title
        holder.descriptionTextView.text = resource.description
        holder.statusTextView.text = "Statut : ${resource.status}"
        holder.editButton.setOnClickListener { onEdit(resource) }
        holder.deleteButton.setOnClickListener { onDelete(resource) }
    }

    override fun getItemCount(): Int = resources.size
}
