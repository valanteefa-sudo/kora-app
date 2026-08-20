package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent

/**
 * ملحوظة أمان: كانت هذه الشاشة تقبل 3 كلمات سر مختلفة معًا ("1234"، "admin"،
 * "123456")، وكانت رسالة الخطأ نفسها تكشف الرمز الافتراضي لأي شخص يجرب الدخول
 * غلط! تم توحيدها في رمز واحد هنا، وإزالة كشف الرمز من رسالة الخطأ.
 *
 * هذا يبقى تحققاً من جهة العميل فقط (client-side) مناسب لمنع أصدقاء عاديين من
 * فتح لوحة الأدمن بالغلط، وليس حماية حقيقية ضد شخص يفكّك ملف الـ APK. لو حبيت
 * حماية أقوى مستقبلاً، الحل الصحيح هو نقل هذا التحقق لخادم/باك إند بدل تخزينه
 * في الكود مباشرة.
 */
private const val ADMIN_ACCESS_CODE = "kora2026"

@Composable
fun AdminPasswordDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var passwordInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = GoldAccent)
                Text(
                    text = "لوحة تحكم الأدمن 🔐",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "الرجاء إدخال كلمة المرور للوصول إلى صلاحيات إدارة المباراة واللاعبين:",
                    fontSize = 14.sp
                )

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = {
                        passwordInput = it
                        errorMessage = ""
                    },
                    label = { Text("كلمة المرور") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (passwordInput.trim() == ADMIN_ACCESS_CODE) {
                        onSuccess()
                    } else {
                        errorMessage = "كلمة المرور غير صحيحة!"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black)
            ) {
                Text("دخول", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
