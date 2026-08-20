package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PlayerEntity
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PitchDarkSurface
import com.example.ui.theme.StadiumGreenPrimary

@Composable
fun AdminAttendanceCard(
    allPlayers: List<PlayerEntity>,
    onToggleRsvp: (playerId: Long, isAttending: Boolean) -> Unit,
    onBulkRsvp: (isAttending: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "تحديد حضور/غياب اللاعبين (الأدمن) 🔐",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GoldAccent
                    )
                }

                TextButton(onClick = { isExpanded = !isExpanded }) {
                    Text(
                        text = if (isExpanded) "🔼" else "عرض القائمة 🔽",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Bulk Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onBulkRsvp(true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StadiumGreenPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text("تأكيد حضور الجمع ✅", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onBulkRsvp(false) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5C1D1D),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text("تصفير الحضور ❌", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.1f))

                    // Players Attendance List
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(allPlayers) { player ->
                                PlayerAttendanceRow(
                                    player = player,
                                    onToggle = { isAttending ->
                                        onToggleRsvp(player.id, isAttending)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerAttendanceRow(
    player: PlayerEntity,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.04f),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (player.isAttending) StadiumGreenPrimary else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                PlayerAvatar3D(
                    avatarType = player.avatarType,
                    size = 38.dp,
                    showBorder = true,
                    borderColor = if (player.isAttending) GoldAccent else Color.Gray
                )

                Column {
                    Text(
                        text = player.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${player.nickname.ifEmpty { player.position }} • ${player.level}",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // "قادم" Button
                FilterChip(
                    selected = player.isAttending,
                    onClick = { onToggle(true) },
                    label = { Text("قادم", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = if (player.isAttending) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StadiumGreenPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White.copy(alpha = 0.05f),
                        labelColor = Color.White.copy(alpha = 0.6f)
                    ),
                    shape = CircleShape
                )

                // "اعتذر" Button
                FilterChip(
                    selected = !player.isAttending,
                    onClick = { onToggle(false) },
                    label = { Text("اعتذر", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = if (!player.isAttending) {
                        { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF8B0000),
                        selectedLabelColor = Color.White,
                        containerColor = Color.White.copy(alpha = 0.05f),
                        labelColor = Color.White.copy(alpha = 0.6f)
                    ),
                    shape = CircleShape
                )
            }
        }
    }
}
