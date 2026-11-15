
package com.shoyeb.englishmasterai.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shoyeb.englishmasterai.news.EditorialReaderScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val tabs = listOf(
        "Home",
        "Grammar",
        "Vocabulary",
        "News",
        "Quiz"
    )

    val entry by
        navController.currentBackStackEntryAsState()

    val route =
        entry?.destination?.route ?: "Home"

    Scaffold(

        bottomBar = {

            NavigationBar {

                tabs.forEach { tab ->

                    NavigationBarItem(

                        selected = route == tab,

                        onClick = {

                            navController.navigate(tab) {

                                popUpTo(
                                    navController.graph.startDestinationId
                                ) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        },

                        icon = {},

                        label = {
                            Text(tab)
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
                    "English learning and BCS preparation"
                )
            }

            composable("Grammar") {
                PlaceholderScreen(
                    "Grammar",
                    "Grammar lessons and exercises"
                )
            }

            composable("Vocabulary") {
                PlaceholderScreen(
                    "Vocabulary",
                    "GRE vocabulary and word notebook"
                )
            }

            composable("News") {
                EditorialReaderScreen()
            }

            composable("Quiz") {
                PlaceholderScreen(
                    "BCS English Quiz",
                    "Practice English MCQ questions"
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
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            title,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            message,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
