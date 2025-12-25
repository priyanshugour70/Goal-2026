package com.lssgoo.planner.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.lssgoo.planner.features.goals.models.Goal
import com.lssgoo.planner.features.goals.models.GoalCategory

/**
 * Extension function for GoalCategory to map category to Material Icon
 */
fun GoalCategory.getIcon(): ImageVector {
    return when (this) {
        GoalCategory.HEALTH -> Icons.Default.FitnessCenter
        GoalCategory.CAREER -> Icons.Default.Work
        GoalCategory.LEARNING -> Icons.AutoMirrored.Filled.MenuBook
        GoalCategory.COMMUNICATION -> Icons.Default.RecordVoiceOver
        GoalCategory.LIFESTYLE -> Icons.Default.WbSunny
        GoalCategory.DISCIPLINE -> Icons.Default.Schedule
        GoalCategory.FINANCE -> Icons.Default.Savings
        GoalCategory.STARTUP -> Icons.Default.RocketLaunch
        GoalCategory.MINDFULNESS -> Icons.Default.SelfImprovement
        GoalCategory.TRAVEL -> Icons.Default.Flight
        GoalCategory.RELATIONSHIPS -> Icons.Default.Favorite
        GoalCategory.SPIRITUALITY -> Icons.Default.SelfImprovement
        GoalCategory.SOCIAL -> Icons.Default.Public
        GoalCategory.CREATIVITY -> Icons.Default.Brush
        GoalCategory.ENVIRONMENT -> Icons.Default.Eco
    }
}

/**
 * Centralized Icons for the app to avoid repeated logic and fix unresolved references
 */
object AppIcons {
    val Health = Icons.Default.FitnessCenter
    val Career = Icons.Default.Work
    val Learning = Icons.AutoMirrored.Filled.MenuBook
    val Communication = Icons.Default.RecordVoiceOver
    val Lifestyle = Icons.Default.WbSunny
    val Discipline = Icons.Default.Schedule
    val Finance = Icons.Default.Savings
    val Startup = Icons.Default.RocketLaunch
    val Mindfulness = Icons.Default.SelfImprovement
    
    val Goal = Icons.Default.Flag
    val Target = Icons.Default.Flag
    val Task = Icons.Default.TaskAlt
    val Tasks = Icons.Default.TaskAlt
    val Note = Icons.AutoMirrored.Filled.StickyNote2
    val Notes = Icons.AutoMirrored.Filled.StickyNote2
    val NoteAdd = Icons.AutoMirrored.Filled.NoteAdd
    val Reminder = Icons.Default.Alarm
    val Reminders = Icons.Default.Alarm
    val Event = Icons.Default.Event
    val Events = Icons.Default.Event
    val Milestone = Icons.Default.FlagCircle
    val Flag = Icons.Default.Flag
    val Journal = Icons.Default.HistoryEdu
    val MenuBook = Icons.AutoMirrored.Filled.MenuBook
    val Habit = Icons.Default.CheckCircle
    
    val Add = Icons.Default.Add
    val Edit = Icons.Default.Edit
    val Delete = Icons.Default.Delete
    val DeleteOutlined = Icons.Outlined.Delete
    val Search = Icons.Default.Search
    val Settings = Icons.Default.Settings
    val SettingsOutlined = Icons.Outlined.Settings
    val Profile = Icons.Default.Person
    val Notifications = Icons.Default.Notifications
    val NotificationsNone = Icons.Outlined.Notifications
    val Calendar = Icons.Default.CalendarToday
    val Dashboard = Icons.Default.Dashboard
    
    val ArrowBack = Icons.AutoMirrored.Filled.ArrowBack
    val ChevronLeft = Icons.Default.ChevronLeft
    val ChevronRight = Icons.Default.ChevronRight
    val Check = Icons.Default.Check
    val CheckCircle = Icons.Default.CheckCircle
    val RadioButtonUnchecked = Icons.Outlined.RadioButtonUnchecked
    val PriorityHigh = Icons.Default.PriorityHigh
    val Schedule = Icons.Default.Schedule
    val Description = Icons.Default.Description
    val AlarmAdd = Icons.Default.AlarmAdd
    val Lightbulb = Icons.Default.Lightbulb
    val TrendingUp = Icons.AutoMirrored.Filled.TrendingUp
    val PushPin = Icons.Default.PushPin
    val Trophy = Icons.Default.EmojiEvents
    val Notification = Icons.Default.Notifications
    val Celebration = Icons.Default.Celebration
    val SettingsBackupRestore = Icons.Default.SettingsBackupRestore
    val Info = Icons.Default.Info
    val HelpOutline = Icons.AutoMirrored.Filled.HelpOutline
    val Assessment = Icons.Default.Assessment
    val AccountBalanceWallet = Icons.Default.AccountBalanceWallet
    val Payments = Icons.Default.Payments
    val DonutLarge = Icons.Default.DonutLarge
    val WaterDrop = Icons.Default.WaterDrop
    val Restaurant = Icons.Default.Restaurant
    val DirectionsCar = Icons.Default.DirectionsCar
    val ShoppingBag = Icons.Default.ShoppingBag
    val Movie = Icons.Default.Movie
    val MedicalServices = Icons.Default.MedicalServices
    val School = Icons.Default.School
    val ShowChart = Icons.Default.ShowChart
    val Receipt = Icons.Default.Receipt
    val Home = Icons.Default.Home
    val CardGiftcard = Icons.Default.CardGiftcard
    val Category = Icons.Default.Category
    val Handshake = Icons.Default.Handshake
    val Rocket = Icons.Default.Rocket
    val Person = Icons.Default.Person
    val Email = Icons.Default.Email
    val Palette = Icons.Default.Palette
    val DarkMode = Icons.Default.DarkMode
    val LightMode = Icons.Default.LightMode
    val Brush = Icons.Default.Brush
    val Favorite = Icons.Default.Favorite
    
