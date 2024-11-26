package iset.dsi.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EditResourceDialogFragment : DialogFragment() {

    interface OnResourceUpdatedListener {
        fun onResourceUpdated()
    }

    private var resourceUpdatedListener: OnResourceUpdatedListener? = null

    fun setOnResourceUpdatedListener(listener: OnResourceUpdatedListener) {
        resourceUpdatedListener = listener
    }

    private lateinit var resourceNameEditText: EditText
    private lateinit var resourceDescriptionEditText: EditText
    private lateinit var saveButton: Button

    private var resourceId: Int = 0
    private var resourceName: String = ""
    private var resourceDescription: String = ""
    private var categoryId: Int = 0
    private var resourceStatus: String = "En attente" // Valeur par défaut

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_edit_resource, container, false)

        // Récupération des données passées dans le Bundle
        resourceId = arguments?.getInt("RESOURCE_ID") ?: 0
        resourceName = arguments?.getString("RESOURCE_NAME") ?: ""
        resourceDescription = arguments?.getString("RESOURCE_DESCRIPTION") ?: ""
        categoryId = arguments?.getInt("CATEGORIE_ID") ?: 0
        resourceStatus = arguments?.getString("RESOURCE_STATUS") ?: "En attente" // Statut récupéré

        // Initialisation des vues
        resourceNameEditText = view.findViewById(R.id.resourceNameEditText)
        resourceDescriptionEditText = view.findViewById(R.id.resourceDescriptionEditText)
        saveButton = view.findViewById(R.id.saveButton)

        // Remplir les champs avec les données existantes
        resourceNameEditText.setText(resourceName)
        resourceDescriptionEditText.setText(resourceDescription)

        // Configurer le bouton "Enregistrer"
        saveButton.setOnClickListener {
            val updatedName = resourceNameEditText.text.toString()
            val updatedDescription = resourceDescriptionEditText.text.toString()

            // Passez le statut récupéré tel quel, sans modification
            updateResource(resourceId, updatedName, updatedDescription, categoryId, resourceStatus)
        }

        return view
    }

    private fun updateResource(id: Int, name: String, description: String, categoryId: Int, status: String) {
        val updatedResource = Resource(id, name, description, categoryId, status)

        RetrofitInstance.api.updateResource(id, updatedResource).enqueue(object : Callback<Resource> {
            override fun onResponse(call: Call<Resource>, response: Response<Resource>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Ressource mise à jour avec succès", Toast.LENGTH_SHORT).show()
                    resourceUpdatedListener?.onResourceUpdated() // Notifier l'appelant que la ressource a été mise à jour
                    dismiss() // Fermer la boîte de dialogue
                } else {
                    Toast.makeText(requireContext(), "Erreur lors de la mise à jour de la ressource", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Resource>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
