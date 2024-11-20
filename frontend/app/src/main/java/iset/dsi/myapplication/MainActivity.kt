// MainActivity.kt
package iset.dsi.myapplication
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import iset.dsi.myapplication.HomeFragment
import iset.dsi.myapplication.R
import iset.dsi.myapplication.ResourcesFragment
import iset.dsi.myapplication.ProfileFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Charger le fragment d'accueil par défaut
        loadFragment(HomeFragment())

        // Gestion de la navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> loadFragment(HomeFragment())
                R.id.navigation_resources -> loadFragment(ResourcesFragment())
                R.id.navigation_profile -> loadFragment(ProfileFragment())
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }
}
