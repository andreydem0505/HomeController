package com.dementiev_a.homecontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dementiev_a.homecontroller.requests.Requests
import com.dementiev_a.homecontroller.ui.theme.HomeControllerTheme
import kotlin.concurrent.thread

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeControllerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Link()
                        KeyInput()
                    }
                }
            }
        }
    }

    @Composable
    private fun Link() {
        val uriHandler = LocalUriHandler.current
        ClickableText(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Зарегистрироваться через Telegram")
                }
            },
            onClick = {
                uriHandler.openUri("https://t.me/HomeControllerTeleBot")
            }
        )
    }

    @Composable
    private fun KeyInput() {
        var text by rememberSaveable { mutableStateOf("") }
        OutlinedTextField(
            value = text,
            modifier = Modifier.padding(0.dp, 20.dp),
            onValueChange = {
                text = it
                if (text.length == 20) {
                    thread {
                        if (Requests.verifyKey(text).body()?.result!!) {
                            println("---------OK----------")
                        }
                    }
                }},
            label = { Text("Ключ") }
        )
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}
