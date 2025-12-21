package com.wajahatkarim.groupphotos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wajahatkarim.groupphotos.ui.screens.GroupPhotoUploadScreen
import com.wajahatkarim.groupphotos.ui.screens.HomeScreen
import com.wajahatkarim.groupphotos.ui.screens.PhotographerUploadScreen
import com.wajahatkarim.groupphotos.ui.screens.ProcessingScreen
import com.wajahatkarim.groupphotos.ui.screens.ResultScreen
import com.wajahatkarim.groupphotos.ui.viewmodel.PhotoViewModel

object Routes {
    const val HOME = "home"
    const val GROUP_PHOTO_UPLOAD = "group_photo_upload"
    const val PHOTOGRAPHER_UPLOAD = "photographer_upload"
    const val PROCESSING = "processing"
    const val RESULT = "result"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    // Shared ViewModel for photo state
    val photoViewModel: PhotoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNewGroupPhotoClick = {
                    photoViewModel.clearAll()
                    navController.navigate(Routes.GROUP_PHOTO_UPLOAD)
                },
                onViewPastCreationsClick = {
                    // TODO: Navigate to past creations
                },
                onSettingsClick = {
                    // TODO: Navigate to settings
                }
            )
        }

        composable(Routes.GROUP_PHOTO_UPLOAD) {
            GroupPhotoUploadScreen(
                selectedPhotoUri = photoViewModel.groupPhotoUri,
                onPhotoSelected = { uri ->
                    photoViewModel.setGroupPhoto(uri)
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onCloseClick = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
                onContinueClick = {
                    navController.navigate(Routes.PHOTOGRAPHER_UPLOAD)
                }
            )
        }

        composable(Routes.PHOTOGRAPHER_UPLOAD) {
            PhotographerUploadScreen(
                selectedPhotoUri = photoViewModel.photographerPhotoUri,
                onPhotoSelected = { uri ->
                    photoViewModel.setPhotographerPhoto(uri)
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onCloseClick = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
                onGenerateClick = {
                    navController.navigate(Routes.PROCESSING)
                }
            )
        }

        composable(Routes.PROCESSING) {
            ProcessingScreen(
                onCancelClick = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
                onProcessingComplete = {
                    navController.navigate(Routes.RESULT) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(Routes.RESULT) {
            ResultScreen(
                onCloseClick = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
                onDoneClick = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
                onShareClick = {
                    // TODO: Share functionality
                },
                onSavePhotoClick = {
                    // TODO: Save to gallery
                },
                onSideBySideClick = {
                    // TODO: Show side by side comparison
                }
            )
        }
    }
}
