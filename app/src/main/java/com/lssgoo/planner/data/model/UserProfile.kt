package com.lssgoo.planner.data.model

import java.util.UUID

/**
 * User profile data collected during onboarding
 */
data class UserProfile(
    val id: String = UUID.randomUUID().toString(),
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: Long? = null,
    val gender: Gender = Gender.PREFER_NOT_TO_SAY,
    val email: String = "",
    val phoneNumber: String = "",
    val profileImageUri: String = "",
    val occupation: String = "",
    val isOnboardingComplete: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
    
    val age: Int?
        get() = dateOfBirth?.let {
            val now = System.currentTimeMillis()
            val diff = now - it
            (diff / (365.25 * 24 * 60 * 60 * 1000)).toInt()
        }
    
    val greeting: String
        get() {
            val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
            val timeGreeting = when {
                hour < 12 -> "Good Morning"
                hour < 17 -> "Good Afternoon"
                else -> "Good Evening"
            }
            return if (firstName.isNotBlank()) {
                val genderSuffix = when (gender) {
                    Gender.MALE -> "Sir"
                    Gender.FEMALE -> "Ma'am"
                    else -> firstName
                }
                "$timeGreeting, $genderSuffix!"
            } else {
                "$timeGreeting!"
            }
        }
}

/**
 * Gender options
 */
enum class Gender(val displayName: String) {
    MALE("Male"),
    FEMALE("Female"),
    NON_BINARY("Non-Binary"),
    PREFER_NOT_TO_SAY("Prefer not to say")
}
