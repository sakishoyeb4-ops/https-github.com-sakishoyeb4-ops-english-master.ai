
package com.shoyeb.englishmasterai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                EnglishMasterApp()
            }
        }
    }
}

@Composable
fun EnglishMasterApp() {
    var sentence by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var selectedSection by remember { mutableStateOf("Home") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            Text(
                text = "English Master AI",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Learn English • Build Vocabulary • Prepare for BCS",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Home", "Grammar", "Vocabulary").forEach { section ->
                    Button(
                        onClick = { selectedSection = section },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(section)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedSection) {
                "Home" -> {
                    Text(
                        "Sentence Analyzer",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Write an English sentence below:")

                    OutlinedTextField(
                        value = sentence,
                        onValueChange = {
                            sentence = it
                            result = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Example: Education is the backbone of a nation.")
                        },
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            result = if (sentence.isBlank()) {
                                "Please enter a sentence first."
                            } else {
                                val phrases = sentence
                                    .split(Regex("[,;:]"))
                                    .map { it.trim() }
                                    .filter { it.isNotEmpty() }

                                phrases.mapIndexed { index, phrase ->
                                    "${index + 1}. $phrase"
                                }.joinToString("\n\n")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Analyze Sentence")
                    }

                    if (result.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Phrase Breakdown",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(result)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    FeatureCard(
                        "Grammar Analysis",
                        "Understand sentence structure and grammar."
                    )

                    FeatureCard(
                        "Phrase Translation",
                        "Learn English phrases with Bengali meanings."
                    )

                    FeatureCard(
                        "BCS English Practice",
                        "Prepare with vocabulary and grammar exercises."
                    )

                    FeatureCard(
                        "News Reader",
                        "Read international news and learn useful expressions."
                    )
                }

                "Grammar" -> {
                    Text(
                        "Grammar Learning",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureCard(
                        "Parts of Speech",
                        "Noun, Pronoun, Verb, Adjective, Adverb and more."
                    )
                    FeatureCard(
                        "Sentence Structure",
                        "Subject, Verb, Object and sentence patterns."
                    )
                    FeatureCard(
                        "Tense",
                        "Learn the twelve English tense forms."
                    )
                }

                "Vocabulary" -> {
                    Text(
                        "Vocabulary Notebook",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val words = listOf(
                        "Abundant — প্রচুর",
                        "Benevolent — দয়ালু",
                        "Candid — অকপট",
                        "Diligent — পরিশ্রমী",
                        "Eloquent — বাগ্মী"
                    )

                    words.forEach { word ->
                        FeatureCard(word, "Learn this word and revise it regularly.")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "English Master AI • Version 1.0",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun FeatureCard(title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
