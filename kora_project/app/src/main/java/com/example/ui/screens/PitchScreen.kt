package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.toArgb
import com.example.ui.components.SoccerPitchBoard
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PitchDarkCanvas
import com.example.ui.theme.PitchDarkSurface
import com.example.ui.theme.StadiumGreenPrimary
import com.example.ui.viewmodel.KooraViewModel
import com.example.util.SoundManager

@Composable
fun PitchScreen(
    viewModel: KooraViewModel,
    onNavigateToLiveMatch: () -> Unit
) {
    val lineupOptions by viewModel.lineupOptions.collectAsState()
    val selectedIndex by viewModel.selectedLineupIndex.collectAsState()
    val team1Players by viewModel.team1Players.collectAsState()
    val team2Players by viewModel.team2Players.collectAsState()
    val aiAnalysis by viewModel.aiTacticalAnalysis.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()

    val vmTeam1Name by viewModel.team1KitName.collectAsState()
    val vmTeam1ColorHex by viewModel.team1KitColor.collectAsState()
    val vmTeam2Name by viewModel.team2KitName.collectAsState()
    val vmTeam2ColorHex by viewModel.team2KitColor.collectAsState()

    var team1KitColor by remember(vmTeam1ColorHex) { mutableStateOf(Color(vmTeam1ColorHex)) }
    var team1KitName by remember(vmTeam1Name) { mutableStateOf(vmTeam1Name) }
    var team2KitColor by remember(vmTeam2ColorHex) { mutableStateOf(Color(vmTeam2ColorHex)) }
    var team2KitName by remember(vmTeam2Name) { mutableStateOf(vmTeam2Name) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchDarkCanvas)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "تشكيل الفريقين والملعب ⚽",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent
            )

            Button(
                onClick = { viewModel.generateAiLineups() },
                colors = ButtonDefaults.buttonColors(containerColor = StadiumGreenPrimary, contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Balance, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    Text("توليد التشكيلات المتوازنة ⚖️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 3 Lineup Proposal Options Tabs
        if (lineupOptions.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                lineupOptions.forEachIndexed { idx, option ->
                    val isSelected = selectedIndex == idx
                    Button(
                        onClick = { viewModel.setLineupOption(idx) },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) GoldAccent else PitchDarkSurface,
                            contentColor = if (isSelected) Color.Black else Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) GoldAccent else Color.Gray.copy(alpha = 0.4f)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "تشكيل ${idx + 1}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Lineup Option Summary Card
            val activeOption = lineupOptions.getOrNull(selectedIndex)
            activeOption?.let { opt ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = opt.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GoldAccent
                            )

                            Text(
                                text = "قوة الفريقين: ${opt.team1Power}% - ${opt.team2Power}%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }

                        Text(
                            text = opt.description,
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        // Interactive Team Colors Customizer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                    Text("تخصيص ألوان قمصان الفريقين 🎨", fontWeight = FontWeight.Bold, color = GoldAccent, fontSize = 13.sp)
                }

                val colorOptions = listOf(
                    Triple("الأبيض", Color.White, Color.Black),
                    Triple("الأحمر", Color(0xFFE53935), Color.White),
                    Triple("الأسود", Color(0xFF212121), Color.White),
                    Triple("الأزرق", Color(0xFF1E88E5), Color.White),
                    Triple("الأصفر", Color(0xFFFDD835), Color.Black),
                    Triple("الأخضر", Color(0xFF43A047), Color.White),
                    Triple("البرتقالي", Color(0xFFFB8C00), Color.White),
                    Triple("السماوي", Color(0xFF00ACC1), Color.Black)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Team 1 Color Selector Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("الفريق 1:", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            colorOptions.forEach { (name, color, _) ->
                                val isSelected = team1KitColor == color
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) GoldAccent else Color.Gray.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            team1KitColor = color
                                            team1KitName = name
                                            viewModel.updateTeamKits(
                                                name, color.toArgb().toLong(),
                                                team2KitName, team2KitColor.toArgb().toLong()
                                            )
                                        }
                                )
                            }
                        }
                    }

                    // Team 2 Color Selector Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("الفريق 2:", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            colorOptions.forEach { (name, color, _) ->
                                val isSelected = team2KitColor == color
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) GoldAccent else Color.Gray.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            team2KitColor = color
                                            team2KitName = name
                                            viewModel.updateTeamKits(
                                                team1KitName, team1KitColor.toArgb().toLong(),
                                                name, color.toArgb().toLong()
                                            )
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Gemini AI Tactical Commentary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = "التحليل والتكتيك الرياضي للمباراة:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = GoldAccent
                    )
                    Text(
                        text = aiAnalysis,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Interactive Soccer Pitch View
        SoccerPitchBoard(
            team1Name = team1KitName,
            team1Color = team1KitColor,
            team1Players = team1Players,
            team2Name = team2KitName,
            team2Color = team2KitColor,
            team2Players = team2Players,
            onSwapPlayers = { p1, p2 ->
                viewModel.swapPlayersInFormation(p1, p2)
            }
        )

        // Confirm Formation & Start Match Action Button
        Button(
            onClick = {
                viewModel.confirmFormation(
                    team1KitName, team1KitColor.toArgb().toLong(),
                    team2KitName, team2KitColor.toArgb().toLong()
                )
                SoundManager.playStartWhistle()
                onNavigateToLiveMatch()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Sports, contentDescription = null, tint = Color.Black)
                Text(
                    text = "اعتماد التشكيل وبدء المباراة ⚽",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
