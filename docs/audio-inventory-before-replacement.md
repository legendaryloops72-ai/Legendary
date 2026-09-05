# Audio inventory before licensed replacement

Generated on 2026-09-05.

## Existing files
police_01_short_siren.wav
police_02_long_siren.wav
police_03_double_siren.wav
police_04_emergency_siren.wav
police_05_fast_alarm.wav
police_06_slow_alarm.wav
police_07_wail_siren.wav
police_08_car_alarm.wav
police_09_police_horn.wav
police_10_engine_start.wav
police_11_engine_idle.wav
police_12_engine_acceleration.wav
police_13_car_brake.wav
police_14_car_stop.wav
police_15_door_open.wav
police_16_door_close.wav
police_17_car_lock.wav
police_18_radio_open.wav
police_19_radio_close.wav
police_20_radio_static.wav
police_21_radio_call.wav
police_22_radio_alert.wav
police_23_car_arrival.wav
police_24_car_departure.wav
police_25_short_emergency.wav
police_26_long_emergency.wav
police_27_patrol_whistle.wav
police_28_police_alert.wav
police_29_police_arrival.wav
police_30_dispatch_tone.wav
voice_01_welcome.wav
voice_02_good_morning.wav
voice_03_sleep.wav
voice_04_healthy_food.wav
voice_05_respect_parents.wav
voice_06_homework.wav
voice_07_good_behavior.wav
voice_08_encouragement.wav
voice_09_points.wav
voice_10_reward.wav
voice_11_star.wav
voice_12_success.wav
voice_13_try_again.wav
voice_14_call_start.wav
voice_15_call_question.wav
voice_16_call_encourage.wav
voice_17_call_advice.wav
voice_18_call_end.wav
voice_19_warning.wav
voice_20_danger.wav
voice_21_emergency.wav
voice_22_badge.wav
voice_23_great_job.wav
voice_24_level_up.wav
voice_25_points_100.wav