    // Additional Category Icons
    val Fastfood = Icons.Default.Fastfood
    val LocalLibrary = Icons.Default.LocalLibrary
    val Laptop = Icons.Default.Laptop
    val Terminal = Icons.Default.Terminal
    val Language = Icons.Default.Language
    val TravelExplore = Icons.Default.TravelExplore
    val DirectionsBike = Icons.Default.DirectionsBike
    val Pool = Icons.Default.Pool
    val Hiking = Icons.Default.Hiking
    val SportsBasketball = Icons.Default.SportsBasketball
    val MusicNote = Icons.Default.MusicNote
    val BrushLogo = Icons.Default.Brush
    val Psychology = Icons.Default.Psychology
    val AutoAwesome = Icons.Default.AutoAwesome
    val MonitorWeight = Icons.Default.MonitorWeight
    val Bedtime = Icons.Default.Bedtime
    val WineBar = Icons.Default.WineBar
    val Coffee = Icons.Default.Coffee
    val Meditation = Icons.Default.SelfImprovement
    val Savings = Icons.Default.Savings
    
    /**
     * Grouped icons for selector
     */
    val SelectionGroups = mapOf(
        "Health & Fitness" to listOf(
            "FitnessCenter", "MonitorWeight", "DirectionsBike", "Pool", "Hiking", 
            "SportsBasketball", "SelfImprovement", "Bedtime", "WaterDrop", "MedicalServices"
        ),
        "Productivity & Learning" to listOf(
            "Laptop", "Terminal", "MenuBook", "LocalLibrary", "School", "Language", 
            "Schedule", "HistoryEdu", "TrendingUp", "Assessment"
        ),
        "Lifestyle & Leisure" to listOf(
            "Restaurant", "Fastfood", "Coffee", "WineBar", "Movie", "MusicNote", 
            "Palette", "Brush", "TravelExplore", "DirectionsCar", "Home"
        ),
        "Finance & Others" to listOf(
            "Savings", "Payments", "AccountBalanceWallet", "Receipt", "ShoppingBag", 
            "CardGiftcard", "RocketLaunch", "AutoAwesome", "Favorite", "Psychology"
        )
    )

    /**
     * Maps a string name to an ImageVector
     */
    fun fromName(name: String): ImageVector {
        return when (name) {
            "WaterDrop" -> WaterDrop
            "AutoMirrored.Filled.MenuBook", "MenuBook" -> Learning
            "FitnessCenter" -> Health
            "SelfImprovement" -> Mindfulness
            "HistoryEdu" -> Journal
            "Restaurant" -> Restaurant
            "DirectionsCar" -> DirectionsCar
            "ShoppingBag" -> ShoppingBag
            "Movie" -> Movie
            "MedicalServices" -> MedicalServices
            "School" -> School
            "Payments" -> Payments
            "ShowChart" -> ShowChart
            "Receipt" -> Receipt
            "Home" -> Home
            "CardGiftcard" -> CardGiftcard
            "Category" -> Category
            "Handshake" -> Handshake
            "RocketLaunch" -> Startup
            "Work" -> Career
            "WbSunny" -> Lifestyle
            "Schedule" -> Discipline
            "Savings" -> Finance
            "RecordVoiceOver" -> Communication
            "Fastfood" -> Fastfood
            "LocalLibrary" -> LocalLibrary
            "Laptop" -> Laptop
            "Terminal" -> Terminal
            "Language" -> Language
            "TravelExplore" -> TravelExplore
            "DirectionsBike" -> DirectionsBike
            "Pool" -> Pool
            "Hiking" -> Hiking
            "SportsBasketball" -> SportsBasketball
            "MusicNote" -> MusicNote
            "Brush" -> Brush
            "Psychology" -> Psychology
            "AutoAwesome" -> AutoAwesome
            "MonitorWeight" -> MonitorWeight
            "Bedtime" -> Bedtime
            "WineBar" -> WineBar
            "LocalCoffee", "Coffee" -> Coffee
            "Favorite" -> Favorite
            else -> Dashboard
        }
    }
}
