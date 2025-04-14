package com.lixferox.app_tfg_2024.data.datasource

import android.util.Log
import androidx.lifecycle.ViewModel
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
import java.util.Locale

class FirestoreDataSource : ViewModel() {
    fun obtainAllRequest(
        db: FirebaseFirestore,
        isHelper: Boolean,
        onResult: (List<Request>) -> Unit
    ) {
        val collection = db.collection(Tables.requests)
        val query = if (isHelper) collection.whereEqualTo(
            "uidHelper",
            ""
        ) else collection.whereEqualTo("uidOlder", "")
        query.get().addOnCompleteListener { task ->
            val requestList = task.result.documents.map { document ->
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
                    dateCreated = document.getTimestamp("dateCreated") ?: Timestamp.now(),
                    status = document.getString("status") ?: ""
                )
            }
            onResult(requestList)
        }
    }

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
                                    updateStats(auth, db, "tasksInProgress", 1)
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
            .whereEqualTo("status", "Aceptada").get()
            .addOnCompleteListener { task ->
                val requestList = task.result.documents.map { document ->
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
                        dateCreated = document.getTimestamp("dateCreated") ?: Timestamp.now(),
                        status = document.getString("status") ?: ""
                    )
                }
                onResult(requestList)
            }
    }

    fun actionAcceptedRequest(
        index: String,
        action: String,
        db: FirebaseFirestore,
        auth: FirebaseAuth
    ) {
        val uid = auth.currentUser?.uid
        db.collection(Tables.users).whereEqualTo("uid", uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updateFields = if (action == "cancel") {
                        mapOf(
                            "acceptedByUid" to "",
                            "status" to "Creada",
                            "helperUsername" to "",
                            "uidHelper" to "",
                            "helperPhone" to "",
                            "helperAddress" to ""
                        )
                    } else {
                        mapOf(
                            "status" to "Completada",
                        )
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
                                } else {
                                    val currentActivity = Activity(
                                        uid = uid!!,
                                        time = Timestamp.now(),
                                        title = "Completaste una tarea",
                                        description = requestFind.getString("description") ?: ""
                                    )
                                    createActivity(db, currentActivity)
                                }
                                updateStats(auth, db, "tasksInProgress", -1)
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
            .orderBy("time", Query.Direction.DESCENDING).limit(3).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val activityList = task.result.documents.map { activity ->
                        Activity(
                            uid = activity.getString("uid") ?: "",
                            time = activity.getTimestamp("time") ?: Timestamp.now(),
                            title = activity.getString("title") ?: "",
                            description = activity.getString("description") ?: ""
                        )
                    }
                    onResult(activityList)
                }

            }
    }

}

fun obtainUserStats(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    onResult: (Stats) -> Unit
) {
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
                    puntuation = document.getLong("puntuation")?.toInt() ?: 0,
                    joinedIn = document.getTimestamp("joinedIn") ?: Timestamp.now(),
                    weekCompletedTasks = weekCompletedTasks
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

fun createRequest(
    title: String,
    description: String,
    urgency: String,
    isHelper: Boolean,
    uid: String,
    db: FirebaseFirestore,
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

fun updateStats(auth: FirebaseAuth, db: FirebaseFirestore, field: String, value: Int) {
    val uid = auth.currentUser!!.uid

    db.collection(Tables.stats).whereEqualTo("uid", uid).get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val document = task.result.documents.firstOrNull()
            if (document != null) {
                document.reference.update(
                    field, FieldValue.increment(value.toLong())
                ).addOnCompleteListener { update ->
                    if (update.isSuccessful) {
                        Log.i("updateStats", "Se han actualizado las estadisticas")
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
    val uid = auth.currentUser!!.uid
    db.collection(Tables.users).whereEqualTo("uid", uid).get()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userObtained = task.result.documents
                if (userObtained != null) {
                    userObtained.map { user ->
                        db.collection(Tables.users).document(user.id).delete()
                            .addOnCompleteListener { delete ->
                                if (delete.isSuccessful) {
                                    auth.currentUser?.delete()
                                        ?.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                auth.signOut()
                                                onSuccess()
                                            } else {
                                                onError(
                                                    task.exception?.message
                                                        ?: "Ha ocurrido un error al eliminar la cuenta"
                                                )
                                            }
                                        }

                                } else {
                                    onError(
                                        task.exception?.message
                                            ?: "Ha ocurrido un error al eliminar la cuenta"
                                    )
                                }
                            }
                    }
                }
            } else {
                onError(
                    task.exception?.message
                        ?: "Ha ocurrido un error al eliminar la cuenta"
                )
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