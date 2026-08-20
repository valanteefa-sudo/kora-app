package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.toArgb
import com.example.data.local.PlayerEntity
import com.example.ui.components.PlayerAvatar3D
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PitchDarkCanvas
import com.example.ui.theme.PitchDarkSurface
import com.example.ui.theme.StadiumGreenPrimary
import com.example.ui.viewmodel.KooraViewModel
import com.example.util.SoundManager

@Composable
fun LiveMatchScreen(
    viewModel: KooraViewModel,
    onMatchFinished: () -> Unit
) {
    val team1Players by viewModel.team1Players.collectAsState()
    val team2Players by viewModel.team2Players.collectAsState()
    val matchEvaluations by viewModel.matchEvaluations.collectAsState()
    val timerSeconds by viewModel.liveTimerSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val allPlayers by viewModel.allPlayers.collectAsState()

    val team1KitName by viewModel.team1KitName.collectAsState()
    val team1KitColorHex by viewModel.team1KitColor.collectAsState()
    val team2KitName by viewModel.team2KitName.collectAsState()
    val team2KitColorHex by viewModel.team2KitColor.collectAsState()

    val team1KitColor = remember(team1KitColorHex) { Color(team1KitColorHex) }
    val team2KitColor = remember(team2KitColorHex) { Color(team2KitColorHex) }

    var team1Score by remember { mutableStateOf(4) }
    var team2Score by remember { mutableStateOf(3) }
    var selectedMotmId by remember { mutableStateOf<Long?>(1L) }
    var showFinishDialog by remember { mutableStateOf(false) }
    var selectedPlayerForDetails by remember { mutableStateOf<PlayerEntity?>(null) }
    var selectedTeamTab by remember { mutableStateOf(0) } // 0: All, 1: Team 1, 2: Team 2

    val formattedTime = remember(timerSeconds) {
        val mins = timerSeconds / 60
        val secs = timerSeconds % 60
        String.format("%02d:%02d", mins, secs)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchDarkCanvas)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Score & Timer Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Match Timer Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "⏱️ $formattedTime",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )

                    IconButton(
                        onClick = {
                            if (isTimerRunning) {
                                viewModel.pauseTimer()
                            } else {
                                viewModel.startTimer()
                                SoundManager.playStartWhistle()
                            }
                        },
                        modifier = Modifier
                            .background(GoldAccent, CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Timer Toggle",
                            tint = Color.Black
                        )
                    }
                }

                // Interactive Sound FX Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { SoundManager.playStartWhistle() },
                        label = { Text("🔊 صافرة", fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.Bold) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color.White.copy(alpha = 0.08f))
                    )
                    AssistChip(
                        onClick = { SoundManager.playCrowdCheerOnly() },
                        label = { Text("📣 جمهور", fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.Bold) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color.White.copy(alpha = 0.08f))
                    )
                    AssistChip(
                        onClick = { SoundManager.playGoalCheer() },
                        label = { Text("⚽ احتفال هدف", fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.Bold) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color.White.copy(alpha = 0.08f))
                    )
                }

                Divider(color = GoldAccent.copy(alpha = 0.3f))

                // Scoreboard with Dynamic Kits
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Team 1
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(team1KitColor)
                                    .border(1.dp, Color.Gray, CircleShape)
                            )
                            Text("فريق $team1KitName", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (team1Score > 0) team1Score-- }) {
                                Text("-", fontSize = 20.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                            }
                            Text(text = "$team1Score", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                            IconButton(onClick = {
                                team1Score++
                                SoundManager.playGoalCheer()
                            }) {
                                Text("+", fontSize = 20.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Text("VS", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                    // Team 2
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(team2KitColor)
                                    .border(1.dp, Color.Gray, CircleShape)
                            )
                            Text("فريق $team2KitName", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (team2Score > 0) team2Score-- }) {
                                Text("-", fontSize = 20.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                            }
                            Text(text = "$team2Score", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                            IconButton(onClick = {
                                team2Score++
                                SoundManager.playGoalCheer()
                            }) {
                                Text("+", fontSize = 20.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Filter Tabs for Team 1 / Team 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val tabs = listOf(
                "الكل (${team1Players.size + team2Players.size})",
                "فريق $team1KitName (${team1Players.size})",
                "فريق $team2KitName (${team2Players.size})"
            )
            tabs.forEachIndexed { idx, label ->
                val isSelected = selectedTeamTab == idx
                Button(
                    onClick = { selectedTeamTab = idx },
                    modifier = Modifier.weight(1f).height(38.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) GoldAccent else PitchDarkSurface,
                        contentColor = if (isSelected) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live Event Evaluator List
        val filteredPlayers = when (selectedTeamTab) {
            1 -> team1Players
            2 -> team2Players
            else -> (team1Players + team2Players)
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredPlayers) { player ->
                val eval = matchEvaluations[player.id]
                val playerFull = allPlayers.find { it.id == player.id } ?: player
                val inTeam1 = team1Players.any { it.id == player.id }
                val playerTeamKitName = if (inTeam1) team1KitName else team2KitName
                val playerTeamKitColor = if (inTeam1) team1KitColor else team2KitColor

                // عداد النقاط الحي: يُحسب مباشرة من إحصائيات اللاعب الحالية بنفس معادلة
                // PointsCalculator الموحّدة، ويتحدث تلقائياً مع كل تعديل على الكارت.
                val liveOpponentScore = if (inTeam1) team2Score else team1Score
                val livePlayerScore = if (inTeam1) team1Score else team2Score
                val liveIsWin = livePlayerScore > liveOpponentScore
                val liveIsDraw = livePlayerScore == liveOpponentScore
                val livePoints = remember(eval, team1Score, team2Score) {
                    com.example.util.PointsCalculator.calculateTotal(
                        eval = eval ?: com.example.data.local.MatchPlayerEntity(
                            matchId = 0L, playerId = player.id, teamNumber = if (inTeam1) 1 else 2,
                            positionOnPitch = player.position
                        ),
                        playerPosition = playerFull.position,
                        opponentScore = liveOpponentScore,
                        isWin = liveIsWin,
                        isDraw = liveIsDraw
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPlayerForDetails = playerFull },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.35f))
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
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                PlayerAvatar3D(avatarType = playerFull.avatarType, size = 40.dp)
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "${playerFull.name} (${playerFull.nickname})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        // Team kit badge
                                        Surface(
                                            color = playerTeamKitColor.copy(alpha = 0.25f),
                                            shape = RoundedCornerShape(4.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, playerTeamKitColor)
                                        ) {
                                            Text(
                                                text = playerTeamKitName,
                                                fontSize = 9.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "المركز: ${playerFull.position}",
                                        fontSize = 11.sp,
                                        color = GoldAccent
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                // عداد النقاط الحي الخاص بهذا اللاعب في هذه المباراة
                                Surface(
                                    color = if (livePoints >= 0) StadiumGreenPrimary.copy(alpha = 0.25f) else Color.Red.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp, if (livePoints >= 0) StadiumGreenPrimary else Color.Red
                                    )
                                ) {
                                    Text(
                                        text = "🏅 $livePoints نقطة",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (livePoints >= 0) StadiumGreenPrimary else Color.Red,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                // MOTM Selection Chip
                                FilterChip(
                                    selected = selectedMotmId == player.id,
                                    onClick = { selectedMotmId = player.id },
                                    label = { Text("رجل المباراة 👑", fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldAccent,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }

                        // Summary badges row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if ((eval?.goals ?: 0) > 0) Surface(color = StadiumGreenPrimary.copy(alpha = 0.3f), shape = RoundedCornerShape(6.dp)) {
                                    Text("⚽ ${eval?.goals}", fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                if ((eval?.assists ?: 0) > 0) Surface(color = Color(0xFF1E3C72).copy(alpha = 0.5f), shape = RoundedCornerShape(6.dp)) {
                                    Text("🎯 ${eval?.assists}", fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                if ((eval?.penaltiesScored ?: 0) > 0) Surface(color = GoldAccent.copy(alpha = 0.3f), shape = RoundedCornerShape(6.dp)) {
                                    Text("🥅 ${eval?.penaltiesScored}", fontSize = 11.sp, color = GoldAccent, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                if ((eval?.yellowCards ?: 0) > 0) Surface(color = Color.Yellow.copy(alpha = 0.3f), shape = RoundedCornerShape(6.dp)) {
                                    Text("🟨 ${eval?.yellowCards}", fontSize = 11.sp, color = Color.Yellow, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                if ((eval?.redCards ?: 0) > 0) Surface(color = Color.Red.copy(alpha = 0.3f), shape = RoundedCornerShape(6.dp)) {
                                    Text("🟥 ${eval?.redCards}", fontSize = 11.sp, color = Color.Red, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }

                            Button(
                                onClick = { selectedPlayerForDetails = playerFull },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("تسجيل البيانات ✍️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Finish Match & Calculate Points Button
        Button(
            onClick = {
                val t1Goals = team1Players.sumOf { p -> matchEvaluations[p.id]?.goals ?: 0 }
                val t2Goals = team2Players.sumOf { p -> matchEvaluations[p.id]?.goals ?: 0 }
                
                SoundManager.playFinishWhistle()
                if (t1Goals != team1Score || t2Goals != team2Score) {
                    showFinishDialog = true
                } else {
                    viewModel.finalizeMatch(team1Score, team2Score, selectedMotmId)
                    onMatchFinished()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("إنهاء المباراة وحساب النقاط تلقائياً 🏆", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }

    // Mismatch Alert Confirmation Dialog
    if (showFinishDialog) {
        val t1Scorers = team1Players.mapNotNull { p ->
            val eval = matchEvaluations[p.id]
            val g = eval?.goals ?: 0
            val pen = eval?.penaltiesScored ?: 0
            if (g > 0) {
                val penStr = if (pen > 0) " (منها $pen جزاء)" else ""
                "• ${p.name}: $g هدف$penStr"
            } else null
        }
        val t2Scorers = team2Players.mapNotNull { p ->
            val eval = matchEvaluations[p.id]
            val g = eval?.goals ?: 0
            val pen = eval?.penaltiesScored ?: 0
            if (g > 0) {
                val penStr = if (pen > 0) " (منها $pen جزاء)" else ""
                "• ${p.name}: $g هدف$penStr"
            } else null
        }

        val t1Goals = team1Players.sumOf { p -> matchEvaluations[p.id]?.goals ?: 0 }
        val t2Goals = team2Players.sumOf { p -> matchEvaluations[p.id]?.goals ?: 0 }

        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = {
                Text("تنبيه: عدم تطابق الأهداف المسجلة! ⚠️", fontWeight = FontWeight.Bold, color = GoldAccent, fontSize = 16.sp)
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "هناك اختلاف بين النتيجة المسجلة على الشاشة وبين مجموع أهداف اللاعبين:",
                        color = Color.White,
                        fontSize = 13.sp
                    )

                    // Team 1 Breakdown
                    Surface(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "فريق $team1KitName: النتيجة ($team1Score) | المسجل للاعبين ($t1Goals)",
                                color = if (t1Goals == team1Score) StadiumGreenPrimary else GoldAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (t1Scorers.isNotEmpty()) {
                                Text(t1Scorers.joinToString("\n"), color = Color.LightGray, fontSize = 11.sp)
                            } else {
                                Text("• لم يتم تسجيل أهداف للاعبي الفريق بعد.", color = Color.Gray, fontSize = 11.sp)
                            }
                        }
                    }

                    // Team 2 Breakdown
                    Surface(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "فريق $team2KitName: النتيجة ($team2Score) | المسجل للاعبين ($t2Goals)",
                                color = if (t2Goals == team2Score) StadiumGreenPrimary else GoldAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (t2Scorers.isNotEmpty()) {
                                Text(t2Scorers.joinToString("\n"), color = Color.LightGray, fontSize = 11.sp)
                            } else {
                                Text("• لم يتم تسجيل أهداف للاعبي الفريق بعد.", color = Color.Gray, fontSize = 11.sp)
                            }
                        }
                    }

                    Text(
                        text = "اختر الإجراء المناسب للإنهاء:",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = {
                            showFinishDialog = false
                            team1Score = t1Goals
                            team2Score = t2Goals
                            viewModel.finalizeMatch(t1Goals, t2Goals, selectedMotmId)
                            onMatchFinished()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = StadiumGreenPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("مزامنة النتيجة تلقائياً ($t1Goals - $t2Goals) والإنهاء ✅", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            showFinishDialog = false
                            viewModel.finalizeMatch(team1Score, team2Score, selectedMotmId)
                            onMatchFinished()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("اعتماد النتيجة ($team1Score - $team2Score) والإنهاء 🏆", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showFinishDialog = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إلغاء للمراجعة والتعديل ✏️", color = Color.White, fontSize = 12.sp)
                }
            },
            containerColor = PitchDarkSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Modal Dialog for Player Match Stats
    selectedPlayerForDetails?.let { player ->
        val currentEval = matchEvaluations[player.id] ?: com.example.data.local.MatchPlayerEntity(
            matchId = 0L,
            playerId = player.id,
            teamNumber = 1,
            positionOnPitch = player.position
        )

        val inTeam1 = team1Players.any { it.id == player.id }
        val playerTeamScore = if (inTeam1) team1Score else team2Score
        val opponentTeamScore = if (inTeam1) team2Score else team1Score
        val teamName = if (inTeam1) "فريق $team1KitName" else "فريق $team2KitName"
        val teamPlayers = if (inTeam1) team1Players else team2Players

        PlayerStatsDetailModal(
            player = player,
            evaluation = currentEval,
            playerTeamScore = playerTeamScore,
            opponentTeamScore = opponentTeamScore,
            teamName = teamName,
            teamPlayers = teamPlayers,
            allPlayers = allPlayers,
            matchEvaluations = matchEvaluations,
            onDismiss = { selectedPlayerForDetails = null },
            onUpdateStat = { updateFunc ->
                viewModel.updatePlayerStat(player.id, updateFunc)
            }
        )
    }
}

@Composable
fun PlayerStatsDetailModal(
    player: PlayerEntity,
    evaluation: com.example.data.local.MatchPlayerEntity,
    playerTeamScore: Int,
    opponentTeamScore: Int,
    teamName: String,
    teamPlayers: List<PlayerEntity>,
    allPlayers: List<PlayerEntity>,
    matchEvaluations: Map<Long, com.example.data.local.MatchPlayerEntity>,
    onDismiss: () -> Unit,
    onUpdateStat: ((com.example.data.local.MatchPlayerEntity) -> com.example.data.local.MatchPlayerEntity) -> Unit
) {
    val isGoalkeeper = player.position == "Goalkeeper" || player.position == "حارس مرمى" || player.position == "GK"
    var warningMessage by remember { mutableStateOf<String?>(null) }

    val currentEval = matchEvaluations[player.id] ?: evaluation

    // Calculate goals registered for teammates
    val otherTeammatesGoalsList = remember(matchEvaluations, teamPlayers, player.id) {
        teamPlayers.filter { it.id != player.id }.mapNotNull { teammate ->
            val eval = matchEvaluations[teammate.id]
            val g = eval?.goals ?: 0
            val p = eval?.penaltiesScored ?: 0
            if (g > 0) {
                val fullP = allPlayers.find { it.id == teammate.id } ?: teammate
                val name = if (fullP.nickname.isNotBlank()) "${fullP.name} (${fullP.nickname})" else fullP.name
                val penStr = if (p > 0) " (منها $p جزاء)" else ""
                Triple(teammate.id, "$name$penStr", g)
            } else null
        }
    }
    val otherTeammatesGoalsSum = remember(otherTeammatesGoalsList) {
        otherTeammatesGoalsList.sumOf { it.third }
    }

    // Calculate assists registered for teammates
    val otherTeammatesAssistsList = remember(matchEvaluations, teamPlayers, player.id) {
        teamPlayers.filter { it.id != player.id }.mapNotNull { teammate ->
            val eval = matchEvaluations[teammate.id]
            val a = eval?.assists ?: 0
            if (a > 0) {
                val fullP = allPlayers.find { it.id == teammate.id } ?: teammate
                val name = if (fullP.nickname.isNotBlank()) "${fullP.name} (${fullP.nickname})" else fullP.name
                Pair(name, a)
            } else null
        }
    }
    val otherTeammatesAssistsSum = remember(otherTeammatesAssistsList) {
        otherTeammatesAssistsList.sumOf { it.second }
    }

    LaunchedEffect(opponentTeamScore) {
        if (opponentTeamScore == 0 && (isGoalkeeper || player.position == "Defender" || player.position == "مدافع")) {
            onUpdateStat { it.copy(isCleanSheet = true) }
        } else if (opponentTeamScore > 0 && currentEval.isCleanSheet) {
            onUpdateStat { it.copy(isCleanSheet = false) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("حفظ وإغلاق 👍", fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlayerAvatar3D(avatarType = player.avatarType, size = 42.dp)
                Column {
                    Text(
                        text = player.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "اللقب: ${player.nickname} • ${player.position}",
                        fontSize = 12.sp,
                        color = GoldAccent
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnimatedVisibility(visible = warningMessage != null) {
                    Surface(
                        color = Color(0xFFD32F2F),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("⚠️", fontSize = 16.sp)
                            Text(
                                text = warningMessage ?: "",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // كارت عداد النقاط الحي الخاص باللاعب - يتحدث تلقائياً مع كل تعديل
                run {
                    val liveIsWin = playerTeamScore > opponentTeamScore
                    val liveIsDraw = playerTeamScore == opponentTeamScore
                    val breakdown = remember(currentEval, playerTeamScore, opponentTeamScore) {
                        com.example.util.PointsCalculator.calculateBreakdown(
                            eval = currentEval,
                            playerPosition = player.position,
                            opponentScore = opponentTeamScore,
                            isWin = liveIsWin,
                            isDraw = liveIsDraw
                        )
                    }
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🏅 نقاط اللاعب في هذه المباراة حتى الآن", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(
                                    text = "${breakdown.total} نقطة",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldAccent
                                )
                            }
                            Text(
                                text = "⚽ ${breakdown.goalPoints}  •  🎯 ${breakdown.assistPoints}" +
                                        (if (breakdown.cleanSheetPoints > 0) "  •  🧤 +${breakdown.cleanSheetPoints}" else "") +
                                        (if (breakdown.resultBonusPoints > 0) "  •  🏆 +${breakdown.resultBonusPoints}" else "") +
                                        (if (breakdown.negativePoints < 0) "  •  ⚠️ ${breakdown.negativePoints}" else ""),
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                }

                Text(
                    text = "تسجيل إحصائيات المباراة للاعب 📊",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )

                // Goals Scored
                StatCounterRow(
                    title = "عدد الأهداف التي سجلها",
                    icon = "⚽",
                    count = currentEval.goals,
                    onValueChange = { newCount ->
                        val proposedTeamGoals = otherTeammatesGoalsSum + newCount

                        if (proposedTeamGoals > playerTeamScore) {
                            val breakdown = if (otherTeammatesGoalsList.isNotEmpty()) {
                                "الأهداف المسجلة لزملائك بالفريق حالياً ($otherTeammatesGoalsSum أهداف):\n" +
                                        otherTeammatesGoalsList.joinToString("\n") { "• ${it.second}: ${it.third} أهداف" }
                            } else {
                                "لم يتم تسجيل أهداف لأي لاعب آخر بالفريق."
                            }

                            warningMessage = "النتيجة غير منطقية بالنسبة لنتيجة المباراة الحقيقية!\n" +
                                    "نتيجة $teamName في المباراة هي ($playerTeamScore) أهداف فقط.\n\n" +
                                    "$breakdown\n\n" +
                                    "تعديل أهداف ${player.name} إلى ($newCount) سيجعل إجمالي أهداف الفريق ($proposedTeamGoals) وهو أعلى من نتيجة الفريق الحقيقية ($playerTeamScore)."
                        } else {
                            warningMessage = null
                            val adjustedPenalties = if (currentEval.penaltiesScored > newCount) newCount else currentEval.penaltiesScored
                            onUpdateStat { it.copy(goals = newCount, penaltiesScored = adjustedPenalties) }
                        }
                    },
                    accentColor = StadiumGreenPrimary
                )

                // Assists
                StatCounterRow(
                    title = "عدد الأهداف التي صنعها (أسيست)",
                    icon = "🎯",
                    count = currentEval.assists,
                    onValueChange = { newCount ->
                        val proposedTeamAssists = otherTeammatesAssistsSum + newCount

                        if (proposedTeamAssists > playerTeamScore) {
                            val breakdown = if (otherTeammatesAssistsList.isNotEmpty()) {
                                "التمريرات الحاسمة (الأسيست) المسجلة لزملائك بالفريق ($otherTeammatesAssistsSum):\n" +
                                        otherTeammatesAssistsList.joinToString("\n") { "• ${it.first}: ${it.second} أسيست" }
                            } else {
                                "لم يتم تسجيل أسيست لأي لاعب آخر بالفريق."
                            }

                            warningMessage = "النتيجة غير منطقية بالنسبة لنتيجة المباراة الحقيقية!\n" +
                                    "عدد التمريرات الحاسمة لا يمكن أن يتجاوز إجمالي أهداف $teamName بالمباراة ($playerTeamScore أهداف).\n\n" +
                                    "$breakdown\n\n" +
                                    "تعديل أسيست ${player.name} إلى ($newCount) سيجعل الإجمالي ($proposedTeamAssists) وهو أكبر من أهداف الفريق ($playerTeamScore)."
                        } else {
                            warningMessage = null
                            onUpdateStat { it.copy(assists = newCount) }
                        }
                    },
                    accentColor = Color(0xFF64B5F6)
                )

                // Penalties Scored
                StatCounterRow(
                    title = "ركلات الجزاء الأحرزها (سجلها)",
                    icon = "🥅",
                    count = currentEval.penaltiesScored,
                    onValueChange = { newCount ->
                        if (newCount > currentEval.goals) {
                            warningMessage = "إحصائية ركلات الجزاء هي إحصائية فرعية ضمن أهداف اللاعب!\n" +
                                    "عدد ركلات الجزاء الأحرزها ($newCount) لا يمكن أن يتجاوز إجمالي الأهداف المسجلة للاعب (${currentEval.goals} أهداف).\n\n" +
                                    "إذا كان قد سجل هدفاً من ركلة جزاء، يرجى زيادة إجمالي أهداف اللاعب أولاً إلى $newCount، ثم تحديد أن أحدها كان من ركلة جزاء."
                        } else {
                            warningMessage = null
                            onUpdateStat { it.copy(penaltiesScored = newCount) }
                        }
                    },
                    accentColor = GoldAccent
                )

                // Penalties Missed
                StatCounterRow(
                    title = "ركلات الجزاء الأهدرها (ضيعها)",
                    icon = "❌",
                    count = currentEval.penaltyMissed,
                    onValueChange = { newCount ->
                        warningMessage = null
                        onUpdateStat { it.copy(penaltyMissed = newCount) }
                    },
                    accentColor = Color.Red
                )

                // Yellow Cards
                StatCounterRow(
                    title = "كارت أصفر",
                    icon = "🟨",
                    count = currentEval.yellowCards,
                    onValueChange = { newCount ->
                        warningMessage = null
                        onUpdateStat { it.copy(yellowCards = newCount) }
                    },
                    accentColor = Color.Yellow
                )

                // Red Cards
                StatCounterRow(
                    title = "كارت أحمر",
                    icon = "🟥",
                    count = currentEval.redCards,
                    onValueChange = { newCount ->
                        warningMessage = null
                        onUpdateStat { it.copy(redCards = newCount) }
                    },
                    accentColor = Color.Red
                )

                // Goal Line Save
                StatCounterRow(
                    title = "إنقاذ هدف محقق (من على الخط)",
                    icon = "🛡️",
                    count = currentEval.goalLineSaves,
                    onValueChange = { newCount ->
                        warningMessage = null
                        onUpdateStat { it.copy(goalLineSaves = newCount) }
                    },
                    accentColor = GoldAccent
                )

                // قسم "مخالفات" منفصل بصريًا: يجمع كل الإحصائيات اللي بتخصم نقاط بسبب
                // تصرف سلبي من اللاعب (مش بطاقات فقط)، عشان الأدمن يميّزها بسهولة عن
                // إحصائيات الأداء الإيجابية، وعشان تُحتسب فعليًا (قبل كده كانت هذه الحقول
                // موجودة في قاعدة البيانات ومحسوبة في PointsCalculator لكن بدون أي واجهة
                // لتسجيلها أثناء المباراة، فكانت لا تُفعَّل أبدًا في الواقع).
                ViolationsSection(
                    ownGoals = currentEval.ownGoals,
                    onOwnGoalsChange = { newCount ->
                        warningMessage = null
                        onUpdateStat { it.copy(ownGoals = newCount) }
                    },
                    violentObjection = currentEval.violentObjection,
                    onViolentObjectionChange = { newCount ->
                        warningMessage = null
                        onUpdateStat { it.copy(violentObjection = newCount) }
                    },
                    errorLeadingToGoal = currentEval.errorLeadingToGoal,
                    onErrorLeadingToGoalChange = { newCount ->
                        warningMessage = null
                        onUpdateStat { it.copy(errorLeadingToGoal = newCount) }
                    }
                )

                // Goalkeeper Section ONLY for Goalkeepers
                if (isGoalkeeper) {
                    Divider(color = GoldAccent.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "خاص بحارس المرمى 🧤 (مفعّل)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = StadiumGreenPrimary
                    )

                    // Penalty Save
                    StatCounterRow(
                        title = "إنقاذ ضربة جزاء",
                        icon = "🛑",
                        count = currentEval.penaltySaves,
                        onValueChange = { newCount ->
                            warningMessage = null
                            onUpdateStat { it.copy(penaltySaves = newCount) }
                        },
                        accentColor = StadiumGreenPrimary
                    )

                    // Clean Sheet Toggle Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🧤", fontSize = 18.sp)
                            Text("شباك نظيفة (كلين شيت)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Switch(
                            checked = currentEval.isCleanSheet,
                            onCheckedChange = { isChecked ->
                                if (isChecked && opponentTeamScore > 0) {
                                    warningMessage = "النتيجة غير منطقية!\nلا يمكن تسجيل شباك نظيفة (كلين شيت) لأن الفريق المنافس سجل ($opponentTeamScore) أهداف في هذه المباراة."
                                } else {
                                    warningMessage = null
                                    onUpdateStat { it.copy(isCleanSheet = isChecked) }
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = StadiumGreenPrimary
                            )
                        )
                    }
                }
            }
        },
        containerColor = PitchDarkSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * قسم "مخالفات" مميّز بلون أحمر داكن، منفصل عن باقي عدادات الأداء، يحتوي كل
 * الإحصائيات التي تخصم نقاط بسبب تصرف سلبي من اللاعب: هدف عكسي، اعتراض غير رياضي،
 * وخطأ أدى لهدف. الفصل البصري هنا مقصود حتى ينتبه الأدمن أنه بصدد تسجيل خصم،
 * ولتقليل احتمال الضغط عليها بالغلط أثناء تسجيل إحصائيات إيجابية.
 */
@Composable
fun ViolationsSection(
    ownGoals: Int,
    onOwnGoalsChange: (Int) -> Unit,
    violentObjection: Int,
    onViolentObjectionChange: (Int) -> Unit,
    errorLeadingToGoal: Int,
    onErrorLeadingToGoalChange: (Int) -> Unit
) {
    val violationRed = Color(0xFFEF5350)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF2A1212).copy(alpha = 0.6f),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, violationRed.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("⚠️", fontSize = 15.sp)
                Text(
                    text = "مخالفات (تخصم نقاط)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = violationRed
                )
            }

            StatCounterRow(
                title = "هدف عكسي (أون جول)",
                icon = "🤦‍♂️",
                count = ownGoals,
                onValueChange = onOwnGoalsChange,
                accentColor = violationRed
            )

            StatCounterRow(
                title = "اعتراض غير رياضي / خروج عن النص",
                icon = "🚫",
                count = violentObjection,
                onValueChange = onViolentObjectionChange,
                accentColor = violationRed
            )

            StatCounterRow(
                title = "خطأ فردي أدى لهدف",
                icon = "🎯",
                count = errorLeadingToGoal,
                onValueChange = onErrorLeadingToGoalChange,
                accentColor = violationRed
            )
        }
    }
}

@Composable
fun StatCounterRow(
    title: String,
    icon: String,
    count: Int,
    onValueChange: (Int) -> Unit,
    accentColor: Color = GoldAccent
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(icon, fontSize = 18.sp)
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { if (count > 0) onValueChange(count - 1) },
                modifier = Modifier
                    .size(28.dp)
                    .background(Color.Red.copy(alpha = 0.25f), CircleShape)
            ) {
                Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Red)
            }

            Text(
                text = "$count",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                modifier = Modifier.widthIn(min = 18.dp),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = { onValueChange(count + 1) },
                modifier = Modifier
                    .size(28.dp)
                    .background(StadiumGreenPrimary.copy(alpha = 0.35f), CircleShape)
            ) {
                Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = StadiumGreenPrimary)
            }
        }
    }
}
