package com.lixferox.app_tfg_2024.data.datasource

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lixferox.app_tfg_2024.data.model.Tables
import com.lixferox.app_tfg_2024.model.Activity
import com.lixferox.app_tfg_2024.model.Request
import com.lixferox.app_tfg_2024.model.Stats
import com.lixferox.app_tfg_2024.model.User
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

class FirestoreDataSource : ViewModel() {
    fun obtainAllRequest(
        db: FirebaseFirestore,
        isHelper: Boolean,
        onResult: (List<Request>) -> Unit
    ) {
        val collection = db.collection(Tables.requests)
        val query = if (isHelper) {
            collection.whereEqualTo("uidHelper", "")
        } else {
            collection.whereEqualTo("uidOlder", "")
        }
        query.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val requestList = snapshot.documents.map { document ->
                    Request(
                        id = document.getString("id") ?: "",
                        uidOlder = document.getString("uidOlder") ?: "",
                        uidHelper = document.getString("uidHelper") ?: "",
                        title = document.getString("title") ?: "",
                        description = document.getString("description") ?: "",
                        urgency = document.getString("urgency"),
                        olderUsername = document.getString("olderUsername") ?: "",
                        helperUsername = document.getString("helperUsername") ?: "",
                        olderAddress = document.getString("olderAddress") ?: "",
                        helperAddress = document.getString("helperAddress") ?: "",
                        olderPhone = document.getString("olderPhone") ?: "",
                        helperPhone = document.getString("helperPhone") ?: "",
                        acceptedByUid = document.getString("acceptedByUid") ?: "",
                        createdByUid = document.getString("createdByUid") ?: "",
                        dateCreated = document.getTimestamp("dateCreated") ?: Timestamp.now(),
                        status = document.getString("status") ?: "",
                    )
                }
                onResult(requestList)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun acceptRequest(index: String, db: FirebaseFirestore, auth: FirebaseAuth) {
        val uid = auth.currentUser?.uid
        db.collection(Tables.users).whereEqualTo("uid", uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result.documents.firstOrNull()
                    val username = document?.getString("username")
                    val isHelper = document?.getBoolean("helper") ?: false
                    val phone = if (isHelper) "helperPhone" else "olderPhone"
                    val address = if (isHelper) "helperAddress" else "olderAddress"

                    val updateFields = if (isHelper) {
                        mapOf(
                            "acceptedByUid" to uid,
                            "status" to "Aceptada",
                            "helperUsername" to username,
                            "uidHelper" to uid,
                            "helperPhone" to phone,
                            "helperAddress" to address
                        )
                    } else {
                        mapOf(
                            "acceptedByUid" to uid,
                            "status" to "Aceptada",
                            "olderUsername" to username,
                            "uidOlder" to uid,
                            "olderPhone" to phone,
                            "olderAddress" to address
                        )
                    }
                    db.collection(Tables.requests).whereEqualTo("id", index).get()
                        .addOnSuccessListener { request ->
                            if (!request.isEmpty) {
                                val requestFind = request.documents.first()
                                requestFind.reference.update(updateFields).addOnSuccessListener {
                                    val description = requestFind.getString("description")

                                    val currentActivity = Activity(
                                        uid = uid!!,
                                        time = Timestamp.now(),
                                        title = "Aceptaste una tarea",
                                        description = description!!
                                    )
                                    createActivity(db, currentActivity)
                                    updateStats(db, auth, "acceptTask")
                                }
                            }
                        }
                }
            }
    }

