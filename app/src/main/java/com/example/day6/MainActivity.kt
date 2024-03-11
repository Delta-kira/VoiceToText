package com.example.day6

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.day6.ui.theme.Day6Theme

class MainActivity : ComponentActivity() {

    val voiceToTextParser by lazy {
        VoiceToTextParser(application)
    }

    @SuppressLint("UnusedContentLambdaTargetStateParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Day6Theme {
                var canRecord by remember {
                    mutableStateOf(false)
                }

                val recordAudioLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        canRecord = isGranted
                    }
                )

                LaunchedEffect(key1 = recordAudioLauncher) {
                    recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
                val state by voiceToTextParser.state.collectAsState()

                var isRecording by remember { mutableStateOf(false) } // New state variable

                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            isRecording = !isRecording
                            if (isRecording) {
                                voiceToTextParser.startListening()
                            } else {
                                voiceToTextParser.stopListening()
                            }
                        }) {
                            AnimatedContent(targetState = isRecording, label = "") { isRecording ->
                                if (isRecording) {
                                    Icon(
                                        imageVector = Icons.Rounded.Stop,
                                        contentDescription = null
                                    )
                                } else {
                                    Icon(imageVector = Icons.Rounded.Mic, contentDescription = null)
                                }
                            }
                        }
                    }
                ) { paddingValues ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedContent(
                            targetState = isRecording, // Use isRecording for animation
                            label = ""
                        ) { isRecording ->
                            if (isRecording) {
                                Text(text = "Speaking...") // Change text when recording
                            } else {
                                BasicTextField(
                                    value = state.spokenText.ifEmpty { "Click on mic to record audio" },
                                    onValueChange = { /* Handle value change if necessary */ },
                                    singleLine = false, // Allow multiple lines for better display
                                    modifier = Modifier.padding(8.dp) // Optional: Adjust padding
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


