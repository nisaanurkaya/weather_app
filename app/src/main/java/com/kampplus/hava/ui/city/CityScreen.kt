package com.kampplus.hava.ui.city

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kampplus.hava.R
import com.kampplus.hava.model.City
import com.kampplus.hava.ui.LoadState
import com.kampplus.hava.ui.component.EmptyView
import com.kampplus.hava.ui.component.ErrorView
import com.kampplus.hava.ui.component.ShimmerList
import com.kampplus.hava.ui.settings.AppThemeMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CityRoute(
    isTurkish: Boolean,
    currentTheme: AppThemeMode,
    favoriteCities: List<City>,
    onToggleFavorite: (City) -> Unit,
    onCityClick: (City) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CityViewModel = viewModel()
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CityScreen(
        query = query,
        uiState = uiState,
        isTurkish = isTurkish,
        currentTheme = currentTheme,
        favoriteCities = favoriteCities,
        onToggleFavorite = onToggleFavorite,
        onQueryChange = { newQuery ->
            viewModel.onQueryChange(newQuery)
            viewModel.search()
        },
        onSearch = viewModel::search,
        onCityClick = onCityClick,
        onSettingsClick = onSettingsClick,
        modifier = modifier
    )
}

@Composable
fun CityScreen(
    query: String,
    uiState: LoadState<List<City>>,
    isTurkish: Boolean,
    currentTheme: AppThemeMode,
    favoriteCities: List<City>,
    onToggleFavorite: (City) -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onCityClick: (City) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDynamicImage = currentTheme == AppThemeMode.DYNAMIC_IMAGE
    val isRainbow = currentTheme == AppThemeMode.RAINBOW

    val backgroundColor = when (currentTheme) {
        AppThemeMode.DARK, AppThemeMode.DYNAMIC_IMAGE -> Color(0xFF121212)
        AppThemeMode.RAINBOW -> Color(0xFFFFF59D)
        else -> Color(0xFFF4F1EA)
    }

    val textColor = if (isDynamicImage || currentTheme == AppThemeMode.DARK) Color.White else Color(0xFF2C2C2C)

    // Manzara ve Rainbow temalarında kartlar şeffaf/buzlu cam olacak, Açık temada krem/bej olacak
    val cardColor = when {
        isDynamicImage -> Color.White.copy(alpha = 0.2f) // Manzara teması için şeffaf cam
        isRainbow -> Color.White.copy(alpha = 0.6f)
        currentTheme == AppThemeMode.DARK -> Color.DarkGray.copy(alpha = 0.4f)
        else -> Color(0xFFEBE7DF) // Açık tema için şık bej
    }

    val today = LocalDate.now()
    val dateFormatter = if (isTurkish) DateTimeFormatter.ofPattern("d MMMM EEEE", Locale.forLanguageTag("tr")) else DateTimeFormatter.ofPattern("MMMM d, EEEE", Locale.ENGLISH)
    val formattedDate = today.format(dateFormatter)

    val localCities = remember { getLocalCities() }
    val localMatches = if (query.isNotBlank()) {
        localCities.filter { it.name.contains(query, ignoreCase = true) }
    } else emptyList()

    Scaffold(
        modifier = modifier,
        containerColor = if (isRainbow || isDynamicImage) Color.Transparent else backgroundColor
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {

            if (isDynamicImage) {
                Image(
                    painter = painterResource(id = R.drawable.bg_gunduz_bulutlu),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
            } else if (isRainbow) {
                Image(
                    painter = painterResource(id = R.drawable.bg_rainbow),
                    contentDescription = "Rainbow Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(
                                text = formattedDate,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = textColor.copy(alpha = 0.7f)
                            )
                            Text(
                                text = if (isTurkish) "Merhaba!" else "Hello!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor.copy(alpha = 0.9f)
                            )
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Filled.Settings, contentDescription = "Ayarlar", tint = textColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isTurkish) "Şehir arayın" else "Search for city",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }

                SearchBar(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                    isTurkish = isTurkish,
                    currentTheme = currentTheme,
                    isDynamicImage = isDynamicImage
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    if (query.isBlank()) {
                        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                            Spacer(modifier = Modifier.height(8.dp))
                            CurrentLocationWidget(isTurkish, textColor, cardColor, onCityClick)
                            Spacer(modifier = Modifier.height(24.dp))
                            FavoriteCitiesWidget(isTurkish, textColor, cardColor, favoriteCities, onCityClick)
                            Spacer(modifier = Modifier.height(24.dp))
                            DailyQuoteWidget(isTurkish, textColor, cardColor)
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    } else {
                        val showLocal = localMatches.isNotEmpty() && (uiState is LoadState.Loading || uiState is LoadState.Empty || uiState is LoadState.Error || query.length < 3)

                        if (showLocal) {
                            CityGrid(cities = localMatches, currentTheme = currentTheme, favoriteCities = favoriteCities, onToggleFavorite = onToggleFavorite, onCityClick = onCityClick)
                        } else {
                            when (uiState) {
                                LoadState.Loading -> ShimmerList()
                                is LoadState.Success -> CityGrid(cities = uiState.data, currentTheme = currentTheme, favoriteCities = favoriteCities, onToggleFavorite = onToggleFavorite, onCityClick = onCityClick)
                                LoadState.Empty -> {
                                    if (query.length < 3) {
                                        EmptyView(
                                            title = if(isTurkish) "Aranıyor..." else "Searching...",
                                            message = if(isTurkish) "Daha fazla harf girin." else "Keep typing..."
                                        )
                                    } else {
                                        EmptyView(
                                            title = if (isTurkish) "Bulunamadı" else "Not found",
                                            message = if (isTurkish) "Farklı bir şehir dene." else "Try another city."
                                        )
                                    }
                                }
                                is LoadState.Error -> ErrorView(message = uiState.message, onRetry = onSearch)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getLocalCities(): List<City> {
    return listOf(
        City(id = 101, name = "Adana", latitude = 37.0000, longitude = 35.3213, country = "Türkiye", countryCode = "TR", admin1 = "Adana"),
        City(id = 102, name = "Ankara", latitude = 39.9334, longitude = 32.8597, country = "Türkiye", countryCode = "TR", admin1 = "Ankara"),
        City(id = 103, name = "Antalya", latitude = 36.8969, longitude = 30.7133, country = "Türkiye", countryCode = "TR", admin1 = "Antalya"),
        City(id = 104, name = "Aydın", latitude = 37.8380, longitude = 27.8456, country = "Türkiye", countryCode = "TR", admin1 = "Aydın"),
        City(id = 105, name = "Balıkesir", latitude = 39.6484, longitude = 27.8826, country = "Türkiye", countryCode = "TR", admin1 = "Balıkesir"),
        City(id = 106, name = "Bursa", latitude = 40.1828, longitude = 29.0667, country = "Türkiye", countryCode = "TR", admin1 = "Bursa"),
        City(id = 107, name = "Çanakkale", latitude = 40.1553, longitude = 26.4142, country = "Türkiye", countryCode = "TR", admin1 = "Çanakkale"),
        City(id = 108, name = "Denizli", latitude = 37.7765, longitude = 29.0864, country = "Türkiye", countryCode = "TR", admin1 = "Denizli"),
        City(id = 109, name = "Diyarbakır", latitude = 37.9144, longitude = 40.2306, country = "Türkiye", countryCode = "TR", admin1 = "Diyarbakır"),
        City(id = 110, name = "Edirne", latitude = 41.6771, longitude = 26.5557, country = "Türkiye", countryCode = "TR", admin1 = "Edirne"),
        City(id = 111, name = "Erzurum", latitude = 39.9000, longitude = 41.2700, country = "Türkiye", countryCode = "TR", admin1 = "Erzurum"),
        City(id = 112, name = "Eskişehir", latitude = 39.7767, longitude = 30.5206, country = "Türkiye", countryCode = "TR", admin1 = "Eskişehir"),
        City(id = 113, name = "Gaziantep", latitude = 37.0662, longitude = 37.3833, country = "Türkiye", countryCode = "TR", admin1 = "Gaziantep"),
        City(id = 114, name = "Hatay", latitude = 36.2000, longitude = 36.1667, country = "Türkiye", countryCode = "TR", admin1 = "Hatay"),
        City(id = 115, name = "Isparta", latitude = 37.7648, longitude = 30.5566, country = "Türkiye", countryCode = "TR", admin1 = "Isparta"),
        City(id = 116, name = "İstanbul", latitude = 41.0082, longitude = 28.9784, country = "Türkiye", countryCode = "TR", admin1 = "İstanbul"),
        City(id = 117, name = "İzmir", latitude = 38.4237, longitude = 27.1428, country = "Türkiye", countryCode = "TR", admin1 = "İzmir"),
        City(id = 118, name = "Kayseri", latitude = 38.7312, longitude = 35.4787, country = "Türkiye", countryCode = "TR", admin1 = "Kayseri"),
        City(id = 119, name = "Konya", latitude = 37.8667, longitude = 32.4833, country = "Türkiye", countryCode = "TR", admin1 = "Konya"),
        City(id = 120, name = "Muğla", latitude = 37.2153, longitude = 28.3636, country = "Türkiye", countryCode = "TR", admin1 = "Muğla"),
        City(id = 121, name = "Nevşehir", latitude = 38.6244, longitude = 34.7144, country = "Türkiye", countryCode = "TR", admin1 = "Nevşehir"),
        City(id = 122, name = "Ordu", latitude = 40.9862, longitude = 37.8797, country = "Türkiye", countryCode = "TR", admin1 = "Ordu"),
        City(id = 123, name = "Rize", latitude = 41.0201, longitude = 40.5234, country = "Türkiye", countryCode = "TR", admin1 = "Rize"),
        City(id = 124, name = "Samsun", latitude = 41.2867, longitude = 36.3300, country = "Türkiye", countryCode = "TR", admin1 = "Samsun"),
        City(id = 125, name = "Sivas", latitude = 39.7477, longitude = 37.0179, country = "Türkiye", countryCode = "TR", admin1 = "Sivas"),
        City(id = 126, name = "Trabzon", latitude = 41.0027, longitude = 39.7168, country = "Türkiye", countryCode = "TR", admin1 = "Trabzon"),
        City(id = 201, name = "New York", latitude = 40.7128, longitude = -74.0060, country = "USA", countryCode = "US", admin1 = "New York"),
        City(id = 202, name = "London", latitude = 51.5074, longitude = -0.1278, country = "UK", countryCode = "GB", admin1 = "England"),
        City(id = 203, name = "Tokyo", latitude = 35.6762, longitude = 139.6503, country = "Japan", countryCode = "JP", admin1 = "Tokyo"),
        City(id = 204, name = "Paris", latitude = 48.8566, longitude = 2.3522, country = "France", countryCode = "FR", admin1 = "Île-de-France"),
        City(id = 205, name = "Berlin", latitude = 52.5200, longitude = 13.4050, country = "Germany", countryCode = "DE", admin1 = "Berlin")
    )
}

@Composable
fun DailyQuoteWidget(isTurkish: Boolean, textColor: Color, cardColor: Color) {
    val quotesTR = listOf(
        "\"Güneşli bir gün, ruhun en iyi ilacıdır.\" ☀️",
        "\"Her yağmur damlası, doğanın bir şarkısıdır.\" 🌧️",
        "\"Gökyüzüne bakmak, umudu her zaman yeşertir.\" ✨",
        "\"Güzel havaların tadını çıkar, yağmurlu günlerde kendi güneşini yarat.\" 🌈",
        "\"Fırtınalar, ağaçların köklerini daha da derinlere salmasını sağlar.\" 🌳",
        "\"Bulutlar ne kadar gri olursa olsun, arkalarında hep güneş saklıdır.\" ☁️",
        "\"Bugün rüzgar hangi yönden eserse essin, yelkenini ona göre ayarla.\" ⛵",
        "\"Doğanın temposuna ayak uydur; onun sırrı sabırdır.\" 🍃"
    )
    val quotesEN = listOf(
        "\"A sunny day is the best medicine for the soul.\" ☀️",
        "\"Every raindrop is a song of nature.\" 🌧️",
        "\"Looking at the sky always nurtures hope.\" ✨",
        "\"Enjoy the good weather, and on rainy days, create your own sunshine.\" 🌈",
        "\"Storms make trees take deeper roots.\" 🌳",
        "\"No matter how gray the clouds, the sun is always shining behind them.\" ☁️",
        "\"No matter which way the wind blows today, adjust your sails accordingly.\" ⛵",
        "\"Pace yourself with nature; her secret is patience.\" 🍃"
    )

    val dayOfYear = LocalDate.now().dayOfYear
    val activeQuote = if (isTurkish) quotesTR[dayOfYear % quotesTR.size] else quotesEN[dayOfYear % quotesEN.size]

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isTurkish) "Günün Sözü" else "Quote of the Day",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(cardColor)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = activeQuote,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                color = textColor.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun CurrentLocationWidget(isTurkish: Boolean, textColor: Color, cardColor: Color, onCityClick: (City) -> Unit) {
    val currentLocation = City(id = 1, name = "Bursa", latitude = 40.1828, longitude = 29.0667, country = "Türkiye", countryCode = "TR", admin1 = "Bursa")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isTurkish) "Mevcut Konum" else "Current Location",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(cardColor)
                .clickable { onCityClick(currentLocation) }
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, contentDescription = "Konum", tint = textColor, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = currentLocation.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
                    Text(text = currentLocation.country ?: currentLocation.admin1 ?: "Türkiye", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textColor.copy(alpha = 0.7f))
                }
            }
            Text(text = "⛅", fontSize = 36.sp)
        }
    }
}

@Composable
fun FavoriteCitiesWidget(
    isTurkish: Boolean,
    textColor: Color,
    cardColor: Color,
    favoriteCities: List<City>,
    onCityClick: (City) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isTurkish) "Favori Şehirlerin" else "Favorite Cities",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (favoriteCities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(cardColor)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isTurkish) "Henüz favori şehrin yok.\nArama yapıp kalp ikonuna basarak ekleyebilirsin." else "No favorite cities yet.\nSearch and click the heart icon to add.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                favoriteCities.forEach { city ->
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(cardColor)
                            .clickable { onCityClick(city) }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = city.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = city.country ?: city.admin1 ?: "", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = textColor.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    isTurkish: Boolean,
    currentTheme: AppThemeMode,
    isDynamicImage: Boolean,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val containerColor = when {
        isDynamicImage -> Color.White.copy(alpha = 0.2f)
        currentTheme == AppThemeMode.RAINBOW -> Color.White.copy(alpha = 0.6f)
        currentTheme == AppThemeMode.DARK -> Color.DarkGray.copy(alpha = 0.3f)
        else -> Color.White.copy(alpha = 0.7f)
    }

    val textColor = if (isDynamicImage || currentTheme == AppThemeMode.DARK) Color.White else Color(0xFF2C2C2C)
    val iconColor = if (isDynamicImage || currentTheme == AppThemeMode.DARK) Color.White else Color.DarkGray

    OutlinedTextField(
        value = query,
        onValueChange = { newQuery ->
            onQueryChange(newQuery)
        },
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(if (isTurkish) "Şehir ara..." else "Search city...", color = iconColor.copy(alpha = 0.6f)) },
        leadingIcon = {
            Icon(Icons.Filled.Search, contentDescription = null, tint = iconColor)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = {
                    onQueryChange("")
                    onSearch()
                }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Temizle", tint = iconColor)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = textColor
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            keyboardController?.hide()
            onSearch()
        })
    )
}

@Composable
private fun CityGrid(
    cities: List<City>,
    currentTheme: AppThemeMode,
    favoriteCities: List<City>,
    onToggleFavorite: (City) -> Unit,
    onCityClick: (City) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cities) { city ->
            val isFavorite = favoriteCities.any { it.id == city.id }
            CityCard(
                city = city,
                currentTheme = currentTheme,
                isFavorite = isFavorite,
                onToggleFavorite = { onToggleFavorite(city) },
                onClick = { onCityClick(city) }
            )
        }
    }
}
