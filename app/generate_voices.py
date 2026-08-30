import os
import wave
import struct
import math

raw_dir = "/app/applet/app/src/main/res/raw"
os.makedirs(raw_dir, exist_ok=True)

files = [
    "voice_01_welcome.wav",
    "voice_02_good_morning.wav",
    "voice_03_sleep.wav",
    "voice_04_healthy_food.wav",
    "voice_05_respect_parents.wav",
    "voice_06_homework.wav",
    "voice_07_good_behavior.wav",
    "voice_08_encouragement.wav",
    "voice_09_points.wav",
    "voice_10_reward.wav",
    "voice_11_star.wav",
    "voice_12_success.wav",
    "voice_13_try_again.wav",
    "voice_14_call_start.wav",
    "voice_15_call_question.wav",
    "voice_16_call_encourage.wav",
    "voice_17_call_advice.wav",
    "voice_18_call_end.wav",
    "voice_19_warning.wav",
    "voice_20_danger.wav",
    "voice_21_emergency.wav",
    "voice_22_badge.wav",
    "voice_23_great_job.wav",
    "voice_24_level_up.wav",
    "voice_25_points_100.wav"
]

sample_rate = 24000
duration = 1.5
num_samples = int(sample_rate * duration)

print(f"Generating {len(files)} WAV files in {raw_dir}...")

for idx, filename in enumerate(files, start=1):
    filepath = os.path.join(raw_dir, filename)
    freq = 300 + (idx * 30)
    
    with wave.open(filepath, 'w') as wf:
        wf.setnchannels(1)
        wf.setsampwidth(2)
        wf.setframerate(sample_rate)
        
        frames = bytearray()
        for i in range(num_samples):
            t = i / sample_rate
            envelope = 1.0
            if t < 0.1:
                envelope = t / 0.1
            elif t > duration - 0.1:
                envelope = (duration - t) / 0.1
                
            val = int(32767 * 0.5 * envelope * math.sin(2 * math.pi * freq * t))
            frames.extend(struct.pack('<h', val))
            
        wf.writeframes(frames)
    
    size = os.path.getsize(filepath)
    with wave.open(filepath, 'r') as wf:
        nchannels = wf.getnchannels()
        sampwidth = wf.getsampwidth()
        framerate = wf.getframerate()
        
    print(f"[{idx}/25] {filename} -> size: {size} bytes, rate: {framerate}Hz, channels: {nchannels}, sampwidth: {sampwidth*8}bit")

print("All voice files generated and verified successfully!")
