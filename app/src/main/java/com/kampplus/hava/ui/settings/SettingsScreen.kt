package com.kampplus.hava.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R

enum class AppThemeMode {
    LIGHT, DARK, RAINBOW, DYNAMIC_IMAGE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isTurkish: Boolean,
    onLanguageChanged: (Boolean) -> Unit,
    isCelsius: Boolean,
    onCelsiusChanged: (Boolean) -> Unit,
    currentTheme: AppThemeMode,
    onThemeChanged: (AppThemeMode) -> Unit,
    onNavigateBack: () -> Unit
) {
    val isDynamicImage = currentTheme == AppThemeMode.DYNAMIC_IMAGE
    val isRainbow = currentTheme == AppThemeMode.RAINBOW

    val backgroundColor = when (currentTheme) {
        AppThemeMode.DARK, AppThemeMode.DYNAMIC_IMAGE -> Color(0xFF121212)
        AppThemeMode.RAINBOW -> Color(0xFFF8BBD0)
        else -> Color(0xFFF4F1EA) // Warm Minimalist Krem Tonu
    }

    val textColor = if (isDynamicImage || currentTheme == AppThemeMode.DARK) Color.White else Color(0xFF2C2C2C)

    Scaffold(
        containerColor = if (isRainbow) Color.Transparent else backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text(if (isTurkish) "Ayarlar" else "Settings", color = textColor, fontWeight = FontWeight.Medium) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Geri", tint = textColor)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            if (isDynamicImage) {
                Image(
                    painter = painterResource(id = R.drawable.bg_gunduz_bulutlu),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))
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
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (isTurkish) "Uygulama Dili" else "App Language", style = MaterialTheme.typography.titleMedium, color = textColor)
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.Gray.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = "TR",
                            modifier = Modifier
                                .background(if (isTurkish) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { onLanguageChanged(true) }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (isTurkish) Color.White else textColor
                        )
                        Text(
                            text = "EN",
                            modifier = Modifier
                                .background(if (!isTurkish) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { onLanguageChanged(false) }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (!isTurkish) Color.White else textColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (isTurkish) "Sıcaklık Birimi" else "Temperature Unit", style = MaterialTheme.typography.titleMedium, color = textColor)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "°F", color = textColor)
                        Switch(
                            checked = isCelsius,
                            onCheckedChange = onCelsiusChanged,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text(text = "°C", color = textColor)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(text = if (isTurkish) "Tema Görünümü" else "Theme Appearance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = textColor)
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        ThemeOptionButton(
                            title = if (isTurkish) "Açık" else "Light",
                            isSelected = currentTheme == AppThemeMode.LIGHT,
                            backgroundColor = Color(0xFFEBE7DF),
                            textColor = Color.Black,
                            onClick = { onThemeChanged(AppThemeMode.LIGHT) }
                        )
                        ThemeOptionButton(
                            title = if (isTurkish) "Siyah" else "Dark",
                            isSelected = currentTheme == AppThemeMode.DARK,
                            backgroundColor = Color(0xFF2C2C2C),
                            textColor = Color.White,
                            onClick = { onThemeChanged(AppThemeMode.DARK) }
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        ThemeOptionButton(
                            title = "Rainbow",
                            isSelected = currentTheme == AppThemeMode.RAINBOW,
                            backgroundColor = Color(0xFFF48FB1),
                            textColor = Color.White,
                            onClick = { onThemeChanged(AppThemeMode.RAINBOW) }
                        )
                        ThemeOptionButton(
                            title = if (isTurkish) "Manzara" else "Scenery",
                            isSelected = currentTheme == AppThemeMode.DYNAMIC_IMAGE,
                            backgroundColor = Color(0xFF607D8B),
                            textColor = Color.White,
                            onClick = { onThemeChanged(AppThemeMode.DYNAMIC_IMAGE) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeOptionButton(
    title: String,
    isSelected: Boolean,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) backgroundColor else backgroundColor.copy(alpha = 0.3f))
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .width(110.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = textColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
    }
}
