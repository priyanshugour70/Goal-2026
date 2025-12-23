package com.lssgoo.goal2026.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import java.security.MessageDigest

/**
 * Utility class for device identification
 * Uses IMEI when available, falls back to Android ID
 */
object DeviceUtils {
    
    /**
     * Get device identifier (IMEI or Android ID)
     * Returns a unique identifier for the device
     */
    fun getDeviceId(context: Context): String {
        return try {
            // Try to get IMEI first (requires READ_PHONE_STATE permission)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ requires special handling
                getImeiOrAndroidId(context)
            } else {
                // Android 9 and below
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_PHONE_STATE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    getImei(context) ?: getAndroidId(context)
                } else {
                    getAndroidId(context)
                }
            }
        } catch (e: Exception) {
            // Fallback to Android ID if IMEI fails
            getAndroidId(context)
        }
    }
    
    /**
     * Get IMEI (for Android 9 and below)
     */
    @Suppress("DEPRECATION")
    private fun getImei(context: Context): String? {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as android.telephony.TelephonyManager
                telephonyManager.deviceId
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Get IMEI or Android ID (for Android 10+)
     */
    private fun getImeiOrAndroidId(context: Context): String {
        return try {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as android.telephony.TelephonyManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    telephonyManager.imei ?: getAndroidId(context)
                } else {
                    getAndroidId(context)
                }
            } else {
                getAndroidId(context)
            }
        } catch (e: Exception) {
            getAndroidId(context)
        }
    }
    
    /**
     * Get Android ID as fallback
     */
    private fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "unknown_device_${System.currentTimeMillis()}"
    }
    
    /**
     * Hash the device ID for privacy (optional)
     */
    fun getHashedDeviceId(context: Context): String {
        val deviceId = getDeviceId(context)
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val hashBytes = md.digest(deviceId.toByteArray())
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            deviceId
        }
    }
}

