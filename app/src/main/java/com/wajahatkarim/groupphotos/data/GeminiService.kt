package com.wajahatkarim.groupphotos.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.wajahatkarim.groupphotos.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class GeminiService {

    companion object {
        private const val TAG = "GeminiService"
    }

    // Nano Banana Pro - Image generation and editing model
    private val imageGenerationModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp-image-generation",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                temperature = 1f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 8192
            }
        )
    }

    /**
     * Merges the photographer into the group photo using Gemini Image Generation
     * Returns the generated image as a Bitmap
     */
    suspend fun mergePhotos(
        context: Context,
        groupPhotoUri: Uri,
        photographerUri: Uri
    ): Result<Bitmap> {
        return withContext(Dispatchers.IO) {
            try {
                val groupBitmap = loadBitmapFromUri(context, groupPhotoUri)
                    ?: return@withContext Result.failure(Exception("Failed to load group photo"))

                val photographerBitmap = loadBitmapFromUri(context, photographerUri)
                    ?: return@withContext Result.failure(Exception("Failed to load photographer photo"))

                // Resize images if they're too large
                val resizedGroupBitmap = resizeBitmapIfNeeded(groupBitmap, 1024)
                val resizedPhotographerBitmap = resizeBitmapIfNeeded(photographerBitmap, 1024)

                Log.d(TAG, "Group photo size: ${resizedGroupBitmap.width}x${resizedGroupBitmap.height}")
                Log.d(TAG, "Photographer photo size: ${resizedPhotographerBitmap.width}x${resizedPhotographerBitmap.height}")

                val prompt = """
                    You are an expert photo editor. I have two photos:
                    1. A group photo with people in it
                    2. A photo of a person (the photographer) who was taking the group photo

                    Your task: Seamlessly merge the photographer into the group photo so it looks like
                    they were always part of the group.

                    Requirements:
                    - Find a natural gap or position in the group photo to place the photographer
                    - Match the lighting, color tone, and perspective
                    - Make sure the scale of the person matches others in the group
                    - Remove any background from the photographer's photo
                    - The result should look completely natural, as if everyone posed together
                    - Maintain the original quality and style of the group photo

                    Generate the merged photo.
                """.trimIndent()

                Log.d(TAG, "Sending request to Gemini...")

                val response = imageGenerationModel.generateContent(
                    content {
                        image(resizedGroupBitmap)
                        image(resizedPhotographerBitmap)
                        text(prompt)
                    }
                )

                Log.d(TAG, "Response received, extracting image...")

                // Extract the generated image from response
                val generatedBitmap = extractImageFromResponse(response)
                    ?: return@withContext Result.failure(Exception("No image generated in response. Response text: ${response.text}"))

                Log.d(TAG, "Image extracted successfully: ${generatedBitmap.width}x${generatedBitmap.height}")

                Result.success(generatedBitmap)
            } catch (e: Exception) {
                Log.e(TAG, "Error merging photos", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Extract image bitmap from Gemini response
     */
    private fun extractImageFromResponse(response: GenerateContentResponse): Bitmap? {
        try {
            response.candidates.forEach { candidate ->
                candidate.content.parts.forEach { part ->
                    Log.d(TAG, "Part type: ${part::class.simpleName}")

                    // Try to get inline data using reflection or direct access
                    // The SDK may return image data in different formats

                    // Check if part has inlineData property
                    try {
                        val partClass = part::class.java

                        // Try to find inlineData or similar field
                        partClass.methods.forEach { method ->
                            Log.d(TAG, "Available method: ${method.name}")
                        }

                        // Try getInlineData method
                        val getInlineData = partClass.methods.find {
                            it.name == "getInlineData" || it.name == "getImage"
                        }

                        if (getInlineData != null) {
                            val inlineData = getInlineData.invoke(part)
                            Log.d(TAG, "InlineData: $inlineData")

                            if (inlineData != null) {
                                // Try to extract data and mimeType
                                val inlineDataClass = inlineData::class.java
                                val getData = inlineDataClass.methods.find { it.name == "getData" }
                                val data = getData?.invoke(inlineData)

                                if (data is ByteArray) {
                                    return BitmapFactory.decodeByteArray(data, 0, data.size)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Reflection error: ${e.message}")
                    }
                }
            }

            // If we couldn't extract image, log the response for debugging
            Log.d(TAG, "Response text: ${response.text}")

        } catch (e: Exception) {
            Log.e(TAG, "Error extracting image from response", e)
        }
        return null
    }

    /**
     * Resize bitmap if it exceeds max dimension
     */
    private fun resizeBitmapIfNeeded(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }

        val scale = if (width > height) {
            maxDimension.toFloat() / width
        } else {
            maxDimension.toFloat() / height
        }

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap from URI", e)
            null
        }
    }

    /**
     * Convert bitmap to base64 string
     */
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
