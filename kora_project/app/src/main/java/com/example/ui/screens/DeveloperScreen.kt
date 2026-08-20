package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PitchDarkCanvas
import com.example.ui.theme.PitchDarkSurface
import com.example.ui.theme.PitchCardContainer
import com.example.ui.theme.StadiumGreenPrimary

@Composable
fun DeveloperScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchDarkCanvas)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Badge,
                contentDescription = null,
                tint = GoldAccent,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "عن مصمم التطبيق",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent
            )
        }

        // Football Stadium Friendship Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.football_banner_1786092439457),
                    contentDescription = "Football Spirit Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                )
                Text(
                    text = "كرة القدم • صداقة • روح رياضية • منافسة ممتعة ⚽❤️",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Developer Profile Main Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
            border = androidx.compose.foundation.BorderStroke(2.dp, GoldAccent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PitchCardContainer, PitchDarkSurface)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Profile Image
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.img_developer_moteefa_1786104291565),
                            contentDescription = "Mo.Teefa Profile",
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .border(3.dp, GoldAccent, CircleShape)
                                .shadow(8.dp, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Developer Name & Title
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            color = GoldAccent,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "Mo.Teefa",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "محمد عبد اللطيف",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "مصمم ومطور تطبيق كورة كل جمعة ⚽📱",
                            fontSize = 14.sp,
                            color = GoldAccent,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Divider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)

                    // Bio Description
                    Text(
                        text = "تم بناء هذا التطبيق بحب وشغف لتنظيم حجز ومباريات الأصدقاء الأسبوعية، توزيع الفرق بتوازن وحيادية تامة، واحتساب نقاط الفوز والأهداف بكل عدل وشفافية لتعزيز الروح الرياضية والمنافسة الممتعة بروح الفكاهة!",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Feature Highlights Grid
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PitchDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "مميزات النظام والتطبيق ✨",
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    fontSize = 15.sp
                )

                FeatureRow("⚙️", "توزيع التشكيل المتوازن عالي الدقة بحسب مركز ومستوى كل لاعب")
                FeatureRow("📊", "حساب تلقائي دقيق للأهداف والأسيست والنقاط والتقييمات")
                FeatureRow("👑", "لوحة شرف وجوائز أسبوعية وشهرية لأفضل لاعبي الجمعة")
                FeatureRow("🔐", "لوحة تحكم أدمن كاملة لإدارة الحضور والنتائج واللاعبين")
            }
        }

        // Footer App Version & Credits
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = "كورة كل جمعة • الإصدار 2.0 (إصدار الأدمن)",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "جميع الحقوق محفوظة © Mo.Teefa (محمد عبد اللطيف)",
                fontSize = 11.sp,
                color = GoldAccent.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FeatureRow(emoji: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = emoji, fontSize = 16.sp)
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.85f),
            fontWeight = FontWeight.Medium
        )
    }
}
