package iset.dsi.myapplication

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // Trouver le WebView par son ID
        val webView: WebView = rootView.findViewById(R.id.webView)

        // Activer JavaScript dans le WebView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true

        // Assurer que les liens s'ouvrent dans le WebView et pas dans le navigateur
        webView.webViewClient = WebViewClient()

        // Charger l'URL de la section "Introduction à la programmation" sur Udemy
        webView.loadUrl("https://www.udemy.com/courses/search/?q=introduction%20to%20programming")

        return rootView
    }
}
