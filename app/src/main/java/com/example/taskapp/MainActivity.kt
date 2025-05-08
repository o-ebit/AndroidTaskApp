package com.example.taskapp

import com.example.taskapp.ui.AppNav
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.taskapp.ui.DebugBackScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContent {
//            MaterialTheme {
//                DebugBackScreen {
//                    Log.d("BackTest", "Back button clicked")
//                    // or finish() if you want to exit
//                }
//            }
//        }
        setContent { MaterialTheme { AppNav() } }
    }
}