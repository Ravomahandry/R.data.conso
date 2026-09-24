package io.arvo.dataconso.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import io.arvo.dataconso.R
import io.arvo.dataconso.*
import io.arvo.dataconso.ui.dashboard.DashboardScreen
import io.arvo.dataconso.ui.onboarding.PermissionOnboardingScreen
import io.arvo.dataconso.ui.onboarding.PermissionViewModel
import io.arvo.dataconso.ui.settings.GhostModeSettingsScreen

sealed class Screen(val route: String, val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Dashboard : Screen("dashboard", R.string.nav_dashboard, Icons.Rounded.Dashboard)
    object Analysis : Screen("analysis", R.string.nav_analysis, Icons.Rounded.Analytics)
    object Quotas : Screen("quotas", R.string.nav_quotas, Icons.Rounded.SecurityUpdateGood)
    object Settings : Screen("settings", R.string.nav_settings, Icons.Rounded.Settings)
    object GhostMode : Screen("ghost_mode", R.string.ghost_mode_label, Icons.Rounded.Security)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val viewModel: MainViewModel = hiltViewModel()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = androidx.compose.ui.platform.LocalContext.current
    var vpnPromptShown by rememberSaveable { mutableStateOf(false) }
    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        vpnPromptShown = false
        viewModel.refreshNow()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshNow()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(
        settings.onboardingCompleted,
        settings.vpnEnabled,
        settings.appFirewallEnabled,
        settings.ghostModeEnabled
    ) {
        val protectionEnabled = settings.vpnEnabled ||
            settings.appFirewallEnabled ||
            settings.ghostModeEnabled
        val prepareIntent = android.net.VpnService.prepare(context)
        if (settings.onboardingCompleted && protectionEnabled &&
            prepareIntent != null && !vpnPromptShown
        ) {
            vpnPromptShown = true
            vpnPermissionLauncher.launch(prepareIntent)
        }
    }
    
    DataConsTheme(appTheme = currentTheme) {
        val colors = getDataConsColors(currentTheme)
        val navController = rememberNavController()

        // Rigueur Sommité : Gestion de la redirection profonde depuis l'overlay
        val activity = context as? androidx.appcompat.app.AppCompatActivity
        LaunchedEffect(Unit) {
            val target = activity?.intent?.getStringExtra("TARGET_SCREEN")
            if (target == "quotas") {
                navController.navigate(Screen.Quotas.route)
            }
        }
        
        if (!settings.onboardingCompleted) {
            OnboardingScreen { viewModel.completeOnboarding() }
        } else {
            val permVm: PermissionViewModel = hiltViewModel()
            val permState by permVm.uiState.collectAsStateWithLifecycle()
            
            if (permState.currentStep < 4) {
                PermissionOnboardingScreen(permVm) { permVm.checkPermissions() }
            } else {
                Scaffold(
                    topBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        if (currentDestination?.route != Screen.GhostMode.route) {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        stringResource(R.string.app_name),
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        fontSize = 20.sp
                                    )
                                },
                                actions = {
                                    IconButton(onClick = { viewModel.refreshNow() }) {
                                        Icon(Icons.Rounded.Refresh, null, tint = colors.primary)
                                    }
                                    ThemeSelector { viewModel.setTheme(it) }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = Color.Transparent,
                                    titleContentColor = colors.onSurface
                                )
                            )
                        }
                    },
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        
                        // On cache la barre si on est dans un écran de réglage profond
                        val showBottomBar = currentDestination?.route != Screen.GhostMode.route
                        
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = colors.surface.copy(alpha = 0.8f),
                                tonalElevation = 8.dp
                            ) {
                                val items = listOf(Screen.Dashboard, Screen.Analysis, Screen.Quotas, Screen.Settings)
                                items.forEach { screen ->
                                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                                    NavigationBarItem(
                                        icon = { Icon(screen.icon, contentDescription = null) },
                                        label = { Text(stringResource(screen.labelRes)) },
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = colors.primary,
                                            unselectedIconColor = colors.onSurface.copy(alpha = 0.4f),
                                            indicatorColor = colors.primary.copy(alpha = 0.1f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController = navController, 
                        startDestination = Screen.Dashboard.route,
                        modifier = Modifier.padding(padding)
                    ) {
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(colors = colors)
                        }
                        composable(Screen.Analysis.route) {
                            AnalysisScreen(
                                padding = padding,
                                uiState = uiState,
                                colors = colors,
                                isPremium = settings.isEliteActive,
                                mainVm = viewModel
                            )
                        }
                        composable(Screen.Quotas.route) {
                            QuotasScreen(
                                uiState = uiState,
                                colors = colors,
                                viewModel = viewModel,
                                onToggleVpn = { active, action -> 
                                    // Logique simple de toggle VPN (normalement via Permission check)
                                    action()
                                }
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                settings = settings,
                                viewModel = viewModel,
                                colors = colors,
                                uiState = uiState,
                                permState = permState,
                                onNavigateToGhostMode = { navController.navigate(Screen.GhostMode.route) },
                                onToggleVpn = { active, action -> action() }
                            )
                        }
                        composable(Screen.GhostMode.route) {
                            val currentProvider = DnsProvider.values().firstOrNull {
                                it.name == settings.dnsProvider
                            } ?: DnsProvider.ADGUARD
                            GhostModeSettingsScreen(
                                settings = settings,
                                currentProvider = currentProvider,
                                isGhostModeEnabled = settings.ghostModeEnabled,
                                onProviderSelected = { viewModel.setDnsProvider(it.name) },
                                onToggleGhostMode = { viewModel.updateGhostModeStatus(it) },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
