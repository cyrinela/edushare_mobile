package iset.dsi.myapplication

data class UserWithoutRole(
    val fullname: String,
    val email: String,
    val password: String // Si vous voulez permettre de changer le mot de passe
)
