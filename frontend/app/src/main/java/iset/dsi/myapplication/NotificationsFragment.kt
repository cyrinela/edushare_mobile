package iset.dsi.myapplication

import ResourceApi
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotificationsFragment : Fragment() {

    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var notificationApi: NotificationApi
    private lateinit var notificationAdapter: NotificationAdapter
    private val notifications = mutableListOf<Notification>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_notifications, container, false)

        // Initialisation de Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.227.34:8100") // Remplacer par l'URL de votre API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        notificationApi = retrofit.create(NotificationApi::class.java)

        // Liaison RecyclerView
        notificationRecyclerView = rootView.findViewById(R.id.notificationRecyclerView)

        // Configuration RecyclerView
        notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Adapter pour les notifications
        notificationAdapter = NotificationAdapter(notifications)
        notificationRecyclerView.adapter = notificationAdapter

        // Charger les notifications pour l'utilisateur authentifié
        fetchNotifications()

        return rootView
    }

    // Charger les notifications pour l'utilisateur authentifié
    private fun fetchNotifications() {
        // Récupérer l'ID de l'utilisateur authentifié depuis SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("USER_ID", -1)

        if (userId != -1L) {
            val call = notificationApi.getNotificationsByUser(userId)
            call.enqueue(object : Callback<List<Notification>> {
                override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                    if (response.isSuccessful) {
                        notifications.clear()
                        notifications.addAll(response.body() ?: emptyList())
                        notificationAdapter.notifyDataSetChanged() // Mettre à jour l'adaptateur
                    }
                }

                override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Erreur de connexion : ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "Utilisateur non authentifié", Toast.LENGTH_SHORT).show()
        }
    }
}
