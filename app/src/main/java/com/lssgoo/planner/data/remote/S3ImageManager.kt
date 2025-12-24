package com.lssgoo.planner.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.lssgoo.planner.data.AppConstants
import com.lssgoo.planner.util.DeviceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.UUID

/**
 * S3ImageManager handles image upload/download operations to AWS S3
 * 
 * Folder Structure:
 * - planner/
 *   - backup/       -> App data backups (JSON)
 *   - images/       -> User uploaded images (profile, journal, notes)
 *   - exports/      -> Exported files (CSV, reports)
 *   - attachments/  -> File attachments
 */
class S3ImageManager(private val context: Context) {
    
    companion object {
        // S3 Folder paths
        const val BASE_FOLDER = "planner"
        const val BACKUP_FOLDER = "$BASE_FOLDER/backup"
        const val IMAGES_FOLDER = "$BASE_FOLDER/images"
        const val PROFILE_IMAGES_FOLDER = "$IMAGES_FOLDER/profile"
        const val JOURNAL_IMAGES_FOLDER = "$IMAGES_FOLDER/journal"
        const val NOTES_IMAGES_FOLDER = "$IMAGES_FOLDER/notes"
        const val EXPORTS_FOLDER = "$BASE_FOLDER/exports"
        const val ATTACHMENTS_FOLDER = "$BASE_FOLDER/attachments"
        
        // Image settings
        private const val MAX_IMAGE_SIZE = 1920 // Max dimension in pixels
        private const val JPEG_QUALITY = 85 // Compression quality
    }
    
    private val s3Client: AmazonS3Client by lazy {
        val credentials = BasicAWSCredentials(
            AppConstants.S3_ACCESS_KEY,
            AppConstants.S3_SECRET_KEY
        )
        AmazonS3Client(credentials).apply {
            setRegion(Region.getRegion(Regions.fromName(AppConstants.S3_REGION)))
        }
    }
    
    private val deviceId by lazy { DeviceUtils.getDeviceId(context) }
    
