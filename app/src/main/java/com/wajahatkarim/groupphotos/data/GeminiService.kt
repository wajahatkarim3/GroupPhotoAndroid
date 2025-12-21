package com.wajahatkarim.groupphotos.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.wajahatkarim.groupphotos.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiService {

    companion object {
        private const val TAG = "GeminiService"
        // Nano Banana Pro - Image generation and editing model
        private const val MODEL_NAME = "gemini-3-pro-image-preview"
        private const val API_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

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

                // Convert bitmaps to base64
                val groupBase64 = bitmapToBase64(resizedGroupBitmap)
                val photographerBase64 = bitmapToBase64(resizedPhotographerBitmap)

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

                // Build the request body (matching your web backend structure)
                val requestBody = buildRequestBody(groupBase64, photographerBase64, prompt)

                Log.d(TAG, "Sending request to Gemini API...")

                val request = Request.Builder()
                    .url("$API_URL?key=${BuildConfig.GEMINI_API_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                Log.d(TAG, "Response code: ${response.code}")

                if (!response.isSuccessful) {
                    Log.e(TAG, "API Error: $responseBody")
                    return@withContext Result.failure(Exception("API Error: ${response.code} - $responseBody"))
                }

                // Parse the response and extract the image
                val generatedBitmap = extractImageFromResponse(responseBody)
                    ?: return@withContext Result.failure(Exception("No image in response. Response: $responseBody"))

                Log.d(TAG, "Image extracted successfully: ${generatedBitmap.width}x${generatedBitmap.height}")

                Result.success(generatedBitmap)
            } catch (e: Exception) {
                Log.e(TAG, "Error merging photos", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Build the JSON request body for Gemini API
     */
    private fun buildRequestBody(groupBase64: String, photographerBase64: String, prompt: String): JSONObject {
        val parts = JSONArray().apply {
            // Group photo
            put(JSONObject().apply {
                put("inlineData", JSONObject().apply {
                    put("mimeType", "image/jpeg")
                    put("data", groupBase64)
                })
            })
            // Text indicating first image
            put(JSONObject().apply {
                put("text", "This is the group photo.")
            })
            // Photographer photo
            put(JSONObject().apply {
                put("inlineData", JSONObject().apply {
                    put("mimeType", "image/jpeg")
                    put("data", photographerBase64)
                })
            })
            // Prompt
            put(JSONObject().apply {
                put("text", prompt)
            })
        }

        val contents = JSONArray().apply {
            put(JSONObject().apply {
                put("parts", parts)
            })
        }

        val generationConfig = JSONObject().apply {
            put("temperature", 1)
            put("topK", 40)
            put("topP", 0.95)
            put("maxOutputTokens", 8192)
            // Request image output
            put("responseModalities", JSONArray().apply {
                put("TEXT")
                put("IMAGE")
            })
        }

        return JSONObject().apply {
            put("contents", contents)
            put("generationConfig", generationConfig)
        }
    }

    /**
     * Extract image bitmap from Gemini API response
     */
    private fun extractImageFromResponse(responseBody: String?): Bitmap? {
        if (responseBody == null) return null

        try {
            val json = JSONObject(responseBody)
            val candidates = json.optJSONArray("candidates") ?: return null

            for (i in 0 until candidates.length()) {
                val candidate = candidates.getJSONObject(i)
                val content = candidate.optJSONObject("content") ?: continue
                val parts = content.optJSONArray("parts") ?: continue

                for (j in 0 until parts.length()) {
                    val part = parts.getJSONObject(j)

                    // Check for inlineData (camelCase)
                    var inlineData = part.optJSONObject("inlineData")
                    // Check for inline_data (snake_case)
                    if (inlineData == null) {
                        inlineData = part.optJSONObject("inline_data")
                    }

                    if (inlineData != null) {
                        val data = inlineData.optString("data", "")
                        val mimeType = inlineData.optString("mimeType", inlineData.optString("mime_type", ""))

                        if (data.isNotEmpty() && mimeType.startsWith("image/")) {
                            Log.d(TAG, "Found image data with mimeType: $mimeType")
                            val imageBytes = Base64.decode(data, Base64.DEFAULT)
                            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        }
                    }
                }
            }

            Log.d(TAG, "No image found in response")
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing response", e)
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
