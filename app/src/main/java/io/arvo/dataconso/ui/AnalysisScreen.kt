package io.arvo.dataconso.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.arvo.dataconso.*
import io.arvo.dataconso.R
import io.arvo.dataconso.ui.dashboard.SourceSelector

@Composable
fun AnalysisScreen(
    padding: PaddingValues,
    uiState: MainViewModel.UiState,
    colors: DataConsColors,
    isPremium: Boolean,
    mainVm: MainViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(stringResource(R.string.tab_history), stringResource(R.string.tab_simulation))
    val currentSource by mainVm.currentSource.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(bottom = padding.calculateBottomPadding())) {
        Spacer(modifier = Modifier.height(16.dp))
        
        SourceSelector(
            selected = currentSource,
            onSelect = { mainVm.setSource(it) },
            colors = colors
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = colors.surface.copy(alpha = 0.3f),
            contentColor = colors.primary,
            modifier = Modifier.clip(RoundedCornerShape(16.dp)),
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = colors.primary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Medium) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (selectedTab) {
                0 -> {
                    val records = uiState.history.map { HistoryRecord(it.dateLabel, it.simId, it.bytes) }
                    HistoryScreen(
                        records = records, 
                        selectedSource = currentSource, 
                        colors = colors, 
                        isPremium = isPremium,
                        onExportPdf = { mainVm.exportUsageToPdf() }
                    )
                }
                1 -> SimulationScreen(mainVm)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
