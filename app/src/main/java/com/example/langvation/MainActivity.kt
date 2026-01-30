package com.example.langvation // Define o pacote do app

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

// Classe principal da aplicação, representa a primeira tela (Activity)
class MainActivity : ComponentActivity() {

    // Função que é chamada ao iniciar a Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lê dados salvos no dispositivo (idioma e frase atual)
        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val savedLang = prefs.getString("lang", "pt") ?: "pt" // idioma padrão: português
        val savedIndex = prefs.getInt("fraseIndex", 0) // índice padrão: primeira frase

        // Define o conteúdo da interface com Compose
        setContent {
            MaterialTheme { // Aplica o tema Material3
                FrasesMotivacionalApp(
                    context = this,
                    initialLanguage = savedLang,
                    initialIndex = savedIndex
                ) { newLang, newIndex ->

                    // Salva novo idioma e índice da frase quando forem alterados
                    prefs.edit()
                        .putString("lang", newLang)
                        .putInt("fraseIndex", newIndex)
                        .apply()
                    recreate() // Recria a Activity para aplicar mudanças
                }
            }
        }
    }
}

// Função de extensão para trocar o idioma do app dinamicamente
fun Context.wrapLocale(localeCode: String): Context {
    val locale = Locale(localeCode)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}

// Função composable que monta toda a interface da tela
@Composable
fun FrasesMotivacionalApp(
    context: Context,
    initialLanguage: String,
    initialIndex: Int,
    onChangeLanguage: (String, Int) -> Unit // função que será chamada ao mudar idioma ou frase
) {
    val frasesIds = listOf(R.string.frase1, R.string.frase2, R.string.frase3) // IDs das frases

    // Variável observável para idioma e índice da frase atual
    var idiomaAtual by remember { mutableStateOf(initialLanguage) }
    var fraseIndex by remember { mutableIntStateOf(initialIndex) }

    // Aplica o idioma atual ao contexto
    val localizedContext = context.wrapLocale(idiomaAtual)

    // Pega a frase e textos dos botões no idioma escolhido
    val fraseMostrada = localizedContext.getString(frasesIds[fraseIndex])
    val nextPhraseText = localizedContext.getString(R.string.next_phrase)
    val changeLanguageText = localizedContext.getString(R.string.change_language)

    // Layout da tela com elementos centralizados
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Card que exibe a frase atual
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp), // bordas arredondadas
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            // Exibe a frase centralizada dentro do card
            Box(
                modifier = Modifier.padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = fraseMostrada,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // espaço entre elementos

        // Botão para mostrar a próxima frase
        Button(onClick = {
            fraseIndex = (fraseIndex + 1) % frasesIds.size // avança ou reinicia a contagem
            onChangeLanguage(idiomaAtual, fraseIndex) // salva novo índice
        }) {
            Text(text = nextPhraseText)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para trocar idioma entre pt/en
        Button(onClick = {
            idiomaAtual = if (idiomaAtual == "pt") "en" else "pt" // alterna entre os dois idiomas
            onChangeLanguage(idiomaAtual, fraseIndex) // salva novo idioma
        }) {
            Text(text = if (idiomaAtual == "pt") "Inglês" else "Portuguese") // texto do botão muda junto
        }
    }
}
