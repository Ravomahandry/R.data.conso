package io.arvo.dataconso.ui.onboarding

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.BatteryAlert
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.VpnLock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

import androidx.compose.ui.res.stringResource
import io.arvo.dataconso.R

private fun android.content.Context.openSettings(intent: Intent) {
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        startActivity(Intent(Settings.ACTION_SETTINGS))
    }
}

@Composable
fun PermissionOnboardingScreen(
    viewModel: PermissionViewModel,
    onComplete: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Sommité : Rafraîchir l'état des permissions dès que l'utilisateur revient de l'écran des réglages
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.onboarding_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.onboarding_subtitle),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(48.dp))

        when (state.currentStep) {
            1 -> PermissionCard(
                icon = Icons.Rounded.BarChart,
                title = stringResource(R.string.perm_usage_title),
                description = stringResource(R.string.perm_usage_desc),
                onClick = {
                    context.openSettings(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
            )
            2 -> PermissionCard(
                icon = Icons.Rounded.Layers,
                title = stringResource(R.string.perm_overlay_title),
                description = stringResource(R.string.perm_overlay_desc),
                onClick = {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                    context.openSettings(intent)
                }
            )
            3 -> PermissionCard(
                icon = Icons.Rounded.VpnLock, // Using VpnLock as a placeholder for Accessibility
                title = "Service d'Accessibilité",
                description = stringResource(R.string.accessibility_description),
                onClick = {
                    context.openSettings(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            )
            4 -> PermissionCard(
                icon = Icons.Rounded.BatteryAlert,
                title = stringResource(R.string.perm_battery_title),
                description = stringResource(R.string.perm_battery_desc),
                buttonText = stringResource(R.string.perm_battery_button),
                onClick = {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:${context.packageName}"))
                    context.openSettings(intent)
                }
            )
            5 -> {
                Text(stringResource(R.string.onboarding_ready), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 22.sp)
                Button(onClick = onComplete, modifier = Modifier.padding(top = 24.dp).fillMaxWidth().height(56.dp)) {
                    Text(stringResource(R.string.onboarding_start))
                }
            }
        }
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    buttonText: String = stringResource(R.string.perm_authorize),
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, fontWeight = FontWeight.Black, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(description, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(buttonText, fontWeight = FontWeight.Bold)
            }
        }
    }
}
