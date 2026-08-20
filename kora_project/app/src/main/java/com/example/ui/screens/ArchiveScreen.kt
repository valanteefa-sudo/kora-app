package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PitchDarkCanvas
import com.example.ui.theme.PitchDarkSurface
import com.example.ui.viewmodel.KooraViewModel

@Composable
fun ArchiveScreen(viewModel: KooraViewModel) {
    val finishedMatches by viewModel.finishedMatches.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchDarkCanvas)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.History, contentDescription = null, tint = GoldAccent)
            Text(
                text = "أرشيف مباريات الجمعة 📜",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent
            )
        }

        if (finishedMatches.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "لا توجد مباريات مؤرشفة بعد. أنشئ وانهِ أول مباراة جمعة!",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(finishedMatches) { match ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = match.dateString,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = match.timeString,
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }

                            Divider(color = Color.Gray.copy(alpha = 0.2f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "الفريق الأبيض",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${match.team1Score} - ${match.team2Score}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = GoldAccent
                                )
                                Text(
                                    text = "الفريق الأسود",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
