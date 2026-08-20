package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KooraDao {

    @Query("SELECT COUNT(*) FROM players")
    suspend fun getPlayersCount(): Int

    @Query("SELECT COUNT(*) FROM matches")
    suspend fun getMatchesCount(): Int

    // Player Operations
    @Query("SELECT * FROM players ORDER BY totalPoints DESC")
    fun getAllPlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :id")
    suspend fun getPlayerById(id: Long): PlayerEntity?

    @Query("SELECT * FROM players WHERE isAttending = 1")
    fun getAttendingPlayers(): Flow<List<PlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)

    @Query("UPDATE players SET isAttending = :isAttending WHERE id = :playerId")
    suspend fun updateRsvpStatus(playerId: Long, isAttending: Boolean)

    @Query("UPDATE players SET isAttending = 0")
    suspend fun resetAllRsvps()

    @Query("UPDATE players SET matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, seasonRating = 6.0")
    suspend fun resetAllPlayerStats()

    // Match Operations
    @Query("SELECT * FROM matches ORDER BY id DESC LIMIT 1")
    fun getLatestMatch(): Flow<MatchEntity?>

    @Query("SELECT * FROM matches WHERE isFinished = 1 ORDER BY id DESC")
    fun getFinishedMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchById(matchId: Long): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity): Long

    @Update
    suspend fun updateMatch(match: MatchEntity)

    // Match Player Operations
    @Query("SELECT * FROM match_players WHERE matchId = :matchId")
    fun getMatchPlayers(matchId: Long): Flow<List<MatchPlayerEntity>>

    @Query("SELECT * FROM match_players WHERE matchId = :matchId")
    suspend fun getMatchPlayersList(matchId: Long): List<MatchPlayerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchPlayers(matchPlayers: List<MatchPlayerEntity>)

    @Query("DELETE FROM match_players WHERE matchId = :matchId")
    suspend fun deleteMatchPlayers(matchId: Long)

    // يجمع كل سجلات اللاعبين من المباريات المنتهية فقط، مع تاريخ ونتيجة كل مباراة،
    // لتُستخدم في حساب سجلات وجوائز الشهر والموسم بدقة من التاريخ الحقيقي للمباراة.
    @Query(
        """
        SELECT mp.*, m.matchTimestamp as matchTimestamp, m.team1Score as team1Score, m.team2Score as team2Score
        FROM match_players mp
        INNER JOIN matches m ON mp.matchId = m.id
        WHERE m.isFinished = 1
        """
    )
    fun getAllFinishedMatchPlayersWithInfo(): Flow<List<MatchPlayerWithMatchInfo>>
}
