sed -i '/item {/a \
                                    Row(\
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),\
                                        horizontalArrangement = Arrangement.End\
                                    ) {\
                                        IconButton(\
                                            onClick = {\
                                                if (isSpeaking) {\
                                                    tts?.stop()\
                                                    isSpeaking = false\
                                                } else {\
                                                    generatedStory?.let {\
                                                        tts?.language = Locale("ar")\
                                                        tts?.speak(it, TextToSpeech.QUEUE_FLUSH, null, null)\
                                                        isSpeaking = true\
                                                    }\
                                                }\
                                            },\
                                            modifier = Modifier.background(Color(0xFFEFF6FF), RoundedCornerShape(50))\
                                        ) {\
                                            Icon(\
                                                if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,\
                                                contentDescription = if (isSpeaking) "إيقاف القراءة" else "قراءة القصة",\
                                                tint = Color(0xFF3B82F6)\
                                            )\
                                        }\
                                    }' app/src/main/java/com.aistudio.kidspolice.abcd/ui/StoriesScreen.kt
