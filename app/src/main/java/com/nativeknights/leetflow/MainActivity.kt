package com.nativeknights.leetflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.nativeknights.leetflow.data.local.SecureStorageManager
import com.nativeknights.leetflow.ui.navigations.NavigationGraph
import com.nativeknights.leetflow.ui.theme.LeetFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LeetFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val storageManager = remember { SecureStorageManager(applicationContext) }
                    val hasApiKey = remember { storageManager.hasApiKey() }

                    NavigationGraph(
                        navController = navController,
                        hasApiKey = hasApiKey
                    )
                }
            }
        }
    }
}