    fun getAcceptedRequest(
        db: FirebaseFirestore,
        auth: FirebaseAuth,
        onResult: (List<Request>) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        db.collection(Tables.requests).whereEqualTo("acceptedByUid", uid)
            .whereEqualTo("status", "Aceptada").addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val requestList = snapshot.documents.map { document ->
                        Request(
                            id = document.getString("id") ?: "",
                            uidOlder = document.getString("uidOlder") ?: "",
                            uidHelper = document.getString("uidHelper") ?: "",
                            title = document.getString("title") ?: "",
                            description = document.getString("description") ?: "",
                            urgency = document.getString("urgency"),
                            olderUsername = document.getString("olderUsername") ?: "",
                            helperUsername = document.getString("helperUsername") ?: "",
                            olderAddress = document.getString("olderAddress") ?: "",
                            helperAddress = document.getString("helperAddress") ?: "",
                            olderPhone = document.getString("olderPhone") ?: "",
                            helperPhone = document.getString("helperPhone") ?: "",
                            acceptedByUid = document.getString("acceptedByUid") ?: "",
                            createdByUid = document.getString("createdByUid") ?: "",
                            dateCreated = document.getTimestamp("dateCreated") ?: Timestamp.now(),
                            status = document.getString("status") ?: "",
                        )
                    }
                    onResult(requestList)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun actionAcceptedRequest(
        index: String,
        action: String,
        isHelper: Boolean,
        db: FirebaseFirestore,
        auth: FirebaseAuth
    ) {
        val uid = auth.currentUser?.uid
        db.collection(Tables.users).whereEqualTo("uid", uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updateFields = mutableMapOf<String, Any>().apply {
                        when (action) {
                            "cancel" -> {
                                this["acceptedByUid"] = ""
                                if (isHelper) {
                                    put("helperAddress", "")
                                    put("helperPhone", "")
                                    put("helperUsername", "")
                                    put("uidHelper", "")
                                } else {
                                    put("olderAddress", "")
                                    put("olderPhone", "")
                                    put("olderUsername", "")
                                    put("uidOlder", "")
                                }
                            }

                            "complete" -> {
                                this["status"] = "Completada"
                            }
                        }
                    }
                    db.collection(Tables.requests).whereEqualTo("id", index).get()
                        .addOnSuccessListener { request ->
                            if (!request.isEmpty) {
                                val requestFind = request.documents.first()
                                requestFind.reference.update(updateFields)
                                if (action == "cancel") {
                                    val currentActivity = Activity(
                                        uid = uid!!,
                                        time = Timestamp.now(),
                                        title = "Cancelaste una tarea",
                                        description = requestFind.getString("description") ?: ""
                                    )
                                    createActivity(db, currentActivity)
                                    updateStats(db, auth, "cancelTask")
                                } else {
                                    val currentActivity = Activity(
                                        uid = uid!!,
                                        time = Timestamp.now(),
                                        title = "Completaste una tarea",
                                        description = requestFind.getString("description") ?: ""
                                    )
                                    createActivity(db, currentActivity)
                                    updateStats(db, auth, "completeTask")
                                }
                            }
                        }
                }
            }
    }

    fun obtainActivity(
        auth: FirebaseAuth,
        db: FirebaseFirestore,
        onResult: (List<Activity>) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        db.collection(Tables.activity).whereEqualTo("uid", uid)
            .orderBy("time", Query.Direction.DESCENDING).limit(3)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val activityList = snapshot.documents.map { document ->
                        Activity(
                            uid = document.getString("uid") ?: "",
                            time = document.getTimestamp("time") ?: Timestamp.now(),
                            title = document.getString("title") ?: "",
                            description = document.getString("description") ?: ""
                        )
                    }
                    onResult(activityList)
                }
            }
    }

    fun limitRequest(auth: FirebaseAuth, db: FirebaseFirestore, onResult: (Int) -> Unit) {
        val uid = auth.currentUser?.uid
        db.collection(Tables.requests).whereEqualTo("acceptedByUid", uid).get()
            .addOnSuccessListener { task ->
                onResult(task.size())
            }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun obtainUserStats(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    onResult: (Stats) -> Unit
) {
    setGraphicValues(db)
    val uid = auth.currentUser?.uid
    db.collection(Tables.stats).whereEqualTo("uid", uid).get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val document = task.result.documents.firstOrNull()
            if (document != null) {
                val weekCompletedTasks =
                    document.get("weekCompletedTasks") as? List<Double> ?: emptyList()
                val statsUser = Stats(
                    uid = document.getString("uid") ?: "",
                    level = document.getLong("level")?.toInt() ?: 0,
                    points = document.getLong("points")?.toInt() ?: 0,
                    totalCompletedTasks = document.getLong("totalCompletedTasks")?.toInt() ?: 0,
                    tasksInProgress = document.getLong("tasksInProgress")?.toInt() ?: 0,
                    puntuation = document.getDouble("puntuation") ?: 0.0,
                    ratingPoints = document.getLong("ratingPoints") ?: 0L,
                    joinedIn = document.getTimestamp("joinedIn") ?: Timestamp.now(),
                    weekCompletedTasks = weekCompletedTasks,
                    resetWeekValues = document.getBoolean("resetWeekValues") ?: false
                )
                onResult(statsUser)
            }
        }
    }
}