## Code references
app/src/main/res/values/themes.xml:4:        <item name="android:statusBarColor">@color/police_navy</item>
app/src/main/res/values/themes.xml:5:        <item name="android:navigationBarColor">@color/police_navy</item>
app/src/main/res/values/colors.xml:10:    <color name="police_navy">#FF0D47A1</color>
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:103:                val resId = com.aistudio.kidspolice.abcd.R.raw.voice_01_welcome
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:216:    fun playRawAudioFile(resId: Int, onComplete: () -> Unit = {}) {
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:263:                val isVoice14 = resId == com.aistudio.kidspolice.abcd.R.raw.voice_14_call_start
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:299:        playRawAudioFile(resId) {
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:307:    fun playScenarioCall(scenarioId: String) {
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:312:            playRawAudioSuspend(com.aistudio.kidspolice.abcd.R.raw.voice_14_call_start)
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:314:                "sleep_early" -> com.aistudio.kidspolice.abcd.R.raw.voice_03_sleep
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:315:                "eating_food" -> com.aistudio.kidspolice.abcd.R.raw.voice_04_healthy_food
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:316:                "listen_parents" -> com.aistudio.kidspolice.abcd.R.raw.voice_05_respect_parents
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:317:                "homework_study" -> com.aistudio.kidspolice.abcd.R.raw.voice_06_homework
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:318:                "hero_reward" -> com.aistudio.kidspolice.abcd.R.raw.voice_23_great_job
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:319:                else -> com.aistudio.kidspolice.abcd.R.raw.voice_15_call_question
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:330:            text.contains("نمت") || text.contains("سريرك") || text.contains("تختك") || text.contains("تصبح على خير") -> com.aistudio.kidspolice.abcd.R.raw.voice_03_sleep
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:331:            text.contains("تاكل") || text.contains("أكلك") || text.contains("وجبتك") || text.contains("طبقك") -> com.aistudio.kidspolice.abcd.R.raw.voice_04_healthy_food
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:332:            text.contains("ماما وبابا") || text.contains("والديك") || text.contains("احترام") || text.contains("طاعة") -> com.aistudio.kidspolice.abcd.R.raw.voice_05_respect_parents
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:333:            text.contains("واجباتك") || text.contains("دراسة") || text.contains("كتبك") || text.contains("المذاكرة") -> com.aistudio.kidspolice.abcd.R.raw.voice_06_homework
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:334:            text.contains("بطلنا العظيم") || text.contains("وسام") || text.contains("مكافأة") || text.contains("ألف مبروك") -> com.aistudio.kidspolice.abcd.R.raw.voice_23_great_job
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:335:            else -> com.aistudio.kidspolice.abcd.R.raw.voice_15_call_question
app/src/main/java/com/aistudio/kidspolice/abcd/audio/PoliceAudioPlayer.kt:337:        playRawAudioFile(resId)
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/CertificateScreen.kt:106:                        painter = painterResource(R.drawable.ic_police_officer_hero),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/CallScreen.kt:99:                audioPlayer.playScenarioCall(scenario.id)
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:48:            PoliceSound("صفارة شرطة قصيرة", R.raw.police_01_short_siren),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:49:            PoliceSound("صفارة شرطة طويلة", R.raw.police_02_long_siren),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:50:            PoliceSound("صفارة مزدوجة", R.raw.police_03_double_siren),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:51:            PoliceSound("صفارة طوارئ", R.raw.police_04_emergency_siren),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:52:            PoliceSound("إنذار شرطة سريع", R.raw.police_05_fast_alarm),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:53:            PoliceSound("إنذار شرطة بطيء", R.raw.police_06_slow_alarm),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:54:            PoliceSound("صفارة متغيرة النغمة", R.raw.police_07_wail_siren),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:55:            PoliceSound("إنذار سيارة شرطة", R.raw.police_08_car_alarm),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:56:            PoliceSound("بوق سيارة شرطة", R.raw.police_09_police_horn),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:57:            PoliceSound("تشغيل محرك الشرطة", R.raw.police_10_engine_start),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:58:            PoliceSound("محرك سيارة الشرطة", R.raw.police_11_engine_idle),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:59:            PoliceSound("تسارع سيارة الشرطة", R.raw.police_12_engine_acceleration),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:60:            PoliceSound("فرامل سيارة الشرطة", R.raw.police_13_car_brake),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:61:            PoliceSound("توقف سيارة الشرطة", R.raw.police_14_car_stop),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:62:            PoliceSound("فتح باب السيارة", R.raw.police_15_door_open),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:63:            PoliceSound("إغلاق باب السيارة", R.raw.police_16_door_close),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:64:            PoliceSound("قفل السيارة", R.raw.police_17_car_lock),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:65:            PoliceSound("فتح اتصال لاسلكي", R.raw.police_18_radio_open),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:66:            PoliceSound("إغلاق اتصال لاسلكي", R.raw.police_19_radio_close),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:67:            PoliceSound("تشويش لاسلكي", R.raw.police_20_radio_static),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:68:            PoliceSound("نداء لاسلكي", R.raw.police_21_radio_call),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:69:            PoliceSound("تنبيه لاسلكي", R.raw.police_22_radio_alert),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:70:            PoliceSound("وصول سيارة الشرطة", R.raw.police_23_car_arrival),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:71:            PoliceSound("مغادرة سيارة الشرطة", R.raw.police_24_car_departure),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:72:            PoliceSound("إنذار طوارئ قصير", R.raw.police_25_short_emergency),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:73:            PoliceSound("إنذار طوارئ طويل", R.raw.police_26_long_emergency),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:74:            PoliceSound("صفارة دورية", R.raw.police_27_patrol_whistle),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:75:            PoliceSound("تنبيه شرطة", R.raw.police_28_police_alert),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:76:            PoliceSound("مؤثر وصول الشرطة", R.raw.police_29_police_arrival),
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:77:            PoliceSound("نغمة النداء المركزي", R.raw.police_30_dispatch_tone)
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/SoundsScreen.kt:136:                                    audioPlayer.playRawAudioFile(sound.resourceId) { playingId = null }
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/HomeScreen.kt:103:                    Image(painterResource(R.drawable.police_child_icon), contentDescription = "شعار شرطة الأطفال", modifier = Modifier.size(78.dp).clip(CircleShape))
app/src/main/java/com/aistudio/kidspolice/abcd/ui/screens/HomeScreen.kt:167:                Image(painterResource(R.drawable.police_child_icon), contentDescription = "شرطي أطفال ثلاثي الأبعاد", modifier = Modifier.fillMaxSize().padding(20.dp), contentScale = ContentScale.Fit)
app/src/main/res/drawable/ic_launcher_foreground.xml:1:<bitmap xmlns:android="http://schemas.android.com/apk/res/android" android:src="@drawable/police_child_icon" android:gravity="fill" />
