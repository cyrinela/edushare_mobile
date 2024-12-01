package iset.dsi.myapplication.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iset.dsi.myapplication.R
import iset.dsi.myapplication.RetrofitClient
import iset.dsi.myapplication.User
import iset.dsi.myapplication.UserAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminUsersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<User>()
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflater le layout pour ce fragment
        val rootView = inflater.inflate(R.layout.fragment_admin_users, container, false)

        recyclerView = rootView.findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(context)

        progressBar = rootView.findViewById(R.id.progressBar)

        // Charger les utilisateurs
        loadUsers()

        return rootView
    }

    private fun loadUsers() {
        val apiService = RetrofitClient.apiService
        val call = apiService.getAllUsers()

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val filteredUsers = it.filter { user -> user.role != "ADMIN" }
                        userList.clear()
                        userList.addAll(filteredUsers)
                        userAdapter = UserAdapter(userList, ::onDeleteUser)
                        recyclerView.adapter = userAdapter
                    }
                } else {
                    Toast.makeText(context, "Erreur de chargement des utilisateurs", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(context, "Erreur de connexion: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onDeleteUser(userId: Long) {
        // Affichage de l'AlertDialog de confirmation
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Voulez-vous vraiment supprimer cet utilisateur ?")
            .setPositiveButton("Oui") { _, _ ->
                // Si l'utilisateur confirme, démarrer la suppression
                deleteUser(userId)
            }
            .setNegativeButton("Non", null)
            .create()
            .show()
    }

    private fun deleteUser(userId: Long) {
        // Afficher le ProgressBar pour indiquer que la suppression est en cours
        progressBar.visibility = View.VISIBLE

        val apiService = RetrofitClient.apiService
        val call = apiService.deleteUser(userId)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // Cacher le ProgressBar une fois l'opération terminée
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    // L'utilisateur a été supprimé avec succès
                    Toast.makeText(context, "Utilisateur supprimé avec succès.", Toast.LENGTH_SHORT).show()
                    loadUsers() // Recharger la liste des utilisateurs après suppression
                } else {
                    Toast.makeText(context, "Erreur de suppression de l'utilisateur.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Cacher le ProgressBar si l'opération échoue
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Erreur de connexion: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
