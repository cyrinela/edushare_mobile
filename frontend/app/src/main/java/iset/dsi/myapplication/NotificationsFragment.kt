package iset.dsi.myapplication

import ResourceApi
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_notifications, container, false)

        // Initialisation de Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.27.34:8100") // Remplace par l'URL de ton API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        notificationApi = retrofit.create(NotificationApi::class.java)

        // Liaison RecyclerView
        notificationRecyclerView = rootView.findViewById(R.id.notificationRecyclerView)

        // Configuration RecyclerView
        notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        notificationRecyclerView.adapter = NotificationAdapter(fetchNotifications())

        return rootView
    }

    private fun fetchNotifications(): List<Notification> {
        // Exemple de récupération de notifications via l'API (si une méthode API existe)
        val notifications = mutableListOf<Notification>()
        val call = notificationApi.getNotificationsByUser(1) // Utiliser un ID utilisateur valide
        call.enqueue(object : Callback<List<Notification>> {
            override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                if (response.isSuccessful) {
                    notifications.addAll(response.body() ?: emptyList())
                    // Actualiser l'adaptateur après avoir récupéré les notifications
                    notificationRecyclerView.adapter?.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                // Gérer l'erreur si la requête échoue
            }
        })
        return notifications
    }
}
