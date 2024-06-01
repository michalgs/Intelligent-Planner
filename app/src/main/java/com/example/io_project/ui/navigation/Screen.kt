package com.example.io_project.ui.navigation

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")
    object CalendarScreen: Screen("calendar_screen")
    object ProfileScreen: Screen("profile_screen")
    object AuthScreen: Screen("auth_screen")
    object ArchiveScreen: Screen("archive_screen")
    object GroupScreen: Screen("group_screen")
    object GoalsScreen: Screen("goals_screen")
    object SocialScreen: Screen("social_screen")
    object SplashScreen: Screen("splash_screen")
    object StatsScreen: Screen("stats_screen")
    object TasksScreen: Screen("tasks_screen")
    object AddActivityDialog: Screen("add_activity_dialog")
    object AddGroupDialog: Screen("add_group_dialog")
    object AddFriendDialog: Screen("add_friend_dialog")

}