package iset.dsi.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationsFragment : Fragment() {

    private lateinit var notificationRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_notifications, container, false)

        // Liaison RecyclerView
        notificationRecyclerView = rootView.findViewById(R.id.notificationRecyclerView)

        // Configuration RecyclerView
        notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        notificationRecyclerView.adapter = NotificationAdapter(fetchNotifications())

        return rootView
    }

    private fun fetchNotifications(): List<Notification> {
        // Simuler des données récupérées
        return listOf(
            Notification("Nouvelle ressource", "Une nouvelle ressource a été ajoutée.", "20/11/2024"),
            Notification("Statut mis à jour", "Votre ressource a été acceptée.", "19/11/2024"),
            Notification("Nouvelle fonctionnalité", "Essayez notre nouvelle fonctionnalité de recherche avancée.", "18/11/2024")
        )
    }
}
