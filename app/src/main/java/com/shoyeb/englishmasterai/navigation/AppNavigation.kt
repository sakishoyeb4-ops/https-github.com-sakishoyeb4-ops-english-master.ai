
package com.shoyeb.englishmasterai.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*

private data class Word(
    val word: String,
    val meaning: String,
    val example: String
)

private data class Question(
    val prompt: String,
    val options: List<String>,
    val answer: Int,
    val explanation: String
)

private val vocabulary = listOf(
    Word("Abundant", "প্রচুর", "The region has abundant resources."),
    Word("Benevolent", "দয়ালু", "A benevolent person helps others."),
    Word("Candid", "অকপট", "She gave a candid opinion."),
    Word("Diligent", "পরিশ্রমী", "A diligent student studies daily."),
    Word("Eloquent", "বাগ্মী", "He delivered an eloquent speech."),
    Word("Frugal", "মিতব্যয়ী", "A frugal person avoids waste."),
    Word("Impartial", "নিরপেক্ষ", "A judge must be impartial."),
    Word("Meticulous", "অতি যত্নশীল", "She is meticulous about details."),
    Word("Resilient", "ঘুরে দাঁড়াতে সক্ষম", "Resilient people recover."),
    Word("Ubiquitous", "সর্বত্র বিরাজমান", "Smartphones are ubiquitous.")
)

private val questions = listOf(
    Question(
        "Choose the synonym of Abundant.",
        listOf("Scarce", "Plentiful", "Tiny", "Empty"),
        1,
        "Abundant means plentiful."
    ),
    Question(
        "Choose the antonym of Diligent.",
        listOf("Hardworking", "Careful", "Lazy", "Active"),
        2,
        "Lazy is the opposite of diligent."
    ),
    Question(
        "She ____ to school every day.",
        listOf("go", "goes", "going", "gone"),
        1,
        "A third-person singular subject takes goes."
    ),
    Question(
        "Choose the passive form: They built the bridge.",
        listOf(
            "The bridge built them.",
            "The bridge was built by them.",
            "The bridge is build.",
            "The bridge has building."
        ),
        1,
        "Past simple passive: was/were + past participle."
    )
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val tabs = listOf("Home", "Grammar", "Vocabulary", "News", "Quiz")

    val entry by navController.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: "Home"

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = route == tab,
                        onClick = {
                            navController.navigate(tab) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {},
                        label = { Text(tab) }
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
            composable("Home") { HomeScreen() }
            composable("Grammar") { GrammarScreen() }
            composable("Vocabulary") { VocabularyScreen() }
            composable("News") { NewsScreen() }
            composable("Quiz") { QuizScreen() }
        }
    }
}

@Composable
private fun Page(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(subtitle)
        content()
    }
}

@Composable
private fun HomeScreen() {
    var sentence by rememberSaveable { mutableStateOf("") }
    var analysed by rememberSaveable { mutableStateOf(false) }

    Page(
        "English Master AI",
        "English learning and BCS preparation"
    ) {
        Text(
            "Sentence Analyzer",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = sentence,
            onValueChange = {
                sentence = it
                analysed = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Write an English sentence") },
            minLines = 3
        )

        Button(
            onClick = { analysed = sentence.isNotBlank() },
            enabled = sentence.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Analyze sentence")
        }

        if (analysed) {
            Card {
                Column(
                    Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Analysis",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text("Sentence: $sentence")

                    Text(
                        "Word count: ${
                            sentence.trim().split(Regex("\\s+")).size
                        }"
                    )

                    Text("Possible tense: ${detectTense(sentence)}")

                    Text(
                        "Phrase breakdown is not yet AI-powered. " +
                            "Full phrase translation will be added with the analyzer engine."
                    )
                }
            }
        }

        Text(
            "Learning sections",
            style = MaterialTheme.typography.titleLarge
        )
        Text("Grammar • Vocabulary • News • BCS English Quiz")
    }
}

private fun detectTense(text: String): String {
    val s = text.lowercase()

    return when {
        Regex("\\b(am|is|are)\\s+\\w+ing\\b").containsMatchIn(s) ->
            "Present Continuous (সম্ভাব্য)"

        Regex("\\b(was|were)\\s+\\w+ing\\b").containsMatchIn(s) ->
            "Past Continuous (সম্ভাব্য)"

        Regex("\\b(has|have)\\s+\\w+ed\\b").containsMatchIn(s) ->
            "Present Perfect (সম্ভাব্য)"

        Regex("\\b(will|shall)\\b").containsMatchIn(s) ->
            "Future (সম্ভাব্য)"

        else -> "Simple Present / Context dependent"
    }
}

