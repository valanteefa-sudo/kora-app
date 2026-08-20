package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.scale
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
import com.example.ui.components.PlayerAvatar3D
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PitchDarkCanvas
import com.example.ui.theme.PitchDarkSurface
import com.example.ui.theme.StadiumGreenPrimary
import com.example.ui.viewmodel.KooraViewModel
import com.example.ui.viewmodel.PlayerPeriodStat

@Composable
fun StatsScreen(viewModel: KooraViewModel) {
    val allPlayers by viewModel.allPlayers.collectAsState()
    val monthlyStats by viewModel.monthlyPlayerStats.collectAsState()
    val seasonStats by viewModel.seasonPlayerStats.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    // 0: الأفضل بالتاريخ, 1: الترتيب العام, 2: سجل الشهر, 3: سجل الموسم
    var selectedTab by remember { mutableStateOf(0) }

    val topPlayer = remember(allPlayers) {
        allPlayers.maxByOrNull { it.totalPoints } ?: allPlayers.firstOrNull()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchDarkCanvas)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "إحصائيات وجوائز 🏆",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = GoldAccent
        )

        // Navigation Sub-tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = PitchDarkSurface,
            contentColor = GoldAccent
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("الأفضل بالتاريخ 🌟", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("ترتيب اللاعبين 📊", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                Text("سجل الشهر 📅", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }) {
                Text("سجل الموسم 🏆", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        when (selectedTab) {
            0 -> {
                // Player of the Month / Year Card
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    topPlayer?.let { mvp ->
                        // Player of the Month Big Feature Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(12.dp, RoundedCornerShape(22.dp)),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
                            border = androidx.compose.foundation.BorderStroke(2.dp, GoldAccent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF2C1E00), PitchDarkSurface)
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        color = GoldAccent,
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text(
                                            text = "👑 لاعب الشهر (Player of the Month) 👑",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                        )
                                    }

                                    // Avatar & Trophy graphic
                                    Box(contentAlignment = Alignment.Center) {
                                        PlayerAvatar3D(
                                            avatarType = mvp.avatarType,
                                            size = 96.dp,
                                            showBorder = true,
                                            borderColor = GoldAccent
                                        )
                                    }

                                    Text(
                                        text = "${mvp.name} (${mvp.nickname})",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    Text(
                                        text = "${mvp.position} • النادي: ${mvp.favoriteClub}",
                                        fontSize = 13.sp,
                                        color = GoldAccent
                                    )

                                    // Stats Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        StatBadge("النقاط", "${mvp.totalPoints}")
                                        StatBadge("الأهداف", "${mvp.goals}")
                                        StatBadge("الأسيست", "${mvp.assists}")
                                        StatBadge("التقييم", "${String.format("%.1f", mvp.seasonRating)}")
                                    }
                                }
                            }
                        }

                        // Player of the Year MVP Banner
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1400)),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = com.example.R.drawable.player_month_trophy_1786036895737),
                                    contentDescription = "Trophy",
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                )

                                Column {
                                    Text(
                                        text = "🏆 جائزة لاعب العام (MVP)",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                    Text(
                                        text = "الأفضل في موسم 2026 بأعلى نسبة فوز وأداء استثنائي!",
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Leaderboard List
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("بحث باسم اللاعب...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldAccent) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    val filteredPlayers = allPlayers.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.nickname.contains(searchQuery, ignoreCase = true)
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredPlayers) { player ->
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
                                            text = "لعب: ${player.matchesPlayed} • فوز: ${player.wins} • خسارة: ${player.losses}",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${player.totalPoints} نقطة",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = GoldAccent
                                        )
                                        Text(
                                            text = if (player.position == "Goalkeeper") "🧤 ${player.cleanSheets} كلين شيت • 🎯 ${player.assists}" else "⚽ ${player.goals} • 🎯 ${player.assists}",
                                            fontSize = 11.sp,
                                            color = Color.LightGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                PeriodRecordsSection(
                    periodLabel = "شهر ${viewModel.currentMonthLabel}",
                    stats = monthlyStats
                )
            }

            3 -> {
                PeriodRecordsSection(
                    periodLabel = "موسم ${viewModel.currentSeasonLabel}",
                    stats = seasonStats
                )
            }
        }
    }
}

/**
 * قسم موحّد لعرض سجل وجوائز أي فترة (شهر أو موسم): يعرض جوائز أفضل الفئات، ثم ترتيب
 * كل اللاعبين المشاركين في هذه الفترة مرتبين حسب النقاط الفعلية التي حصلوا عليها فيها فقط.
 */
@Composable
private fun PeriodRecordsSection(periodLabel: String, stats: List<PlayerPeriodStat>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = PitchDarkSurface,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "📆 $periodLabel",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
                modifier = Modifier.padding(10.dp),
                textAlign = TextAlign.Center
            )
        }

        if (stats.isEmpty()) {
            Text(
                text = "لا توجد مباريات منتهية بعد خلال هذه الفترة. سجل النقاط والجوائز سيظهر هنا تلقائياً بمجرد إنهاء أول مباراة.",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(24.dp)
            )
            return@Column
        }

        val topPoints = stats.maxByOrNull { it.totalPoints }
        val topScorer = stats.maxByOrNull { it.goals }?.takeIf { it.goals > 0 }
        val topAssist = stats.maxByOrNull { it.assists }?.takeIf { it.assists > 0 }
        val topGk = stats.filter { it.player.position == "Goalkeeper" }.maxByOrNull { it.cleanSheets }?.takeIf { it.cleanSheets > 0 }
        val topMotm = stats.maxByOrNull { it.motmCount }?.takeIf { it.motmCount > 0 }

        Text("🏆 جوائز الفترة", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldAccent)

        topPoints?.let { PeriodAwardCard("👑 لاعب الفترة (الأعلى نقاطاً)", it.player, "${it.totalPoints} نقطة في ${it.matchesPlayed} مباراة") }
        topScorer?.let { PeriodAwardCard("⚽ الهداف", it.player, "${it.goals} هدف") }
        topAssist?.let { PeriodAwardCard("🎯 صانع الألعاب", it.player, "${it.assists} أسيست") }
        topGk?.let { PeriodAwardCard("🧤 أفضل حارس مرمى", it.player, "${it.cleanSheets} شباك نظيفة") }
        topMotm?.let { PeriodAwardCard("⭐ الأكثر تتويجاً برجل المباراة", it.player, "${it.motmCount} مرة") }

        Divider(color = GoldAccent.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))

        Text("📊 ترتيب كل اللاعبين في هذه الفترة", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldAccent)

        stats.forEachIndexed { index, stat ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "#${index + 1}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (index == 0) GoldAccent else Color.Gray,
                        modifier = Modifier.width(28.dp)
                    )
                    PlayerAvatar3D(avatarType = stat.player.avatarType, size = 38.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${stat.player.name} (${stat.player.nickname})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Text(
                            text = "لعب: ${stat.matchesPlayed} • ⚽ ${stat.goals} • 🎯 ${stat.assists}" +
                                    if (stat.player.position == "Goalkeeper") " • 🧤 ${stat.cleanSheets}" else "",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                    Text(
                        text = "${stat.totalPoints} نقطة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = GoldAccent
                    )
                }
            }
        }
    }
}

@Composable
private fun PeriodAwardCard(title: String, player: PlayerEntity, statText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlayerAvatar3D(avatarType = player.avatarType, size = 48.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GoldAccent)
                Text(text = "${player.name} (${player.nickname})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                Text(text = statText, fontSize = 12.sp, color = Color.LightGray)
            }
        }
    }
}

@Composable
private fun StatBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
    }
}


