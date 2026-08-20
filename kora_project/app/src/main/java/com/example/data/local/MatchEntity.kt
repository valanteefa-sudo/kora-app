package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateString: String,      // e.g. "جمعة 15 أغسطس 2026"
    val timeString: String,      // e.g. "08:00 مساءً"
    val targetPlayerCount: Int = 16, // 16 or 18
    val team1ColorName: String = "الأبيض",
    val team1HexColor: Long = 0xFFFFFFFF,
    val team2ColorName: String = "الأسود",
    val team2HexColor: Long = 0xFF1E1E24,
    val isFinished: Boolean = false,
    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val motmPlayerId: Long? = null,
    val isFormationConfirmed: Boolean = false,
    val aiTacticalAnalysis: String = "",
    // التاريخ الفعلي للمباراة كـ epoch millis - يُستخدم لتجميع سجلات الشهر والموسم بدقة
    // (بدلاً من الاعتماد على تحليل النص العربي في dateString)
    val matchTimestamp: Long = System.currentTimeMillis()
)
