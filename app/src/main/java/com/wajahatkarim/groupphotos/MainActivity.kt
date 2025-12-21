package com.wajahatkarim.groupphotos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.wajahatkarim.groupphotos.ui.navigation.AppNavigation
import com.wajahatkarim.groupphotos.ui.theme.GroupPhotosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GroupPhotosTheme {
                AppNavigation()
            }
        }
    }
}