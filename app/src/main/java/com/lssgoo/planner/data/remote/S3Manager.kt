package com.lssgoo.planner.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.S3Object
import com.lssgoo.planner.util.DeviceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.lssgoo.planner.BuildConfig
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * S3Manager handles all AWS S3 operations for cloud backup and sync
 * Uses device IMEI as the filename to ensure device-specific data
 */
class S3Manager(private val context: Context) {
    
    companion object {
        private val BUCKET_NAME = BuildConfig.S3_BUCKET_NAME
        private val ACCESS_KEY = BuildConfig.S3_ACCESS_KEY
        private val SECRET_KEY = BuildConfig.S3_SECRET_KEY
        private val REGION = BuildConfig.S3_REGION
    }
    
    private val s3Client: AmazonS3Client by lazy {
        val credentials = BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)
        AmazonS3Client(credentials).apply {
            setRegion(Region.getRegion(Regions.fromName(REGION)))
        }
    }
    
    /**
     * Get the S3 key (filename) based on device IMEI
     */
    private suspend fun getS3Key(): String = withContext(Dispatchers.IO) {
        val imei = DeviceUtils.getDeviceId(context)
        "planner_backup_$imei.json"
    }
    
    /**
     * Check if device has internet connection
     */
    fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    /**
     * Upload data to S3
     * @param data JSON string of app data
     * @return Pair of success boolean and error message (if any)
     */
    suspend fun uploadToS3(data: String): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        if (!isOnline()) {
            return@withContext Pair(false, "No internet connection")
        }
        
        try {
            val s3Key = getS3Key()
            val inputStream: InputStream = ByteArrayInputStream(data.toByteArray(Charsets.UTF_8))
            
            val putRequest = PutObjectRequest(BUCKET_NAME, s3Key, inputStream, null)
            s3Client.putObject(putRequest)
            
            Pair(true, null)
        } catch (e: Exception) {
            Pair(false, e.message ?: "Unknown error")
        }
    }
    
    /**
     * Download data from S3
     * @return Pair of data string (null if not found) and error message (if any)
     */
    suspend fun downloadFromS3(): Pair<String?, String?> = withContext(Dispatchers.IO) {
        if (!isOnline()) {
            return@withContext Pair(null, "No internet connection")
        }
        
        try {
            val s3Key = getS3Key()
            
            // Check if object exists
            if (!s3Client.doesObjectExist(BUCKET_NAME, s3Key)) {
                return@withContext Pair(null, null) // No backup found, but no error
            }
            
            val s3Object: S3Object = s3Client.getObject(BUCKET_NAME, s3Key)
            val inputStream = s3Object.objectContent
            val data = inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            
            Pair(data, null)
        } catch (e: Exception) {
            Pair(null, e.message ?: "Unknown error")
        }
    }
    
    /**
     * Check if backup exists in S3
     */
    suspend fun backupExists(): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        if (!isOnline()) {
            return@withContext Pair(false, "No internet connection")
        }
        
        try {
            val s3Key = getS3Key()
            val exists = s3Client.doesObjectExist(BUCKET_NAME, s3Key)
            Pair(exists, null)
        } catch (e: Exception) {
            Pair(false, e.message ?: "Unknown error")
        }
    }
    
    /**
     * Delete backup from S3
     */
    suspend fun deleteBackup(): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        if (!isOnline()) {
            return@withContext Pair(false, "No internet connection")
        }
        
        try {
            val s3Key = getS3Key()
            s3Client.deleteObject(BUCKET_NAME, s3Key)
            Pair(true, null)
        } catch (e: Exception) {
            Pair(false, e.message ?: "Unknown error")
        }
    }
}

