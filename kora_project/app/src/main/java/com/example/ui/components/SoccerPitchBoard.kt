package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PlayerEntity
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PitchFieldBase
import com.example.ui.theme.PitchFieldStripe
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.StadiumGreenPrimary
import com.example.util.PlayerBanterQuotes
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SoccerPitchBoard(
    team1Name: String,
    team1Color: Color,
    team1Players: List<PlayerEntity>,
    team2Name: String,
    team2Color: Color,
    team2Players: List<PlayerEntity>,
    onSwapPlayers: (PlayerEntity, PlayerEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPlayer by remember { mutableStateOf<PlayerEntity?>(null) }

    // كل لاعبي المباراة (الفريقين مع بعض) - مستخدمة لاختيار اسم عشوائي في رسائل
    // البانتر الكوميدية ("لاعبني في أي فريق المهم بعيد عن فلان").
    val allMatchPlayers = remember(team1Players, team2Players) { team1Players + team2Players }

    // حالة رسالة البانتر الكوميدية: اللاعب اللي "بيتكلم" + الجملة المختارة له.
    var banterPlayer by remember { mutableStateOf<PlayerEntity?>(null) }
    var banterText by remember { mutableStateOf("") }

    // تختفي رسالة البانتر تلقائيًا بعد ثواني قليلة عشان متعطلش استخدام الشاشة.
    LaunchedEffect(banterPlayer) {
        if (banterPlayer != null) {
            delay(2600)
            banterPlayer = null
        }
    }

    // ينفّذ التبديل الفعلي، وإذا كان تبديلاً بين فريقين مختلفين (يعني لاعب فعليًا
    // "بيتشال" من فريقه ويحل مكانه التاني)، بيظهر رسالة كوميدية من أحد اللاعبين
    // المتبدلين عشوائيًا، احتفالًا بخفة الدم بدل ما يكون التبديل حدث جامد.
    fun performSwap(p1: PlayerEntity, p2: PlayerEntity) {
        val p1InTeam1 = team1Players.any { it.id == p1.id }
        val p2InTeam1 = team1Players.any { it.id == p2.id }
        val isCrossTeamSwap = p1InTeam1 != p2InTeam1

        if (isCrossTeamSwap) {
            val speaker = if (Random.nextBoolean()) p1 else p2
            val otherNames = allMatchPlayers
                .filter { it.id != speaker.id }
                .map { it.nickname.ifBlank { it.name.split(" ").first() } }
            banterPlayer = speaker
            banterText = PlayerBanterQuotes.randomQuote(otherNames)
        }

        onSwapPlayers(p1, p2)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(600.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(PitchFieldBase)
            .border(2.dp, GoldAccent, RoundedCornerShape(20.dp))
    ) {
        // Draw Soccer Field Markings Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pitchWidth = size.width
            val pitchHeight = size.height

            // Grass Strips Pattern
            val stripeHeight = pitchHeight / 10
            for (i in 0..9) {
                if (i % 2 == 0) {
                    drawRect(
                        color = PitchFieldStripe,
                        topLeft = Offset(0f, i * stripeHeight),
                        size = Size(pitchWidth, stripeHeight)
                    )
                }
            }

            // Field Outer Border Lines
            val padding = 20f
            val fieldRectWidth = pitchWidth - (padding * 2)
            val fieldRectHeight = pitchHeight - (padding * 2)

            drawRect(
                color = Color.White.copy(alpha = 0.7f),
                topLeft = Offset(padding, padding),
                size = Size(fieldRectWidth, fieldRectHeight),
                style = Stroke(width = 3.5f)
            )

            // Halfway Line
            val midY = pitchHeight / 2
            drawLine(
                color = Color.White.copy(alpha = 0.7f),
                start = Offset(padding, midY),
                end = Offset(pitchWidth - padding, midY),
                strokeWidth = 3.5f
            )

            // Center Circle
            val centerRadius = fieldRectWidth * 0.18f
            drawCircle(
                color = Color.White.copy(alpha = 0.7f),
                radius = centerRadius,
                center = Offset(pitchWidth / 2, midY),
                style = Stroke(width = 3.5f)
            )
            drawCircle(
                color = Color.White,
                radius = 5f,
                center = Offset(pitchWidth / 2, midY)
            )

            // Penalty Boxes Top & Bottom
            val penaltyWidth = fieldRectWidth * 0.55f
            val penaltyHeight = fieldRectHeight * 0.17f
            
            // Top Penalty Area
            drawRect(
                color = Color.White.copy(alpha = 0.7f),
                topLeft = Offset((pitchWidth - penaltyWidth) / 2, padding),
                size = Size(penaltyWidth, penaltyHeight),
                style = Stroke(width = 3.5f)
            )

            // Bottom Penalty Area
            drawRect(
                color = Color.White.copy(alpha = 0.7f),
                topLeft = Offset((pitchWidth - penaltyWidth) / 2, pitchHeight - padding - penaltyHeight),
                size = Size(penaltyWidth, penaltyHeight),
                style = Stroke(width = 3.5f)
            )
        }

        // Field Players Container Overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            // TEAM 1 AREA (Top Half)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Team 1 Header Banner
                Surface(
                    color = team1Color,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(GoldAccent, CircleShape)
                        )
                        Text(
                            text = "فريق $team1Name (${team1Players.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (team1Color == Color.White || team1Color == Color.Yellow || team1Color == Color.Cyan) Color.Black else Color.White
                        )
                    }
                }

                // Render ALL Team 1 Players by Position cleanly
                val t1Gk = team1Players.filter { it.position == "Goalkeeper" }
                val t1Df = team1Players.filter { it.position == "Defender" }
                val t1Mf = team1Players.filter { it.position == "Midfielder" }
                val t1Fw = team1Players.filter { it.position == "Forward" }
                val t1Other = team1Players.filter { it.position !in listOf("Goalkeeper", "Defender", "Midfielder", "Forward") }

                t1Gk.chunked(4).forEach { row ->
                    PlayerRowOnPitch(row, team1Color, selectedPlayer, nameAbove = true) { player ->
                        handlePlayerTap(player, selectedPlayer, onSwap = { p1, p2 -> performSwap(p1, p2) }) { selectedPlayer = it }
                    }
                }
                t1Df.chunked(4).forEach { row ->
                    PlayerRowOnPitch(row, team1Color, selectedPlayer, nameAbove = true) { player ->
                        handlePlayerTap(player, selectedPlayer, onSwap = { p1, p2 -> performSwap(p1, p2) }) { selectedPlayer = it }
                    }
                }
                t1Mf.chunked(4).forEach { row ->
                    PlayerRowOnPitch(row, team1Color, selectedPlayer, nameAbove = true) { player ->
                        handlePlayerTap(player, selectedPlayer, onSwap = { p1, p2 -> performSwap(p1, p2) }) { selectedPlayer = it }
                    }
                }
                t1Fw.chunked(4).forEach { row ->
                    PlayerRowOnPitch(row, team1Color, selectedPlayer, nameAbove = true) { player ->
                        handlePlayerTap(player, selectedPlayer, onSwap = { p1, p2 -> performSwap(p1, p2) }) { selectedPlayer = it }
                    }
                }
                t1Other.chunked(4).forEach { row ->
                    PlayerRowOnPitch(row, team1Color, selectedPlayer, nameAbove = true) { player ->
                        handlePlayerTap(player, selectedPlayer, onSwap = { p1, p2 -> performSwap(p1, p2) }) { selectedPlayer = it }
                    }
                }
            }

            Divider(color = GoldAccent.copy(alpha = 0.6f), thickness = 1.5.dp)

            // TEAM 2 AREA (Bottom Half)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Render ALL Team 2 Players by Position cleanly
                val t2Gk = team2Players.filter { it.position == "Goalkeeper" }
                val t2Df = team2Players.filter { it.position == "Defender" }
                val t2Mf = team2Players.filter { it.position == "Midfielder" }
                val t2Fw = team2Players.filter { it.position == "Forward" }
                val t2Other = team2Players.filter { it.position !in listOf("Goalkeeper", "Defender", "Midfielder", "Forward") }

                t2Fw.chunked(4).forEach { row ->
                    PlayerRowOnPitch(row, team2Color, selectedPlayer, nameAbove = false) { player ->
                        handlePlayerTap(player, selectedPlayer, onSwap = { p1, p2 -> performSwap(p1, p2) }) { selectedPlayer = it }
                    }
                }
                t2Mf.chunked(4).forEach { row ->
                    PlayerRowOnPitch(row, team2Color, selectedPlayer, nameAbove = false) { player ->
                        handlePlayerTap(player, selectedPlayer, onSwap = { p1, p2 -> performSwap(p1, p2) }) { selectedPlayer = it }
                    }
                }
                t2Df.chunked(4).forEach { row ->
                    PlayerRowOnPitch(row, team2Color, selectedPlayer, nameAbove = false) { player ->
                        handlePlayerTap(player, selectedPlayer, onSwap = { p1, p2 -> performSwap(p1, p2) }) { selectedPlayer = it }
                    }
                }
                t2Other.chunked(4).forEach { row ->
                    PlayerRowOnPitch(row, team2Color, selectedPlayer, nameAbove = false) { player ->
                        handlePlayerTap(player, selectedPlayer, onSwap = { p1, p2 -> performSwap(p1, p2) }) { selectedPlayer = it }
                    }
                }
                t2Gk.chunked(4).forEach { row ->
                    PlayerRowOnPitch(row, team2Color, selectedPlayer, nameAbove = false) { player ->
                        handlePlayerTap(player, selectedPlayer, onSwap = { p1, p2 -> performSwap(p1, p2) }) { selectedPlayer = it }
                    }
                }

                // Team 2 Header Banner
                Surface(
                    color = team2Color,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(GoldAccent, CircleShape)
                        )
                        Text(
                            text = "فريق $team2Name (${team2Players.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (team2Color == Color.White || team2Color == Color.Yellow || team2Color == Color.Cyan) Color.Black else Color.White
                        )
                    }
                }
            }
        }

        // Tap hint banner if player selected - شكل أحدث: أيقونة بخلفية دائرية ملوّنة
        // بدل أيقونة عادية، وحواف أنعم، وتنويع لوني بلمسة السماوي الجديدة.
        selectedPlayer?.let { player ->
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp)
                    .shadow(10.dp, RoundedCornerShape(18.dp)),
                color = Color.Black.copy(alpha = 0.88f),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(SkyAccent.copy(alpha = 0.22f), CircleShape)
                            .border(1.dp, SkyAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = SkyAccent, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = "اختر لاعباً آخر لتبديل الأماكن مع ${player.nickname.ifEmpty { player.name }}",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // فقاعة كلام كوميدية تظهر فوق اللاعب اللي اتنقل من فريقه، بشكل بالون كلام
        // (فقاعة + ذيل صغير) قريب من صورته، وبتختفي تلقائيًا بعد كذا ثانية.
        AnimatedVisibility(
            visible = banterPlayer != null,
            enter = fadeIn() + scaleIn(initialScale = 0.7f),
            exit = fadeOut() + scaleOut(targetScale = 0.7f),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            val speaker = banterPlayer
            if (speaker != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .shadow(12.dp, RoundedCornerShape(16.dp))
                            .widthIn(max = 240.dp),
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, GoldAccent)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PlayerAvatar3D(avatarType = speaker.avatarType, size = 30.dp, showBorder = true, borderColor = GoldAccent)
                            Column {
                                Text(
                                    text = speaker.nickname.ifEmpty { speaker.name.split(" ").first() },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StadiumGreenPrimary
                                )
                                Text(
                                    text = banterText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1A1A1A)
                                )
                            }
                        }
                    }
                    // ذيل الفقاعة (المثلث الصغير) بيوهم إنها طالعة من فوق اللاعب
                    Box(
                        modifier = Modifier
                            .size(width = 14.dp, height = 8.dp)
                            .background(Color.White, RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                    )
                }
            }
        }
    }
}

