package com.example.util

import com.example.data.local.MatchPlayerEntity

/**
 * =====================================================================================
 *  المصدر الوحيد لحساب نقاط اللاعبين في كل التطبيق (Single Source of Truth)
 * =====================================================================================
 * قبل هذا التعديل كانت معادلة النقاط مكررة في 3 أماكن مختلفة (شاشة المباراة المباشرة،
 * شاشة لائحة النقاط، وعند حفظ نتيجة المباراة) وكانت كل نسخة تحسب بشكل مختلف قليلاً
 * (مثلاً: نقاط الفوز/التعادل موجودة في اللائحة المعروضة للمستخدم لكنها لم تكن تُحتسب
 * فعلياً عند حفظ النتيجة!). الآن كل شاشة تستدعي هذا الكلاس فقط، فأصبح الشرح المعروض
 * للمستخدم يطابق تماماً ما يُحسب فعلياً - بدون أي تعارض.
 *
 * جميع الثوابت هنا موثّقة بالعربي، ويمكنك تعديلها من مكان واحد فقط في المستقبل.
 */
object PointsCalculator {

    // ---------- نقاط الهدف حسب مركز اللاعب (تشجيعاً للمدافعين ولاعبي الوسط) ----------
    fun goalPointsPerGoal(position: String): Int = when (position) {
        "Forward" -> 3
        "Midfielder" -> 4
        else -> 5 // Defender & Goalkeeper
    }

    // ---------- نقاط صناعة الهدف (الأسيست) حسب المركز ----------
    fun assistPointsPerAssist(position: String): Int = when (position) {
        "Forward" -> 4
        "Midfielder" -> 4
        else -> 6 // Defender & Goalkeeper
    }

    const val CLEAN_SHEET_POINTS = 5
    const val MOTM_POINTS = 5
    const val PENALTY_SAVE_POINTS = 5
    const val GOAL_LINE_SAVE_POINTS = 3
    const val WIN_BONUS_POINTS = 3
    const val DRAW_BONUS_POINTS = 1

    const val PENALTY_MISSED_PENALTY = -1
    const val YELLOW_CARD_PENALTY = -2
    const val RED_CARD_PENALTY = -3
    const val VIOLENT_OBJECTION_PENALTY = -5
    const val OWN_GOAL_PENALTY = -4
    const val ERROR_LEADING_TO_GOAL_PENALTY = -3

    /**
     * الشرط الرسمي لاستحقاق "شباك نظيفة": الفريق المنافس لم يسجل أي هدف طوال المباراة،
     * بالإضافة إلى أن اللاعب إما حارس مرمى/مدافع (تُحتسب تلقائياً)، أو تم تفعيلها يدوياً
     * له من قِبل الأدمن (حالات استثنائية).
     */
    fun isCleanSheetAchieved(eval: MatchPlayerEntity, playerPosition: String, opponentScore: Int): Boolean {
        if (opponentScore != 0) return false
        return eval.isCleanSheet || playerPosition == "Goalkeeper" || playerPosition == "Defender"
    }

    /**
     * تفصيل نقاط اللاعب بندًا ببند - نفس البنود المعروضة في "لوائح النقاط"، تُستخدم أيضاً
     * لعرض عداد النقاط الحي على كارت اللاعب أثناء المباراة.
     */
    data class PointsBreakdown(
        val goalPoints: Int = 0,
        val assistPoints: Int = 0,
        val cleanSheetPoints: Int = 0,
        val motmPoints: Int = 0,
        val penaltySavePoints: Int = 0,
        val goalLineSavePoints: Int = 0,
        val resultBonusPoints: Int = 0, // فوز (+3) أو تعادل (+1) أو خسارة (0)
        val penaltyMissedDeduction: Int = 0,
        val yellowCardDeduction: Int = 0,
        val redCardDeduction: Int = 0,
        val objectionDeduction: Int = 0,
        val ownGoalDeduction: Int = 0,
        val errorDeduction: Int = 0
    ) {
        val positivePoints: Int get() = goalPoints + assistPoints + cleanSheetPoints + motmPoints +
                penaltySavePoints + goalLineSavePoints + resultBonusPoints
        val negativePoints: Int get() = penaltyMissedDeduction + yellowCardDeduction + redCardDeduction +
                objectionDeduction + ownGoalDeduction + errorDeduction
        val total: Int get() = positivePoints + negativePoints
    }

    /**
     * @param eval إحصائيات اللاعب في هذه المباراة (أهداف، أسيست، كروت...)
     * @param playerPosition مركز اللاعب: Forward / Midfielder / Defender / Goalkeeper
     * @param opponentScore عدد أهداف الفريق المنافس (لتحديد الشباك النظيفة)
     * @param isWin / isDraw نتيجة فريق اللاعب في هذه المباراة (لحساب مكافأة الفوز/التعادل)
     */
    fun calculateBreakdown(
        eval: MatchPlayerEntity,
        playerPosition: String,
        opponentScore: Int,
        isWin: Boolean = false,
        isDraw: Boolean = false
    ): PointsBreakdown {
        val cleanSheetAchieved = isCleanSheetAchieved(eval, playerPosition, opponentScore)
        return PointsBreakdown(
            goalPoints = eval.goals * goalPointsPerGoal(playerPosition),
            assistPoints = eval.assists * assistPointsPerAssist(playerPosition),
            cleanSheetPoints = if (cleanSheetAchieved) CLEAN_SHEET_POINTS else 0,
            motmPoints = if (eval.isMotm) MOTM_POINTS else 0,
            penaltySavePoints = eval.penaltySaves * PENALTY_SAVE_POINTS,
            goalLineSavePoints = eval.goalLineSaves * GOAL_LINE_SAVE_POINTS,
            resultBonusPoints = when {
                isWin -> WIN_BONUS_POINTS
                isDraw -> DRAW_BONUS_POINTS
                else -> 0
            },
            penaltyMissedDeduction = eval.penaltyMissed * PENALTY_MISSED_PENALTY,
            yellowCardDeduction = eval.yellowCards * YELLOW_CARD_PENALTY,
            redCardDeduction = eval.redCards * RED_CARD_PENALTY,
            objectionDeduction = eval.violentObjection * VIOLENT_OBJECTION_PENALTY,
            ownGoalDeduction = eval.ownGoals * OWN_GOAL_PENALTY,
            errorDeduction = eval.errorLeadingToGoal * ERROR_LEADING_TO_GOAL_PENALTY
        )
    }

    fun calculateTotal(
        eval: MatchPlayerEntity,
        playerPosition: String,
        opponentScore: Int,
        isWin: Boolean = false,
        isDraw: Boolean = false
    ): Int = calculateBreakdown(eval, playerPosition, opponentScore, isWin, isDraw).total
}
