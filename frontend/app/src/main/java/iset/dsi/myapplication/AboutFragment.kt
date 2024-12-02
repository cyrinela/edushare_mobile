package iset.dsi.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment

class AboutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        // Référencer l'icône de partage
        val shareIcon: ImageView = view.findViewById(R.id.share_icon)

        // Ajouter un OnClickListener à l'icône
        shareIcon.setOnClickListener {
            shareContent()
        }

        return view
    }

    private fun shareContent() {
        // Création d'un Intent pour le partage
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain" // Type MIME
            putExtra(Intent.EXTRA_SUBJECT, "EduShare")
            putExtra(Intent.EXTRA_TEXT, "Découvrez EduShare ! Une plateforme pour partager des ressources éducatives entre étudiants et enseignants.")
        }

        // Lancer le sélecteur d'applications
        val chooser = Intent.createChooser(shareIntent, "Partager via")
        startActivity(chooser)
    }
}
