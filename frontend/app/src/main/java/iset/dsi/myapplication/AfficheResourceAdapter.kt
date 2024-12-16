package iset.dsi.myapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
class AfficheResourceAdapter(private val resources: List<Resource>) :
    RecyclerView.Adapter<AfficheResourceAdapter.ResourceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resource, parent, false)
        return ResourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        val resource = resources[position]
        holder.bind(resource)
    }

    override fun getItemCount(): Int = resources.size

    inner class ResourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val resourceNameTextView: TextView = itemView.findViewById(R.id.categoryName)
        private val resourceDescriptionTextView: TextView = itemView.findViewById(R.id.categoryDescription)
        private val downloadButton: Button = itemView.findViewById(R.id.downloadButton)
        private val likeButton: ImageView = itemView.findViewById(R.id.heartIcon)
     //   private val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton) // Icône pour supprimer

        fun bind(resource: Resource) {
            resourceNameTextView.text = "Ressource name: ${resource.nom}"
            resourceDescriptionTextView.text = "Description: ${resource.description}"

            // Clic pour télécharger
            downloadButton.setOnClickListener {
                fetchResourceMetaDataAndDownload(resource.id)
            }

            // Clic pour ajouter aux favoris
            likeButton.setOnClickListener {
                val favoritesDatabaseHelper = FavoritesDatabaseHelper(itemView.context)
                favoritesDatabaseHelper.addFavorite(resource)
                Toast.makeText(itemView.context, "Ressource ajoutée aux favoris", Toast.LENGTH_SHORT).show()
            }

        }

        private fun fetchResourceMetaDataAndDownload(resourceId: Int?) {
            if (resourceId != null) {
                RetrofitInstance.api.getResourceMetaData(resourceId).enqueue(object : Callback<FileMetaData> {
                    override fun onResponse(call: Call<FileMetaData>, response: Response<FileMetaData>) {
                        if (response.isSuccessful && response.body() != null) {
                            val fileName = response.body()!!.fileName
                            downloadResource(resourceId, fileName)
                        } else {
                            Log.e("API_ERROR", "Erreur de récupération des métadonnées : ${response.code()} - ${response.message()}")
                            Toast.makeText(itemView.context, "Erreur de récupération des métadonnées.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<FileMetaData>, t: Throwable) {
                        Log.e("NETWORK_ERROR", "Erreur réseau : ${t.message}", t)
                        Toast.makeText(itemView.context, "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Log.e("INPUT_ERROR", "ID de ressource invalide")
                Toast.makeText(itemView.context, "ID de ressource invalide", Toast.LENGTH_SHORT).show()
            }
        }

        private fun downloadResource(resourceId: Int, fileName: String) {
            RetrofitInstance.api.downloadFile(resourceId).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful && response.body() != null) {
                        saveFileLocally(response.body()!!, fileName)
                    } else {
                        Log.e("DOWNLOAD_ERROR", "Erreur de téléchargement : ${response.code()} - ${response.message()}")
                        Toast.makeText(itemView.context, "Erreur de téléchargement : ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("NETWORK_ERROR", "Erreur réseau lors du téléchargement : ${t.message}", t)
                    Toast.makeText(itemView.context, "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        private fun saveFileLocally(body: ResponseBody, fileName: String) {
            try {
                val file = File(itemView.context.getExternalFilesDir(null), fileName)
                val outputStream = FileOutputStream(file)
                outputStream.use { stream ->
                    stream.write(body.bytes())
                }

                Log.i("FILE_SAVED", "Fichier téléchargé et enregistré : ${file.absolutePath}")

                val uri: Uri = FileProvider.getUriForFile(
                    itemView.context,
                    "${itemView.context.packageName}.provider",
                    file
                )

                openDownloadedFile(itemView.context, uri)

                Toast.makeText(itemView.context, "Fichier téléchargé : ${file.absolutePath}", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Log.e("FILE_SAVE_ERROR", "Erreur lors de l'enregistrement du fichier : ${e.message}", e)
                Toast.makeText(itemView.context, "Erreur lors de l'enregistrement du fichier : ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        private fun openDownloadedFile(context: Context, uri: Uri) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("OPEN_FILE_ERROR", "Erreur lors de l'ouverture du fichier : ${e.message}")
                Toast.makeText(context, "Aucune application compatible pour ouvrir ce fichier.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
