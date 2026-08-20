package com.example.data.repository

import com.example.data.local.*
import com.example.util.PointsCalculator
import kotlinx.coroutines.flow.Flow

class KooraRepository(private val dao: KooraDao) {

    val allPlayers: Flow<List<PlayerEntity>> = dao.getAllPlayers()
    val attendingPlayers: Flow<List<PlayerEntity>> = dao.getAttendingPlayers()
    val latestMatch: Flow<MatchEntity?> = dao.getLatestMatch()
    val finishedMatches: Flow<List<MatchEntity>> = dao.getFinishedMatches()

    // كل سجلات اللاعبين من المباريات المنتهية مع تاريخ ونتيجة كل مباراة - يُستخدم
    // لحساب سجلات وجوائز الشهر والموسم الحالي في الواجهة.
    val allFinishedMatchPlayersWithInfo: Flow<List<MatchPlayerWithMatchInfo>> =
        dao.getAllFinishedMatchPlayersWithInfo()

    fun getMatchPlayers(matchId: Long): Flow<List<MatchPlayerEntity>> = dao.getMatchPlayers(matchId)

    suspend fun updateRsvpStatus(playerId: Long, isAttending: Boolean) {
        dao.updateRsvpStatus(playerId, isAttending)
    }

    suspend fun insertPlayer(player: PlayerEntity) {
        dao.insertPlayer(player)
    }

    suspend fun updatePlayer(player: PlayerEntity) {
        dao.updatePlayer(player)
    }

    suspend fun deletePlayer(player: PlayerEntity) {
        dao.deletePlayer(player)
    }

    suspend fun insertMatch(match: MatchEntity): Long {
        return dao.insertMatch(match)
    }

    suspend fun updateMatch(match: MatchEntity) {
        dao.updateMatch(match)
    }

    suspend fun saveMatchPlayers(matchPlayers: List<MatchPlayerEntity>) {
        dao.insertMatchPlayers(matchPlayers)
    }

    suspend fun resetAllPlayerStats() {
        dao.resetAllPlayerStats()
    }

