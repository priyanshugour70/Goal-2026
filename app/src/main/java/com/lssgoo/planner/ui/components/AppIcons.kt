package com.lssgoo.planner.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.lssgoo.planner.data.model.GoalCategory
import com.lssgoo.planner.data.model.ItemPriority

/**
 * Icon utilities to replace emojis with Material Icons throughout the app
 */
object AppIcons {
    
    // Navigation Icons
    val Dashboard = Icons.Filled.Dashboard
    val DashboardOutlined = Icons.Outlined.Dashboard
    val Goals = Icons.Filled.EmojiEvents
    val GoalsOutlined = Icons.Outlined.EmojiEvents
    val Calendar = Icons.Filled.CalendarMonth
    val CalendarOutlined = Icons.Outlined.CalendarMonth
    val Notes = Icons.Filled.StickyNote2
    val NotesOutlined = Icons.Outlined.StickyNote2
    val Tasks = Icons.Filled.TaskAlt
    val TasksOutlined = Icons.Outlined.TaskAlt
    val Settings = Icons.Filled.Settings
    val SettingsOutlined = Icons.Outlined.Settings
    val Reminders = Icons.Filled.Notifications
    val RemindersOutlined = Icons.Outlined.Notifications
    val Alarm = Icons.Filled.Alarm
    val AlarmAdd = Icons.Filled.AlarmAdd
    val ArrowBack = Icons.Filled.ArrowBack
    val PushPin = Icons.Filled.PushPin
    val Notifications = Icons.Filled.Notifications
    val NotificationsNone = Icons.Outlined.Notifications
    
    // Goal Category Icons
    val Health = Icons.Filled.FitnessCenter
    val Career = Icons.Filled.Work
    val Learning = Icons.Filled.MenuBook
    val Communication = Icons.Filled.RecordVoiceOver
    val Lifestyle = Icons.Filled.WbSunny
    val Discipline = Icons.Filled.Schedule
    val Finance = Icons.Filled.Savings
    val Startup = Icons.Filled.RocketLaunch
    val Mindfulness = Icons.Filled.SelfImprovement
    
    // Action Icons
    val Add = Icons.Filled.Add
    val Edit = Icons.Filled.Edit
    val Delete = Icons.Filled.Delete
    val DeleteOutlined = Icons.Outlined.Delete
    val Check = Icons.Filled.Check
    val Close = Icons.Filled.Close
    val Back = Icons.Filled.ArrowBack
    val Forward = Icons.Filled.ArrowForward
    val MoreVert = Icons.Filled.MoreVert
    val Search = Icons.Filled.Search
    val Filter = Icons.Filled.FilterList
    val Sort = Icons.Filled.Sort
    val Share = Icons.Filled.Share
    val Download = Icons.Filled.CloudDownload
    val Upload = Icons.Filled.CloudUpload
    val SettingsBackupRestore = Icons.Filled.SettingsBackupRestore
    val Assessment = Icons.Filled.Assessment
    val PriorityHigh = Icons.Filled.PriorityHigh
    
    // Status Icons
    val CheckCircle = Icons.Filled.CheckCircle
    val CheckCircleOutlined = Icons.Outlined.CheckCircle
    val RadioButtonUnchecked = Icons.Outlined.RadioButtonUnchecked
    val Warning = Icons.Filled.Warning
    val Error = Icons.Filled.Error
    val Info = Icons.Filled.Info
    val Help = Icons.Filled.HelpOutline
    val Pending = Icons.Filled.Pending
    
    // Feature Icons
    val Pin = Icons.Filled.PushPin
    val PinOutlined = Icons.Outlined.PushPin
    val Notification = Icons.Filled.Notifications
    val NotificationOutlined = Icons.Outlined.Notifications
    val Reminder = Icons.Filled.Alarm
    val ReminderOutlined = Icons.Outlined.Alarm
    val Event = Icons.Filled.Event
    val EventOutlined = Icons.Outlined.Event
    val Flag = Icons.Filled.Flag
    val FlagOutlined = Icons.Outlined.Flag
    val Star = Icons.Filled.Star
    val StarOutlined = Icons.Outlined.Star
    val Favorite = Icons.Filled.Favorite
    val FavoriteOutlined = Icons.Outlined.FavoriteBorder
    val Bookmark = Icons.Filled.Bookmark
    val BookmarkOutlined = Icons.Outlined.BookmarkBorder
    
