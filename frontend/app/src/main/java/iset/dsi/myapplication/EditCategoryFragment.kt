package iset.dsi.myapplication
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditCategoryFragment : Fragment() {

    private lateinit var categoryNameEditText: EditText
    private lateinit var categoryDescriptionEditText: EditText
    private var categoryId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_category, container, false)

        categoryNameEditText = view.findViewById(R.id.categoryNameEditText)
        categoryDescriptionEditText = view.findViewById(R.id.categoryDescriptionEditText)

        // Obtenez les informations de la catégorie à partir des arguments
        arguments?.let {
            categoryId = it.getInt("categoryId")
            val categoryName = it.getString("categoryName")
            val categoryDescription = it.getString("categoryDescription")

            // Remplissez les champs avec les données existantes
            categoryNameEditText.setText(categoryName)
            categoryDescriptionEditText.setText(categoryDescription)
        }

        // Gestion du bouton de sauvegarde
        view.findViewById<View>(R.id.saveButton).setOnClickListener {
            updateCategory()
        }

        return view
    }

    private fun updateCategory() {
        // Récupérer les nouvelles valeurs des champs de texte
        val newCategoryName = categoryNameEditText.text.toString()
        val newCategoryDescription = categoryDescriptionEditText.text.toString()

        // Vérifier que les champs ne sont pas vides
        if (categoryId != null && newCategoryName.isNotEmpty() && newCategoryDescription.isNotEmpty()) {
            // Créer l'objet Category avec les nouvelles valeurs
            val updatedCategory = Category(categoryId!!, newCategoryName, newCategoryDescription)

            // Appeler l'API pour mettre à jour la catégorie
            RetrofitInstance.api.updateCategory(categoryId!!, updatedCategory).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Catégorie mise à jour avec succès", Toast.LENGTH_SHORT).show()
                        // Rediriger l'utilisateur ou mettre à jour l'affichage
                        activity?.onBackPressed() // Retour à l'écran précédent
                    } else {
                        Toast.makeText(requireContext(), "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
        }
    }


}
