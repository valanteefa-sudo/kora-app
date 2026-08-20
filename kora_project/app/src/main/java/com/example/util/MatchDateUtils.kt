package com.example.util

import java.util.Calendar

object MatchDateUtils {

    /**
     * Calculates the date of the upcoming Friday dynamically.
     */
    fun getNextFridayDateString(): String {
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Calendar.FRIDAY = 6. 
        // Sunday = 1, Monday = 2, Tuesday = 3, Wednesday = 4, Thursday = 5, Friday = 6, Saturday = 7
        var daysUntilFriday = Calendar.FRIDAY - currentDayOfWeek
        if (daysUntilFriday < 0) {
            daysUntilFriday += 7
        } else if (daysUntilFriday == 0) {
            // If today is Friday after 10 PM, target next week's Friday
            if (calendar.get(Calendar.HOUR_OF_DAY) >= 22) {
                daysUntilFriday = 7
            }
        }

        calendar.add(Calendar.DAY_OF_YEAR, daysUntilFriday)

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val monthNames = arrayOf(
            "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
            "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
        )
        val month = monthNames[calendar.get(Calendar.MONTH)]
        val year = calendar.get(Calendar.YEAR)

        return "الجمعة $day $month $year"
    }

    /**
     * Standard match time in Egypt / Arab region.
     */
    fun getDefaultTimeString(): String = "10:00 مساءً"

    /**
     * يحسب التاريخ الفعلي (epoch millis) لأقرب يوم جمعة قادم، بنفس منطق getNextFridayDateString
     * تماماً، لضبط منتصف الليل كبداية لليوم. يُستخدم لحفظ matchTimestamp في المباراة حتى تُصنَّف
     * بشكل صحيح ضمن سجلات الشهر والموسم لاحقاً.
     */
    fun getNextFridayEpochMillis(): Long {
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        var daysUntilFriday = Calendar.FRIDAY - currentDayOfWeek
        if (daysUntilFriday < 0) {
            daysUntilFriday += 7
        } else if (daysUntilFriday == 0) {
            if (calendar.get(Calendar.HOUR_OF_DAY) >= 22) {
                daysUntilFriday = 7
            }
        }

        calendar.add(Calendar.DAY_OF_YEAR, daysUntilFriday)
        calendar.set(Calendar.HOUR_OF_DAY, 22)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * حدود "الموسم الرياضي" الحالي: يبدأ 1 أغسطس وينتهي 31 يوليو من العام التالي
     * (نفس تقسيم مواسم الدوريات المصرية والأوروبية). يعيد Pair(بداية الموسم, نهاية الموسم)
     * كـ epoch millis، بالإضافة لاسم الموسم المعروض مثل "2025/2026".
     */
    fun getCurrentSeasonRange(): Triple<Long, Long, String> {
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH) // 0-indexed, Calendar.AUGUST = 7

        val seasonStartYear = if (month >= Calendar.AUGUST) year else year - 1
        val seasonEndYear = seasonStartYear + 1

        val start = Calendar.getInstance().apply {
            set(seasonStartYear, Calendar.AUGUST, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val end = Calendar.getInstance().apply {
            set(seasonEndYear, Calendar.JULY, 31, 23, 59, 59)
            set(Calendar.MILLISECOND, 999)
        }

        return Triple(start.timeInMillis, end.timeInMillis, "$seasonStartYear/$seasonEndYear")
    }

    /**
     * حدود الشهر الحالي كـ epoch millis (بداية اليوم الأول ونهاية آخر يوم في الشهر)
     * بالإضافة لاسم الشهر المعروض بالعربي مثل "أغسطس 2026".
     */
    fun getCurrentMonthRange(): Triple<Long, Long, String> {
        val now = Calendar.getInstance()
        val monthNames = arrayOf(
            "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
            "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
        )
        val label = "${monthNames[now.get(Calendar.MONTH)]} ${now.get(Calendar.YEAR)}"

        val start = (now.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val end = (now.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        return Triple(start.timeInMillis, end.timeInMillis, label)
    }
}
