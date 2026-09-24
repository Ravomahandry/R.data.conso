package io.arvo.dataconso.ui

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            "Bienvenue sur ARVO",
            "La première intelligence locale dédiée à la souveraineté de vos données mobiles.",
            Icons.Rounded.AutoAwesome,
            Color(0xFF2563EB)
        ),
        OnboardingPage(
            "Contrôle Absolu",
            "Définissez des quotas par application. ARVO intercepte et bloque le trafic dès que la limite est atteinte.",
            Icons.Rounded.Security,
            Color(0xFF10B981)
        ),
        OnboardingPage(
            "Mode Fantôme",
            "Économisez jusqu'à 15% de data en bloquant les traqueurs et publicités invisibles.",
            Icons.Rounded.VisibilityOff,
            Color(0xFFF43F5E)
        ),
        OnboardingPage(
            "Analyses Prédictives",
            "Anticipez vos besoins. ARVO vous alerte avant que votre forfait ne soit épuisé.",
            Icons.Rounded.Analytics,
            Color(0xFF8B5CF6)
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Indicators
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(pages.size) { i ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == i) 24.dp else 8.dp, 8.dp)
                                .clip(CircleShape)
                                .background(if (pagerState.currentPage == i) pages[i].color else Color.LightGray)
                        )
                    }
                }

                // Button
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onFinished()
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = pages[pagerState.currentPage].color)
                ) {
                    Text(if (pagerState.currentPage == pages.size - 1) "COMMENCER" else "SUIVANT")
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(160.dp),
                    shape = CircleShape,
                    color = page.color.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        modifier = Modifier.padding(40.dp),
                        tint = page.color
                    )
                }
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = page.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = page.description,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }
    }
}
