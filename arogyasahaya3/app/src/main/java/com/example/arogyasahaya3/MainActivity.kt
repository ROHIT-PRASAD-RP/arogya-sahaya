package com.example.arogyasahaya3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.arogyasahaya3.navigation.Destination
import com.example.arogyasahaya3.ui.auth.AuthViewModel
import com.example.arogyasahaya3.ui.auth.ForgotPasswordScreen
import com.example.arogyasahaya3.ui.auth.LoginScreen
import com.example.arogyasahaya3.ui.auth.RegisterScreen
import com.example.arogyasahaya3.ui.home.HomeScreen
import com.example.arogyasahaya3.ui.pillreminder.PillReminderScreen
import com.example.arogyasahaya3.ui.pillreminder.PillReminderViewModel
import com.example.arogyasahaya3.ui.profile.ProfileScreen
import com.example.arogyasahaya3.ui.profile.ProfileViewModel
import com.example.arogyasahaya3.ui.vitals.VitalsScreen
import com.example.arogyasahaya3.ui.vitals.VitalsViewModel
import com.example.arogyasahaya3.ui.sos.SOSScreen
import com.example.arogyasahaya3.ui.asha.AshaConnectScreen
import com.example.arogyasahaya3.ui.splash.SplashScreen
import com.example.arogyasahaya3.ui.theme.ArogyaSahaya3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArogyaSahaya3Theme {
                ArogyaSahayaApp()
            }
        }
    }
}

@Composable
fun ArogyaSahayaApp() {
    val authViewModel: AuthViewModel = viewModel()
    
    val backStack = rememberNavBackStack(Destination.Splash)
    val currentDestination = backStack.lastOrNull() as? Destination

    val showBottomNav = currentDestination !is Destination.Splash && 
                        currentDestination !is Destination.Login && 
                        currentDestination !is Destination.Register && 
                        currentDestination !is Destination.ForgotPassword

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                AppBottomBar(
                    currentDestination = currentDestination,
                    onNavigate = { dest ->
                        if (currentDestination != dest) {
                            backStack.clear()
                            backStack.add(Destination.Home) 
                            if (dest != Destination.Home) {
                                backStack.add(dest)
                            }
                        }
                    },
                    uid = authViewModel.currentUser?.uid ?: ""
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.fillMaxSize().padding(if (showBottomNav) padding else PaddingValues(0.dp)),
            entryDecorators = listOf(
                rememberViewModelStoreNavEntryDecorator()
            ),
            onBack = { 
                if (backStack.size > 1) {
                    backStack.removeAt(backStack.lastIndex)
                }
            },
            entryProvider = entryProvider {
                entry<Destination.Splash> {
                    SplashScreen(
                        onTimeout = {
                            val next = if (authViewModel.currentUser != null) {
                                Destination.Home
                            } else {
                                Destination.Login
                            }
                            backStack.clear()
                            backStack.add(next)
                        }
                    )
                }
                entry<Destination.Login> {
                    LoginScreen(
                        onLoginSuccess = { 
                            backStack.clear()
                            backStack.add(Destination.Home) 
                        },
                        onNavigateToRegister = { backStack.add(Destination.Register) },
                        onNavigateToForgotPassword = { backStack.add(Destination.ForgotPassword) },
                        viewModel = authViewModel
                    )
                }
                entry<Destination.Register> {
                    RegisterScreen(
                        onRegisterSuccess = { 
                            backStack.clear()
                            backStack.add(Destination.Home) 
                        },
                        onNavigateToLogin = { backStack.removeAt(backStack.lastIndex) },
                        viewModel = authViewModel
                    )
                }
                entry<Destination.ForgotPassword> {
                    ForgotPasswordScreen(
                        onNavigateBack = { backStack.removeAt(backStack.lastIndex) },
                        viewModel = authViewModel
                    )
                }
                entry<Destination.Home> {
                    val profileViewModel: ProfileViewModel = viewModel()
                    val vitalsViewModel: VitalsViewModel = viewModel()
                    val pillReminderViewModel: PillReminderViewModel = viewModel()
                    HomeScreen(
                        viewModel = authViewModel,
                        profileViewModel = profileViewModel,
                        vitalsViewModel = vitalsViewModel,
                        pillReminderViewModel = pillReminderViewModel,
                        onNavigateToPillReminder = { backStack.add(Destination.PillReminder) },
                        onNavigateToVitals = { backStack.add(Destination.Vitals) },
                        onNavigateToAshaConnect = { backStack.add(Destination.AshaConnect) },
                        onNavigateToProfile = { uid -> backStack.add(Destination.Profile(uid)) },
                        onNavigateToSOS = { backStack.add(Destination.SOS) }
                    )
                }
                entry<Destination.Profile> { key ->
                    val profileViewModel: ProfileViewModel = viewModel()
                    val pillReminderViewModel: PillReminderViewModel = viewModel()
                    ProfileScreen(
                        uid = key.uid,
                        onNavigateBack = { backStack.removeAt(backStack.lastIndex) },
                        onLogout = {
                            authViewModel.logout()
                            backStack.clear()
                            backStack.add(Destination.Login)
                        },
                        viewModel = profileViewModel,
                        pillReminderViewModel = pillReminderViewModel
                    )
                }
                entry<Destination.PillReminder> {
                    val pillReminderViewModel: PillReminderViewModel = viewModel()
                    PillReminderScreen(
                        onNavigateBack = { backStack.removeAt(backStack.lastIndex) },
                        viewModel = pillReminderViewModel
                    )
                }
                entry<Destination.Vitals> {
                    val vitalsViewModel: VitalsViewModel = viewModel()
                    val profileViewModel: ProfileViewModel = viewModel()
                    VitalsScreen(
                        onNavigateBack = { backStack.removeAt(backStack.lastIndex) },
                        viewModel = vitalsViewModel,
                        profileViewModel = profileViewModel,
                        uid = authViewModel.currentUser?.uid ?: ""
                    )
                }
                entry<Destination.SOS> {
                    val profileViewModel: ProfileViewModel = viewModel()
                    SOSScreen(
                        onNavigateBack = { backStack.removeAt(backStack.lastIndex) },
                        viewModel = profileViewModel,
                        uid = authViewModel.currentUser?.uid ?: ""
                    )
                }
                entry<Destination.AshaConnect> {
                    AshaConnectScreen(
                        onNavigateBack = { backStack.removeAt(backStack.lastIndex) }
                    )
                }
            }
        )
    }
}

@Composable
fun AppBottomBar(
    currentDestination: Destination?,
    onNavigate: (Destination) -> Unit,
    uid: String
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            BottomNavItem("Home", Icons.Default.Home, Destination.Home),
            BottomNavItem("Medicine", Icons.Default.Medication, Destination.PillReminder),
            BottomNavItem("Vitals", Icons.Default.MonitorHeart, Destination.Vitals),
            BottomNavItem("Profile", Icons.Default.Person, Destination.Profile(uid))
        )

        items.forEach { item ->
            NavigationBarItem(
                selected = when (item.destination) {
                    is Destination.Home -> currentDestination is Destination.Home
                    is Destination.PillReminder -> currentDestination is Destination.PillReminder
                    is Destination.Vitals -> currentDestination is Destination.Vitals
                    is Destination.AshaConnect -> currentDestination is Destination.AshaConnect
                    is Destination.Profile -> currentDestination is Destination.Profile
                    else -> false
                },
                onClick = { onNavigate(item.destination) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.outline,
                    unselectedTextColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val destination: Destination
)
