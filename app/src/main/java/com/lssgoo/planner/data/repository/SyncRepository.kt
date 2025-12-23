package com.lssgoo.planner.data.repository

import android.content.Context
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.remote.S3Manager
import com.lssgoo.planner.util.DeviceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for handling cloud sync operations
 */
class SyncRepository(private val context: Context) {
    
    private val s3Manager = S3Manager(context)
    private val storageManager = LocalStorageManager(context)
    
    /**
     * Check if a backup exists on S3 and download it if present
     * @return true if data was found and imported, false otherwise
     */
    suspend fun checkAndDownloadBackup(): Boolean = withContext(Dispatchers.IO) {
        val (data, error) = s3Manager.downloadFromS3()
        if (data != null) {
            return@withContext storageManager.importAllData(data)
        }
        false
    }
    
    /**
     * Sync local data to cloud
     * @return Pair of success and optional error message
     */
    suspend fun syncToCloud(): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        val allData = storageManager.exportAllData()
        s3Manager.uploadToS3(allData)
    }
    
    /**
     * Get device information for metadata (optional use)
     */
    fun getDeviceInfo(): Map<String, String> {
        return DeviceUtils.getDeviceInfo(context)
    }
}
