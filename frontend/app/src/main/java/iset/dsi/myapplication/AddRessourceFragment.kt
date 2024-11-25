package iset.dsi.myapplication
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AddRessourceFragment : Fragment() {
    private lateinit var fileEditText: EditText
    private lateinit var selectFileButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var addButton: Button

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val FILE_PICKER_REQUEST_CODE = 1001
    private var selectedFileUri: Uri? = null
    private var categoryMap: Map<String, Int> = emptyMap()

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

        // Charger les catégories depuis l'API
        fetchCategories()

        // Ajouter des écouteurs pour les boutons
        addButton.setOnClickListener { addResource() }
        selectFileButton.setOnClickListener { openFileSelector() }

        return view
    }

    private fun fetchCategories() {
        RetrofitInstance.api.getCategories().enqueue(object : retrofit2.Callback<List<Category>> {
            override fun onResponse(
                call: retrofit2.Call<List<Category>>,
                response: Response<List<Category>>
            ) {
                if (response.isSuccessful) {
                    val categories = response.body()
                    categories?.let {
                        categoryMap = it.associate { category -> category.nom to category.id }

                        val categoryNames = mutableListOf("Sélectionnez une catégorie")
                        categoryNames.addAll(categoryMap.keys)

                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            categoryNames
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        categorySpinner.adapter = adapter
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Erreur lors du chargement des catégories",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Category>>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Échec de la connexion au serveur",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun addResource() {
        val name = nameEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val selectedCategory = categorySpinner.selectedItem.toString()
        val fileUri = selectedFileUri

        if (name.isEmpty() || description.isEmpty() || selectedCategory == "Sélectionnez une catégorie" || fileUri == null) {
            Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        val categoryId = categoryMap[selectedCategory] ?: return
        val file = createTemporaryFileFromUri(requireContext(), fileUri)

        val resourceJson = """
            {
                "nom": "$name",
                "description": "$description",
                "categorie": {"id": $categoryId}
            }
        """.trimIndent().toRequestBody("application/json".toMediaTypeOrNull())

        val filePart = MultipartBody.Part.createFormData(
            "file",
            file.name,
            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.addResource(resourceJson, filePart)
                }

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Ressource ajoutée avec succès", Toast.LENGTH_SHORT).show()
                    resetForm()
                } else {
                    Toast.makeText(requireContext(), "Erreur lors de l'ajout de la ressource", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Échec de la connexion au serveur", Toast.LENGTH_SHORT).show()
                Log.e("AddResource", "Error adding resource", e)
            }
        }
    }

    private fun openFileSelector() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedFileUri = data?.data
            selectedFileUri?.let {
                fileEditText.setText(it.path)
            }
        }
    }

    private fun createTemporaryFileFromUri(context: Context, uri: Uri): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, "tempFile")
        inputStream.use { input ->
            FileOutputStream(tempFile).use { output ->
                input?.copyTo(output)
            }
        }
        return tempFile
    }

    private fun resetForm() {
        nameEditText.text.clear()
        descriptionEditText.text.clear()
        categorySpinner.setSelection(0)
        fileEditText.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel()
    }
}
