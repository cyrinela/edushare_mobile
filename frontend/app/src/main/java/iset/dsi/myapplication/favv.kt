package iset.dsi.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView

class favv : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var favoritesAdapter: AfficheResourceAdapter
    private lateinit var dbHelper: FavoritesDatabaseHelper
    private lateinit var navigationView: NavigationView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_favorite_resources, container, false)

        // Configuration du RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewFavorites)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialisation de la base de données et récupération des ressources favorites
        dbHelper = FavoritesDatabaseHelper(requireContext())
        val favoriteResources = dbHelper.getAllFavorites()

        // Configuration de l'adaptateur du RecyclerView
        favoritesAdapter = AfficheResourceAdapter(favoriteResources)
        recyclerView.adapter = favoritesAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuration de la Toolbar (si applicable)
        val toolbar: Toolbar? = view.findViewById(R.id.toolbar)
        toolbar?.let {
            (requireActivity() as AppCompatActivity).setSupportActionBar(it)
        }

        // Configuration du NavigationView (si applicable)
        navigationView = view.findViewById(R.id.sidebar_nav)
        // Configure the navigation actions here if necessary
    }
}