    /**
     * Upload an image to S3 and return the public URL
     * @param uri Local URI of the image
     * @param folder Target folder in S3 (e.g., PROFILE_IMAGES_FOLDER)
     * @param customName Optional custom filename (without extension)
     * @return Pair of (imageUrl, error) - imageUrl is null if upload failed
     */
    suspend fun uploadImage(
        uri: Uri,
        folder: String = IMAGES_FOLDER,
        customName: String? = null
    ): Pair<String?, String?> = withContext(Dispatchers.IO) {
        try {
            // Read and compress the image
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Pair(null, "Failed to open image")
            
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            
            if (originalBitmap == null) {
                return@withContext Pair(null, "Failed to decode image")
            }
            
            // Resize if needed
            val resizedBitmap = resizeBitmap(originalBitmap, MAX_IMAGE_SIZE)
            
            // Compress to JPEG
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
            val imageBytes = outputStream.toByteArray()
            outputStream.close()
            
            // Clean up bitmaps
            if (resizedBitmap != originalBitmap) {
                resizedBitmap.recycle()
            }
            originalBitmap.recycle()
            
            // Generate S3 key
            val fileName = customName ?: "${UUID.randomUUID()}"
            val s3Key = "$folder/${deviceId}_${fileName}.jpg"
            
            // Upload to S3
            val metadata = ObjectMetadata().apply {
                contentType = "image/jpeg"
                contentLength = imageBytes.size.toLong()
            }
            
            val putRequest = PutObjectRequest(
                AppConstants.S3_BUCKET_NAME,
                s3Key,
                ByteArrayInputStream(imageBytes),
                metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead)
            
            s3Client.putObject(putRequest)
            
            // Generate public URL
            val imageUrl = "https://${AppConstants.S3_BUCKET_NAME}.s3.${AppConstants.S3_REGION}.amazonaws.com/$s3Key"
            
            Pair(imageUrl, null)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, e.message ?: "Upload failed")
        }
    }
    
    /**
     * Upload a bitmap directly to S3
     */
    suspend fun uploadBitmap(
        bitmap: Bitmap,
        folder: String = IMAGES_FOLDER,
        customName: String? = null
    ): Pair<String?, String?> = withContext(Dispatchers.IO) {
        try {
            val resizedBitmap = resizeBitmap(bitmap, MAX_IMAGE_SIZE)
            
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
            val imageBytes = outputStream.toByteArray()
            outputStream.close()
            
            if (resizedBitmap != bitmap) {
                resizedBitmap.recycle()
            }
            
            val fileName = customName ?: "${UUID.randomUUID()}"
            val s3Key = "$folder/${deviceId}_${fileName}.jpg"
            
            val metadata = ObjectMetadata().apply {
                contentType = "image/jpeg"
                contentLength = imageBytes.size.toLong()
            }
            
            val putRequest = PutObjectRequest(
                AppConstants.S3_BUCKET_NAME,
                s3Key,
                ByteArrayInputStream(imageBytes),
                metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead)
            
            s3Client.putObject(putRequest)
            
            val imageUrl = "https://${AppConstants.S3_BUCKET_NAME}.s3.${AppConstants.S3_REGION}.amazonaws.com/$s3Key"
            
            Pair(imageUrl, null)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, e.message ?: "Upload failed")
        }
    }
    
    /**
     * Upload profile image
     */
    suspend fun uploadProfileImage(uri: Uri): Pair<String?, String?> {
        return uploadImage(uri, PROFILE_IMAGES_FOLDER, "profile_$deviceId")
    }
    
    /**
     * Upload journal image
     */
    suspend fun uploadJournalImage(uri: Uri, entryId: String): Pair<String?, String?> {
        return uploadImage(uri, JOURNAL_IMAGES_FOLDER, "journal_${entryId}_${System.currentTimeMillis()}")
    }
    
    /**
     * Upload note attachment image
     */
    suspend fun uploadNoteImage(uri: Uri, noteId: String): Pair<String?, String?> {
        return uploadImage(uri, NOTES_IMAGES_FOLDER, "note_${noteId}_${System.currentTimeMillis()}")
    }
    
    /**
     * Delete an image from S3
     */
    suspend fun deleteImage(imageUrl: String): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        try {
            // Extract S3 key from URL
            val urlPattern = "https://${AppConstants.S3_BUCKET_NAME}.s3.${AppConstants.S3_REGION}.amazonaws.com/"
            if (!imageUrl.startsWith(urlPattern)) {
                return@withContext Pair(false, "Invalid S3 URL")
            }
            
            val s3Key = imageUrl.removePrefix(urlPattern)
            s3Client.deleteObject(AppConstants.S3_BUCKET_NAME, s3Key)
            
            Pair(true, null)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(false, e.message ?: "Delete failed")
        }
    }
    
    /**
     * Upload a file (JSON, CSV, etc.) to S3
     */
    suspend fun uploadFile(
        content: String,
        folder: String,
        fileName: String,
        contentType: String = "application/json"
    ): Pair<String?, String?> = withContext(Dispatchers.IO) {
        try {
            val s3Key = "$folder/${deviceId}_$fileName"
            val bytes = content.toByteArray(Charsets.UTF_8)
            
            val metadata = ObjectMetadata().apply {
                this.contentType = contentType
                contentLength = bytes.size.toLong()
            }
            
            val putRequest = PutObjectRequest(
                AppConstants.S3_BUCKET_NAME,
                s3Key,
                ByteArrayInputStream(bytes),
                metadata
            )
            
            s3Client.putObject(putRequest)
            
            val fileUrl = "https://${AppConstants.S3_BUCKET_NAME}.s3.${AppConstants.S3_REGION}.amazonaws.com/$s3Key"
            
            Pair(fileUrl, null)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, e.message ?: "Upload failed")
        }
    }
    
    /**
     * Resize bitmap while maintaining aspect ratio
     */
    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }
        
        val ratio = width.toFloat() / height.toFloat()
        
        val newWidth: Int
        val newHeight: Int
        
        if (width > height) {
            newWidth = maxSize
            newHeight = (maxSize / ratio).toInt()
        } else {
            newHeight = maxSize
            newWidth = (maxSize * ratio).toInt()
        }
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    /**
     * Check if URL is an S3 image URL from this app
     */
    fun isValidS3Url(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        return url.startsWith("https://${AppConstants.S3_BUCKET_NAME}.s3.")
    }
}
