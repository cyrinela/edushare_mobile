package iset.dsi.myapplication.admin

import android.os.Bundle
import android.view.View
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

class AddCategoryFragment : Fragment(R.layout.fragment_add_category) {

    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameEditText = view.findViewById(R.id.nameEditText)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        saveButton = view.findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            if (name.isNotEmpty() && description.isNotEmpty()) {
                addCategory(name, description)
            } else {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addCategory(name: String, description: String) {
        val newCategory = Category(0, name, description)
        RetrofitInstance.api.addCategory(newCategory).enqueue(object : Callback<Category> {
            override fun onResponse(call: Call<Category>, response: Response<Category>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Catégorie ajoutée avec succès", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack() // Retour à la liste
                } else {
                    Toast.makeText(requireContext(), "Erreur lors de l'ajout de la catégorie", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Category>, t: Throwable) {
                Toast.makeText(requireContext(), "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
