package iset.dsi.myapplication.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import iset.dsi.myapplication.R

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Écouteur pour les éléments de navigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null

            when (item.itemId) {
                R.id.nav_home_admin -> selectedFragment = AdminHomeFragment()
                R.id.nav_resources_admin -> selectedFragment = AdminResourcesFragment()
                R.id.nav_profile_admin -> selectedFragment = AdminProfileFragment()
            }

            // Remplacer le fragment actuel par le sélectionné
            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }

            true
        }

        // Par défaut, charger le fragment d'accueil
        bottomNavigationView.selectedItemId = R.id.nav_home_admin
    }
}
