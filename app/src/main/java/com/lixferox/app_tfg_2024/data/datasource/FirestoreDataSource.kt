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
import java.util.Locale

class FirestoreDataSource : ViewModel() {

    // FUNCION QUE OBTIENE TODAS LAS PETICIONES

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

    // FUNCION QUE PERMITE ACEPTAR PETICIONES

    @RequiresApi(Build.VERSION_CODES.O)
    fun acceptRequest(index: String, db: FirebaseFirestore, auth: FirebaseAuth) {
        val uid = auth.currentUser?.uid
        db.collection(Tables.users).whereEqualTo("uid", uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result.documents.firstOrNull()
                    val username = document?.getString("username")
                    val isHelper = document?.getBoolean("helper") ?: false
                    val phone = document?.getString("phone")
                    val address = document?.getString("address")

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

    // FUNCION QUE OBTIENE TODAS LAS PETICIONES ACPETADAS

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

    // FUNCION QUE CMABIA LOS VALORES DE LA PETICION AL COMPLETARLA O CANCELARLA

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

    // FUNCION QUE OBTIENE LA ACTIVIDAD DEL USUARIO

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

    // FUNCION QUE OBTIENE LAS PETICIONES DEL USUARIO

    fun obtainRequests(
        auth: FirebaseAuth,
        db: FirebaseFirestore,
        onResult: (List<Request>) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        db.collection(Tables.requests).whereEqualTo("createdByUid", uid)
            .addSnapshotListener { snapshot, _ ->
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

    // FUNCION QUE LIMITA LAS PETICIONES DEL USUARIO

    fun limitRequest(auth: FirebaseAuth, db: FirebaseFirestore, onResult: (Int) -> Unit) {
        val uid = auth.currentUser?.uid
        db.collection(Tables.requests).whereEqualTo("acceptedByUid", uid).get()
            .addOnSuccessListener { task ->
                onResult(task.size())
            }
    }

    // FUNCION QUE LIMITA LAS PETICIONES CREADAS

    fun limitCreated(auth: FirebaseAuth, db: FirebaseFirestore, onResult: (Int) -> Unit) {
        val uid = auth.currentUser?.uid
        db.collection(Tables.requests).whereEqualTo("createdByUid", uid).get()
            .addOnSuccessListener { task ->
                onResult(task.size())
            }
    }

    // FUNCION QUE RECUPERA LA PETICION ACTUAL
    fun getCurrentRequest(
        db: FirebaseFirestore,
        id: String,
        onResult: (Request?) -> Unit
    ) {
        db.collection(Tables.requests).whereEqualTo("id", id).limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ErrorObtainRequest", "Error obtener la peticion")
                    onResult(null)
                    return@addSnapshotListener
                }
                val document = snapshot?.documents?.firstOrNull()
                if (document == null) {
                    Log.w("NoRequestObtained", "No se encontró ninguna petición con id=$id")
                    onResult(null)
                    return@addSnapshotListener
                }
                val request = Request(
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
                onResult(request)
            }
    }
}

// FUNCION QUE OBTIENE LAS ESTADISTICAS DEL USUARIO

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

// FUNCION QUE OBTIENE LA INFORMACION DEL USUARIO

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
                    address = document.getString("address") ?: "",
                    dni = document.getString("dni") ?: "",
                    image = document.getString("image") ?: ""
                )
                onResult(user)
            }
        }
    }
}

// FUNCION QUE PERMITE CREAR UNA PETICION

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

// FUNCION QUE ACTUALIZA LOS DATOS DEL USUARIO

fun updateInfo(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    email: String,
    username: String,
    phone: String,
    birth: String,
    address: String,
    dni: String,
    image: String
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
                    "address", address,
                    "dni", dni,
                    "image", image
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

// FUNCION QUE PERMITE BORRAR UNA CUENTA

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

// FUNCION QUE CREA LOS REGISTROS DEL USUARIO

private fun createActivity(db: FirebaseFirestore, currentActivity: Activity) {
    db.collection(Tables.activity).add(currentActivity).addOnCompleteListener { activity ->
        if (activity.isSuccessful) {
            Log.i("activityAdded", "Se ha registrado la actividad")
        }
    }
}

// FUNCION QUE ACTUALIZA LAS ESTADISTICAS DEL USUARIO

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
        action == "createTask" -> mapOf("points" to FieldValue.increment(50))
        action == "acceptTask" -> mapOf(
            "tasksInProgress" to FieldValue.increment(1),
            "points" to FieldValue.increment(100)
        )

        action == "completeTask" -> mapOf(
            "totalCompletedTasks" to FieldValue.increment(1),
            "points" to FieldValue.increment(200),
            "tasksInProgress" to FieldValue.increment(-1),
        )

        action == "cancelTask" -> mapOf(
            "tasksInProgress" to FieldValue.increment(-1),
            "points" to FieldValue.increment(-120)
        )

        else -> mapOf("status" to "No se ha escogido ninguna opcion")
    }
    if (action == "completeTask") {
        db.collection(Tables.stats)
            .whereEqualTo("uid", uid)
            .limit(1)
            .get()
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) return@addOnCompleteListener

                val document = task.result?.documents?.firstOrNull() ?: return@addOnCompleteListener
                val reference = document.reference

                val weekArray = (document.get("weekCompletedTasks") as? MutableList<Double>)
                    ?: MutableList(7) { 0.0 }
                while (weekArray.size < 7) weekArray.add(0.0)
                weekArray[dateOfWeek] = weekArray[dateOfWeek] + 1

                reference.update("weekCompletedTasks", weekArray)
                    .addOnSuccessListener {
                        Log.d("updateWeekTasks", "Se ha actualizado el dia de la semana")
                    }
            }
        db.collection(Tables.requests).whereEqualTo("acceptedByUid", uid)
            .whereEqualTo("status", "Completada").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dataObtained = task.result.documents

                    dataObtained.forEach { document ->
                        db.collection(Tables.requests).document(document.id).delete()
                            .addOnSuccessListener {
                                Log.i("deleteRequest", "Se ha borrado la peticion ${document.id}")
                            }
                    }
                }
            }
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
                                                Log.i(
                                                    "updateLevel",
                                                    "Se han actualizado el nivel"
                                                )
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

// FUNCION QUE ELIMINA UN PETICION

fun deleteRequest(db: FirebaseFirestore, uid: String, description: String, id: String) {
    db.collection(Tables.requests).whereEqualTo("id", id).get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val dataObtained = task.result.documents
            dataObtained.forEach { document ->
                db.collection(Tables.requests).document(document.id).delete()
                val currentActivity = Activity(
                    uid = uid,
                    time = Timestamp.now(),
                    title = "Eliminaste una tarea",
                    description = description
                )
                createActivity(db, currentActivity)
            }
        }
    }
}

// FUNCION QUE ACTUALIZA LA PUNTUACION DEL USUARIO

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

// FUNCION QUE CAMBIA LOS VALORES DE LA SEMANA DEL USUARIO A 0

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