    // Progress Icons
    val TrendingUp = Icons.Filled.TrendingUp
    val TrendingDown = Icons.Filled.TrendingDown
    val Timeline = Icons.Filled.Timeline
    val Analytics = Icons.Filled.Analytics
    val Insights = Icons.Filled.Insights
    val BarChart = Icons.Filled.BarChart
    val PieChart = Icons.Filled.PieChart
    
    // Time Icons
    val Today = Icons.Filled.Today
    val DateRange = Icons.Filled.DateRange
    val Timer = Icons.Filled.Timer
    val History = Icons.Filled.History
    val Schedule = Icons.Filled.Schedule
    val AccessTime = Icons.Filled.AccessTime
    
    // Action Icons
    val Streak = Icons.Filled.LocalFireDepartment
    val Milestone = Icons.Filled.Flag
    val Target = Icons.Filled.TrackChanges
    val Trophy = Icons.Filled.EmojiEvents
    val Medal = Icons.Filled.MilitaryTech
    val Celebration = Icons.Filled.Celebration
    
    // Motivation Icons
    val Lightbulb = Icons.Filled.Lightbulb
    val LightbulbOutlined = Icons.Outlined.Lightbulb
    val Bolt = Icons.Filled.Bolt
    val Psychology = Icons.Filled.Psychology
    val EmojiObjects = Icons.Filled.EmojiObjects
    
    // Person Icons
    val Person = Icons.Filled.Person
    val PersonOutlined = Icons.Outlined.Person
    val Group = Icons.Filled.Group
    val Face = Icons.Filled.Face
    val AccountCircle = Icons.Filled.AccountCircle
    
    // Content Icons
    val NoteAdd = Icons.Filled.NoteAdd
    val Description = Icons.Filled.Description
    val Article = Icons.Filled.Article
    val ListAlt = Icons.Filled.ListAlt
    val Checklist = Icons.Filled.Checklist
    
    // UI Icons
    val ExpandMore = Icons.Filled.ExpandMore
    val ExpandLess = Icons.Filled.ExpandLess
    val ChevronLeft = Icons.Filled.ChevronLeft
    val ChevronRight = Icons.Filled.ChevronRight
    val Menu = Icons.Filled.Menu
    val Grid = Icons.Filled.GridView
    val List = Icons.Filled.ViewAgenda
    val Refresh = Icons.Filled.Refresh
    
    /**
     * Get icon for goal category
     */
    fun getGoalCategoryIcon(category: GoalCategory): ImageVector {
        return when (category) {
            GoalCategory.HEALTH -> Health
            GoalCategory.CAREER -> Career
            GoalCategory.LEARNING -> Learning
            GoalCategory.COMMUNICATION -> Communication
            GoalCategory.LIFESTYLE -> Lifestyle
            GoalCategory.DISCIPLINE -> Discipline
            GoalCategory.FINANCE -> Finance
            GoalCategory.STARTUP -> Startup
            GoalCategory.MINDFULNESS -> Mindfulness
        }
    }
    
    /**
     * Get icon for priority level
     */
    fun getPriorityIcon(priority: ItemPriority): ImageVector {
        return when (priority.level) {
            in 1..3 -> Icons.Filled.KeyboardDoubleArrowUp
            in 4..5 -> Icons.Filled.KeyboardArrowUp
            6 -> Icons.Filled.Remove
            in 7..8 -> Icons.Filled.KeyboardArrowDown
            else -> Icons.Filled.KeyboardDoubleArrowDown
        }
    }
    
    /**
     * Get section icon by name
     */
    fun getSectionIcon(section: String): ImageVector {
        return when (section.lowercase()) {
            "dashboard", "home" -> Dashboard
            "goals", "targets" -> Goals
            "calendar" -> Calendar
            "notes" -> Notes
            "tasks", "todo" -> Tasks
            "settings" -> Settings
            "reminders" -> Reminders
            "events" -> Event
            "pinned" -> Pin
            "upcoming" -> AccessTime
            "completed" -> CheckCircle
            "overdue" -> Warning
            "progress" -> TrendingUp
            "streak" -> Streak
            "motivation" -> Lightbulb
            else -> Info
        }
    }
}

/**
 * Extension function to get goal icon
 */
fun GoalCategory.getIcon(): ImageVector = AppIcons.getGoalCategoryIcon(this)

/**
 * Extension function to get priority icon
 */
fun ItemPriority.getIcon(): ImageVector = AppIcons.getPriorityIcon(this)