private fun handlePlayerTap(
    clickedPlayer: PlayerEntity,
    currentlySelected: PlayerEntity?,
    onSwap: (PlayerEntity, PlayerEntity) -> Unit,
    setSelected: (PlayerEntity?) -> Unit
) {
    if (currentlySelected == null) {
        setSelected(clickedPlayer)
    } else if (currentlySelected.id == clickedPlayer.id) {
        setSelected(null)
    } else {
        onSwap(currentlySelected, clickedPlayer)
        setSelected(null)
    }
}

@Composable
private fun PlayerRowOnPitch(
    players: List<PlayerEntity>,
    kitColor: Color,
    selectedPlayer: PlayerEntity?,
    nameAbove: Boolean = false,
    onSelect: (PlayerEntity) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        players.forEach { player ->
            PitchPlayerNode(
                player = player,
                kitColor = kitColor,
                isSelected = selectedPlayer?.id == player.id,
                nameAbove = nameAbove,
                onClick = { onSelect(player) }
            )
        }
    }
}

@Composable
fun PitchPlayerNode(
    player: PlayerEntity,
    kitColor: Color,
    isSelected: Boolean,
    nameAbove: Boolean = false,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (isSelected) 1.3f else 1.0f)
    val avatarSize = 36.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable { onClick() }
            .padding(2.dp)
    ) {
        if (nameAbove) {
            PitchNameTag(player, isSelected)
            Spacer(modifier = Modifier.height(3.dp))
            PitchAvatarWithBadge(player, kitColor, isSelected, avatarSize)
        } else {
            PitchAvatarWithBadge(player, kitColor, isSelected, avatarSize)
            Spacer(modifier = Modifier.height(3.dp))
            PitchNameTag(player, isSelected)
        }
    }
}

