import re

with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace('onNavigateToPhotoFrame', 'onNavigateToPoliceCars')

# Replace the photo_frame route with police_cars and add police_car_detail
photo_frame_route = """                        "photo_frame" -> {
                            PhotoFrameScreen(
                                
                                onBack = { currentScreen = "home" }
                            )
                        }"""

new_routes = """                        "police_cars" -> {
                            PoliceCarsGalleryScreen(
                                onNavigateBack = { currentScreen = "home" },
                                onVehicleSelected = { id ->
                                    selectedVehicleId = id
                                    currentScreen = "police_car_detail"
                                }
                            )
                        }
                        "police_car_detail" -> {
                            PoliceCarDetailScreen(
                                vehicleId = selectedVehicleId,
                                onNavigateBack = { currentScreen = "police_cars" }
                            )
                        }"""

content = content.replace(photo_frame_route, new_routes)
content = content.replace('"photo_frame"', '"police_cars"')

# We need to insert `var selectedVehicleId by remember { mutableIntStateOf(0) }` inside MainActivity Compose content block
if 'var selectedVehicleId by remember' not in content:
    content = content.replace('var currentScreen by remember { mutableStateOf("splash") }', 
                              'var currentScreen by remember { mutableStateOf("splash") }\n                var selectedVehicleId by remember { mutableIntStateOf(0) }')

with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'w') as f:
    f.write(content)
