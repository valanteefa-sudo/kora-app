package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PlayerEntity
import com.example.ui.components.AdminAttendanceCard
import com.example.ui.components.AdminPasswordDialog
import com.example.ui.components.NewMatchDialog
import com.example.ui.components.PlayerAvatar3D
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PitchDarkCanvas
import com.example.ui.theme.PitchDarkSurface
import com.example.ui.theme.PitchCardContainer
import com.example.ui.theme.StadiumGreenPrimary
import com.example.ui.viewmodel.KooraViewModel
import com.example.util.MatchDateUtils

@Composable
fun HomeScreen(
    viewModel: KooraViewModel,
    onNavigateToPitch: () -> Unit
) {
    val latestMatch by viewModel.latestMatch.collectAsState()
    val allPlayers by viewModel.allPlayers.collectAsState()
    val attendingPlayers by viewModel.attendingPlayers.collectAsState()

    var showNewMatchDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchDarkCanvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Welcome Header Card with Stadium Football Theme
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PitchCardContainer, PitchDarkSurface)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            color = GoldAccent,
                            shape = CircleShape
                        ) {
                            Text(
                                text = "كورة كل جمعة ⚽",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "( أهلا بيك يا حريف في كورة كل جمعة )",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "المتعة • الصداقة • الروح الرياضية ❤️🏆",
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldAccent,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Compact Next Match Banner
            val match = latestMatch
            val targetCount = match?.targetPlayerCount ?: 16
            val attendCount = attendingPlayers.size
            val progress = (attendCount.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("📅", fontSize = 15.sp)
                            Text(
                                text = "التاريخ: ${match?.dateString ?: MatchDateUtils.getNextFridayDateString()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Surface(
                            color = GoldAccent,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "اللاعبون: $attendCount / $targetCount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = GoldAccent,
                        trackColor = Color.Black.copy(alpha = 0.4f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showNewMatchDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text("إضافة مباراة جديدة ➕", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.autoSetMatchToNextFriday()
                                showNewMatchDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = StadiumGreenPrimary, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text("ضبط الجمعة القادمة 📅", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Attendance Selection List (Direct Admin Control)
            AdminAttendanceCard(
                allPlayers = allPlayers,
                onToggleRsvp = { playerId, isAttending ->
                    viewModel.toggleRsvp(playerId, isAttending)
                },
                onBulkRsvp = { isAttending ->
                    viewModel.setBulkRsvp(isAttending)
                }
            )

            // Confirmed Players List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "قائمة الحضور المؤكدة (${attendingPlayers.size}) 🏃‍♂️",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Button(
                    onClick = onNavigateToPitch,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("عرض التشكيل ⚽", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // Confirmed Players Grid with 3D Avatars
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(attendingPlayers) { player ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        Surface(
                            color = PitchDarkSurface,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                PlayerAvatar3D(
                                    avatarType = player.avatarType,
                                    size = 46.dp,
                                    showBorder = true
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = player.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = player.nickname.ifEmpty { player.position },
                                        fontSize = 11.sp,
                                        color = GoldAccent,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${player.position} - ${player.level}",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // New Match Dialog
        if (showNewMatchDialog) {
            NewMatchDialog(
                onDismiss = { showNewMatchDialog = false },
                onSave = { date, time, targetCount ->
                    viewModel.createNextMatch(date, time, targetCount)
                    showNewMatchDialog = false
                }
            )
        }
    }
}
