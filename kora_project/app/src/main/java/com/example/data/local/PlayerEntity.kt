package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val nickname: String,
    val position: String, // "Goalkeeper", "Defender", "Midfielder", "Forward"
    val level: String,    // "Level 1", "Level 2", "Level 3", or "N/A" for GK
    val favoriteClub: String,
    val topTraits: String, // Comma separated traits
    val avatarType: String, // "sultan", "ameed", "ostora", "asad", "maldini", "ronaldinho", "treka", "motifa", "asad_aali", "taja", "default"
    val matchesPlayed: Int = 0,
    val goals: Int = 0,
    val assists: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val totalPoints: Int = 0,
    val cleanSheets: Int = 0,
    val seasonRating: Float = 7.5f,
    val isAttending: Boolean = false // RSVP status for next Friday match
)
