package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import com.example.util.MatchDateUtils

@Composable
fun NewMatchDialog(
    onDismiss: () -> Unit,
    onSave: (date: String, time: String, targetCount: Int) -> Unit
) {
    var date by remember { mutableStateOf(MatchDateUtils.getNextFridayDateString()) }
    var time by remember { mutableStateOf(MatchDateUtils.getDefaultTimeString()) }
    var playerCount by remember { mutableStateOf("16") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("⚽", fontSize = 22.sp)
                Text(
                    text = "إضافة مباراة جديدة بتاريخ جديد",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "حدد موعد المباراة الجديدة وسيقوم البرنامج بتحديث الكشف والتشكيل تلقائياً.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )

                // Date Input
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("تاريخ المباراة") },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = GoldAccent) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Quick Button to auto-set Next Friday
                OutlinedButton(
                    onClick = {
                        date = MatchDateUtils.getNextFridayDateString()
                        time = MatchDateUtils.getDefaultTimeString()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("📅 ضبط تلقائي ليوم الجمعة القادم", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // Time Input
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("توقيت المباراة") },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = GoldAccent) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Player Count Input
                OutlinedTextField(
                    value = playerCount,
                    onValueChange = { playerCount = it },
                    label = { Text("عدد اللاعبين المستهدف (مثال: 16)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val count = playerCount.toIntOrNull() ?: 16
                    if (date.isNotBlank() && time.isNotBlank()) {
                        onSave(date, time, count)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("حفظ وإنشاء المباراة ⚽", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Color.White.copy(alpha = 0.7f))
            }
        }
    )
}
