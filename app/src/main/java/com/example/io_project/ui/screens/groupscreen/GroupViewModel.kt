package com.example.io_project.ui.screens.groupscreen

import androidx.compose.ui.graphics.vector.Group
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.io_project.dataclasses.Event
import com.example.io_project.dataclasses.Group
import com.example.io_project.datamanagement.fetchEvents
import com.example.io_project.datamanagement.getUserNameFromUID
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class GroupViewModel(
    groupJSON: String
) : ViewModel() {
    var group: Group = Group()
    var members = ArrayList<String>()

    var eventsListState = mutableListOf<Event>()
    val dateState = MutableStateFlow(LocalDate.now())

    init {
        group = Gson().fromJson(groupJSON, Group::class.java)
        refreshData()
    }

    fun getDateString() = dateState.value.format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy"))
    fun changeDate(newDate: String) {
        dateState.update {
            LocalDate.parse(
                newDate,
                DateTimeFormatter.ofPattern("EEE, MMM d yyyy")
            )
        }
        updateEvents()
    }



    fun currentUserIsAdmin(): Boolean {
        return Firebase.auth.currentUser?.uid == group.ownerID
    }

    fun updateEvents() {
        runBlocking {
            Firebase.auth.currentUser?.let {
                //TODO(Zmien funkcje na pobierajaca eventy z danej grupy)
                eventsListState = fetchEvents(it.uid, getDateString())?.toMutableList() ?: eventsListState
            }
        }
    }

    fun getPreviousDay() {
        dateState.update { date -> date.minusDays(1) }
        updateEvents()
    }

    fun getNextDay() {
        dateState.update { date -> date.plusDays(1) }
        updateEvents()
    }

    fun refreshData() {
        runBlocking {
            for (member in group.groupMembers) {
                members.add(getUserNameFromUID(member))
            }
            updateEvents()
        }
    }

}