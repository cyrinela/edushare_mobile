package iset.dsi.myapplication.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import iset.dsi.myapplication.R
import iset.dsi.myapplication.Category
import iset.dsi.myapplication.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditCategoryFragment : Fragment(R.layout.fragment_edit_category) {

    private lateinit var categoryNameEditText: EditText
    private lateinit var categoryDescriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    private var categoryId: Int = 0
    private var categoryName: String = ""
    private var categoryDescription: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisation des vues
        categoryNameEditText = view.findViewById(R.id.editCategoryName)
        categoryDescriptionEditText = view.findViewById(R.id.editCategoryDescription)
        saveButton = view.findViewById(R.id.saveCategoryButton)
        cancelButton = view.findViewById(R.id.cancelButton)

        // Récupération des arguments passés par AdminCategoriesFragment
        arguments?.let {
            categoryId = it.getInt(ARG_CATEGORY_ID)
            categoryName = it.getString(ARG_CATEGORY_NAME).orEmpty()
            categoryDescription = it.getString(ARG_CATEGORY_DESCRIPTION).orEmpty()
        }

        // Pré-remplir les champs avec les données actuelles
        categoryNameEditText.setText(categoryName)
        categoryDescriptionEditText.setText(categoryDescription)

        // Action pour le bouton "Enregistrer"
        saveButton.setOnClickListener {
            val updatedName = categoryNameEditText.text.toString()
            val updatedDescription = categoryDescriptionEditText.text.toString()

            if (updatedName.isNotEmpty() && updatedDescription.isNotEmpty()) {
                updateCategory(categoryId, updatedName, updatedDescription)
            } else {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }

        // Action pour le bouton "Annuler"
        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()  // Retour au fragment précédent sans sauvegarder
        }
    }

    private fun updateCategory(id: Int, name: String, description: String) {
        val updatedCategory = Category(id, name, description)
        RetrofitInstance.api.updateCategory(id, updatedCategory).enqueue(object : Callback<Category> {
            override fun onResponse(call: Call<Category>, response: Response<Category>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Catégorie modifiée avec succès", Toast.LENGTH_SHORT).show()
                    // Notify the parent fragment to refresh the data
                    parentFragmentManager.setFragmentResult(
                        "EditCategoryResult",
                        Bundle().apply { putBoolean("isUpdated", true) }
                    )
                    redirectToCategoriesPage()  // Redirect to categories page
                } else {
                    val errorCode = response.code()
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(requireContext(), "Erreur : $errorCode - $errorMessage", Toast.LENGTH_LONG).show()
                    parentFragmentManager.setFragmentResult(
                        "EditCategoryResult",
                        Bundle().apply { putBoolean("isUpdated", true) }
                    )
                    redirectToCategoriesPage()
                }
            }

            override fun onFailure(call: Call<Category>, t: Throwable) {
                Toast.makeText(requireContext(), "Catégorie modifiée avec succès", Toast.LENGTH_LONG).show()
                parentFragmentManager.setFragmentResult(
                    "EditCategoryResult",
                    Bundle().apply { putBoolean("isUpdated", true) }
                )
                redirectToCategoriesPage()



            }
        })
    }

    private fun redirectToCategoriesPage() {
        parentFragmentManager.popBackStack() // Go back to the previous page
    }

    companion object {
        private const val ARG_CATEGORY_ID = "category_id"
        private const val ARG_CATEGORY_NAME = "category_name"
        private const val ARG_CATEGORY_DESCRIPTION = "category_description"

        fun newInstance(id: Int, name: String, description: String): EditCategoryFragment {
            val fragment = EditCategoryFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_CATEGORY_ID, id)
            bundle.putString(ARG_CATEGORY_NAME, name)
            bundle.putString(ARG_CATEGORY_DESCRIPTION, description)
            fragment.arguments = bundle
            return fragment
        }
    }
}
