#!/bin/bash
cd /tmp/sounds_folder

mkdir -p /app/app/src/main/assets/sounds/animals
mkdir -p /app/app/src/main/assets/sounds/vehicles
mkdir -p /app/app/src/main/assets/sounds/other

mv *horse*.wav /app/app/src/main/assets/sounds/animals/ || true
mv *dog*.wav /app/app/src/main/assets/sounds/animals/ || true
mv *crow*.aiff /app/app/src/main/assets/sounds/animals/ || true
mv *goat*.mp3 /app/app/src/main/assets/sounds/animals/ || true
mv *goat*.wav /app/app/src/main/assets/sounds/animals/ || true
mv *alligator*.mp3 /app/app/src/main/assets/sounds/animals/ || true
mv *alligator*.ogg /app/app/src/main/assets/sounds/animals/ || true
mv *tiger*.wav /app/app/src/main/assets/sounds/animals/ || true
mv *cat*.wav /app/app/src/main/assets/sounds/animals/ || true
mv *cat*.mp3 /app/app/src/main/assets/sounds/animals/ || true
mv *fox*.mp3 /app/app/src/main/assets/sounds/animals/ || true
mv *lion*.mp3 /app/app/src/main/assets/sounds/animals/ || true
mv *bat*.mp3 /app/app/src/main/assets/sounds/animals/ || true
mv *bear*.wav /app/app/src/main/assets/sounds/animals/ || true
mv *bull*.mp3 /app/app/src/main/assets/sounds/animals/ || true
mv *camel*.mp3 /app/app/src/main/assets/sounds/animals/ || true
mv *Donkey*.mp3 /app/app/src/main/assets/sounds/animals/ || true
mv *duck*.wav /app/app/src/main/assets/sounds/animals/ || true
mv *frog*.mp3 /app/app/src/main/assets/sounds/animals/ || true
mv *insect*.mp3 /app/app/src/main/assets/sounds/animals/ || true
mv *monkey*.mp3 /app/app/src/main/assets/sounds/animals/ || true
mv *monkey*.flac /app/app/src/main/assets/sounds/animals/ || true
mv *rooster*.mp3 /app/app/src/main/assets/sounds/animals/ || true
mv *sheep*.wav /app/app/src/main/assets/sounds/animals/ || true
mv *turkey*.mp3 /app/app/src/main/assets/sounds/animals/ || true
mv *snake*.wav /app/app/src/main/assets/sounds/animals/ || true
mv *growl*.wav /app/app/src/main/assets/sounds/animals/ || true
mv *Creatures*.mp3 /app/app/src/main/assets/sounds/animals/ || true
mv *Animals*.mp3 /app/app/src/main/assets/sounds/animals/ || true

mv *police*.wav /app/app/src/main/assets/sounds/vehicles/ || true
mv *police*.mp3 /app/app/src/main/assets/sounds/vehicles/ || true
mv *ambulance*.mp3 /app/app/src/main/assets/sounds/vehicles/ || true
mv *car*.mp3 /app/app/src/main/assets/sounds/vehicles/ || true
mv *forklift*.mp3 /app/app/src/main/assets/sounds/vehicles/ || true
mv *helicopter*.mp3 /app/app/src/main/assets/sounds/vehicles/ || true
mv *motorcycle*.mp3 /app/app/src/main/assets/sounds/vehicles/ || true
mv *rocket*.mp3 /app/app/src/main/assets/sounds/vehicles/ || true
mv *train*.mp3 /app/app/src/main/assets/sounds/vehicles/ || true

mv Sugar_High.mp3 /app/app/src/main/assets/sounds/other/ || true

# Any remaining
mv * /app/app/src/main/assets/sounds/other/ 2>/dev/null || true

