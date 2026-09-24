package io.arvo.dataconso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.arvo.dataconso.ui.DataConsTheme
import io.arvo.dataconso.backend.CloudSyncManager
import io.arvo.dataconso.ui.MainApp
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var cloudSyncManager: CloudSyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Rigueur Sommité : Persistence manuelle de la langue pour éviter les NPE de AppCompat
        val prefs = getSharedPreferences("arvo_settings", MODE_PRIVATE)
        val savedLang = prefs.getString("selected_language", null)
        if (savedLang != null) {
            val appLocale = androidx.core.os.LocaleListCompat.forLanguageTags(savedLang)
            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)
        }

        enableEdgeToEdge()
        
        // Phase 4 : Synchronisation cloud à l'ouverture pour restaurer les économies
        cloudSyncManager.syncNow()

        setContent {
            // Utiliser le thème métier ARVO au lieu du thème dynamique par défaut
            DataConsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }

    companion object {
        val APP_LANGUAGES = listOf(
            Triple("fr", "🇫🇷", R.string.lang_fr),
            Triple("en", "🇺🇸", R.string.lang_en),
            Triple("es", "🇪🇸", R.string.lang_es),
            Triple("ar", "🇦🇪", R.string.lang_ar),
            Triple("de", "🇩🇪", R.string.lang_de),
            Triple("hi", "🇮🇳", R.string.lang_hi),
            Triple("it", "🇮🇹", R.string.lang_it),
            Triple("ja", "🇯🇵", R.string.lang_ja),
            Triple("ko", "🇰🇷", R.string.lang_ko),
            Triple("mg", "🇲🇬", R.string.lang_mg),
            Triple("nl", "🇳🇱", R.string.lang_nl),
            Triple("pl", "🇵🇱", R.string.lang_pl),
            Triple("pt", "🇵🇹", R.string.lang_pt),
            Triple("ru", "🇷🇺", R.string.lang_ru),
            Triple("tr", "🇹🇷", R.string.lang_tr),
            Triple("zh", "🇨🇳", R.string.lang_zh)
        )
    }
}
