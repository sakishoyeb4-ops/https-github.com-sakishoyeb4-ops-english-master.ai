
package com.shoyeb.englishmasterai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    var grammarResult by remember { mutableStateOf("") }
    var selectedSection by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    icon = {},
                    label = { Text("Home", maxLines = 1) }
                )

                NavigationBarItem(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    icon = {},
                    label = { Text("Grammar", maxLines = 1) }
                )

                NavigationBarItem(
                    selected = selectedSection == 2,
                    onClick = { selectedSection = 2 },
                    icon = {},
                    label = { Text("Vocabulary", maxLines = 1, fontSize = 11.sp) }
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {

            Text(
                text = "English Master AI",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Learn English • Build Vocabulary • Prepare for BCS",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (selectedSection) {

                0 -> {
                    Text(
                        "Sentence Analyzer",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Write an English sentence below:")

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = sentence,
                        onValueChange = {
                            sentence = it
                            result = ""
                            grammarResult = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Example: Education is the backbone of a nation.")
                        },
                        minLines = 3,
                        maxLines = 6
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (sentence.isBlank()) {
                                result = "Please enter a sentence first."
                                grammarResult = ""
                            } else {
                                val phrases = sentence
                                    .split(Regex("[,;:]"))
                                    .map { it.trim() }
                                    .filter { it.isNotEmpty() }

                                result = phrases.mapIndexed { index, phrase ->
                                    "${index + 1}. $phrase"
                                }.joinToString("\n\n")

                                grammarResult = checkBasicGrammar(sentence)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Analyze Sentence")
                    }

                    if (result.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(18.dp))

                        InfoCard(
                            title = "Phrase Breakdown",
                            description = result
                        )

                        InfoCard(
                            title = "Basic Grammar Check",
                            description = grammarResult
                        )

                        InfoCard(
                            title = "Bengali Translation",
                            description = "AI translation will be added after connecting the secure AI service."
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    InfoCard(
                        "Grammar Analysis",
                        "Learn sentence structure, tense and parts of speech."
                    )

                    InfoCard(
                        "Phrase Translation",
                        "Understand English phrases with Bengali meanings."
                    )

                    InfoCard(
                        "BCS English Practice",
                        "Practice vocabulary and grammar for competitive exams."
                    )

                    InfoCard(
                        "News Reader",
                        "Read international news and learn useful expressions."
                    )
                }

                1 -> {
                    Text(
                        "Grammar Learning",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoCard(
                        "Parts of Speech",
                        "Noun, Pronoun, Verb, Adjective, Adverb, Preposition, Conjunction and Interjection."
                    )

                    InfoCard(
                        "Sentence Structure",
                        "Subject + Verb + Object. Learn how English sentences are formed."
                    )

                    InfoCard(
                        "Tense",
                        "Present, Past and Future tense with their twelve common forms."
                    )

                    InfoCard(
                        "Subject-Verb Agreement",
                        "Learn how subjects and verbs agree in a sentence."
                    )

                    InfoCard(
                        "Right Form of Verbs",
                        "Practice the correct use of English verbs."
                    )
                }

                2 -> {
                    Text(
                        "Vocabulary Notebook",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Sample GRE Vocabulary",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val words = listOf(
                        "Abundant" to "প্রচুর",
                        "Benevolent" to "দয়ালু",
                        "Candid" to "অকপট",
                        "Diligent" to "পরিশ্রমী",
                        "Eloquent" to "বাগ্মী",
                        "Ephemeral" to "ক্ষণস্থায়ী",
                        "Meticulous" to "অতিসতর্ক",
                        "Obsolete" to "সেকেলে",
                        "Pragmatic" to "বাস্তববাদী",
                        "Resilient" to "ঘুরে দাঁড়াতে সক্ষম"
                    )

                    words.forEach { (word, meaning) ->
                        InfoCard(word, meaning)
                    }

                    Text(
                        "More vocabulary and personal word saving will be added in the next version.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "English Master AI • Version 1.0",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

fun checkBasicGrammar(text: String): String {
    val lower = text.lowercase()
    val suggestions = mutableListOf<String>()

    if ("this problems" in lower) {
        suggestions.add(
            "Suggestion: Use 'these problems' instead of 'this problems'."
        )
    }

    if ("amid this situation" in lower) {
        suggestions.add(
            "Suggestion: 'In this situation' sounds more natural here."
        )
    }

    if (suggestions.isEmpty()) {
        return "No matching common error was detected by the basic offline checker. This does not guarantee that the sentence is grammatically correct."
    }

    return suggestions.joinToString("\n\n")
}

@Composable
fun InfoCard(
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
