package com.lssgoo.planner.features.settings.data

import kotlinx.datetime.LocalDate

data class VersionRelease(
    val version: String,
    val date: String,
    val title: String,
    val description: String,
    val changes: List<String>,
    val type: ReleaseType = ReleaseType.FEATURE
)

enum class ReleaseType {
    FEATURE, BUGFIX, MAJOR
}

data class TeamMember(
    val name: String,
    val role: String,
    val bio: String,
    val image: String? = null // Using logic name or resource path
)

object AppInfoData {
    val versionHistory = listOf(
        VersionRelease(
            version = "1.2.0",
            date = "Dec 24, 2025",
            title = "The Analytics Update",
            description = "Introducing comprehensive data visualization tools to help you track your progress like never before.",
            changes = listOf(
                "New Donut, Line, and Bar charts for better insights",
                "Finance Dashboard overhaul with spending breakdown",
                "Habit consistency tracking improvements",
                "Performance optimizations for smooth stats loading"
            ),
            type = ReleaseType.FEATURE
        ),
        VersionRelease(
            version = "1.1.5",
            date = "Dec 20, 2025",
            title = "Cloud Sync & Security",
            description = "Ensuring your data is safe and accessible across devices.",
            changes = listOf(
                "AWS S3 Cloud Backup integration",
                "App Lock with PIN protection",
                "Secure data encryption",
                "Fixed login persistence issues"
            ),
            type = ReleaseType.FEATURE
        ),
        VersionRelease(
            version = "1.1.0",
            date = "Dec 10, 2025",
            title = "Habits & Journal",
            description = "Building the core pillars of daily productivity.",
            changes = listOf(
                "Habit Tracker with streak calculation",
                "Daily Journal with mood tracking",
                "Notification system for reminders",
                "UI Polish: Midnight Purple theme update"
            ),
            type = ReleaseType.FEATURE
        ),
        VersionRelease(
            version = "1.0.2",
            date = "Dec 05, 2025",
            title = "Critical Bug Fixes",
            description = "Squashing bugs for a smoother experience.",
            changes = listOf(
                "Fixed crash on task deletion",
                "Resolved date formatting issues on some devices",
                "Improved startup time"
            ),
            type = ReleaseType.BUGFIX
        ),
        VersionRelease(
            version = "1.0.0",
            date = "Nov 25, 2025",
            title = "Initial Launch",
            description = "Welcome to Planner - Your ultimate life companion.",
            changes = listOf(
                "Goal Setting & Tracking",
                "Task Management",
                "Smart Notes",
                "Local-first architecture"
            ),
            type = ReleaseType.MAJOR
        )
    )

    val teamMembers = listOf(
        TeamMember(
            name = "Priyanshu Gour",
            role = "Lead Developer & Architect",
            bio = "The visionary behind Planner. Priyanshu engineered the core Kotlin Multiplatform architecture and designed the fluid UI system. Passionate about creating tools that empower users."
        ),
        TeamMember(
            name = "Devanshu Gour",
            role = "Senior Engineer",
            bio = "A master of backend logic and data synchronization. Devanshu ensures your data is safe, secure, and always available when you need it."
        ),
        TeamMember(
            name = "LssGoo Team",
            role = "Design & QA",
            bio = "Dedicated to perfection. Our wider team focuses on user experience research, extensive testing, and pixel-perfect design implementation."
        )
    )
}
