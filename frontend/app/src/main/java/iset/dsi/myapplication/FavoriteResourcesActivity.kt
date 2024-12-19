package iset.dsi.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteResourcesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var favoritesAdapter: AfficheResourceAdapter
    private lateinit var dbHelper: FavoritesDatabaseHelper
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_resources)

        // Initialisation du DrawerLayout et du NavigationView
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView =
            findViewById(R.id.sidebar_nav)  // Utilisation du bon ID pour NavigationView

        // Configuration de la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configuration de l'ActionBarDrawerToggle pour lier la toolbar au DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Configuration du RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFavorites)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialisation de la base de données et récupération des ressources favorites
        dbHelper = FavoritesDatabaseHelper(this)
        val favoriteResources = dbHelper.getAllFavorites()

        // Configuration de l'adaptateur du RecyclerView
        favoritesAdapter = AfficheResourceAdapter(favoriteResources)
        recyclerView.adapter = favoritesAdapter

        // Configuration du menu de navigation (Drawer)


    }

}
