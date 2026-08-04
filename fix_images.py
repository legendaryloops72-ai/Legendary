import re

with open('app/src/main/java/com.aistudio.kidspolice.abcd/ui/PoliceCarsGalleryScreen.kt', 'r') as f:
    content = f.read()

# Replace Icon with Image in PoliceVehicleCard
old_icon_card = """            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // TODO: Replace Icon with AsyncImage(model = "file:///android_asset/${vehicle.imageAssetPath}.png") when assets are provided
                Icon(
                    imageVector = Icons.Default.LocalPolice,
                    contentDescription = vehicle.name,
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFF1E3A8A)
                )
            }"""

new_image_card = """            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                val context = LocalContext.current
                val imageResId = remember(vehicle.imageAssetPath) {
                    context.resources.getIdentifier(vehicle.imageAssetPath, "drawable", context.packageName)
                }
                
                if (imageResId != 0) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = imageResId),
                        contentDescription = vehicle.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.LocalPolice,
                        contentDescription = vehicle.name,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF1E3A8A)
                    )
                }
            }"""

content = content.replace(old_icon_card, new_image_card)

# Replace Icon with Image in PoliceCarDetailScreen
old_icon_detail = """            // Hero Image Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // TODO: Replace Icon with AsyncImage when assets are provided
                Icon(
                    imageVector = Icons.Default.LocalPolice,
                    contentDescription = vehicle.name,
                    modifier = Modifier.size(120.dp),
                    tint = Color(0xFF1E3A8A)
                )
            }"""

new_image_detail = """            // Hero Image Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                val context = LocalContext.current
                val imageResId = remember(vehicle.imageAssetPath) {
                    context.resources.getIdentifier(vehicle.imageAssetPath, "drawable", context.packageName)
                }
                
                if (imageResId != 0) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = imageResId),
                        contentDescription = vehicle.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.LocalPolice,
                        contentDescription = vehicle.name,
                        modifier = Modifier.size(120.dp),
                        tint = Color(0xFF1E3A8A)
                    )
                }
            }"""

content = content.replace(old_icon_detail, new_image_detail)

with open('app/src/main/java/com.aistudio.kidspolice.abcd/ui/PoliceCarsGalleryScreen.kt', 'w') as f:
    f.write(content)
