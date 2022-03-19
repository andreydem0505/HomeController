package com.dementiev_a.homecontroller

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dementiev_a.homecontroller.requests.Requests
import com.dementiev_a.homecontroller.shared_preferences.SharedPreferencesService
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
                        Instruction()
                        KeyInput()
                    }
                }
            }
        }
    }

    @Composable
    private fun Instruction() {
        Column (
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = stringResource(R.string.login_activity_register_using_telegram))
            Link()
            Text(text = "2) ${stringResource(R.string.login_activity_press_start_and_get_key)}")
            Text(text = "3) ${stringResource(R.string.login_activity_insert_key_below)}")
        }
    }

    @Composable
    private fun Link() {
        val uriHandler = LocalUriHandler.current
        Row {
            Text(text = "1) ")
            ClickableText(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline,
                            fontSize = MaterialTheme.typography.body1.fontSize
                        )
                    ) {
                        append(stringResource(R.string.login_activity_go_to_bot))
                    }
                },
                onClick = {
                    uriHandler.openUri("https://t.me/HomeControllerTeleBot")
                }
            )
        }
    }

    @Composable
    private fun KeyInput() {
        var text by rememberSaveable { mutableStateOf("") }
        OutlinedTextField(
            value = text,
            modifier = Modifier.padding(0.dp, 20.dp),
            singleLine = true,
            onValueChange = {
                text = it
                if (text.length == 20) {
                    thread {
                        val response = Requests.verifyKey(text)
                        if (response.code() == 200) {
                            if (response.body()?.result!!) {
                                val sps = SharedPreferencesService(baseContext)
                                sps.saveKey(text)
                                finish()
                            }
                        } else {
                            Toast.makeText(applicationContext, getText(R.string.server_error), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            label = { Text(stringResource(R.string.login_activity_key)) }
        )
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}