fun obtainUserInfo(auth: FirebaseAuth, db: FirebaseFirestore, onResult: (User) -> Unit) {
    val uid = auth.currentUser?.uid
    db.collection(Tables.users).whereEqualTo("uid", uid).get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val document = task.result.documents.firstOrNull()
            if (document != null) {
                val user = User(
                    uid = document.getString("uid") ?: "",
                    email = document.getString("email") ?: "",
                    username = document.getString("username") ?: "",
                    birth = document.getTimestamp("birth") ?: Timestamp.now(),
                    isHelper = document.getBoolean("isHelper") ?: false,
                    phone = document.getString("phone") ?: "",
                    address = document.getString("address") ?: ""
                )
                onResult(user)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun createRequest(
    title: String,
    description: String,
    urgency: String,
    isHelper: Boolean,
    uid: String,
    db: FirebaseFirestore,
    auth: FirebaseAuth,
    onSuccess: () -> Unit
) {
    db.collection(Tables.users).whereEqualTo("uid", uid).get().addOnCompleteListener { task ->
        val document = task.result.documents.firstOrNull()
        if (document != null) {
            val username = document.getString("username")
            val address = document.getString("address")
            val phone = document.getString("phone")
            val docRef = db.collection(Tables.requests).document()
            val currentRequest = Request(
                id = docRef.id,
                uidOlder = if (!isHelper) uid else "",
                uidHelper = if (isHelper) uid else "",
                title = title,
                description = description,
                urgency = if (!isHelper) urgency else "",
                olderUsername = if (!isHelper) username else "",
                helperUsername = if (isHelper) username else "",
                olderAddress = if (!isHelper) address else "",
                helperAddress = if (isHelper) address else "",
                olderPhone = if (!isHelper) phone ?: "" else "",
                helperPhone = if (isHelper) document.getString("phone") ?: "" else "",
                acceptedByUid = "",
                createdByUid = uid,
                dateCreated = Timestamp.now(),
                status = "Creada"
            )
            db.collection(Tables.requests).add(currentRequest).addOnCompleteListener { activity ->
                if (activity.isSuccessful) {
                    onSuccess()
                    val currentActivity = Activity(
                        uid = uid,
                        time = Timestamp.now(),
                        title = "Creaste una tarea",
                        description = description
                    )
                    createActivity(db, currentActivity)
                    updateStats(db, auth, "createTask")
                }
            }
        }
    }
}

fun updateInfo(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    email: String,
    username: String,
    phone: String,
    birth: String,
    address: String
) {
    val uid = auth.currentUser?.uid
    db.collection(Tables.users).whereEqualTo("uid", uid).get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val document = task.result.documents.firstOrNull()
            if (document != null) {
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val convertBirth = format.parse(birth)
                document.reference.update(
                    "email", email,
                    "username", username,
                    "phone", phone,
                    "birth", convertBirth,
                    "address", address
                ).addOnCompleteListener { update ->
                    if (update.isSuccessful) {
                        Log.i(
                            "UpdateInfo",
                            "Se han actualizado los datos del usuario correctamente"
                        )
                    }
                }
            }
        }
    }
}

fun deleteAccount(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    data class Search(
        val name: String,
        val field: String
    )

    val listTables = listOf(
        Search(Tables.users, "uid"),
        Search(Tables.stats, "uid"),
        Search(Tables.requests, "createdByUid"),
        Search(Tables.activity, "uid")
    )

    val uid = auth.currentUser!!.uid
    val deleteTasks = mutableListOf<Task<Void>>()

    listTables.map { table ->
        db.collection(table.name).whereEqualTo(table.field, uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dataObtained = task.result.documents
                    println("LOG $dataObtained")
                    dataObtained.forEach { document ->
                        val delete = db.collection(table.name).document(document.id).delete()
                        deleteTasks.add(delete)
                    }
                }
            }
    }
    Tasks.whenAllComplete(deleteTasks).addOnCompleteListener { allDelete ->
        if (allDelete.isSuccessful) {
            auth.currentUser?.delete()?.addOnCompleteListener { delete ->
                if (delete.isSuccessful) {
                    auth.signOut()
                    onSuccess()
                } else {
                    onError(
                        delete.exception?.message
                            ?: "Ha ocurrido un error al eliminar la cuenta en una tabla"
                    )
                }
            }
        }
    }
}