    suspend fun checkAndRestoreDefaultPlayers() {
        val full30DefaultPlayers = listOf(
            PlayerEntity(
                id = 1, name = "هاني عبد الخالق", nickname = "السلطان", position = "Forward",
                level = "Level 3", favoriteClub = "الأهلي",
                topTraits = "هداف, مهاري, خلوق, يكره الهزيمة", avatarType = "sultan",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 2, name = "أيمن محمد", nickname = "العميد", position = "Forward",
                level = "Level 3", favoriteClub = "الأهلي",
                topTraits = "مهاجم مخضرم, ضربات رأس, ذكي جداً", avatarType = "ameed",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 3, name = "حمادة عبد الفتاح", nickname = "الشيخ حمادة", position = "Midfielder",
                level = "Level 2", favoriteClub = "الزمالك",
                topTraits = "قوي بدنياً, زملكاوي غير متحيز", avatarType = "sheikh",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 4, name = "محمد عبد اللاه", nickname = "الأسطورة", position = "Defender",
                level = "Level 1", favoriteClub = "الأهلي",
                topTraits = "قراءة ممتازة للعب, قوي في الالتحامات, كرات عالية", avatarType = "ostora",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 5, name = "كريم صلاح", nickname = "الأسد", position = "Defender",
                level = "Level 1", favoriteClub = "الأهلي",
                topTraits = "قوي, عنيف, أحياناً متهور", avatarType = "asad",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 6, name = "أحمد مصطفى", nickname = "مالديني", position = "Defender",
                level = "Level 2", favoriteClub = "الزمالك",
                topTraits = "مرح, كوميدي, يجيد الكرات الأرضية", avatarType = "maldini",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 7, name = "عماد صلاح", nickname = "العمدة", position = "Defender",
                level = "Level 2", favoriteClub = "الأهلي",
                topTraits = "يجيد التسديد البعيد, مرح", avatarType = "omda",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 8, name = "محمد حلمي", nickname = "حلمي", position = "Defender",
                level = "Level 2", favoriteClub = "الأهلي",
                topTraits = "أاهلاوي, يجيد العرضيات, يجيد التسديد", avatarType = "helmy",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 9, name = "وجدي", nickname = "رونالدينيو", position = "Midfielder",
                level = "Level 1", favoriteClub = "الزمالك",
                topTraits = "مهاري, تسديد قوي, ركلات ثابتة, زملكاوي", avatarType = "ronaldinho",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 10, name = "السيد صديق", nickname = "بوبو", position = "Forward",
                level = "Level 1", favoriteClub = "الأهلي",
                topTraits = "هداف, يجيد صناعة اللعب, تسديد بسن القدم", avatarType = "bubu",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 11, name = "محمد مصطفى", nickname = "تريكة", position = "Forward",
                level = "Level 1", favoriteClub = "الأهلي",
                topTraits = "مهاري, هداف, يجيد اللعب بكلتا القدمين", avatarType = "treka",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 12, name = "محمد عبد اللطيف", nickname = "مو تيفا", position = "Forward",
                level = "Level 1", favoriteClub = "الأهلي",
                topTraits = "سريع, هداف, يجيد صناعة اللعب, بكلتا القدمين", avatarType = "motifa",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 13, name = "محمد عبد الفتاح", nickname = "تاحا", position = "Goalkeeper",
                level = "N/A", favoriteClub = "الأهلي",
                topTraits = "حارس مرمى قوي, رد فعل سريع", avatarType = "taja",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 14, name = "الشناوي", nickname = "شنو", position = "Goalkeeper",
                level = "N/A", favoriteClub = "الأهلي",
                topTraits = "حارس مرمى عملاق, شباك نظيفة", avatarType = "asad_aali",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 15, name = "أحمد هاني", nickname = "لوفي", position = "Midfielder",
                level = "Level 1", favoriteClub = "الأهلي",
                topTraits = "شاب, قوي دفاعياً, لياقة عالية", avatarType = "ahmed_hany",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 16, name = "مصطفى", nickname = "درش", position = "Midfielder",
                level = "Level 2", favoriteClub = "الزمالك",
                topTraits = "تسديد قوي, صناعة لعب, زملكاوي", avatarType = "darsh",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 17, name = "ديسكا", nickname = "الفهد الأسمر", position = "Forward",
                level = "Level 2", favoriteClub = "الأهلي",
                topTraits = "مهاجم سريع, مهاري, الفهد الأسمر", avatarType = "sultan",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 18, name = "فيجو", nickname = "الفنان فيجو", position = "Midfielder",
                level = "Level 2", favoriteClub = "الزمالك",
                topTraits = "لاعب وسط ممتع, تمريرات دقيقة, زملكاوي", avatarType = "sheikh",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 19, name = "مدحت", nickname = "الفيلسوف مدحت", position = "Midfielder",
                level = "Level 2", favoriteClub = "الأهلي",
                topTraits = "فيلسوف الملعب, رؤية ممتازة, هادئ", avatarType = "darsh",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 20, name = "إسلام", nickname = "دام روما", position = "Midfielder",
                level = "Level 2", favoriteClub = "الزمالك",
                topTraits = "لياقة عالية, تسديدات قوية, زملكاوي", avatarType = "ahmed_hany",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 21, name = "إسلام السويركي", nickname = "السويركي", position = "Midfielder",
                level = "Level 2", favoriteClub = "الأهلي",
                topTraits = "صانع ألعاب, مراوغ, أاهلاوي", avatarType = "ronaldinho",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 22, name = "وليد", nickname = "الوحش", position = "Goalkeeper",
                level = "N/A", favoriteClub = "الأهلي",
                topTraits = "حارس مرمى شجاع, رد فعل ممتاز", avatarType = "taja",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 23, name = "سامح", nickname = "الساحر", position = "Forward",
                level = "Level 2", favoriteClub = "الأهلي",
                topTraits = "مهاجم, لمسة سحرية", avatarType = "bubu",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 24, name = "زياد", nickname = "الجناح", position = "Forward",
                level = "Level 2", favoriteClub = "الأهلي",
                topTraits = "سريع, انطلاقات قوية", avatarType = "motifa",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 25, name = "طارق", nickname = "الصخرة", position = "Defender",
                level = "Level 2", favoriteClub = "الزمالك",
                topTraits = "مدافع قوي, قطع كرات", avatarType = "asad",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 26, name = "حازم", nickname = "البرنس", position = "Midfielder",
                level = "Level 2", favoriteClub = "الأهلي",
                topTraits = "تمريرات حاسمة, تحكم بالكرة", avatarType = "omda",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 27, name = "عمر", nickname = "الدينامو", position = "Midfielder",
                level = "Level 2", favoriteClub = "الزمالك",
                topTraits = "مجهود وافر, تغطية دفاعية", avatarType = "maldini",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 28, name = "خالد", nickname = "السد العالي", position = "Goalkeeper",
                level = "N/A", favoriteClub = "الزمالك",
                topTraits = "حارس مرمى, تصديات حاسمة", avatarType = "asad_aali",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 29, name = "يوسف", nickname = "الموهوب", position = "Forward",
                level = "Level 2", favoriteClub = "الأهلي",
                topTraits = "مهاجم شاب, تسديدات دقيقة", avatarType = "treka",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 30, name = "محمود", nickname = "الجنرال", position = "Defender",
                level = "Level 2", favoriteClub = "الأهلي",
                topTraits = "قائد خط الدفاع, تنظيم اللعب", avatarType = "ostora",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 31, name = "مصطفى كوارشي", nickname = "الصخرة", position = "Midfielder",
                level = "Level 2", favoriteClub = "الزمالك",
                topTraits = "لاعب وسط صخرة, قطع كرات, لياقة عريضة", avatarType = "maldini",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            ),
            PlayerEntity(
                id = 32, name = "أحمد عبد الخالق", nickname = "المجرم", position = "Forward",
                level = "Level 2", favoriteClub = "الأهلي",
                topTraits = "مهاجم مجرم أمام المرمى, تسديدات قوية, هداف", avatarType = "ameed",
                matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
            )
        )

        if (dao.getPlayersCount() < 32) {
            full30DefaultPlayers.forEach { player ->
                val existing = dao.getPlayerById(player.id)
                if (existing == null) {
                    dao.insertPlayer(player)
                }
            }
        }

        if (dao.getMatchesCount() == 0) {
            val initialMatch = MatchEntity(
                id = 1,
                dateString = com.example.util.MatchDateUtils.getNextFridayDateString(),
                timeString = com.example.util.MatchDateUtils.getDefaultTimeString(),
                targetPlayerCount = 16,
                team1ColorName = "الأبيض",
                team1HexColor = 0xFFFFFFFF,
                team2ColorName = "الأسود",
                team2HexColor = 0xFF1E1E24,
                isFinished = false,
                matchTimestamp = com.example.util.MatchDateUtils.getNextFridayEpochMillis()
            )
            dao.insertMatch(initialMatch)
        }
    }

