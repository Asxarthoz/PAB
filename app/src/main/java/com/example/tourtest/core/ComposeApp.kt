package com.example.tourtest.core

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tourtest.ui.theme.TourizmeTheme
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tourtest.core.components.TourizmeBottombar
import com.example.tourtest.core.components.TourizmeDeleteDialog
import com.example.tourtest.feature.auth.manager.AuthManager
import com.example.tourtest.feature.auth.presentation.AuthScreen
import com.example.tourtest.feature.detaildestination.presentation.DestinationDetailScreen
import com.example.tourtest.feature.homepage.presentation.HomepageScreen
import com.example.tourtest.feature.profile.manager.PasswordManager
import com.example.tourtest.feature.profile.manager.ProfileManager
import com.example.tourtest.feature.profile.presentation.ChangePasswordScreen
import com.example.tourtest.feature.profile.presentation.EditProfileScreen
import com.example.tourtest.feature.profile.presentation.FullScreenImageScreen
import com.example.tourtest.feature.profile.presentation.ProfileScreen
import com.example.tourtest.feature.favorite.manager.FavoriteManager
import com.example.tourtest.feature.favorite.presentation.FavoriteScreen
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.homepage.viewmodel.HomepageViewModel
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.itinerary.presentation.ItineraryScreen
import com.example.tourtest.feature.itinerary.viewmodel.ItineraryViewModel
import com.example.tourtest.feature.profile.viewmodel.ProfileViewModel
import kotlin.collections.contains

@Composable
fun ComposeApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val application = context.applicationContext as Application
    val currentRoute = navBackStackEntry?.destination?.route

    val homepageViewModel: HomepageViewModel = viewModel {
        HomepageViewModel(
            getAllDestinations = { HomepageManager.readDestinationsFromData(context) }
        )
    }

    val profileViewModel: ProfileViewModel = viewModel {
        ProfileViewModel(
            profileManager = ProfileManager(context)
        )
    }

    val favoriteViewModel: FavoriteViewModel = viewModel {
        FavoriteViewModel(
            application = application,
            favoriteManager = FavoriteManager,
            homepageManager = HomepageManager
        )
    }

    val itineraryViewModel: ItineraryViewModel = viewModel {
        ItineraryViewModel(
            application = application,
            itineraryManager = ItineraryManager,
            homepageManager = HomepageManager
        )
    }

    TourizmeTheme {
        Scaffold(
            bottomBar = {
                val mainRoutes = listOf("home", "itinerary", "favorite", "profile")
                if (currentRoute in mainRoutes) {
                    TourizmeBottombar(navController, currentRoute)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "auth",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("auth") {
                    AuthScreen(onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    })
                }
                composable("home") {
                    HomepageScreen(
                        viewModel = homepageViewModel,
                        onNavigateToDetail = { id -> navController.navigate("detail/$id") }
                    )
                }

                composable("favorite") {
                    FavoriteScreen (
                        viewModel = favoriteViewModel,
                        onNavigateToDetail = { id -> navController.navigate("detail/$id") }
                    )
                }

                composable("itinerary") {
                    ItineraryScreen(
                        viewModel = itineraryViewModel,
                        onNavigateToDetail = { id -> navController.navigate("detail/$id") }
                    )
                }

                composable("profile") {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onLogout = {
                            navController.navigate("auth") {
                                popUpTo(0) // Bersihkan semua backstack
                            }
                        },
                        onNavigateToEditProfile = { navController.navigate("edit_profile") },
                        onNavigateToChangePassword = { navController.navigate("change_password") },
                        onNavigateToFullScreenImage = { navController.navigate("full_image") }
                    )
                }

                composable("detail/{destinationId}") { backStackEntry ->
                    val context = LocalContext.current
                    val destinationId = backStackEntry.arguments?.getString("destinationId") ?: ""
                    val currentUserId = AuthManager.getCurrentUserId() ?: ""

                    var favoriteListIds by remember { mutableStateOf(setOf<String>()) }
                    var itineraryListIds by remember { mutableStateOf(setOf<String>()) }

                    var showDeleteFavoriteDialog by remember { mutableStateOf(false) }
                    var selectedDestinationId by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(currentUserId) {
                        favoriteListIds = FavoriteManager.getAllFavorite(context)
                            .filter { it.userId == currentUserId }
                            .map { it.destinationId }.toSet()

                        itineraryListIds = ItineraryManager.getAllItinerary(context)
                            .filter { it.userId == currentUserId }
                            .map { it.destinationId }.toSet()
                    }

                    val isFavorite = favoriteListIds.contains(destinationId)
                    val isItineraried = itineraryListIds.contains(destinationId)

                    TourizmeDeleteDialog(
                        show = showDeleteFavoriteDialog,
                        message =  "Yakin hapus destinasi dari daftar favorit?",
                        onConfirm = {
                            selectedDestinationId?.let { id ->
                                FavoriteManager.removeDestination(context, currentUserId, id)
                                favoriteListIds = favoriteListIds - id
                                Toast.makeText(context, "Berhasil dihapus dari favorit", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            showDeleteFavoriteDialog = false
                            selectedDestinationId = null
                        },
                        onDismiss = {
                            showDeleteFavoriteDialog = false
                            selectedDestinationId = null
                        }
                    )

                    DestinationDetailScreen(
                        destinationId = destinationId,
                        isWishlisted = isFavorite,
                        isItineraried = isItineraried,
                        onWishListClick = {
                            if (currentUserId.isBlank()) {
                                Toast.makeText(context, "Gagal: User ID tidak ditemukan!", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                if (isFavorite) {
                                    selectedDestinationId = destinationId
                                    showDeleteFavoriteDialog = true
                                } else {
                                    val success = FavoriteManager.addDestination(context, currentUserId, destinationId)
                                    if (success) {
                                        favoriteListIds = favoriteListIds + destinationId
                                        Toast.makeText(context, "Berhasil disimpan ke daftar favorit", android.widget.Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Gagal disimpan ke daftar favorit", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        onItineraryClick = { selectedDate ->
                            if (currentUserId.isBlank()) {
                                Toast.makeText(context, "Gagal: User ID tidak ditemukan!", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                val success = ItineraryManager.addDestination(context, currentUserId, destinationId, selectedDate)
                                if (success) {
                                    itineraryListIds = itineraryListIds + destinationId
                                    Toast.makeText(context, "Berhasil disimpan ke daftar rencana!", android.widget.Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Gagal disimpan ke daftar rencana!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("full_image") { // Disamakan routenya
                    FullScreenImageScreen(
                        onBack = { navController.popBackStack() },
                        imageBitmap = null
                    )
                }

                composable("edit_profile") {
                    val context = LocalContext.current
                    val profileManager = ProfileManager(context)
                    EditProfileScreen(
                        onBack = { navController.popBackStack() },
                        profileManager = profileManager
                    )
                }
                composable("change_password") {
                    val context = LocalContext.current
                    val passwordManager = PasswordManager(context)
                    ChangePasswordScreen(
                        onBack = { navController.popBackStack() },
                        passwordManager = passwordManager
                    )
                }
                composable(("fullscrenn_image")) {
                    FullScreenImageScreen(
                        onBack = { navController.popBackStack() },
                        imageBitmap = null
                    )
                }
            }
        }
    }
}