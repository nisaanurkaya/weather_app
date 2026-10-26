package com.kampplus.hava.ui.forecast

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kampplus.hava.R
import com.kampplus.hava.model.*
import com.kampplus.hava.ui.LoadState
import com.kampplus.hava.ui.component.ErrorView
import com.kampplus.hava.ui.settings.AppThemeMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ForecastRoute(
    isCelsius: Boolean,
    isTurkish: Boolean,
    currentTheme: AppThemeMode,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForecastViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ForecastScreen(
        cityName = viewModel.cityName,
        uiState = uiState,
        isCelsius = isCelsius,
        isTurkish = isTurkish,
        currentTheme = currentTheme,
        onBack = onBack,
        onRetry = viewModel::onRetry,
        modifier = modifier
    )
}

fun getRainbowCardColor(weatherCode: Int, isDayTime: Boolean): Color {
    val baseColor = when {
        !isDayTime -> Color(0xFFC5CAE9)
        weatherCode in listOf(51, 53, 55, 61, 63, 65, 80, 81, 82) -> Color(0xFFA7FFEB)
        weatherCode in listOf(71, 73, 75, 77, 85, 86) -> Color(0xFFC5CAE9)
        weatherCode in listOf(0, 1) -> Color(0xFFFFF9C4)
        else -> Color(0xFFF8BBD0)
    }
    return baseColor.copy(alpha = 0.7f)
}