    // يحفظ نتيجة المباراة النهائية ويحسب نقاط وإحصائيات كل لاعب تلقائياً،
    // باستخدام PointsCalculator كمصدر وحيد للحساب (مطابق تماماً لِما يظهر في
    // شاشة "لوائح النقاط" وفي عداد النقاط الحي أثناء المباراة).
    suspend fun finalizeMatchResults(
        match: MatchEntity,
        evaluations: List<MatchPlayerEntity>
    ) {
        val finishedMatch = match.copy(isFinished = true)
        dao.updateMatch(finishedMatch)

        // نجهز قائمة السجلات المصحّحة (مع نقاط كل مباراة الفعلية + حالة الشباك النظيفة
        // الحقيقية) لحفظها في جدول match_players، بدلاً من حفظ القيم الخام فقط كما كان
        // يحدث سابقاً (كانت totalPointsEarned تُحفظ دائماً = 0، مما يمنع بناء سجلات
        // الشهر/الموسم بدقة من التاريخ).
        val correctedEvaluations = mutableListOf<MatchPlayerEntity>()

        evaluations.forEach { eval ->
            val player = dao.getPlayerById(eval.playerId) ?: return@forEach

            val isTeam1 = eval.teamNumber == 1
            val opponentScore = if (isTeam1) finishedMatch.team2Score else finishedMatch.team1Score
            val isWin = if (isTeam1) finishedMatch.team1Score > finishedMatch.team2Score else finishedMatch.team2Score > finishedMatch.team1Score
            val isLoss = if (isTeam1) finishedMatch.team1Score < finishedMatch.team2Score else finishedMatch.team2Score < finishedMatch.team1Score
            val isDraw = finishedMatch.team1Score == finishedMatch.team2Score

            val cleanSheetAchieved = PointsCalculator.isCleanSheetAchieved(eval, player.position, opponentScore)
            val breakdown = PointsCalculator.calculateBreakdown(
                eval = eval,
                playerPosition = player.position,
                opponentScore = opponentScore,
                isWin = isWin,
                isDraw = isDraw
            )
            val matchPointsEarned = breakdown.total

            correctedEvaluations += eval.copy(
                isCleanSheet = cleanSheetAchieved,
                totalPointsEarned = matchPointsEarned
            )

            // Update player entity all-time totals
            val updatedPlayer = player.copy(
                matchesPlayed = player.matchesPlayed + 1,
                goals = player.goals + eval.goals,
                assists = player.assists + eval.assists,
                wins = player.wins + (if (isWin) 1 else 0),
                losses = player.losses + (if (isLoss) 1 else 0),
                draws = player.draws + (if (isDraw) 1 else 0),
                totalPoints = player.totalPoints + matchPointsEarned,
                cleanSheets = player.cleanSheets + (if (cleanSheetAchieved) 1 else 0),
                seasonRating = ((player.seasonRating * player.matchesPlayed) + (7.0f + (matchPointsEarned / 5.0f))) / (player.matchesPlayed + 1)
            )

            dao.updatePlayer(updatedPlayer)
        }

        dao.insertMatchPlayers(correctedEvaluations)

        // Reset RSVPs for next match cycle
        dao.resetAllRsvps()
    }
}
