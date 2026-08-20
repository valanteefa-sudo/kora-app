package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PlayerEntity
import com.example.ui.components.NewMatchDialog
import com.example.ui.components.PlayerAvatar3D
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PitchDarkCanvas
import com.example.ui.theme.PitchDarkSurface
import com.example.ui.theme.StadiumGreenPrimary
import com.example.ui.viewmodel.KooraViewModel

@Composable
fun AdminScreen(viewModel: KooraViewModel) {
    val allPlayers by viewModel.allPlayers.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()

    var showAddPlayerDialog by remember { mutableStateOf(false) }
    var showEditPlayerDialog by remember { mutableStateOf<PlayerEntity?>(null) }
    var showNewMatchDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchDarkCanvas)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldAccent)
                Text(
                    text = "لوحة تحكم الأدمن 🔐",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
            }

            Surface(
                color = if (isAdminMode) GoldAccent else Color.Gray,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isAdminMode) "وضع الأدمن مفعل ✅" else "وضع العرض فقط",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Quick Admin Action Cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("إدارة المباريات ⚽", fontWeight = FontWeight.Bold, color = GoldAccent, fontSize = 14.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showNewMatchDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = StadiumGreenPrimary)
                    ) {
                        Text("تحديد موعد جديد", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.generateAiLineups() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black)
                    ) {
                        Text("توليد التشكيلات المتوازنة ⚖️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { viewModel.resetAllPlayerPoints() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000), contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = GoldAccent)
                        Text("تصفير كل النقاط والإحصائيات للبدء من جديد 🔄", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Manage Roster
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("قائمة اللاعبين (${allPlayers.size}) 🏃", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)

            Button(
                onClick = { showAddPlayerDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                    Text("إضافة لاعب جديد", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        allPlayers.forEach { player ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PlayerAvatar3D(avatarType = player.avatarType, size = 42.dp)

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${player.name} (${player.nickname})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${player.position} • ${player.level} • النادي: ${player.favoriteClub}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Row {
                        IconButton(onClick = { showEditPlayerDialog = player }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GoldAccent)
                        }
                        IconButton(onClick = { viewModel.deletePlayer(player) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }

    // Add Player Dialog
    if (showAddPlayerDialog) {
        PlayerEditDialog(
            player = null,
            onDismiss = { showAddPlayerDialog = false },
            onSave = { newPlayer ->
                viewModel.addPlayer(newPlayer)
                showAddPlayerDialog = false
            }
        )
    }

    // Edit Player Dialog
    showEditPlayerDialog?.let { editingPlayer ->
        PlayerEditDialog(
            player = editingPlayer,
            onDismiss = { showEditPlayerDialog = null },
            onSave = { updatedPlayer ->
                viewModel.updatePlayer(updatedPlayer)
                showEditPlayerDialog = null
            }
        )
    }

    // New Match Dialog
    if (showNewMatchDialog) {
        NewMatchDialog(
            onDismiss = { showNewMatchDialog = false },
            onSave = { date, time, count ->
                viewModel.createNextMatch(date, time, count)
                showNewMatchDialog = false
            }
        )
    }
}

@Composable
private fun PlayerEditDialog(
    player: PlayerEntity?,
    onDismiss: () -> Unit,
    onSave: (PlayerEntity) -> Unit
) {
    var name by remember { mutableStateOf(player?.name ?: "") }
    var nickname by remember { mutableStateOf(player?.nickname ?: "") }
    var position by remember { mutableStateOf(player?.position ?: "Forward") }
    var level by remember { mutableStateOf(player?.level ?: "Level 1") }
    var club by remember { mutableStateOf(player?.favoriteClub ?: "الأهلي") }
    var traits by remember { mutableStateOf(player?.topTraits ?: "هداف, مهاري") }
    var avatarType by remember { mutableStateOf(player?.avatarType ?: "sultan") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (player == null) "إضافة لاعب جديد" else "تعديل بيانات اللاعب") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("الاسم") }, singleLine = true)
                OutlinedTextField(value = nickname, onValueChange = { nickname = it }, label = { Text("اللقب") }, singleLine = true)
                OutlinedTextField(value = position, onValueChange = { position = it }, label = { Text("المركز (Forward, Midfielder, Defender, Goalkeeper)") }, singleLine = true)
                OutlinedTextField(value = level, onValueChange = { level = it }, label = { Text("المستوى (Level 1, Level 2, Level 3)") }, singleLine = true)
                OutlinedTextField(value = club, onValueChange = { club = it }, label = { Text("النادي المفضل") }, singleLine = true)
                OutlinedTextField(value = traits, onValueChange = { traits = it }, label = { Text("أهم المميزات") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val p = player?.copy(
                            name = name, nickname = nickname, position = position, level = level, favoriteClub = club, topTraits = traits
                        ) ?: PlayerEntity(
                            name = name, nickname = nickname, position = position, level = level, favoriteClub = club, topTraits = traits, avatarType = avatarType, isAttending = true
                        )
                        onSave(p)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black)
            ) {
                Text("حفظ", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
