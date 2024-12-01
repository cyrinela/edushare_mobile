package iset.dsi.myapplication.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import iset.dsi.myapplication.R
import iset.dsi.myapplication.Resource

class AdminResourcesAdapter(
    private val context: Context,
    private val resources: MutableList<Resource>,
    private val onStatusChange: (Resource, String) -> Unit
) : RecyclerView.Adapter<AdminResourcesAdapter.ResourceViewHolder>() {

    inner class ResourceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nomTextView: TextView = view.findViewById(R.id.resourceNom)
        val descriptionTextView: TextView = view.findViewById(R.id.resourceDescription)
        val statusSpinner: Spinner = view.findViewById(R.id.statusSpinner)
        val saveButton: Button = view.findViewById(R.id.saveStatusButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_admin_resource, parent, false)
        return ResourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        val resource = resources[position]

        // Remplir les vues avec les données
        holder.nomTextView.text = resource.nom
        holder.descriptionTextView.text = resource.description

        // Configurer le Spinner avec les statuts
        val statuses = listOf("en attente", "accepté", "refusé")
        val adapter = android.widget.ArrayAdapter(context, android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.statusSpinner.adapter = adapter

        // Sélectionner le statut actuel
        holder.statusSpinner.setSelection(statuses.indexOf(resource.status))

        // Évitez les écoutes multiples si on scrolle
        holder.saveButton.setOnClickListener(null)
        holder.saveButton.setOnClickListener {
            val newStatus = holder.statusSpinner.selectedItem.toString()
            if (newStatus != resource.status) {
                // Passer la ressource et le nouveau statut à la méthode de mise à jour
                onStatusChange(resource, newStatus)
            } else {
                Toast.makeText(context, "Aucun changement détecté", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = resources.size
}
