 package com.j.antiojo.currencyapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.j.antiojo.currencyapp.di.initializeKoin
import com.j.antiojo.currencyapp.presentation.screen.HomeScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

 @Composable
@Preview
fun App() {
    initializeKoin()

     MaterialTheme {
         Navigator(HomeScreen())
     }
}