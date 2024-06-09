package com.example.io_project.ui.components

import android.graphics.Color.toArgb
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.io_project.dataclasses.Event
import com.example.io_project.dataclasses.GreetingData
import com.example.io_project.datamanagement.fetchEvents
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DaySummary(
    modifier: Modifier = Modifier,
    events: List<Event>
)
{
    LaunchedEffect(Unit) {
        if(GreetingData.events == null)
        {
            val userID = Firebase.auth.currentUser?.uid.toString()
            val formatter = DateTimeFormatter.ofPattern("EEE, MMM dd yyyy", Locale.ENGLISH)
            val date = LocalDate.now().format(formatter)
            GreetingData.events = fetchEvents(userID, date)?.toMutableList()
            for (i in 1..10)
            {
                if(GreetingData.events != null) break
                delay(500)
            }
        }
    }

    events.let {
        for (i in it.indices)
        {
            Row(
                modifier = modifier
                    .padding(4.dp, top = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(it[i].color.toColorInt())),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Column(modifier = modifier
                    .padding(start = 4.dp, top = 4.dp, bottom = 4.dp)
                    .fillMaxWidth(0.4F),
                )
                {
                    Text(
                        text = it[i].time,
                        modifier = Modifier.padding(start = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
                Column(modifier = modifier
                    .padding(top = 4.dp, bottom = 4.dp)
                    .fillMaxWidth()
                )
                {
                    Text(
                        text = it[i].name,
                        modifier = Modifier.padding(end = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}