/**
 * الاسم فوق/تحت اللاعب - شكل أنعم وأوضح، بحدود ذهبية واضحة لما يكون محدد.
 */
@Composable
private fun PitchNameTag(player: PlayerEntity, isSelected: Boolean) {
    Surface(
        color = Color.Black.copy(alpha = 0.8f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.2.dp else 0.6.dp,
            if (isSelected) GoldAccent else Color.White.copy(alpha = 0.35f)
        )
    ) {
        Text(
            text = player.nickname.ifEmpty { player.name.split(" ").first() },
            fontSize = 9.sp,
            color = if (isSelected) GoldAccent else Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
        )
    }
}

/**
 * صورة اللاعب على الملعب مع شارة قميص أنيقة (بدل نقطة بسيطة) وهالة ذهبية خفيفة
 * حول الصورة لما يكون محدد للتبديل، عشان يبان الاختيار بوضوح ويحس المستخدم إنه
 * فعلاً "لمس" اللاعب ده.
 */
@Composable
private fun PitchAvatarWithBadge(player: PlayerEntity, kitColor: Color, isSelected: Boolean, avatarSize: androidx.compose.ui.unit.Dp) {
    Box(contentAlignment = Alignment.Center) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(avatarSize + 10.dp)
                    .shadow(10.dp, CircleShape)
                    .background(GoldAccent.copy(alpha = 0.35f), CircleShape)
            )
        }

        PlayerAvatar3D(
            avatarType = player.avatarType,
            size = avatarSize,
            showBorder = true,
            borderColor = if (isSelected) GoldAccent else Color.White.copy(alpha = 0.8f)
        )

        // شارة قميص أنيقة: دائرة صغيرة بلون الفريق مع حلقة بيضاء وظل خفيف، بدل نقطة
        // سادة، عشان تحس إنها "شارة" حقيقية مش مجرد لون.
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(13.dp)
                .shadow(2.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(1.5.dp)
                .background(kitColor, CircleShape)
        )
    }
}
