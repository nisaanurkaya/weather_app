package com.kampplus.hava.ui

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kampplus.hava.model.City
import com.kampplus.hava.ui.city.CityRoute
import com.kampplus.hava.ui.forecast.ForecastRoute
import com.kampplus.hava.ui.forecast.ForecastViewModel
import com.kampplus.hava.ui.settings.AppThemeMode
import com.kampplus.hava.ui.settings.SettingsScreen

@Composable
fun HavaNavHost(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {

    var isCelsius by remember { mutableStateOf(true) }
    var currentTheme by remember { mutableStateOf(AppThemeMode.LIGHT) }
    var isTurkish by remember { mutableStateOf(true) }

    // SİHİRLİ ÇÖZÜM: Favori şehirleri en üste taşıdık. Tema değişse bile silinmeyecek!
    val favoriteCities = remember { mutableStateListOf<City>() }

    NavHost(
        navController = navController,
        startDestination = CITY_ROUTE,
        modifier = modifier
    ) {
        composable(CITY_ROUTE) {
            CityRoute(
                isTurkish = isTurkish,
                currentTheme = currentTheme,
                favoriteCities = favoriteCities, // Global favorileri yolluyoruz
                onToggleFavorite = { city ->
                    val existing = favoriteCities.find { it.id == city.id }
                    if (existing != null) favoriteCities.remove(existing)
                    else favoriteCities.add(city)
                },
                onCityClick = { city -> navController.navigate(forecastRoute(city)) },
                onSettingsClick = { navController.navigate(SETTINGS_ROUTE) }
            )
        }

        composable(
            route = FORECAST_ROUTE,
            arguments = listOf(
                navArgument(ForecastViewModel.ARG_NAME) { type = NavType.StringType; defaultValue = "" },
                navArgument(ForecastViewModel.ARG_SUBTITLE) { type = NavType.StringType; defaultValue = "" },
                navArgument(ForecastViewModel.ARG_LATITUDE) { type = NavType.StringType; defaultValue = "0" },
                navArgument(ForecastViewModel.ARG_LONGITUDE) { type = NavType.StringType; defaultValue = "0" }
            )
        ) {
            ForecastRoute(
                isCelsius = isCelsius,
                isTurkish = isTurkish,
                currentTheme = currentTheme,
                onBack = { navController.popBackStack() }
            )
        }

        composable(SETTINGS_ROUTE) {
            SettingsScreen(
                isTurkish = isTurkish,
                onLanguageChanged = { isTurkish = it },
                isCelsius = isCelsius,
                onCelsiusChanged = { isCelsius = it },
                currentTheme = currentTheme,
                onThemeChanged = { currentTheme = it },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

private const val CITY_ROUTE = "city"
private const val SETTINGS_ROUTE = "settings"

private const val FORECAST_ROUTE =
    "forecast?name={name}&subtitle={subtitle}&latitude={latitude}&longitude={longitude}"

private fun forecastRoute(city: City): String = buildString {
    append("forecast")
    append("?name=").append(Uri.encode(city.name))
    append("&subtitle=").append(Uri.encode(city.subtitle))
    append("&latitude=").append(city.latitude)
    append("&longitude=").append(city.longitude)
}
