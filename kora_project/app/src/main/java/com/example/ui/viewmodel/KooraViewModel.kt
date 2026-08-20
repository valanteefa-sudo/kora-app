package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.BalancedLineup
import com.example.data.ai.GeminiAiService
import com.example.data.local.*
import com.example.data.repository.KooraRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class KooraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KooraRepository
    val allPlayers: StateFlow<List<PlayerEntity>>
    val attendingPlayers: StateFlow<List<PlayerEntity>>
    val latestMatch: StateFlow<MatchEntity?>
    val finishedMatches: StateFlow<List<MatchEntity>>

    private val _isAdminMode = MutableStateFlow(true)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    private val _lineupOptions = MutableStateFlow<List<BalancedLineup>>(emptyList())
    val lineupOptions: StateFlow<List<BalancedLineup>> = _lineupOptions.asStateFlow()

    private val _selectedLineupIndex = MutableStateFlow(0)
    val selectedLineupIndex: StateFlow<Int> = _selectedLineupIndex.asStateFlow()

    private val _team1Players = MutableStateFlow<List<PlayerEntity>>(emptyList())
    val team1Players: StateFlow<List<PlayerEntity>> = _team1Players.asStateFlow()

    private val _team2Players = MutableStateFlow<List<PlayerEntity>>(emptyList())
    val team2Players: StateFlow<List<PlayerEntity>> = _team2Players.asStateFlow()

    private val _team1KitName = MutableStateFlow("الفريق الأبيض")
    val team1KitName: StateFlow<String> = _team1KitName.asStateFlow()

    private val _team1KitColor = MutableStateFlow(0xFFFFFFFFL)
    val team1KitColor: StateFlow<Long> = _team1KitColor.asStateFlow()

    private val _team2KitName = MutableStateFlow("الفريق الأسود")
    val team2KitName: StateFlow<String> = _team2KitName.asStateFlow()

    private val _team2KitColor = MutableStateFlow(0xFF1E1E24L)
    val team2KitColor: StateFlow<Long> = _team2KitColor.asStateFlow()

    private val _aiTacticalAnalysis = MutableStateFlow("جاري تجهيز التحليل والتكتيك المتوازن للمباراة...")
    val aiTacticalAnalysis: StateFlow<String> = _aiTacticalAnalysis.asStateFlow()

    // Live Match Timer
    private val _liveTimerSeconds = MutableStateFlow(0L)
    val liveTimerSeconds: StateFlow<Long> = _liveTimerSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

    // Live Match Evaluation Map: playerId -> MatchPlayerEntity
    private val _matchEvaluations = MutableStateFlow<Map<Long, MatchPlayerEntity>>(emptyMap())
    val matchEvaluations: StateFlow<Map<Long, MatchPlayerEntity>> = _matchEvaluations.asStateFlow()

    init {
        val database = KooraDatabase.getDatabase(application, viewModelScope)
        repository = KooraRepository(database.kooraDao())

        allPlayers = repository.allPlayers.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        attendingPlayers = repository.attendingPlayers.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        latestMatch = repository.latestMatch.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), null
        )

        finishedMatches = repository.finishedMatches.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        // Ensure default players and match exist
        viewModelScope.launch {
            repository.checkAndRestoreDefaultPlayers()
        }

        // Whenever latest match is updated, sync kit colors and names
        viewModelScope.launch {
            latestMatch.collect { match ->
                if (match != null) {
                    _team1KitName.value = match.team1ColorName
                    _team1KitColor.value = match.team1HexColor
                    _team2KitName.value = match.team2ColorName
                    _team2KitColor.value = match.team2HexColor
                }
            }
        }

        // Whenever attending players change, regenerate lineups
        viewModelScope.launch {
            attendingPlayers.collect { attending ->
                if (attending.isNotEmpty()) {
                    val options = GeminiAiService.generateLineupOptions(attending)
                    _lineupOptions.value = options
                    if (options.isNotEmpty()) {
                        setLineupOption(0)
                    }
                }
            }
        }
    }

    fun resetAllPlayerPoints() {
        viewModelScope.launch {
            repository.resetAllPlayerStats()
        }
    }

    fun setAdminMode(enabled: Boolean) {
        _isAdminMode.value = enabled
    }

    fun toggleRsvp(playerId: Long, isAttending: Boolean) {
        viewModelScope.launch {
            repository.updateRsvpStatus(playerId, isAttending)
        }
    }

    fun setBulkRsvp(isAttending: Boolean) {
        viewModelScope.launch {
            allPlayers.value.forEach { player ->
                repository.updateRsvpStatus(player.id, isAttending)
            }
        }
    }

    fun autoSetMatchToNextFriday() {
        viewModelScope.launch {
            val current = latestMatch.value
            val nextFridayDate = com.example.util.MatchDateUtils.getNextFridayDateString()
            val nextFridayTime = com.example.util.MatchDateUtils.getDefaultTimeString()
            val nextFridayTimestamp = com.example.util.MatchDateUtils.getNextFridayEpochMillis()
            if (current != null) {
                val updated = current.copy(
                    dateString = nextFridayDate,
                    timeString = nextFridayTime,
                    matchTimestamp = nextFridayTimestamp
                )
                repository.updateMatch(updated)
            } else {
                createNextMatch(nextFridayDate, nextFridayTime, 16)
            }
        }
    }

    fun generateAiLineups() {
        val attending = attendingPlayers.value
        if (attending.isNotEmpty()) {
            val options = GeminiAiService.generateLineupOptions(attending)
            _lineupOptions.value = options
            setLineupOption(0)

            viewModelScope.launch {
                val advice = GeminiAiService.fetchGeminiTacticalAdvice(attending)
                _aiTacticalAnalysis.value = advice
            }
        }
    }

    fun setLineupOption(index: Int) {
        if (index in lineupOptions.value.indices) {
            _selectedLineupIndex.value = index
            val option = lineupOptions.value[index]
            _team1Players.value = option.team1
            _team2Players.value = option.team2
            initMatchEvaluations(option.team1, option.team2)
        }
    }

    private fun initMatchEvaluations(t1: List<PlayerEntity>, t2: List<PlayerEntity>) {
        val matchId = latestMatch.value?.id ?: 1L
        val map = mutableMapOf<Long, MatchPlayerEntity>()
        t1.forEach {
            map[it.id] = MatchPlayerEntity(
                matchId = matchId, playerId = it.id, teamNumber = 1, positionOnPitch = it.position
            )
        }
        t2.forEach {
            map[it.id] = MatchPlayerEntity(
                matchId = matchId, playerId = it.id, teamNumber = 2, positionOnPitch = it.position
            )
        }
        _matchEvaluations.value = map
    }

    fun swapPlayersInFormation(p1: PlayerEntity, p2: PlayerEntity) {
        val t1 = _team1Players.value.toMutableList()
        val t2 = _team2Players.value.toMutableList()

        val inT1_p1 = t1.any { it.id == p1.id }
        val inT1_p2 = t1.any { it.id == p2.id }

        if (inT1_p1 && !inT1_p2) { // Swap across teams
            t1.removeIf { it.id == p1.id }
            t2.removeIf { it.id == p2.id }
            t1.add(p2)
            t2.add(p1)
        } else if (!inT1_p1 && inT1_p2) {
            t1.removeIf { it.id == p2.id }
            t2.removeIf { it.id == p1.id }
            t1.add(p1)
            t2.add(p2)
        } else if (inT1_p1 && inT1_p2) { // Within Team 1
            val idx1 = t1.indexOfFirst { it.id == p1.id }
            val idx2 = t1.indexOfFirst { it.id == p2.id }
            if (idx1 != -1 && idx2 != -1) {
                t1[idx1] = p2
                t1[idx2] = p1
            }
        } else { // Within Team 2
            val idx1 = t2.indexOfFirst { it.id == p1.id }
            val idx2 = t2.indexOfFirst { it.id == p2.id }
            if (idx1 != -1 && idx2 != -1) {
                t2[idx1] = p2
                t2[idx2] = p1
            }
        }

        _team1Players.value = t1
        _team2Players.value = t2
        initMatchEvaluations(t1, t2)
    }

    fun updateTeamKits(team1Name: String, team1ColorHex: Long, team2Name: String, team2ColorHex: Long) {
        _team1KitName.value = team1Name
        _team1KitColor.value = team1ColorHex
        _team2KitName.value = team2Name
        _team2KitColor.value = team2ColorHex

        viewModelScope.launch {
            val current = latestMatch.value ?: return@launch
            val updated = current.copy(
                team1ColorName = team1Name,
                team1HexColor = team1ColorHex,
                team2ColorName = team2Name,
                team2HexColor = team2ColorHex
            )
            repository.updateMatch(updated)
        }
    }

    fun confirmFormation(team1Name: String, team1ColorHex: Long, team2Name: String, team2ColorHex: Long) {
        _team1KitName.value = team1Name
        _team1KitColor.value = team1ColorHex
        _team2KitName.value = team2Name
        _team2KitColor.value = team2ColorHex

        viewModelScope.launch {
            val current = latestMatch.value ?: return@launch
            val updated = current.copy(
                isFormationConfirmed = true,
                team1ColorName = team1Name,
                team1HexColor = team1ColorHex,
                team2ColorName = team2Name,
                team2HexColor = team2ColorHex,
                aiTacticalAnalysis = aiTacticalAnalysis.value
            )
            repository.updateMatch(updated)
        }
    }

    // Live Match Timer
    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value) {
                delay(1000)
                _liveTimerSeconds.value += 1
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        _liveTimerSeconds.value = 0
    }

    // Evaluation updates
    fun updatePlayerStat(playerId: Long, update: (MatchPlayerEntity) -> MatchPlayerEntity) {
        val currentMap = _matchEvaluations.value.toMutableMap()
        val currentEval = currentMap[playerId] ?: return
        currentMap[playerId] = update(currentEval)
        _matchEvaluations.value = currentMap
    }

    fun finalizeMatch(team1Score: Int, team2Score: Int, motmPlayerId: Long?) {
        viewModelScope.launch {
            val match = latestMatch.value ?: return@launch
            val updatedMatch = match.copy(
                team1Score = team1Score,
                team2Score = team2Score,
                motmPlayerId = motmPlayerId,
                isFinished = true
            )

            val evals = _matchEvaluations.value.values.map { eval ->
                if (eval.playerId == motmPlayerId) {
                    eval.copy(isMotm = true)
                } else eval
            }

            repository.finalizeMatchResults(updatedMatch, evals)
            pauseTimer()
        }
    }

    // Admin Player Actions
    fun addPlayer(player: PlayerEntity) {
        viewModelScope.launch {
            repository.insertPlayer(player)
        }
    }

    fun updatePlayer(player: PlayerEntity) {
        viewModelScope.launch {
            repository.updatePlayer(player)
        }
    }

    fun deletePlayer(player: PlayerEntity) {
        viewModelScope.launch {
            repository.deletePlayer(player)
        }
    }

    fun createNextMatch(dateString: String, timeString: String, targetCount: Int) {
        viewModelScope.launch {
            val newMatch = MatchEntity(
                dateString = dateString,
                timeString = timeString,
                targetPlayerCount = targetCount,
                isFinished = false,
                matchTimestamp = com.example.util.MatchDateUtils.getNextFridayEpochMillis()
            )
            repository.insertMatch(newMatch)
            resetTimer()
        }
    }

    // ================= سجلات وجوائز الشهر والموسم =================
    // نجمّع نقاط/أهداف/أسيست/شباك نظيفة كل لاعب من المباريات المنتهية فعلياً خلال
    // الشهر الحالي أو الموسم الحالي (أغسطس - يوليو)، بدلاً من الاعتماد على أرقام
    // تراكمية منذ بداية استخدام التطبيق. هذا يضمن أن "لاعب الشهر" و"لاعب الموسم"
    // يعكسان الأداء الفعلي في تلك الفترة فقط.

    private val monthRange = com.example.util.MatchDateUtils.getCurrentMonthRange()
    private val seasonRange = com.example.util.MatchDateUtils.getCurrentSeasonRange()

    val currentMonthLabel: String = monthRange.third
    val currentSeasonLabel: String = seasonRange.third

    val monthlyPlayerStats: StateFlow<List<PlayerPeriodStat>> = combine(
        repository.allFinishedMatchPlayersWithInfo, allPlayers
    ) { records, players ->
        buildPeriodStats(records, players, monthRange.first, monthRange.second)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val seasonPlayerStats: StateFlow<List<PlayerPeriodStat>> = combine(
        repository.allFinishedMatchPlayersWithInfo, allPlayers
    ) { records, players ->
        buildPeriodStats(records, players, seasonRange.first, seasonRange.second)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun buildPeriodStats(
        records: List<MatchPlayerWithMatchInfo>,
        players: List<PlayerEntity>,
        rangeStart: Long,
        rangeEnd: Long
    ): List<PlayerPeriodStat> {
        val inRange = records.filter { it.matchTimestamp in rangeStart..rangeEnd }
        return inRange
            .groupBy { it.matchPlayer.playerId }
            .mapNotNull { (playerId, playerRecords) ->
                val player = players.find { it.id == playerId } ?: return@mapNotNull null
                PlayerPeriodStat(
                    player = player,
                    matchesPlayed = playerRecords.size,
                    goals = playerRecords.sumOf { it.matchPlayer.goals },
                    assists = playerRecords.sumOf { it.matchPlayer.assists },
                    cleanSheets = playerRecords.count { it.matchPlayer.isCleanSheet },
                    motmCount = playerRecords.count { it.matchPlayer.isMotm },
                    yellowCards = playerRecords.sumOf { it.matchPlayer.yellowCards },
                    redCards = playerRecords.sumOf { it.matchPlayer.redCards },
                    totalPoints = playerRecords.sumOf { it.matchPlayer.totalPointsEarned }
                )
            }
            .sortedByDescending { it.totalPoints }
    }
}

/**
 * إحصائيات لاعب مجمّعة خلال فترة معينة (شهر أو موسم) - محسوبة من سجلات المباريات
 * الفعلية الواقعة داخل تلك الفترة فقط.
 */
data class PlayerPeriodStat(
    val player: PlayerEntity,
    val matchesPlayed: Int,
    val goals: Int,
    val assists: Int,
    val cleanSheets: Int,
    val motmCount: Int,
    val yellowCards: Int,
    val redCards: Int,
    val totalPoints: Int
)
