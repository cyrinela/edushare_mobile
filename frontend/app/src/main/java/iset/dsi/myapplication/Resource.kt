package iset.dsi.myapplication

data class Resource(
    val id: Int,
    val title: String,
    val description: String,
    val status: String // "Accepté", "En attente", "Refusé"
)
