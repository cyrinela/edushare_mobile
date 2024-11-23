package iset.dsi.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import retrofit2.Response

class AddRessourceFragment : Fragment() {

    private lateinit var categorySpinner: Spinner
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var addButton: Button
    private lateinit var fileEditText: EditText
    private lateinit var selectFileButton: Button

    // Créer un scope de coroutine
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // Constante pour identifier le sélecteur de fichier
    private val FILE_PICKER_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_addressource, container, false)

        // Initialisation des vues
        categorySpinner = view.findViewById(R.id.categorySpinner)
        nameEditText = view.findViewById(R.id.nameEditText)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        addButton = view.findViewById(R.id.addButton)
        fileEditText = view.findViewById(R.id.fileEditText)
        selectFileButton = view.findViewById(R.id.selectFileButton)

        // Appel de l'API pour récupérer les catégories
        fetchCategories()

        // Définir un listener pour le bouton d'ajout
        addButton.setOnClickListener {
            addResource()
        }

        // Ouvrir le sélecteur de fichier lorsqu'on clique sur le bouton
        selectFileButton.setOnClickListener {
            openFileSelector()
        }

        return view
    }

    private fun fetchCategories() {
        // Appel à l'API Retrofit pour récupérer les catégories
        RetrofitInstance.api.getCategories().enqueue(object : retrofit2.Callback<List<Category>> {
            override fun onResponse(call: retrofit2.Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    val categories = response.body()
                    categories?.let {
                        // Créer un ArrayAdapter avec les catégories
                        val categoryNames = it.map { category -> category.nom }
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            categoryNames
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        categorySpinner.adapter = adapter
                    }
                } else {
                    Toast.makeText(requireContext(), "Erreur de récupération des catégories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Category>>, t: Throwable) {
                // Afficher un message d'erreur si l'appel échoue
                Toast.makeText(requireContext(), "Échec de la connexion au serveur", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addResource() {
        // Récupérer les données du formulaire
        val name = nameEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val categoryPosition = categorySpinner.selectedItemPosition
        val fileUri = fileEditText.text.toString()

        if (name.isEmpty() || description.isEmpty() || categoryPosition == 0 || fileUri.isEmpty()) {
            // Vérifier que toutes les informations sont saisies
            Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        // Créer l'objet Resource avec les données
        val categoryId = categoryPosition // L'ID réel de la catégorie devrait être ici, mappé selon la catégorie sélectionnée
        val resource = Resource(
            id = null,
            description = description,
            nom = name,
            categorie_id = categoryId
        )

        // Appel API pour ajouter la ressource à l'intérieur d'une coroutine
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.addResource(resource)
                }

                if (response.isSuccessful) {
                    // Afficher un message de succès
                    Toast.makeText(requireContext(), "Ressource ajoutée avec succès", Toast.LENGTH_SHORT).show()
                    // Réinitialiser le formulaire
                    nameEditText.text.clear()
                    descriptionEditText.text.clear()
                    categorySpinner.setSelection(0)
                    fileEditText.text.clear()
                } else {
                    // Afficher un message d'erreur
                    Toast.makeText(requireContext(), "Erreur lors de l'ajout de la ressource", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Gérer l'exception si l'appel échoue
                Toast.makeText(requireContext(), "Échec de la connexion au serveur", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Ouvrir le sélecteur de fichier
    private fun openFileSelector() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" // Ou spécifier un type particulier comme "application/pdf" ou "image/*"
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
    }

    // Gérer le résultat du sélecteur de fichier
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val fileUri: Uri? = data?.data
            fileUri?.let {
                fileEditText.setText(it.toString()) // Afficher l'URI du fichier dans l'EditText
            }
        }
    }

    // Nettoyage lorsque le fragment est détruit
    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel() // Annule toutes les coroutines lancées par ce fragment
    }
}
