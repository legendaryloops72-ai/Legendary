package com.aistudio.kidspolice.abcd.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.R

private data class PoliceCar(val title: String, val imageId: Int)

@Composable
fun PoliceCarsScreen(onBack: () -> Unit) {
    val cars = remember {
        listOf(
            PoliceCar("دورية سيدان", R.drawable.car_01_sedan), PoliceCar("دورية SUV", R.drawable.car_02_suv),
            PoliceCar("سيارة رياضية", R.drawable.car_03_sport), PoliceCar("دورية حديثة", R.drawable.car_04_modern),
            PoliceCar("دورية ليلية", R.drawable.car_05_night), PoliceCar("دورية نهارية", R.drawable.car_06_day),
            PoliceCar("دفع رباعي", R.drawable.car_07_4x4), PoliceCar("شاحنة شرطة", R.drawable.car_08_truck),
            PoliceCar("سيارة كلاسيكية", R.drawable.car_09_classic), PoliceCar("سيارة مستقبلية", R.drawable.car_10_future),
            PoliceCar("دراجة شرطة", R.drawable.car_11_motorcycle), PoliceCar("مركبة إنقاذ", R.drawable.car_12_rescue_van),
            PoliceCar("مركبة أمنية", R.drawable.car_13_armored), PoliceCar("مركز قيادة متنقل", R.drawable.car_14_command),
            PoliceCar("دورية المدينة الصغيرة", R.drawable.car_15_mini), PoliceCar("دراجة رياضية", R.drawable.car_16_sportbike),
            PoliceCar("دراجة دفع رباعي", R.drawable.car_17_quad), PoliceCar("قارب الشرطة", R.drawable.car_18_water),
            PoliceCar("مروحية الشرطة", R.drawable.car_19_helicopter), PoliceCar("طائرة مراقبة", R.drawable.car_20_drone),
            PoliceCar("حافلة الشرطة", R.drawable.car_21_bus), PoliceCar("شاحنة الاستجابة", R.drawable.car_22_fire_rescue),
            PoliceCar("دورية الرالي", R.drawable.car_23_rally), PoliceCar("دورية الثلج", R.drawable.car_24_snow),
            PoliceCar("دورية الصحراء", R.drawable.car_25_desert), PoliceCar("سيدان كلاسيكية", R.drawable.car_26_classic_sedan),
            PoliceCar("عربة الدورية", R.drawable.car_27_patrol_wagon), PoliceCar("سيارة كهربائية", R.drawable.car_28_electric),
            PoliceCar("إنقاذ الجبال", R.drawable.car_29_mountain), PoliceCar("SUV القيادة", R.drawable.car_30_command_suv)
        )
    }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    if (selectedIndex >= 0) {
        PoliceCarDetail(cars = cars, selectedIndex = selectedIndex, onSelected = { selectedIndex = it }, onBack = { selectedIndex = -1 })
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F8FF)).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(24.dp))
        Text("صور سيارات الشرطة", color = Color(0xFF0D47A1), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text("30 مركبة أصلية — اضغط على أي صورة للتفاصيل", color = Color(0xFF2C3E50), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(cars) { index, car ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp)
                        .clickable { selectedIndex = index },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8FAFC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(car.imageId),
                                contentDescription = car.title,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            car.title,
                            color = Color(0xFF1D2B42),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PoliceCarDetail(cars: List<PoliceCar>, selectedIndex: Int, onSelected: (Int) -> Unit, onBack: () -> Unit) {
    val car = cars[selectedIndex]
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F8FF)).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "العودة لقائمة السيارات", tint = Color(0xFF0D47A1)) }
            Text("تفاصيل سيارة الشرطة", color = Color(0xFF0D47A1), fontSize = 21.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth().weight(1f), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(car.imageId),
                    contentDescription = car.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(car.title, color = Color(0xFF1D2B42), fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("${selectedIndex + 1} من ${cars.size}", color = Color(0xFF4A5568), fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(28.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onSelected(if (selectedIndex == 0) cars.lastIndex else selectedIndex - 1) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "السيارة السابقة", tint = Color(0xFF1565C0), modifier = Modifier.size(32.dp))
            }
            Icon(Icons.Default.Star, contentDescription = "مركبة شرطة معتمدة", tint = Color(0xFF00838F), modifier = Modifier.size(30.dp))
            IconButton(
                onClick = { onSelected(if (selectedIndex == cars.lastIndex) 0 else selectedIndex + 1) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "السيارة التالية", tint = Color(0xFF1565C0), modifier = Modifier.size(32.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}
