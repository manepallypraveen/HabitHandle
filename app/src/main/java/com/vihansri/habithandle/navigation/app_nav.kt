package com.vihansri.habithandle.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vihansri.habithandle.TaskViewModel
import com.vihansri.habithandle.screens.TaskListScreen
import com.vihansri.habithandle.screens.TimerScreen


@Composable
fun AppNav() {
    val navController = rememberNavController()
    val vm: TaskViewModel = viewModel()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            TaskListScreen(vm) { id -> navController.navigate("timer/$id") }
        }
        composable(
            route = "timer/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("taskId") ?: -1
            val task = vm.getTask(id)
            if (task != null) {
                TimerScreen(
                    task = task,
                    onFinished = { vm.markCompleted(id) },
                    onExit = { navController.popBackStack() }
                )
            }
        }
    }
}