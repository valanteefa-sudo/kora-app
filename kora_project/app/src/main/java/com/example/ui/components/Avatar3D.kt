package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.StadiumGreenLight

private data class AvatarStyle(
    val bgGradient: List<Color>,
    val badgeIcon: ImageVector,
    val overlaySymbol: String,
    val emojiBadge: String
)

@Composable
fun PlayerAvatar3D(
    avatarType: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    showBorder: Boolean = true,
    borderColor: Color = GoldAccent
) {
    val style = when (avatarType) {
        "sultan" -> AvatarStyle(
            listOf(Color(0xFFFFD700), Color(0xFFB8860B)),
            Icons.Filled.EmojiEvents,
            "👑",
            "👑"
        )
        "ameed" -> AvatarStyle(
            listOf(Color(0xFF1E3C72), Color(0xFF2A5298)),
            Icons.Filled.MilitaryTech,
            "🎖️",
            "🎖️"
        )
        "ostora" -> AvatarStyle(
            listOf(Color(0xFFFF8C00), Color(0xFFFFD700)),
            Icons.Filled.Star,
            "⭐",
            "⭐"
        )
        "asad" -> AvatarStyle(
            listOf(Color(0xFF8B0000), Color(0xFFFF4500)),
            Icons.Filled.LocalFireDepartment,
            "🦁",
            "🦁"
        )
        "maldini" -> AvatarStyle(
            listOf(Color(0xFF003366), Color(0xFF336699)),
            Icons.Filled.Shield,
            "🇮🇹",
            "🛡️"
        )
        "ronaldinho" -> AvatarStyle(
            listOf(Color(0xFF8E24AA), Color(0xFFD81B60)),
            Icons.Filled.AutoAwesome,
            "✨",
            "✨"
        )
        "treka" -> AvatarStyle(
            listOf(Color(0xFFD32F2F), Color(0xFFB71C1C)),
            Icons.Filled.SportsSoccer,
            "⚽",
            "😊"
        )
        "motifa" -> AvatarStyle(
            listOf(Color(0xFFE65100), Color(0xFFFF9800)),
            Icons.Filled.Speed,
            "🔥",
            "⚡"
        )
        "asad_aali" -> AvatarStyle(
            listOf(Color(0xFF00695C), Color(0xFF00897B)),
            Icons.Filled.SportsHandball,
            "🧤",
            "🧤"
        )
        "taja" -> AvatarStyle(
            listOf(Color(0xFF2E7D32), Color(0xFF4CAF50)),
            Icons.Filled.Shield,
            "🧤",
            "🛡️"
        )
        "sheikh" -> AvatarStyle(
            listOf(Color(0xFF1565C0), Color(0xFF1E88E5)),
            Icons.Filled.Psychology,
            "☪️",
            "⚽"
        )
        else -> AvatarStyle(
            listOf(StadiumGreenLight, Color(0xFF0B461D)),
            Icons.Filled.Person,
            "⚽",
            "🏃"
        )
    }

    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 6.dp, shape = CircleShape)
            .then(
                if (showBorder) Modifier.border(width = 2.dp, color = borderColor, shape = CircleShape)
                else Modifier
            )
            .clip(CircleShape)
            .background(Brush.radialGradient(style.bgGradient)),
        contentAlignment = Alignment.Center
    ) {
        // Inner 3D Cartoon Avatar Character Representation
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = style.overlaySymbol,
                fontSize = (size.value * 0.45f).sp,
                textAlign = TextAlign.Center
            )
        }

        // Top right specialty badge overlay
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-2).dp, y = 2.dp)
                .size((size.value * 0.35f).dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                .border(1.dp, GoldAccent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = style.emojiBadge,
                fontSize = (size.value * 0.22f).sp
            )
        }
    }
}
