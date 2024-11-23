package iset.dsi.myapplication
data class Resource(
    val id: Int?,               // ID unique (auto-généré ou null lors de la création)
    val description: String,
    val nom: String,
    val categorie_id: Int      // ID de la catégorie associée
)

data class FileMetaData(
    val id: Long,
    val fileName: String,
    val fileType: String,
    val fileSize: Long,
    val fileUrlId: String?
)
