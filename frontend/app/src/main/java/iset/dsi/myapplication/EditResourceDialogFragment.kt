package iset.dsi.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditResourceDialogFragment : DialogFragment() {

    private lateinit var resourceNameEditText: EditText
    private lateinit var resourceDescriptionEditText: EditText
    private lateinit var saveButton: Button
    private var resourceId: Int = 0
    private var resourceName: String = ""
    private var resourceDescription: String = ""
    private var categoryId: Int = 0 // Ajouter la variable pour category_id

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infler la vue du fragment
        val view = inflater.inflate(R.layout.dialog_edit_resource, container, false)

        // Récupérer les arguments passés au dialogue
        resourceId = arguments?.getInt("RESOURCE_ID") ?: 0
        resourceName = arguments?.getString("RESOURCE_NAME") ?: ""
        resourceDescription = arguments?.getString("RESOURCE_DESCRIPTION") ?: ""
        categoryId = arguments?.getInt("CATEGORIE_ID") ?: 0 // Récupérer le category_id

        // Initialisation des vues
        resourceNameEditText = view.findViewById(R.id.resourceNameEditText)
        resourceDescriptionEditText = view.findViewById(R.id.resourceDescriptionEditText)
        saveButton = view.findViewById(R.id.saveButton)

        // Remplir les champs avec les valeurs actuelles
        resourceNameEditText.setText(resourceName)
        resourceDescriptionEditText.setText(resourceDescription)

        // Définir le comportement du bouton "Enregistrer"
        saveButton.setOnClickListener {
            val updatedName = resourceNameEditText.text.toString()
            val updatedDescription = resourceDescriptionEditText.text.toString()

            // Appeler la méthode pour mettre à jour la ressource via Retrofit
            updateResource(resourceId, updatedName, updatedDescription, categoryId)
        }

        return view
    }

    // Mettre à jour la ressource avec l'ajout de category_id
    private fun updateResource(id: Int, name: String, description: String, categoryId: Int) {
        val updatedResource = Resource(id, name, description, categoryId) // Inclure category_id

        // Appel à l'API pour mettre à jour la ressource
        RetrofitInstance.api.updateResource(id, updatedResource).enqueue(object : Callback<Resource> {
            override fun onResponse(call: Call<Resource>, response: Response<Resource>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Ressource mise à jour", Toast.LENGTH_SHORT).show()
                    dismiss() // Fermer le dialogue après la mise à jour
                } else {
                    Toast.makeText(requireContext(), "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Resource>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
