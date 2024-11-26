package iset.dsi.myapplication.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import iset.dsi.myapplication.R
import iset.dsi.myapplication.Category

class AdminCategoryAdapter(
    private val categories: List<Category>,
    private val onEditClick: (Category) -> Unit,  // Callback pour le bouton Modifier
    private val onDeleteClick: (Category) -> Unit // Callback pour le bouton Supprimer
) : RecyclerView.Adapter<AdminCategoryAdapter.AdminCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_category_details, parent, false)
        return AdminCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminCategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int = categories.size

    inner class AdminCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryName)
        private val categoryDescriptionTextView: TextView = itemView.findViewById(R.id.categoryDescription)
        private val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(category: Category) {
            categoryNameTextView.text = category.nom
            categoryDescriptionTextView.text = category.description

            // Liez les boutons avec les callbacks
            editButton.setOnClickListener { onEditClick(category) }
            deleteButton.setOnClickListener { onDeleteClick(category) }
        }
    }
}
