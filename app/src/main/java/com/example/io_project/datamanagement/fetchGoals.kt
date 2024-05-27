package com.example.io_project.datamanagement

import android.util.Log
import com.example.io_project.dataclasses.Goal
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject




suspend fun fetchUncompletedGoals(userID: String): List<Goal>? {
    val returnList = ArrayList<Goal>()

    val documentSnapshot = getUserDocument(userID)
    try {
        if (documentSnapshot != null && documentSnapshot.exists()) {
            val uncompletedGoalsData =  documentSnapshot
                .get("goals.unfinished") as List<Map<String, Any>>

            if (uncompletedGoalsData is List<*>) {
                for (goal in uncompletedGoalsData) {
                    if (goal != null) {
                        Log.d(
                            "FetchGoals",
                            "Single fetched goal: ${goal.javaClass.simpleName} -> ${goal}"
                        )
                        returnList.add(
                            Goal(
                                name = goal["name"].toString(),
                                deadline = goal["deadline"].toString(),
                                done = goal["done"].toString() == "true"
                            )
                        )
                    }
                }
                @Suppress("UNCHECKED_CAST")
                return returnList
            }
        } else {
            println("Dokument użytkownika nie istnieje lub nie został pobrany")
        }
    } catch (e: Exception) {
        println("Błąd podczas pobierania nieukończonych celów: ${e.message}")
        e.printStackTrace()
    }
    return null
}


suspend fun fetchGoalsSnapshot(userID: String) {
    val goalsPath = Firebase.firestore.collection("users")
        .document(userID).collection("goals.unfinished")

    goalsPath.addSnapshotListener { value, e ->
        if (e != null) {
            Log.w("FetchGoalsSnapshot", "Listen failed.", e)
            return@addSnapshotListener
        }
        val goals = ArrayList<Goal>()
        for (doc in value!!) {
            val goal = Goal(
                name = doc.data["name"].toString(),
                deadline = doc.data["deadline"].toString(),
                done = doc.data["done"].toString() == "true"
            )
            goals.add(goal)
        }
        Log.d("FetchGoalsSnapshot", "Retrieved data: $goals")
    }


}




