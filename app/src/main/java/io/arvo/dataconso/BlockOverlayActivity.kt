package io.arvo.dataconso

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.arvo.dataconso.ui.DataConsTheme
import io.arvo.dataconso.ui.getDataConsColors

@AndroidEntryPoint
class BlockOverlayActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appName = intent.getStringExtra("appName") ?: "Application"
        val pkg = intent.getStringExtra("pkg") ?: ""
        
        setContent {
            val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            
            DataConsTheme(appTheme = currentTheme) {
                val colors = getDataConsColors(currentTheme)
                Surface(modifier = Modifier.fillMaxSize(), color = colors.surface) {
                    Box(modifier = Modifier.fillMaxSize().background(colors.gradient), contentAlignment = Alignment.Center) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Lock, null, modifier = Modifier.size(64.dp), tint = Color.Red)
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(stringResource(R.string.overlay_quota_title), fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.Red)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(R.string.overlay_quota_desc, appName), textAlign = TextAlign.Center, color = colors.onSurface)
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            // Bouton EXIT : Retour à l'accueil pour quitter l'app bloquée
                            Button(
                                onClick = { 
                                    val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                                        addCategory(Intent.CATEGORY_HOME)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    startActivity(homeIntent)
                                    finish() 
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                            ) {
                                Text(stringResource(R.string.overlay_back), fontWeight = FontWeight.Bold)
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Bouton EDIT (ASK FOR MORE) : Ramène vers l'édition manuelle du quota dans l'app principale
                            OutlinedButton(
                                onClick = { 
                                    val mainIntent = Intent(applicationContext, MainActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        putExtra("TARGET_SCREEN", "quotas")
                                        putExtra("EDIT_PKG", pkg)
                                    }
                                    startActivity(mainIntent)
                                    finish()
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, colors.primary)
                            ) {
                                Text(stringResource(R.string.edit_limit), color = colors.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
