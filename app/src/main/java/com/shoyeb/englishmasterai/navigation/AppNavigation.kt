
package com.shoyeb.englishmasterai.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

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

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: "Home"

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item,
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
                        label = {
                            Text(item, maxLines = 1)
                        }
                    )
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "Home",
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            composable("Home") {
                PlaceholderScreen(
                    "English Master AI",
                    "Welcome to your English learning app."
                )
            }

            composable("Grammar") {
                PlaceholderScreen(
                    "Grammar Learning",
                    "Grammar lessons and exercises."
                )
            }

            composable("Vocabulary") {
                PlaceholderScreen(
                    "Vocabulary",
                    "GRE words and vocabulary notebook."
                )
            }

            composable("News") {
                PlaceholderScreen(
                    "News Reader",
                    "International news and article learning."
                )
            }

            composable("Quiz") {
                PlaceholderScreen(
                    "BCS English Practice",
                    "Practice English MCQ questions."
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(
    title: String,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