@Composable
private fun GrammarScreen() {
    val lessons = listOf(
        "Parts of Speech" to
            "Noun names a person, place or thing. Verb expresses an action. Adjective describes a noun.",

        "Subject Verb Agreement" to
            "He works. They work. A singular third-person subject generally takes s or es.",

        "Tense" to
            "Present, Past and Future describe time. Each has different forms.",

        "Voice" to
            "Active: They built the bridge. Passive: The bridge was built by them.",

        "Articles" to
            "Use a before a consonant sound, an before a vowel sound and the for a specific noun."
    )

    Page("Grammar", "Grammar lessons for BCS English") {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(lessons) { lesson ->
                Card {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            lesson.first,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(lesson.second)
                    }
                }
            }
        }
    }
}

@Composable
private fun VocabularyScreen() {
    var search by rememberSaveable { mutableStateOf("") }
    var saved by rememberSaveable {
        mutableStateOf(setOf<String>())
    }

    val shown = vocabulary.filter {
        it.word.contains(search, true) ||
            it.meaning.contains(search, true)
    }

    Page("Vocabulary", "GRE-style starter vocabulary") {
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search vocabulary") }
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(shown) { word ->
                Card {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            word.word,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(word.meaning)
                        Text("Example: ${word.example}")

                        TextButton(
                            onClick = {
                                saved = if (word.word in saved) {
                                    saved - word.word
                                } else {
                                    saved + word.word
                                }
                            }
                        ) {
                            Text(
                                if (word.word in saved) {
                                    "Saved"
                                } else {
                                    "Save word"
                                }
                            )
                        }
                    }
                }
            }
        }

        Text("Saved words: ${saved.size}")
    }
}

@Composable
private fun NewsScreen() {
    val context = LocalContext.current

    val sources = listOf(
        Triple(
            "BBC News",
            "International news",
            "https://www.bbc.com/news"
        ),
        Triple(
            "The Daily Star",
            "Bangladesh news",
            "https://www.thedailystar.net/"
        ),
        Triple(
            "World Bank",
            "Economy and development",
            "https://www.worldbank.org/en/news"
        ),
        Triple(
            "United Nations",
            "Global affairs",
            "https://news.un.org/"
        ),
        Triple(
            "WHO",
            "Health news",
            "https://www.who.int/news"
        )
    )

    Page("News Reader", "Trusted news and learning sources") {
        sources.forEach { source ->
            Card {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        source.first,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(source.second)

                    TextButton(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(source.third)
                            )
                            context.startActivity(intent)
                        }
                    ) {
                        Text("Open website")
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizScreen() {
    var index by rememberSaveable { mutableIntStateOf(0) }
    var selected by rememberSaveable { mutableIntStateOf(-1) }
    var score by rememberSaveable { mutableIntStateOf(0) }

    val q = questions[index]

    Page(
        "BCS English Quiz",
        "Practice question ${index + 1} of ${questions.size}"
    ) {
        LinearProgressIndicator(
            progress = (index + 1).toFloat() / questions.size,
            modifier = Modifier.fillMaxWidth()
        )

        Card {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    q.prompt,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                q.options.forEachIndexed { i, option ->
                    OutlinedButton(
                        onClick = {
                            if (selected == -1) {
                                selected = i
                                if (i == q.answer) score++
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selected == -1
                    ) {
                        Text("${'A' + i}. $option")
                    }
                }

                if (selected != -1) {
                    Text(
                        if (selected == q.answer) {
                            "Correct!"
                        } else {
                            "Correct answer: ${q.options[q.answer]}"
                        },
                        fontWeight = FontWeight.Bold
                    )

                    Text(q.explanation)
                }
            }
        }

        if (index < questions.lastIndex) {
            Button(
                onClick = {
                    index++
                    selected = -1
                },
                enabled = selected != -1,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Next question")
            }
        } else {
            Text(
                "Score: $score / ${questions.size}",
                style = MaterialTheme.typography.titleLarge
            )

            Button(
                onClick = {
                    index = 0
                    selected = -1
                    score = 0
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Restart quiz")
            }
        }
    }
}
