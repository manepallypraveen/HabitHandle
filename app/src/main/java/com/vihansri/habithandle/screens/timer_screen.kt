package com.vihansri.habithandle.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.vihansri.habithandle.helpers.formatTime
import com.vihansri.habithandle.model.TaskItem
import kotlinx.coroutines.delay

@Composable
fun TimerScreen(task: TaskItem, onFinished: () -> Unit, onExit: () -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity
    val view = LocalView.current

    var remaining by rememberSaveable { mutableLongStateOf(task.totalSeconds) }
    var finished by rememberSaveable { mutableStateOf(false) }

    // Block back press until the timer completes
    BackHandler(enabled = !finished) { /* ignored */ }

    // Landscape + fullscreen + keep screen on while this screen is visible
    DisposableEffect(Unit) {
        val window = activity.window
        val controller = WindowCompat.getInsetsController(window, view)

        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        onDispose {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            WindowCompat.setDecorFitsSystemWindows(window, true)
            controller.show(WindowInsetsCompat.Type.systemBars())
            // Let the user rotate freely again on the list screen
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // Countdown (stopwatch style) until time is complete
    LaunchedEffect(Unit) {
        while (remaining > 0) {
            delay(1000)
            remaining--
        }
        finished = true
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (finished) Color(0xFF1B5E20) else Color.Black),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            val text = formatTime(remaining)
            // Scale font so the time always fills the width
            val fontSize = (maxWidth.value / (text.length * 0.62f)).coerceAtMost(260f).sp

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = task.name,
                    color = Color.LightGray,
                    fontSize = 28.sp,
                    maxLines = 1
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    softWrap = false
                )
                if (finished) {
                    Spacer(Modifier.height(16.dp))
                    Text("Task completed 🎉", color = Color.White, fontSize = 28.sp)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onExit,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.White)
                    ) { Text("Done", color = Color.Black, fontSize = 20.sp) }
                }
            }
        }
    }
}
