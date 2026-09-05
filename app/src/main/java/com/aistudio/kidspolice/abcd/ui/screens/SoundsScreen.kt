package com.aistudio.kidspolice.abcd.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.R
import com.aistudio.kidspolice.abcd.audio.PoliceAudioPlayer

private data class PoliceSound(val title: String, val resourceId: Int, val imageId: Int)

@Composable
fun SoundsScreen(audioPlayer: PoliceAudioPlayer, onBack: () -> Unit) {
    val sounds = remember {
        listOf(
            PoliceSound("صفارة شرطة أمريكية", R.raw.licensed_01_police_siren_us, R.drawable.sound_01_siren_us),
            PoliceSound("صفارة شرطة", R.raw.licensed_02_police_siren, R.drawable.sound_02_siren),
            PoliceSound("وصول سيارة طوارئ", R.raw.licensed_03_emergency_car_arrival, R.drawable.sound_03_arrival),
            PoliceSound("صفارة إنذار يدوية", R.raw.licensed_04_manual_emergency_siren, R.drawable.sound_04_manual_siren),
            PoliceSound("نغمة صفارة", R.raw.licensed_05_siren_tone, R.drawable.sound_05_siren_tone),
            PoliceSound("صفارة مركبة تمر", R.raw.licensed_06_passing_emergency_siren, R.drawable.sound_06_passing_siren),
            PoliceSound("إنذار مركبة", R.raw.licensed_07_vehicle_alarm, R.drawable.sound_07_vehicle_alarm),
            PoliceSound("صفارة شاحنة إطفاء أوروبية", R.raw.licensed_08_fire_truck_siren_eu, R.drawable.sound_08_fire_siren),
            PoliceSound("صفارة شاحنة إطفاء أمريكية", R.raw.licensed_09_fire_truck_siren_us, R.drawable.sound_09_us_siren),
            PoliceSound("صفارة إنذار قديمة", R.raw.licensed_10_vintage_emergency_siren, R.drawable.sound_10_vintage_siren),
            PoliceSound("تشغيل السيارات", R.raw.licensed_11_cars_starting, R.drawable.sound_11_car_start),
            PoliceSound("إشعال السيارة", R.raw.licensed_12_car_ignition, R.drawable.sound_12_ignition),
            PoliceSound("تشغيل محرك السيارة", R.raw.licensed_13_car_engine_start, R.drawable.sound_13_engine),
            PoliceSound("همهمة محرك مركبة", R.raw.licensed_14_vehicle_engine_hum, R.drawable.sound_14_vehicle_hum),
            PoliceSound("مرور سيارة سريع", R.raw.licensed_15_fast_car_driveby, R.drawable.sound_15_fast_drive),
            PoliceSound("إغلاق باب السيارة", R.raw.licensed_16_car_door_slam, R.drawable.sound_16_door_slam),
            PoliceSound("إغلاق باب سيارة", R.raw.licensed_17_car_door_close, R.drawable.sound_17_door_close),
            PoliceSound("تحريك مفاتيح السيارة", R.raw.licensed_18_car_keys, R.drawable.sound_18_keys),
            PoliceSound("وصول سيارة", R.raw.licensed_19_car_arriving, R.drawable.sound_19_arriving_car),
            PoliceSound("بوق مركبة", R.raw.licensed_20_truck_horn, R.drawable.sound_20_horn),
            PoliceSound("تشويش لاسلكي", R.raw.licensed_21_radio_static, R.drawable.sound_21_radio_static),
            PoliceSound("إشارة تردد لاسلكي", R.raw.licensed_22_radio_signal, R.drawable.sound_22_radio_signal),
            PoliceSound("إرسال لاسلكي", R.raw.licensed_23_radio_transmission, R.drawable.sound_23_radio_transmission),
            PoliceSound("تشويش موجات لاسلكية", R.raw.licensed_24_radio_glitch, R.drawable.sound_24_radio_glitch),
            PoliceSound("نغمة زر لاسلكي", R.raw.licensed_25_radio_button_ping, R.drawable.sound_25_radio_button),
            PoliceSound("تنبيه مزدوج", R.raw.licensed_26_double_beep_alert, R.drawable.sound_26_double_beep),
            PoliceSound("نغمة طوارئ عاجلة", R.raw.licensed_27_urgent_emergency_tone, R.drawable.sound_27_urgent_tone),
            PoliceSound("إنذار طوارئ", R.raw.licensed_28_emergency_alarm, R.drawable.sound_28_emergency_alarm),
            PoliceSound("صفارة دورية", R.raw.licensed_29_police_whistle, R.drawable.sound_29_police_whistle),
            PoliceSound("صفارة دورية قصيرة", R.raw.licensed_30_police_short_whistle, R.drawable.sound_30_short_whistle)
        )
    }
    var playingId by remember { mutableStateOf<Int?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stopSpeaking()
            playingId = null
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F8FF)).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(24.dp))
        Text("أصوات الشرطة", color = Color(0xFF0D47A1), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text("30 مؤثرًا حقيقيًا بترخيص موثق للاستخدام التجاري", color = Color(0xFF2C3E50), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(14.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF0D47A1))) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.foundation.layout.Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(Color(0xFF1976D2)), contentAlignment = Alignment.Center) {
                    Icon(painterResource(R.drawable.ic_sound_wave), contentDescription = "موجة صوت الشرطة", tint = Color.White, modifier = Modifier.size(34.dp))
                }
                Spacer(Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(if (playingId == null) "المشغل جاهز" else "يتم تشغيل صوت الشرطة الآن", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("اضغط تشغيل أو إيقاف — صوت واحد في كل مرة", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                }
                if (playingId != null) {
                    IconButton(
                        onClick = {
                            audioPlayer.stopSpeaking()
                            playingId = null
                        },
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFD32F2F))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إيقاف الصوت الحالي", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
            items(sounds, key = { it.resourceId }) { sound ->
                val isPlaying = playingId == sound.resourceId
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(sound.imageId), contentDescription = sound.title, modifier = Modifier.size(76.dp).clip(RoundedCornerShape(13.dp)), contentScale = ContentScale.Crop)
                        Spacer(Modifier.size(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(sound.title, color = Color(0xFF1D2B42), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(2.dp))
                            Text("ترخيص موثق للاستخدام التجاري", color = Color(0xFF4A5568), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        IconButton(
                            onClick = {
                                if (isPlaying) {
                                    audioPlayer.stopSpeaking()
                                    playingId = null
                                } else {
                                    audioPlayer.stopSpeaking()
                                    playingId = sound.resourceId
                                    audioPlayer.playRawAudioFile(sound.resourceId) { playingId = null }
                                }
                            },
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(if (isPlaying) Color(0xFFFFE7E7) else Color(0xFFE6F1FF))
                        ) {
                            Icon(
                                if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "إيقاف ${sound.title}" else "تشغيل ${sound.title}",
                                tint = if (isPlaying) Color(0xFFD32F2F) else Color(0xFF1565C0),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
