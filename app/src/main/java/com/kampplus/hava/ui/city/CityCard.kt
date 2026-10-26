package com.kampplus.hava.ui.city

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kampplus.hava.model.City
import com.kampplus.hava.ui.settings.AppThemeMode

@Composable
fun CityCard(
    city: City,
    currentTheme: AppThemeMode,
    isFavorite: Boolean,         // Favori mi kontrolü
    onToggleFavorite: () -> Unit, // Kalbe tıklama aksiyonu
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor = when (currentTheme) {
        AppThemeMode.DARK -> Color.DarkGray.copy(alpha = 0.4f)
        AppThemeMode.LIGHT -> Color.LightGray.copy(alpha = 0.5f)
        AppThemeMode.DYNAMIC_IMAGE -> Color.White.copy(alpha = 0.15f)
        AppThemeMode.RAINBOW -> Color.White.copy(alpha = 0.6f)
    }

    val textColor = if (currentTheme == AppThemeMode.DARK || currentTheme == AppThemeMode.DYNAMIC_IMAGE) Color.White else Color(0xFF2C2C2C)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(cardColor)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        // Sağ üst köşedeki Kalp İkonu
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.align(Alignment.TopEnd).size(32.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Favori",
                tint = if (isFavorite) Color(0xFFE57373) else textColor.copy(alpha = 0.3f)
            )
        }

        // Şehir Bilgileri (Kartın tam ortasında)
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = city.name,
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "📍 ${city.country ?: city.admin1 ?: ""}",
                color = textColor.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
