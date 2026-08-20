package com.example.data.local

import androidx.room.Embedded
import androidx.room.Entity

@Entity(
    tableName = "match_players",
    primaryKeys = ["matchId", "playerId"]
)
data class MatchPlayerEntity(
    val matchId: Long,
    val playerId: Long,
    val teamNumber: Int, // 1 for Team 1, 2 for Team 2
    val positionOnPitch: String, // "GK", "DF1", "DF2", "MF1", "MF2", "FW1", etc.
    val goals: Int = 0,
    val assists: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val isMotm: Boolean = false,
    val isCleanSheet: Boolean = false,
    val penaltyMissed: Int = 0,
    val violentObjection: Int = 0,
    val ownGoals: Int = 0,
    val errorLeadingToGoal: Int = 0,
    val totalPointsEarned: Int = 0,
    val penaltiesScored: Int = 0,
    val penaltySaves: Int = 0,
    val goalLineSaves: Int = 0
)

/**
 * نتيجة استعلام يجمع بيانات اللاعب في مباراة معينة مع تاريخ تلك المباراة ونتيجتها،
 * يُستخدم لتجميع سجلات الشهر والموسم (بدون الحاجة لتحميل كل المباريات كل مرة).
 */
data class MatchPlayerWithMatchInfo(
    @Embedded val matchPlayer: MatchPlayerEntity,
    val matchTimestamp: Long,
    val team1Score: Int,
    val team2Score: Int
)
