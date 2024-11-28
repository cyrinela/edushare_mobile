package iset.dsi.myapplication

data class User(
    val id: Long? = null, // Nullable car non requis lors de l'inscription
    val fullname: String,
    val email: String,
    val password: String,
    val role: String
)

