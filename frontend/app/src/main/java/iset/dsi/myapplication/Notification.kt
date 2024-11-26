package iset.dsi.myapplication
/*
data class Notification(
    val id: Long,
    val userId: Long,
    val message: String,
    val status: String,  // "READ" ou "UNREAD"
    val date: String
)
*/


data class Notification(
    val message: String,
    val status:String,
    val title: String,
    val description: String,
    val date: String
)
