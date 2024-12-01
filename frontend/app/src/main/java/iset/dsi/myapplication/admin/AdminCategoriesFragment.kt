package iset.dsi.myapplication.admin

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iset.dsi.myapplication.R
import iset.dsi.myapplication.Category
import iset.dsi.myapplication.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminCategoriesFragment : Fragment(R.layout.fragment_admin_categories) {

    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: AdminCategoryAdapter
    private lateinit var addCategoryButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Références aux vues
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView2)
        addCategoryButton = view.findViewById(R.id.addCategoryButton)

        categoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Bouton pour ajouter une catégorie
        addCategoryButton.setOnClickListener {
            openAddCategoryFragment()
        }

        // Charger les catégories
        fetchCategories()
    }

    private fun fetchCategories() {
        RetrofitInstance.api.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    categoryAdapter = AdminCategoryAdapter(
                        categories,
                        onEditClick = { category ->
                            // Modifier une catégorie
                            val editCategoryFragment = EditCategoryFragment.newInstance(
                                category.id,
                                category.nom,
                                category.description
                            )

                            // Ouvrir le fragment de modification
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.container, editCategoryFragment)
                                .addToBackStack(null)
                                .commit()
                        },
                        onDeleteClick = { category ->
                            // Supprimer une catégorie
                            deleteCategory(category.id)
                        }
                    )

                    categoryRecyclerView.adapter = categoryAdapter
                } else {
                    Toast.makeText(requireContext(), "Erreur lors de la récupération des catégories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(requireContext(), "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteCategory(categoryId: Int) {
        // Affichage de l'AlertDialog pour confirmer la suppression
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Voulez-vous vraiment supprimer cette catégorie ?")
            .setPositiveButton("Oui") { _, _ ->
                // Si l'utilisateur confirme, procéder à la suppression avec Retrofit
                RetrofitInstance.api.deleteCategory(categoryId).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            // La catégorie a été supprimée avec succès
                            Toast.makeText(requireContext(), "Catégorie supprimée", Toast.LENGTH_SHORT).show()
                            // Rafraîchir la liste des catégories après suppression
                            fetchCategories()
                        } else {
                            // Erreur lors de la suppression
                            Toast.makeText(requireContext(), "Erreur lors de la suppression", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        // Afficher un message d'erreur en cas de problème de connexion
                        Toast.makeText(requireContext(), "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .setNegativeButton("Non", null)  // L'utilisateur annule la suppression
            .create()
            .show()
    }


    private fun openAddCategoryFragment() {
        val fragment = parentFragmentManager.findFragmentByTag("AddCategoryFragment")
        if (fragment == null) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, AddCategoryFragment(), "AddCategoryFragment")
                .addToBackStack(null)
                .commit()
        }
    }

}
