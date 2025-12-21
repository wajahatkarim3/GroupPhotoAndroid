package com.wajahatkarim.groupphotos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wajahatkarim.groupphotos.ui.screens.GroupPhotoUploadScreen
import com.wajahatkarim.groupphotos.ui.screens.HomeScreen
import com.wajahatkarim.groupphotos.ui.screens.PhotographerUploadScreen
import com.wajahatkarim.groupphotos.ui.screens.ProcessingScreen
import com.wajahatkarim.groupphotos.ui.screens.ResultScreen

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
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNewGroupPhotoClick = {
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
                onBackClick = {
                    navController.popBackStack()
                },
                onCloseClick = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
                onUploadAreaClick = {
                    // TODO: Open image picker
                    navController.navigate(Routes.PHOTOGRAPHER_UPLOAD)
                },
                onOpenCameraClick = {
                    // TODO: Open camera
                    navController.navigate(Routes.PHOTOGRAPHER_UPLOAD)
                },
                onSelectFromGalleryClick = {
                    // TODO: Open gallery
                    navController.navigate(Routes.PHOTOGRAPHER_UPLOAD)
                }
            )
        }

        composable(Routes.PHOTOGRAPHER_UPLOAD) {
            PhotographerUploadScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCloseClick = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
                onUploadAreaClick = {
                    // TODO: Open image picker
                },
                onGenerateClick = {
                    navController.navigate(Routes.PROCESSING)
                },
                onCameraClick = {
                    // TODO: Open camera
                },
                onGalleryClick = {
                    // TODO: Open gallery
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