fun getCardColor(theme: AppThemeMode, weatherCode: Int? = null, isDayTime: Boolean = true): Color {
    return when (theme) {
        AppThemeMode.DARK -> Color.DarkGray.copy(alpha = 0.4f)
        AppThemeMode.LIGHT -> Color(0xFFEBE7DF)
        AppThemeMode.DYNAMIC_IMAGE -> Color.White.copy(alpha = 0.2f)
        AppThemeMode.RAINBOW -> if (weatherCode != null) getRainbowCardColor(weatherCode, isDayTime) else Color.White.copy(alpha = 0.6f)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(
    cityName: String,
    uiState: LoadState<Forecast>,
    isCelsius: Boolean,
    isTurkish: Boolean,
    currentTheme: AppThemeMode,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDynamicImage = currentTheme == AppThemeMode.DYNAMIC_IMAGE
    val isRainbow = currentTheme == AppThemeMode.RAINBOW

    val backgroundColor = when (currentTheme) {
        AppThemeMode.DARK, AppThemeMode.DYNAMIC_IMAGE -> Color(0xFF121212)
        AppThemeMode.RAINBOW -> Color(0xFFFFF59D)
        else -> Color(0xFFF4F1EA)
    }

    val dynamicImageRes = if (uiState is LoadState.Success) {
        val current = uiState.data.current
        when {
            current.weatherCode in listOf(51, 53, 55, 61, 63, 65, 80, 81, 82) -> R.drawable.bg_gunduz_yagmurlu
            current.weatherCode in listOf(1, 2, 3) -> R.drawable.bg_gunduz_bulutlu
            else -> R.drawable.bg_gunduz_gunesli
        }
    } else {
        R.drawable.bg_gunduz_gunesli
    }

    Scaffold(
        modifier = modifier,
        containerColor = if (isRainbow || isDynamicImage) Color.Transparent else backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        cityName,
                        color = if (isDynamicImage || currentTheme == AppThemeMode.DARK) Color.White else Color(0xFF2C2C2C),
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Serif // Klasik zarif Times New Roman tarzı font
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = if (isTurkish) "Geri" else "Back", tint = if (isDynamicImage || currentTheme == AppThemeMode.DARK) Color.White else Color(0xFF2C2C2C))
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {

            if (isDynamicImage) {
                Image(
                    painter = painterResource(id = dynamicImageRes),
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

            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    LoadState.Loading -> CircularProgressIndicator(color = if (isDynamicImage) Color.White else MaterialTheme.colorScheme.primary)
                    LoadState.Empty -> ErrorView(message = if (isTurkish) "Tahmin bulunamadı." else "Forecast not found.")
                    is LoadState.Error -> ErrorView(message = uiState.message, onRetry = onRetry)
                    is LoadState.Success -> {
                        ForecastContent(
                            forecast = uiState.data,
                            isCelsius = isCelsius,
                            isTurkish = isTurkish,
                            currentTheme = currentTheme,
                            isDynamicImage = isDynamicImage
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ForecastContent(
    forecast: Forecast,
    isCelsius: Boolean,
    isTurkish: Boolean,
    currentTheme: AppThemeMode,
    isDynamicImage: Boolean,
    modifier: Modifier = Modifier
) {
    val textColor = if (isDynamicImage || currentTheme == AppThemeMode.DARK) Color.White else Color(0xFF2C2C2C)
    val cardColor = getCardColor(currentTheme, forecast.current.weatherCode, forecast.current.isDayTime)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        CurrentWeatherHeader(
            current = forecast.current,
            isCelsius = isCelsius,
            isTurkish = isTurkish,
            textColor = textColor,
            cardColor = cardColor
        )

        ActivityRecommendationCard(
            current = forecast.current,
            isTurkish = isTurkish,
            textColor = textColor,
            cardColor = cardColor
        )

        SectionTitle(if (isTurkish) "Saatlik" else "Hourly", textColor)
        HourlyRow(hours = forecast.hourly, isCelsius = isCelsius, textColor = textColor, cardColor = cardColor)

        SectionTitle(if (isTurkish) "7 Günlük" else "7-Day", textColor)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(cardColor)
                .padding(16.dp)
        ) {
            Column {
                forecast.daily.forEachIndexed { index, day ->
                    if (index > 0) HorizontalDivider(color = textColor.copy(alpha = 0.1f))
                    DailyRow(day = day, isToday = index == 0, isCelsius = isCelsius, isTurkish = isTurkish, textColor = textColor)
                }
            }
        }
    }
}

@Composable
private fun CurrentWeatherHeader(
    current: CurrentWeather,
    isCelsius: Boolean,
    isTurkish: Boolean,
    textColor: Color,
    cardColor: Color
) {
    val condition = WeatherCondition.fromWeatherCode(current.weatherCode)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(cardColor)
            .padding(vertical = 32.dp, horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Şık Times New Roman / Serif Tipografi Ailesi
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = degreesValue(current.temperatureC, isCelsius),
                    fontSize = 88.sp,
                    fontWeight = FontWeight.Light,
                    color = textColor,
                    fontFamily = FontFamily.Serif, // Klasik serif font
                    letterSpacing = (-2).sp
                )
                Text(
                    text = "°",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Light,
                    color = textColor,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(start = 2.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = condition.label,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = textColor.copy(alpha = 0.85f),
                fontFamily = FontFamily.Serif // Klasik serif font
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Metric(if (isTurkish) "Hissedilen" else "Feels", degrees(current.apparentTemperatureC ?: current.temperatureC, isCelsius), textColor)
                Metric(if (isTurkish) "Nem" else "Humidity", "%${current.humidityPercent ?: 0}", textColor)
                Metric(if (isTurkish) "Rüzgar" else "Wind", "${current.windSpeedKmh?.roundToInt() ?: 0} km/s", textColor)
            }
        }
    }
}

@Composable
private fun ActivityRecommendationCard(
    current: CurrentWeather,
    isTurkish: Boolean,
    textColor: Color,
    cardColor: Color
) {
    val isWindy = current.windSpeedKmh != null && current.windSpeedKmh > 30.0

    val (intro, suggestion) = when {
        isWindy -> if (isTurkish) {
            Pair("Dışarıda rüzgar epey sert esiyor, bugün evde dinlenme vakti!", "Güvenli ve sıcak evinde kalıp favori dizinin yeni sezonunu izleyebilir veya sakin bir müzik açabilirsin.")
        } else {
            Pair("The wind is blowing hard, time to stay indoors!", "Stay safe and warm at home to enjoy your favorite show or listen to relaxing music.")
        }

        current.weatherCode in listOf(71, 73, 75, 77, 85, 86) -> if (isTurkish) {
            Pair("Dışarıda harika bir kar manzarası var!", "Atkını takıp kardan adam yapmaya çıkabilir ya da pencerenden sıcak kahveni yudumlayarak karı izleyebilirsin.")
        } else {
            Pair("There's a wonderful snowy view outside!", "Bundle up to build a snowman or watch the snow from your window while sipping hot coffee.")
        }

        current.weatherCode in listOf(51, 53, 55, 61, 63, 65, 80, 81, 82) -> if (isTurkish) {
            Pair("Battaniye, kahve ve kitap için en mükemmel gün.", "Yağmur sesini dinlerken mutfakta yeni bir tarif deneyebilir veya ertelediğin filme başlayabilirsin.")
        } else {
            Pair("The ultimate trio: blanket, coffee, and a book.", "Listen to the rain while trying a new recipe or starting a movie you've been putting off.")
        }

        current.weatherCode in listOf(2, 3) -> if (isTurkish) {
            Pair("Hava bulutlu ve sakin.", "Müze gezmek, sevdiğin bir kafede vakit geçirmek veya evde dinlenmek için harika bir gün.")
        } else {
            Pair("The weather is cloudy and calm.", "Great day for visiting a museum, spending time at a cafe, or resting at home.")
        }

        current.temperatureC > 15.0 -> if (isTurkish) {
            Pair("Hava çok ferah ve güzel!", "Açık havada yürüyüş yapabilir, bisiklete binebilir ya da doğanın tadını çıkarabilirsin.")
        } else {
            Pair("The weather is fresh and lovely!", "You can take a walk outside, ride a bike, or enjoy nature.")
        }

        else -> if (isTurkish) {
            Pair("Dışarıda tatlı bir serinlik var.", "Sıkıca giyinip güzel bir yürüyüş yapabilir ya da sıcak bir içecekle anın tadını çıkarabilirsin.")
        } else {
            Pair("There's a sweet chill outside.", "Bundle up for a nice walk or enjoy a hot drink.")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(cardColor)
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "💡 ", fontSize = 18.sp)
                Text(
                    text = intro,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    fontFamily = FontFamily.Serif
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = suggestion,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = textColor.copy(alpha = 0.8f),
                lineHeight = 20.sp,
                fontFamily = FontFamily.Serif
            )
        }
    }
}

@Composable
private fun HourlyRow(
    hours: List<HourlyForecast>,
    isCelsius: Boolean,
    textColor: Color,
    cardColor: Color
) {
    val now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
    val upcoming = hours.filter { !it.time.isBefore(now) }.take(24)
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = upcoming, key = { it.time.toString() }) { hour ->
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardColor)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = hour.time.format(DateTimeFormatter.ofPattern("HH:mm")), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textColor.copy(alpha = 0.7f), fontFamily = FontFamily.Serif)
                    Text(text = WeatherCondition.fromWeatherCode(hour.weatherCode).emoji, fontSize = 22.sp)
                    Text(text = degrees(hour.temperatureC, isCelsius), fontSize = 16.sp, fontWeight = FontWeight.Light, color = textColor, fontFamily = FontFamily.Serif)
                }
            }
        }
    }
}

