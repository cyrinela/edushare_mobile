package iset.dsi.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private val categories: List<Category>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryName)
        private val categoryDescriptionTextView: TextView = itemView.findViewById(R.id.categoryDescription)

        fun bind(category: Category) {
            categoryNameTextView.text = category.nom
            categoryDescriptionTextView.text = category.description

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, AfficheRessourceActivity::class.java)
                intent.putExtra("CATEGORY_ID", category.id)
                itemView.context.startActivity(intent)
            }
        }
    }
}
