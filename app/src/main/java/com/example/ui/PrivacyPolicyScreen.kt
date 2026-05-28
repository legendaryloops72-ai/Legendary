package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onAccept: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("سياسة الخصوصية والأمان") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("مرحباً بكم في تطبيق شرطة الأطفال", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "نحن نهتم جداً بخصوصية أطفالكم وأمانهم. هذا التطبيق مصمم للعائلات والأطفال ويتوافق التوافق التام مع سياسة: COPPA و Google Play Families Policy.\n\n" +
                "1. لا نجمع أي معلومات شخصية حساسة.\n" +
                "2. البيانات مثل اسم الطفل والنقاط يتم تخزينها محلياً على جهازكم.\n" +
                "3. الإعلانات الموجودة مناسبة للأطفال فقط (G-rated) وتم تعطيل تتبع الإعلانات المخصصة.\n" +
                "4. عند استخدام تسجيل الدخول لحفظ التقدم عبر Firebase، يتم ذلك لغرض أمان حسابكم فقط.\n\n" +
                "لأي استفسار يرجى التواصل معنا عبر: support@aistudio.kids"
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("موافقة الوالدين (أوافق)", modifier = Modifier.padding(8.dp))
            }
        }
    }
}
