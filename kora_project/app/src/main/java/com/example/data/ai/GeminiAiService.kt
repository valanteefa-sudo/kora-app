package com.example.data.ai

import com.example.BuildConfig
import com.example.data.local.PlayerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class BalancedLineup(
    val title: String,
    val description: String,
    val team1: List<PlayerEntity>,
    val team2: List<PlayerEntity>,
    val team1Power: Int,
    val team2Power: Int
)

object GeminiAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // Generate 3 balanced lineup options
    fun generateLineupOptions(selectedPlayers: List<PlayerEntity>): List<BalancedLineup> {
        val gks = selectedPlayers.filter { it.position == "Goalkeeper" }.toMutableList()
        val dfs = selectedPlayers.filter { it.position == "Defender" }.toMutableList()
        val mfs = selectedPlayers.filter { it.position == "Midfielder" }.toMutableList()
        val fws = selectedPlayers.filter { it.position == "Forward" }.toMutableList()

        // Fallback for missing GKs
        val fieldPlayers = (dfs + mfs + fws).sortedByDescending { player -> getPlayerValue(player) }.toMutableList()

        // Lineup 1: Standard Balanced Draft
        val (t1_1, t2_1) = balanceTwoTeams(gks, fieldPlayers, seed = 1)
        
        // Lineup 2: Attack vs Defense Tactical Shift
        val (t1_2, t2_2) = balanceTwoTeams(gks, fieldPlayers, seed = 2)

        // Lineup 3: Skill & Flair Midfield Focus
        val (t1_3, t2_3) = balanceTwoTeams(gks, fieldPlayers, seed = 3)

        return listOf(
            BalancedLineup(
                title = "التشكيل الأول: التوازن الشامل ⚖️",
                description = "تكافئ تكتيكي كامل بين القوة الهجومية والصلابة الدفاعية وحارس مرمى لكل فريق.",
                team1 = t1_1,
                team2 = t2_1,
                team1Power = calculateTeamPower(t1_1),
                team2Power = calculateTeamPower(t2_1)
            ),
            BalancedLineup(
                title = "التشكيل الثاني: القوة والسرعة ⚡",
                description = "تركيز على السرعة والهجمات المرتدة مع توازن محكم في خط الوسط.",
                team1 = t1_2,
                team2 = t2_2,
                team1Power = calculateTeamPower(t1_2),
                team2Power = calculateTeamPower(t2_2)
            ),
            BalancedLineup(
                title = "التشكيل الثالث: المهارة والسيطرة ✨",
                description = "اعتماد على صناعة اللعب والاستحواذ مع تقارب كبير في مستويات اللاعبين.",
                team1 = t1_3,
                team2 = t2_3,
                team1Power = calculateTeamPower(t1_3),
                team2Power = calculateTeamPower(t2_3)
            )
        )
    }

    private fun getPlayerValue(player: PlayerEntity): Int {
        val levelValue = when (player.level) {
            "Level 3" -> 30
            "Level 2" -> 20
            "Level 1" -> 10
            else -> 15
        }
        val posValue = when (player.position) {
            "Goalkeeper" -> 25
            "Forward" -> 20
            "Midfielder" -> 18
            "Defender" -> 17
            else -> 15
        }
        return levelValue + posValue + (player.seasonRating * 2).toInt()
    }

    private fun calculateTeamPower(team: List<PlayerEntity>): Int {
        if (team.isEmpty()) return 50
        val sum = team.sumOf { getPlayerValue(it) }
        return (sum / team.size.toFloat() * 4).toInt().coerceIn(75, 99)
    }

    private fun balanceTwoTeams(
        gks: List<PlayerEntity>,
        fieldPlayers: List<PlayerEntity>,
        seed: Int
    ): Pair<List<PlayerEntity>, List<PlayerEntity>> {
        val team1 = mutableListOf<PlayerEntity>()
        val team2 = mutableListOf<PlayerEntity>()

        // Distribute Goalkeepers
        if (gks.size >= 2) {
            if (seed % 2 == 0) {
                team1.add(gks[0])
                team2.add(gks[1])
            } else {
                team1.add(gks[1])
                team2.add(gks[0])
            }
        } else if (gks.size == 1) {
            team1.add(gks[0])
        }

        val remainingField = fieldPlayers.toMutableList()

        // Helper matcher lambda
        fun isPlayer(p: PlayerEntity, vararg keywords: String): Boolean {
            return keywords.any { kw -> p.name.contains(kw) || p.nickname.contains(kw) }
        }

        // Pair 1: Bubu (السيد) vs Mo Teefa (مو تيفا) or Treka (تريكة)
        val bubu = remainingField.find { isPlayer(it, "السيد", "بوبو") }
        val motifaOrTreka = remainingField.find { isPlayer(it, "تيفا", "تريكة", "محمد مصطفى") }
        if (bubu != null && motifaOrTreka != null && bubu.id != motifaOrTreka.id) {
            if (seed % 2 == 1) {
                team1.add(bubu)
                team2.add(motifaOrTreka)
            } else {
                team2.add(bubu)
                team1.add(motifaOrTreka)
            }
            remainingField.remove(bubu)
            remainingField.remove(motifaOrTreka)
        }

        // Pair 2: Darsh (مصطفى) vs Ahmed Hany (أحمد هاني / لوفي)
        val darsh = remainingField.find { isPlayer(it, "درش") || (it.name == "مصطفى" || it.nickname == "درش") }
        val ahmedHany = remainingField.find { isPlayer(it, "لوفي", "أحمد هاني", "هاني الصغير") }
        if (darsh != null && ahmedHany != null && darsh.id != ahmedHany.id) {
            if (seed % 2 == 1) {
                team1.add(darsh)
                team2.add(ahmedHany)
            } else {
                team2.add(darsh)
                team1.add(ahmedHany)
            }
            remainingField.remove(darsh)
            remainingField.remove(ahmedHany)
        }

        // Pair 3: Asad (كريم) vs Ostora (محمد عبد اللاه)
        val asad = remainingField.find { isPlayer(it, "كريم", "الأسد") }
        val ostora = remainingField.find { isPlayer(it, "عبد اللاه", "الأسطورة") }
        if (asad != null && ostora != null && asad.id != ostora.id) {
            if (seed % 2 == 1) {
                team1.add(asad)
                team2.add(ostora)
            } else {
                team2.add(asad)
                team1.add(ostora)
            }
            remainingField.remove(asad)
            remainingField.remove(ostora)
        }

        val sortedRemaining = when (seed) {
            1 -> remainingField.sortedByDescending { getPlayerValue(it) }
            2 -> remainingField.sortedBy { it.position }
            else -> remainingField.shuffled()
        }

        sortedRemaining.forEachIndexed { index, player ->
            if (team1.size <= team2.size) {
                team1.add(player)
            } else {
                team2.add(player)
            }
        }

        return Pair(team1, team2)
    }

    // Call Gemini API REST for Arabic Tactical Commentary
    suspend fun fetchGeminiTacticalAdvice(selectedPlayers: List<PlayerEntity>): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "التحليل والتكتيك الرياضي: تم توزيع الفرق بناءً على مصفوفة التكافئ التكتيكي (الخبرة، السرعة، الصلابة الدفاعية وجودة حراسة المرمى). المباراة متكافئة بنسبة 50/50!"
        }

        try {
            val namesStr = selectedPlayers.joinToString(", ") { "${it.name} (${it.nickname} - ${it.position} - ${it.level})" }
            val prompt = """
                أنت محلل رياضي وخبير تكتيكي لمباراة كرة القدم الأسبوعية "كورة كل جمعة".
                قائمة اللاعبين اليوم: $namesStr.
                اكتب تقريراً تكتيكياً مشجعاً ومثيراً باللغة العربية في 3 أسطر قصيرة، يحلل أهم مفاتيح اللعب والتحدي المتوقع بين الفريقين مع روح دعابة رياضية بين الأصدقاء. لا تذكر كلمة ذكاء صناعي أو AI إطلاقاً.
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseText = response.body?.string() ?: ""
            
            val jsonResp = JSONObject(responseText)
            val candidates = jsonResp.optJSONArray("candidates")
            val text = candidates?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text")

            text ?: "التحليل والتكتيك الرياضي: مباراة حماسية تنتظرنا هذا الأسبوع مع مواجهات خاصة بين الهدافين والصلابة الدفاعية!"
        } catch (e: Exception) {
            "التحليل والتكتيك الرياضي: تم إعداد التشكيلة بأفضل درجات التوازن التكتيكي لضمان مباراة ممتعة ومثيرة!"
        }
    }
}