@Composable
private fun DailyRow(
    day: DailyForecast,
    isToday: Boolean,
    isCelsius: Boolean,
    isTurkish: Boolean,
    textColor: Color
) {
    val dayFormatter = if (isTurkish) {
        DateTimeFormatter.ofPattern("EEEE", Locale.forLanguageTag("tr"))
    } else {
        DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH)
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isToday) (if (isTurkish) "Bugün" else "Today") else day.date.format(dayFormatter).replaceFirstChar(Char::titlecase),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = percent(day.precipitationProbability).orEmpty(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (textColor == Color.White) Color(0xFF81D4FA) else Color(0xFF0288D1),
            fontFamily = FontFamily.Serif,
            modifier = Modifier.width(44.dp),
            textAlign = TextAlign.End
        )
        Text(
            text = WeatherCondition.fromWeatherCode(day.weatherCode).emoji,
            fontSize = 22.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Text(
            text = degrees(day.minTemperatureC, isCelsius),
            fontSize = 15.sp,
            fontWeight = FontWeight.Light,
            color = textColor.copy(alpha = 0.6f),
            fontFamily = FontFamily.Serif,
            modifier = Modifier.width(44.dp),
            textAlign = TextAlign.End
        )
        Text(
            text = degrees(day.maxTemperatureC, isCelsius),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.width(44.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun Metric(label: String, value: String, textColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Light, color = textColor, fontFamily = FontFamily.Serif)
        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textColor.copy(alpha = 0.7f), fontFamily = FontFamily.Serif)
    }
}

@Composable
private fun SectionTitle(text: String, textColor: Color) {
    Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor, fontFamily = FontFamily.Serif)
}

private fun degrees(tempC: Double, isCelsius: Boolean): String {
    val finalTemp = if (isCelsius) tempC else (tempC * 9 / 5) + 32
    return "${finalTemp.roundToInt()}°"
}

private fun degreesValue(tempC: Double, isCelsius: Boolean): String {
    val finalTemp = if (isCelsius) tempC else (tempC * 9 / 5) + 32
    return "${finalTemp.roundToInt()}"
}

private fun percent(value: Int?) = value?.takeIf { it > 0 }?.let { "%$it" }
