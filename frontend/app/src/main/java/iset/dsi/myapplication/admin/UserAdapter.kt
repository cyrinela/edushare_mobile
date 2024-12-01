package iset.dsi.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import iset.dsi.myapplication.R

class UserAdapter(
    private val userList: List<User>,
    private val onDeleteUser: (Long) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.nameTextView.text = user.fullname
        holder.emailTextView.text = user.email
        holder.deleteIcon.setOnClickListener {
            onDeleteUser(user.id ?: -1)  // Assurez-vous que l'ID de l'utilisateur est non null
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        val emailTextView: TextView = itemView.findViewById(R.id.userEmailTextView)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteUserIcon)
    }
}
