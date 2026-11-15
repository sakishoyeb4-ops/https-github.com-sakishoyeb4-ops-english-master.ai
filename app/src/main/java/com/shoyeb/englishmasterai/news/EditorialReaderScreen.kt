
package com.shoyeb.englishmasterai.news

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.util.Xml
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

data class EditorialArticle(
    val title: String,
    val description: String,
    val link: String,
    val date: String,
    val source: String
) {
    val id: String get() = link.ifBlank { "$source|$title" }
}

private data class NewsFeed(
    val name: String,
    val url: String
)

private val BangladeshFeeds = listOf(
    NewsFeed("প্রথম আলো", "https://www.prothomalo.com/feed/"),
    NewsFeed("The Daily Star", "https://www.thedailystar.net/frontpage/rss.xml"),
    NewsFeed("যুগান্তর", "https://www.jugantor.com/feed/rss.xml"),
    NewsFeed("কালের কণ্ঠ", "https://www.kalerkantho.com/rss.xml"),
    NewsFeed("Jago News", "https://www.jagonews24.com/rss/rss.xml"),
    NewsFeed(
        "bdnews24",
        "https://bdnews24.com/?widgetName=rssfeed&widgetId=1150&getXmlFeed=true"
    ),
    NewsFeed(
        "Bangla Tribune",
        "https://www.banglatribune.com/feed"
    )
)

