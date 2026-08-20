package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PitchDarkCanvas
import com.example.ui.theme.PitchDarkSurface
import com.example.ui.theme.PitchCardContainer
import com.example.ui.theme.StadiumGreenPrimary

data class RuleItem(
    val category: String,
    val title: String,
    val icon: String,
    val pointsText: String,
    val isPositive: Boolean,
    val details: String,
    val fwPoints: String? = null,
    val mfPoints: String? = null,
    val dfPoints: String? = null
)

/**
 * صف عداد بسيط (+/-) يُستخدم داخل محاكي النقاط في هذه الشاشة فقط، بنفس نمط صف
 * الإنذارات الأصفر الموجود أصلاً، حتى تكون كل متغيرات المحاكي قابلة للاختبار فعليًا.
 */
@Composable
private fun CalcCounterRow(label: String, value: Int, onChange: (Int) -> Unit, accentColor: Color = GoldAccent) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = Color.White)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (value > 0) onChange(value - 1) }) {
                Text("-", fontSize = 18.sp, color = accentColor, fontWeight = FontWeight.Bold)
            }
            Text("$value", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = accentColor)
            IconButton(onClick = { onChange(value + 1) }) {
                Text("+", fontSize = 18.sp, color = accentColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PointsRulesScreen() {
    var selectedCategory by remember { mutableStateOf("الكل") }
    
    // Interactive Calculator State
    var calcPosition by remember { mutableStateOf("مدافع") } // "مهاجم", "خط وسط", "مدافع"
    var calcGoals by remember { mutableStateOf(0) }
    var calcAssists by remember { mutableStateOf(0) }
    var calcCleanSheet by remember { mutableStateOf(false) }
    var calcMotm by remember { mutableStateOf(false) }
    var calcPenaltySave by remember { mutableStateOf(0) }
    var calcPenaltyMissed by remember { mutableStateOf(0) }
    var calcYellowCards by remember { mutableStateOf(0) }
    var calcRedCards by remember { mutableStateOf(0) }
    var calcObjections by remember { mutableStateOf(0) }
    var calcOwnGoals by remember { mutableStateOf(0) }
    var calcErrorLeadingToGoal by remember { mutableStateOf(0) }
    var calcResult by remember { mutableStateOf("لا شيء") } // "فوز", "تعادل", "لا شيء"

    // نستخدم PointsCalculator نفسه المستخدم فعلياً عند حفظ نتيجة المباراة وفي عداد
    // النقاط الحي، حتى تكون النتيجة هنا مطابقة 100% لما سيحصل عليه اللاعب فعلاً.
    // ملحوظة: كل متغيرات الحاسبة هنا (بما فيها الكروت الحمراء وركلات الجزاء الممنوعة/
    // المصدودة والاعتراض والهدف العكسي والخطأ المؤدي لهدف) لازم يكون لها عنصر تحكم
    // فعلي بالأسفل، وإلا هتفضل قيمتها صفر دائمًا ولن تُختبر أبدًا رغم ظهورها في الحساب.
    val calculatedScore = remember(
        calcPosition, calcGoals, calcAssists, calcCleanSheet, calcMotm,
        calcPenaltySave, calcPenaltyMissed, calcYellowCards, calcRedCards, calcObjections,
        calcOwnGoals, calcErrorLeadingToGoal, calcResult
    ) {
        val positionKey = when (calcPosition) {
            "مهاجم" -> "Forward"
            "خط وسط" -> "Midfielder"
            else -> "Defender"
        }
        val eval = com.example.data.local.MatchPlayerEntity(
            matchId = 0L, playerId = 0L, teamNumber = 1, positionOnPitch = positionKey,
            goals = calcGoals, assists = calcAssists, yellowCards = calcYellowCards,
            redCards = calcRedCards, isMotm = calcMotm, isCleanSheet = calcCleanSheet,
            penaltyMissed = calcPenaltyMissed, violentObjection = calcObjections,
            penaltySaves = calcPenaltySave, ownGoals = calcOwnGoals,
            errorLeadingToGoal = calcErrorLeadingToGoal
        )
        com.example.util.PointsCalculator.calculateTotal(
            eval = eval,
            playerPosition = positionKey,
            opponentScore = if (calcCleanSheet) 0 else 1,
            isWin = calcResult == "فوز",
            isDraw = calcResult == "تعادل"
        )
    }

    val rulesList = listOf(
        RuleItem(
            category = "الأهداف",
            title = "تسجيل هدف في المباراة ⚽",
            icon = "⚽",
            pointsText = "+3 إلى +5 نقاط",
            isPositive = true,
            details = "تختلف نقاط الهدف حسب مركز اللاعب لتشجيع المدافعين ولاعبي الوسط على التقدم:",
            fwPoints = "مهاجم: +3 نقاط",
            mfPoints = "خط وسط: +4 نقاط",
            dfPoints = "مدافع / حارس: +5 نقاط"
        ),
        RuleItem(
            category = "الصناعة",
            title = "صناعة هدف (أسيست) 🎯",
            icon = "🎯",
            pointsText = "+4 إلى +6 نقاط",
            isPositive = true,
            details = "مكافأة التمريرة الحاسمة المؤدية لهدف مباشر لتكريم صناع اللعب والمساندين:",
            fwPoints = "مهاجم: +4 نقاط",
            mfPoints = "خط وسط: +4 نقاط",
            dfPoints = "مدافع / حارس: +6 نقاط"
        ),
        RuleItem(
            category = "الدفاع والإنقاذ",
            title = "شباك نظيفة (كلين شيت) 🧤",
            icon = "🧤",
            pointsText = "+5 نقاط",
            isPositive = true,
            details = "تُمنح للمدافع وحارس المرمى في حال عدم استقبال أهداف طوال المباراة."
        ),
        RuleItem(
            category = "الدفاع والإنقاذ",
            title = "تصدي لركلة جزاء 🛑",
            icon = "🛑",
            pointsText = "+5 نقاط",
            isPositive = true,
            details = "تُمنح لحارس المرمى عند التصدي الناجح لركلة جزاء أثناء المباراة."
        ),
        RuleItem(
            category = "الدفاع والإنقاذ",
            title = "إنقاذ هدف محقق من على الخط 🛡️",
            icon = "🛡️",
            pointsText = "+3 نقاط",
            isPositive = true,
            details = "تُمنح للمدافع عند إبعاد كرة حاسمة متجهة للشباك."
        ),
        RuleItem(
            category = "ركلات الجزاء",
            title = "تسجيل ركلة جزاء 🥅",
            icon = "🥅",
            pointsText = "حسب المركز",
            isPositive = true,
            details = "تُحسب بنفس نقاط الهدف العادي طبقاً لمركز اللاعب المنسوب له."
        ),
        RuleItem(
            category = "ركلات الجزاء",
            title = "إهدار ركلة جزاء ❌",
            icon = "❌",
            pointsText = "-1 نقطة",
            isPositive = false,
            details = "خصم نقطة واحدة عند تسديد ركلة جزاء خارج المرمى أو تصدي الحارس لها."
        ),
        RuleItem(
            category = "الكروت والمخالفات",
            title = "إنذار كارت أصفر 🟨",
            icon = "🟨",
            pointsText = "-2 نقاط",
            isPositive = false,
            details = "خصم نقطتين فور تلقي الإنذار الأصفر للحفاظ على الانضباط."
        ),
        RuleItem(
            category = "الكروت والمخالفات",
            title = "طرد كارت أحمر 🟥",
            icon = "🟥",
            pointsText = "-3 نقاط",
            isPositive = false,
            details = "خصم 3 نقاط عند الحصول على البطاقة الحمراء أو الإنذار الثاني."
        ),
        RuleItem(
            category = "الكروت والمخالفات",
            title = "اعتراض غير رياضي / خروج عن النص 🚫",
            icon = "🚫",
            pointsText = "-5 نقاط",
            isPositive = false,
            details = "خصم مشدد 5 نقاط لأي اعتراض حاد أو سلوك منافي للروح الرياضية."
        ),
        RuleItem(
            category = "الكروت والمخالفات",
            title = "هدف عكسي بالخطأ (أون جول) 🤦‍♂️",
            icon = "🤦‍♂️",
            pointsText = "-4 نقاط",
            isPositive = false,
            details = "خصم 4 نقاط عند تسديد الكرة بالخطأ في مرمى الفريق."
        ),
        RuleItem(
            category = "الكروت والمخالفات",
            title = "خطأ فردي أدى لهدف 🎯",
            icon = "🎯",
            pointsText = "-3 نقاط",
            isPositive = false,
            details = "خصم 3 نقاط عند ارتكاب خطأ فردي واضح (تمريرة خاطئة، تمركز خاطئ...) أدى مباشرة لتسجيل المنافس هدفاً."
        ),
        RuleItem(
            category = "الجوائز والنتائج",
            title = "رجل المباراة (MOTM) 👑",
            icon = "👑",
            pointsText = "+5 نقاط",
            isPositive = true,
            details = "جائزة أفضل لاعب في المباراة باختيار وتقييم الحضور والأدمن."
        ),
        RuleItem(
            category = "الجوائز والنتائج",
            title = "الفوز بالمباراة 🏆",
            icon = "🏆",
            pointsText = "+3 نقاط",
            isPositive = true,
            details = "تُضاف لجميع لاعبي الفريق الفائز بنهاية المباراة."
        ),
        RuleItem(
            category = "الجوائز والنتائج",
            title = "التعادل في المباراة ⚖️",
            icon = "⚖️",
            pointsText = "+1 نقطة",
            isPositive = true,
            details = "تُمنح لكلا الفريقين عند انتهاء المباراة بالتعادل."
        )
    )

    val categories = listOf("الكل", "الأهداف", "الصناعة", "الدفاع والإنقاذ", "ركلات الجزاء", "الكروت والمخالفات", "الجوائز والنتائج")

    val filteredRules = if (selectedCategory == "الكل") {
        rulesList
    } else {
        rulesList.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchDarkCanvas)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(PitchCardContainer, PitchDarkSurface)
                        )
                    )
                    .padding(18.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = GoldAccent,
                        shape = CircleShape
                    ) {
                        Text(
                            text = "لائحة كورة كل جمعة 📜",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = "جدول لوائح ونظام النقاط الرسمي ⚖️",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "تفاصيل وحسابات جميع أفعال وأحداث المباراة بالتفصيل",
                        style = MaterialTheme.typography.bodySmall,
                        color = GoldAccent,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory),
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                contentColor = GoldAccent,
                divider = {}
            ) {
                categories.forEach { cat ->
                    Tab(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        text = {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedCategory == cat) GoldAccent else Color.White.copy(alpha = 0.6f)
                            )
                        }
                    )
                }
            }
        }

        // Rules List Items
        filteredRules.forEach { rule ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (rule.isPositive) GoldAccent.copy(alpha = 0.4f) else Color.Red.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(rule.icon, fontSize = 20.sp)
                            Text(
                                text = rule.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 19.sp
                            )
                        }

                        Surface(
                            color = if (rule.isPositive) StadiumGreenPrimary else Color(0xFF8B0000),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = rule.pointsText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                lineHeight = 15.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Text(
                        text = rule.details,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Position breakdown badges if available
                    if (rule.fwPoints != null && rule.mfPoints != null && rule.dfPoints != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = rule.fwPoints,
                                    fontSize = 10.sp,
                                    color = GoldAccent,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 14.sp
                                )
                            }
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = rule.mfPoints,
                                    fontSize = 10.sp,
                                    color = GoldAccent,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 14.sp
                                )
                            }
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = rule.dfPoints,
                                    fontSize = 10.sp,
                                    color = GoldAccent,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interactive Points Calculator Simulator Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = GoldAccent)
                    Text(
                        text = "محاكي حساب نقاط المباراة التجريبي 🧮",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }

                Text(
                    text = "حدد المركز والمهام واكتشف كم نقطة سيحصل عليها اللاعب فوراً:",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )

                // Position Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("المركز:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    listOf("مهاجم", "خط وسط", "مدافع").forEach { pos ->
                        FilterChip(
                            selected = calcPosition == pos,
                            onClick = { calcPosition = pos },
                            label = { Text(pos, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Divider(color = GoldAccent.copy(alpha = 0.2f))

                // Stats Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚽ الأهداف:", fontSize = 13.sp, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (calcGoals > 0) calcGoals-- }) {
                            Text("-", fontSize = 18.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                        }
                        Text("$calcGoals", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        IconButton(onClick = { calcGoals++ }) {
                            Text("+", fontSize = 18.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎯 الأسيست:", fontSize = 13.sp, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (calcAssists > 0) calcAssists-- }) {
                            Text("-", fontSize = 18.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                        }
                        Text("$calcAssists", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        IconButton(onClick = { calcAssists++ }) {
                            Text("+", fontSize = 18.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🟨 الإنذارات:", fontSize = 13.sp, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (calcYellowCards > 0) calcYellowCards-- }) {
                            Text("-", fontSize = 18.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                        }
                        Text("$calcYellowCards", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        IconButton(onClick = { calcYellowCards++ }) {
                            Text("+", fontSize = 18.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                CalcCounterRow("🛑 تصدي لركلة جزاء:", calcPenaltySave, { calcPenaltySave = it }, StadiumGreenPrimary)

                // قسم المخالفات في المحاكي - نفس مبدأ الفصل البصري المستخدم في شاشة
                // المباراة المباشرة، حتى يستطيع المستخدم تجربة كل قاعدة خصم موجودة فعليًا.
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF2A1212).copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF5350).copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("⚠️ مخالفات (خصم نقاط)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF5350))
                        CalcCounterRow("❌ ركلات جزاء أهدرها:", calcPenaltyMissed, { calcPenaltyMissed = it }, Color(0xFFEF5350))
                        CalcCounterRow("🟥 كروت حمراء:", calcRedCards, { calcRedCards = it }, Color(0xFFEF5350))
                        CalcCounterRow("🚫 اعتراض غير رياضي:", calcObjections, { calcObjections = it }, Color(0xFFEF5350))
                        CalcCounterRow("🤦‍♂️ هدف عكسي (أون جول):", calcOwnGoals, { calcOwnGoals = it }, Color(0xFFEF5350))
                        CalcCounterRow("🎯 خطأ أدى لهدف:", calcErrorLeadingToGoal, { calcErrorLeadingToGoal = it }, Color(0xFFEF5350))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = calcCleanSheet,
                            onClick = { calcCleanSheet = !calcCleanSheet },
                            label = { Text("🧤 كلين شيت", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = StadiumGreenPrimary)
                        )
                        FilterChip(
                            selected = calcMotm,
                            onClick = { calcMotm = !calcMotm },
                            label = { Text("👑 رجل المباراة", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldAccent, selectedLabelColor = Color.Black)
                        )
                    }
                }

                // نتيجة الفريق - تضيف مكافأة الفوز (+3) أو التعادل (+1)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("نتيجة الفريق:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    listOf("لا شيء", "فوز", "تعادل").forEach { res ->
                        FilterChip(
                            selected = calcResult == res,
                            onClick = { calcResult = res },
                            label = { Text(res, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StadiumGreenPrimary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("مجموع النقاط المحسوبة:", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text(
                            text = "$calculatedScore نقطة",
                            fontWeight = FontWeight.Black,
                            color = GoldAccent,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}