private fun createActivity(db: FirebaseFirestore, currentActivity: Activity) {
    db.collection(Tables.activity).add(currentActivity).addOnCompleteListener { activity ->
        if (activity.isSuccessful) {
            Log.i("activityAdded", "Se ha registrado la actividad")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun updateStats(db: FirebaseFirestore, auth: FirebaseAuth, action: String) {
    val uid = auth.currentUser?.uid
    val date = LocalDate.now()
    val dateOfWeek = when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> 0
        DayOfWeek.TUESDAY -> 1
        DayOfWeek.WEDNESDAY -> 2
        DayOfWeek.THURSDAY -> 3
        DayOfWeek.FRIDAY -> 4
        DayOfWeek.SATURDAY -> 5
        DayOfWeek.SUNDAY -> 6
    }

    val updateFields = when {
        action == "createTask" -> mapOf("points" to FieldValue.increment(500))
        action == "acceptTask" -> mapOf(
            "tasksInProgress" to 1,
            "points" to FieldValue.increment(100)
        )

        action == "completeTask" -> mapOf(
            "totalCompletedTasks" to FieldValue.increment(1),
            "points" to FieldValue.increment(200),
            "weekCompletedTasks" to FieldValue.increment(1)
        ).plus(
            mapOf("weekCompletedTasks" to FieldValue.arrayUnion(dateOfWeek))
        )

        action == "cancelTask" -> mapOf(
            "tasksInProgress" to FieldValue.increment(-1),
            "points" to FieldValue.increment(-120)
        )

        else -> mapOf("status" to "No se ha escogido ninguna opcion")
    }

    db.collection(Tables.stats).whereEqualTo("uid", uid).get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val document = task.result.documents.firstOrNull()
            if (document != null) {
                document.reference.update(updateFields).addOnCompleteListener { update ->
                    if (update.isSuccessful) {
                        Log.i("updateStats", "Se han actualizado las estadisticas")
                        document.reference.get().addOnCompleteListener { dataUser ->
                            if (dataUser.isSuccessful) {
                                val points = dataUser.result.getLong("points") ?: 0L
                                if (points >= 500) {
                                    val fieldsLevel = mapOf(
                                        "level" to FieldValue.increment(points / 500),
                                        "points" to points % 500
                                    )
                                    document.reference.update(fieldsLevel)
                                        .addOnCompleteListener { updateLevel ->
                                            if (updateLevel.isSuccessful) {
                                                Log.i("updateLevel", "Se han actualizado el nivel")
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun updatePuntuationStars(db: FirebaseFirestore, uid: String, points: Int) {
    db.collection(Tables.stats).whereEqualTo("uid", uid).get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val document = task.result.documents.firstOrNull()
            if (document != null) {

                val currentRating = document.getLong("ratingPoints") ?: 0L
                val currentPoints = document.getDouble("puntuation") ?: 0.0

                val newRating = currentRating + 1
                val newPuntuation = currentPoints + (points - currentRating) / newRating

                val updateFields = mapOf(
                    ("ratingPoints" to newRating),
                    ("puntuation" to newPuntuation)
                )

                document.reference.update(updateFields).addOnCompleteListener { update ->
                    if (update.isSuccessful) {
                        Log.i("updatePoints", "Se han actualizado la puntiación")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun setGraphicValues(db: FirebaseFirestore) {
    val date = LocalDate.now()
    val isMonday = date.dayOfWeek == DayOfWeek.MONDAY

    db.collection(Tables.stats).get().addOnSuccessListener { task ->
        val document = task.documents
        document.chunked(500).forEach { update ->
            val data = db.batch()
            update.forEach { document ->
                val resetFlag = document.getBoolean("resetWeekValues") ?: false
                when {
                    isMonday && !resetFlag -> {
                        data.update(
                            document.reference, mapOf(
                                "weekCompletedTasks" to List(8) { 0 },
                                "resetWeekValues" to true
                            )
                        )
                    }

                    !isMonday && resetFlag -> {
                        data.update(document.reference, "resetWeekValues", false)
                    }
                }

            }
            data.commit()
        }
    }
}