@Composable
fun EditorialReaderScreen() {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("editorial_reader", Context.MODE_PRIVATE)
    }

    val articles = remember { mutableStateListOf<EditorialArticle>() }
    val savedArticles = remember { mutableStateListOf<EditorialArticle>() }

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var showSaved by remember { mutableStateOf(false) }
    var editorialOnly by remember { mutableStateOf(true) }
    var selectedSource by remember { mutableStateOf("সব পত্রিকা") }

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }

    fun loadSaved() {
        savedArticles.clear()
        val raw = prefs.getString("saved_articles", "[]") ?: "[]"
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                savedArticles.add(
                    EditorialArticle(
                        title = obj.optString("title"),
                        description = obj.optString("description"),
                        link = obj.optString("link"),
                        date = obj.optString("date"),
                        source = obj.optString("source")
                    )
                )
            }
        } catch (_: Exception) {
            savedArticles.clear()
        }
    }

    fun saveArticle(article: EditorialArticle) {
        if (savedArticles.none { it.id == article.id }) {
            savedArticles.add(0, article)
            val array = JSONArray()
            savedArticles.forEach {
                array.put(
                    JSONObject().apply {
                        put("title", it.title)
                        put("description", it.description)
                        put("link", it.link)
                        put("date", it.date)
                        put("source", it.source)
                    }
                )
            }
            prefs.edit().putString("saved_articles", array.toString()).apply()
        }
    }

    fun removeArticle(article: EditorialArticle) {
        savedArticles.removeAll { it.id == article.id }
        val array = JSONArray()
        savedArticles.forEach {
            array.put(
                JSONObject().apply {
                    put("title", it.title)
                    put("description", it.description)
                    put("link", it.link)
                    put("date", it.date)
                    put("source", it.source)
                }
            )
        }
        prefs.edit().putString("saved_articles", array.toString()).apply()
    }

    fun refreshNews() {
        if (loading) return
        loading = true
        error = ""

        Thread {
            val result = mutableListOf<EditorialArticle>()
            val failures = mutableListOf<String>()

            BangladeshFeeds.forEach { feed ->
                try {
                    val connection = URL(feed.url).openConnection()
                        as HttpURLConnection
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000
                    connection.setRequestProperty(
                        "User-Agent",
                        "EnglishMasterAI/1.0 RSS Reader"
                    )
                    connection.setRequestProperty(
                        "Accept",
                        "application/rss+xml, application/xml, text/xml"
                    )

                    connection.inputStream.use { stream ->
                        val parser = Xml.newPullParser()
                        parser.setInput(stream, "UTF-8")

                        var event = parser.eventType
                        var inItem = false
                        var title = ""
                        var description = ""
                        var link = ""
                        var date = ""

                        while (event != XmlPullParser.END_DOCUMENT) {
                            if (event == XmlPullParser.START_TAG) {
                                when (parser.name.lowercase()) {
                                    "item", "entry" -> {
                                        inItem = true
                                        title = ""
                                        description = ""
                                        link = ""
                                        date = ""
                                    }

                                    "title" -> if (inItem) {
                                        title = parser.nextText()
                                    }

                                    "description", "summary", "content" ->
                                        if (inItem && description.isBlank()) {
                                            description = parser.nextText()
                                        }

                                    "link" -> if (inItem) {
                                        val href = parser.getAttributeValue(
                                            null,
                                            "href"
                                        )
                                        link = href ?: parser.nextText()
                                    }

                                    "pubdate", "published", "updated" ->
                                        if (inItem) {
                                            date = parser.nextText()
                                        }
                                }
                            }

                            if (
                                event == XmlPullParser.END_TAG &&
                                (parser.name.equals("item", true) ||
                                    parser.name.equals("entry", true))
                            ) {
                                if (title.isNotBlank()) {
                                    result.add(
                                        EditorialArticle(
                                            title = title.trim(),
                                            description = description
                                                .replace(Regex("<[^>]*>"), " ")
                                                .trim(),
                                            link = link.trim(),
                                            date = date.trim(),
                                            source = feed.name
                                        )
                                    )
                                }
                                inItem = false
                            }

                            event = parser.next()
                        }
                    }
                    connection.disconnect()
                } catch (_: Exception) {
                    failures.add(feed.name)
                }
            }

            android.os.Handler(
                android.os.Looper.getMainLooper()
            ).post {
                articles.clear()
                articles.addAll(result.distinctBy { it.id })
                loading = false

                if (result.isEmpty()) {
                    error = "খবর আনা যায়নি। ইন্টারনেট ও RSS ফিড পরীক্ষা করো।"
                } else if (failures.isNotEmpty()) {
                    error = "কিছু পত্রিকার ফিড পাওয়া যায়নি: " +
                        failures.joinToString(", ")
                }
            }
        }.start()
    }

    DisposableEffect(Unit) {
        loadSaved()

        val engine = TextToSpeech(context) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
            if (status == TextToSpeech.SUCCESS) {
                engineLanguage@ run {
                    val result = tts?.setLanguage(Locale("bn", "BD"))
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED
                    ) {
                        tts?.setLanguage(Locale.ENGLISH)
                    }
                }
            }
        }
        tts = engine

        onDispose {
            tts?.stop()
            tts?.shutdown()
            tts = null
        }
    }

    val editorialWords = listOf(
        "editorial", "opinion", "view", "comment",
        "সম্পাদকীয়", "সম্পাদকীয়", "মতামত", "উপসম্পাদকীয়",
        "উপসম্পাদকীয়", "বিশ্লেষণ"
    )

    val baseList = if (showSaved) savedArticles else articles

    val visibleArticles = baseList.filter { article ->
        val sourceMatches =
            selectedSource == "সব পত্রিকা" ||
                article.source == selectedSource

        val text = (article.title + " " + article.description)
            .lowercase()

        val editorialMatches =
            !editorialOnly ||
                editorialWords.any { text.contains(it.lowercase()) }

        sourceMatches && editorialMatches
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Text(
            "Bangladesh Editorial Reader",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            "বাংলাদেশের সংবাদপত্রের সম্পাদকীয় ও মতামত পড়ুন"
        )

        Spacer(Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    showSaved = false
                    refreshNews()
                },
                enabled = !loading
            ) {
                Text(if (loading) "খবর আসছে..." else "নতুন খবর আনুন")
            }

            OutlinedButton(
                onClick = {
                    showSaved = !showSaved
                }
            ) {
                Text(if (showSaved) "সব খবর" else "অফলাইন সেভ")
            }
        }

        Row {
            Checkbox(
                checked = editorialOnly,
                onCheckedChange = { editorialOnly = it }
            )
            Text(
                "শুধু সম্পাদকীয় / মতামত",
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        var expanded by remember { mutableStateOf(false) }

        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(selectedSource)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("সব পত্রিকা") },
                    onClick = {
                        selectedSource = "সব পত্রিকা"
                        expanded = false
                    }
                )

                BangladeshFeeds.forEach { feed ->
                    DropdownMenuItem(
                        text = { Text(feed.name) },
                        onClick = {
                            selectedSource = feed.name
                            expanded = false
                        }
                    )
                }
            }
        }

        if (error.isNotBlank()) {
            Text(
                error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Text(
            "পাওয়া গেছে: ${visibleArticles.size} টি",
            style = MaterialTheme.typography.labelLarge
        )

        HorizontalDivider()

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                visibleArticles,
                key = { it.id }
            ) { article ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Text(
                            article.source,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            article.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (article.date.isNotBlank()) {
                            Text(
                                article.date,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        if (article.description.isNotBlank()) {
                            Text(article.description)
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (ttsReady) {
                                        tts?.speak(
                                            article.title + ". " +
                                                article.description,
                                            TextToSpeech.QUEUE_FLUSH,
                                            null,
                                            article.id
                                        )
                                    }
                                }
                            ) {
                                Text("শুনুন")
                            }

                            OutlinedButton(
                                onClick = {
                                    saveArticle(article)
                                }
                            ) {
                                Text(
                                    if (savedArticles.any {
                                        it.id == article.id
                                    }) "সেভ হয়েছে" else "অফলাইনে সেভ"
                                )
                            }
                        }

                        Row {
                            TextButton(
                                onClick = {
                                    if (article.link.startsWith("https://") ||
                                        article.link.startsWith("http://")
                                    ) {
                                        context.startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(article.link)
                                            )
                                        )
                                    }
                                },
                                enabled = article.link.startsWith("http")
                            ) {
                                Text("মূল সংবাদ পড়ুন")
                            }

                            if (showSaved) {
                                TextButton(
                                    onClick = { removeArticle(article) }
                                ) {
                                    Text("মুছুন")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
