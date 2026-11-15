

package com.shoyeb.englishmasterai.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val items = listOf(
        "Home",
        "Grammar",
        "Vocabulary",
        "News",
        "Quiz"
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            navController.navigate(item) {
                                popUpTo("Home") {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {},
                        label = { Text(item) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "Home",
            modifier = Modifier.padding(padding)
        ) {
            composable("Home") {
                PlaceholderScreen("English Master AI", "Welcome to your learning app")
            }
            composable("Grammar") {
                PlaceholderScreen("Grammar", "Grammar lessons will appear here")
            }
            composable("Vocabulary") {
                PlaceholderScreen("Vocabulary", "GRE vocabulary will appear here")
            }
            composable("News") {
                PlaceholderScreen("News", "News and articles will appear here")
            }
            composable("Quiz") {
                PlaceholderScreen("BCS Quiz", "Practice questions will appear here")
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Text(message)
    }
}
