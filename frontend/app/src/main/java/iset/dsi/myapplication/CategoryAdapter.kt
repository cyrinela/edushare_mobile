package iset.dsi.myapplication
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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
        private val categoryNameTitleTextView: TextView = itemView.findViewById(R.id.categoryNameTitle)
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryName)
        private val categoryDescriptionTitleTextView: TextView = itemView.findViewById(R.id.categoryDescriptionTitle)
        private val categoryDescriptionTextView: TextView = itemView.findViewById(R.id.categoryDescription)

        fun bind(category: Category) {
            // Lier les données aux TextViews
            categoryNameTextView.text = category.nom
            categoryDescriptionTextView.text = category.description
        }
    }
}
