package com.aistudio.kidspolice.abcd.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.R
import com.aistudio.kidspolice.abcd.audio.PoliceAudioPlayer
import com.aistudio.kidspolice.abcd.data.Dialect
import kotlinx.coroutines.launch

private val Background = Color(0xFFF4F8FF)
private val Navy = Color(0xFF0D47A1)
private val Blue = Color(0xFF1976D2)

@Composable
fun HomeScreen(
    selectedDialect: Dialect,
    onDialectSelected: (Dialect) -> Unit,
    onStartCall: (com.aistudio.kidspolice.abcd.data.PoliceScenario) -> Unit,
    onOpenDialer: () -> Unit,
    onOpenSounds: () -> Unit,
    onOpenMissions: () -> Unit,
    onOpenCertificate: () -> Unit,
    userScore: Int,
    onTestInterstitial: () -> Unit = {},
    audioPlayer: PoliceAudioPlayer? = null
) {
    val player = audioPlayer ?: return
    var tab by remember { mutableIntStateOf(0) }
    var showInfo by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun shareApp() {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "تطبيق شرطة الأطفال\nhttps://play.google.com/store/apps/details?id=com.aistudio.kidspolice.abcd")
            }
            context.startActivity(Intent.createChooser(intent, "مشاركة تطبيق شرطة الأطفال"))
        } catch (_: Exception) {}
    }

    fun openPrivacyPolicy() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://kidspolicy-munpfkb8.manus.space/"))
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Color.White) {
                Column(modifier = Modifier.fillMaxWidth().background(Navy).padding(20.dp)) {
                    Image(painterResource(R.drawable.police_child_icon), contentDescription = "شعار شرطة الأطفال", modifier = Modifier.size(78.dp).clip(CircleShape))
                    Spacer(Modifier.height(10.dp))
                    Text("شرطة الأطفال", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
                    Text("أصوات ومركبات الشرطة", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                }
                DrawerItem("الرئيسية", Icons.Default.Home) { if (tab != 0) player.stopSpeaking(); tab = 0; scope.launch { drawerState.close() } }
                DrawerItem("أصوات الشرطة", painterResource(R.drawable.ic_sound_wave)) { if (tab != 1) player.stopSpeaking(); tab = 1; scope.launch { drawerState.close() } }
                DrawerItem("سيارات الشرطة", painterResource(R.drawable.ic_section_cars)) { if (tab != 2) player.stopSpeaking(); tab = 2; scope.launch { drawerState.close() } }
                DrawerItem("مشاركة التطبيق", Icons.Default.Share) { shareApp(); scope.launch { drawerState.close() } }
                DrawerItem("سياسة الخصوصية", Icons.Default.Info) {
                    openPrivacyPolicy()
                    scope.launch { drawerState.close() }
                }
                DrawerItem("معلومات التطبيق", Icons.Default.Info) { showInfo = true; scope.launch { drawerState.close() } }
            }
        }
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl
        ) {
            Column(modifier = Modifier.fillMaxSize().background(Background)) {
                Box(modifier = Modifier.weight(1f)) {
                    when (tab) {
                        0 -> HomeLanding(
                            onMenu = { scope.launch { drawerState.open() } },
                            onSounds = { if (tab != 1) player.stopSpeaking(); tab = 1 },
                            onCars = { if (tab != 2) player.stopSpeaking(); tab = 2 },
                            onShare = ::shareApp
                        )
                        1 -> SoundsScreen(audioPlayer = player, onBack = { player.stopSpeaking(); tab = 0 })
                        else -> PoliceCarsScreen(onBack = { tab = 0 })
                    }
                }
                NavigationBar(modifier = Modifier.navigationBarsPadding(), containerColor = Color.White, tonalElevation = 8.dp) {
                    NavigationBarItem(
                        selected = tab == 0,
                        onClick = { if (tab != 0) player.stopSpeaking(); tab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية", modifier = Modifier.size(26.dp)) },
                        label = { Text("الرئيسية", fontWeight = if (tab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    NavigationBarItem(
                        selected = tab == 1,
                        onClick = { if (tab != 1) player.stopSpeaking(); tab = 1 },
                        icon = { Icon(painterResource(R.drawable.ic_sound_wave), contentDescription = "أصوات الشرطة", modifier = Modifier.size(26.dp)) },
                        label = { Text("الأصوات", fontWeight = if (tab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                    NavigationBarItem(
                        selected = tab == 2,
                        onClick = { if (tab != 2) player.stopSpeaking(); tab = 2 },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_section_cars),
                                contentDescription = "سيارات الشرطة",
                                modifier = Modifier.size(26.dp),
                                tint = Color.Unspecified
                            )
                        },
                        label = { Text("السيارات", fontWeight = if (tab == 2) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
        }
    }
    if (showInfo) {
        AlertDialog(onDismissRequest = { showInfo = false }, title = { Text("معلومات التطبيق") }, text = { Text("شرطة الأطفال\nأصوات ومركبات شرطة محلية للأطفال\nالإصدار 1.0.6") }, confirmButton = { TextButton(onClick = { showInfo = false }) { Text("إغلاق") } })
    }
}

@Composable
private fun DrawerItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    androidx.compose.material3.NavigationDrawerItem(label = { Text(title) }, selected = false, onClick = onClick, icon = { Icon(icon, contentDescription = title) }, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
}

@Composable
private fun DrawerItem(title: String, icon: androidx.compose.ui.graphics.painter.Painter, onClick: () -> Unit) {
    androidx.compose.material3.NavigationDrawerItem(label = { Text(title) }, selected = false, onClick = onClick, icon = { Icon(icon, contentDescription = title) }, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
}

@Composable
private fun DrawerItem(title: String, icon: @Composable () -> Unit, onClick: () -> Unit) {
    androidx.compose.material3.NavigationDrawerItem(label = { Text(title) }, selected = false, onClick = onClick, icon = icon, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
}

@Composable
private fun HomeLanding(onMenu: () -> Unit, onSounds: () -> Unit, onCars: () -> Unit, onShare: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(18.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onMenu, modifier = Modifier.size(48.dp)) { Icon(Icons.Default.Menu, contentDescription = "فتح القائمة", tint = Navy, modifier = Modifier.size(30.dp)) }
            Text("شرطة الأطفال", color = Navy, fontSize = 29.sp, fontWeight = FontWeight.ExtraBold)
            IconButton(onClick = onShare, modifier = Modifier.size(48.dp)) { Icon(Icons.Default.Share, contentDescription = "مشاركة التطبيق", tint = Navy, modifier = Modifier.size(26.dp)) }
        }
        Text("أصوات ومركبات الشرطة", color = Color(0xFF2C3E50), fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(14.dp))
        Card(modifier = Modifier.fillMaxWidth().height(220.dp), shape = RoundedCornerShape(30.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1128))) {
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0A1128), Color(0xFF001F54)))), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.police_child_icon),
                    contentDescription = "شرطي أطفال ثلاثي الأبعاد",
                    modifier = Modifier.fillMaxSize().padding(14.dp).clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        HomeActionCard(
            title = "أصوات الشرطة",
            subtitle = "30 مؤثرًا صوتيًا محليًا",
            onClick = onSounds,
            tint = Color(0xFF1565C0)
        ) {
            Box(modifier = Modifier.size(58.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFF1565C0).copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.ic_sound_wave), contentDescription = "أصوات الشرطة", tint = Color(0xFF1565C0), modifier = Modifier.size(34.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        HomeActionCard(
            title = "سيارات الشرطة",
            subtitle = "30 صورة أصلية للمركبات",
            onClick = onCars,
            tint = Color(0xFF00838F)
        ) {
            Box(modifier = Modifier.size(58.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFF00838F).copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_section_cars),
                    contentDescription = "سيارات الشرطة",
                    modifier = Modifier.size(42.dp),
                    tint = Color.Unspecified
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("سياسة الخصوصية ومعلومات التطبيق متاحة من القائمة الجانبية", color = Color(0xFF4A5568), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun HomeActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tint: Color,
    iconSlot: @Composable () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            iconSlot()
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) { Text(title, color = Color(0xFF18263D), fontSize = 18.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(3.dp)); Text(subtitle, color = Color(0xFF4A5568), fontSize = 13.sp, fontWeight = FontWeight.Medium) }
            Icon(Icons.Default.PlayArrow, contentDescription = "فتح", tint = tint)
        }
    }
}
