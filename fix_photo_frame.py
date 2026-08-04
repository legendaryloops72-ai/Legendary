import re
with open('app/src/main/java/com.aistudio.kidspolice.abcd/ui/PhotoFrameScreen.kt', 'r') as f:
    content = f.read()

# Fix fonts
content = re.sub(r',\s*fontFamily\s*=\s*[a-zA-Z]+Family', '', content)

# Fix shadowLayer
content = content.replace("shadowLayer(5f, 0f, 0f, android.graphics.Color.BLACK)", "setShadowLayer(5f, 0f, 0f, android.graphics.Color.BLACK)")

# Fix saveBitmapToGallery return value
content = content.replace("""        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }""", """        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                return@withContext it
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
        return@withContext null""")

with open('app/src/main/java/com.aistudio.kidspolice.abcd/ui/PhotoFrameScreen.kt', 'w') as f:
    f.write(content)
