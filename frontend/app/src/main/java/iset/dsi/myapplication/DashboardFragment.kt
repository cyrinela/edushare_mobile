package iset.dsi.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : Fragment() {

    private lateinit var sharedCountTextView: TextView
    private lateinit var waitingCountTextView: TextView
    private lateinit var acceptedCountTextView: TextView
    private lateinit var refusedCountTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Initialize the TextViews
        sharedCountTextView = view.findViewById(R.id.sharedCount)
        waitingCountTextView = view.findViewById(R.id.waitingCount)
        acceptedCountTextView = view.findViewById(R.id.acceptedCount)
        refusedCountTextView = view.findViewById(R.id.refusedCount)

        // Fetch and display the resource count by status
        fetchResourceCount()

        return view
    }

    // Function to fetch the total resources shared by the user
    private fun fetchResourceCount() {
        // Get the user ID from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("USER_ID", -1)

        if (userId != -1L) {
            // Make the API call to fetch the resources shared by the user
            RetrofitInstance.api.getResourcesByUser(userId).enqueue(object : Callback<List<Resource>> {
                override fun onResponse(call: Call<List<Resource>>, response: Response<List<Resource>>) {
                    if (response.isSuccessful) {
                        // Get the resources list from the response
                        val resources = response.body() ?: emptyList()

                        // Count the resources by status
                        val waitingCount = resources.count { it.status == "en_attente" }
                        val acceptedCount = resources.count { it.status == "accepté" }
                        val refusedCount = resources.count { it.status == "refusé" }

                        // Update the TextViews with the counts
                        sharedCountTextView.text = resources.size.toString()  // Total shared resources
                        waitingCountTextView.text = waitingCount.toString()  // Resources in waiting
                        acceptedCountTextView.text = acceptedCount.toString()  // Accepted resources
                        refusedCountTextView.text = refusedCount.toString()  // Refused resources
                    } else {
                        Toast.makeText(requireContext(), "Erreur de chargement des ressources", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Resource>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "Utilisateur non authentifié", Toast.LENGTH_SHORT).show()
        }
    }
}
