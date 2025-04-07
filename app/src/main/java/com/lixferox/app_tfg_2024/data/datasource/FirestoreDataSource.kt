package com.lixferox.app_tfg_2024.data.datasource

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.data.model.Tables
import com.lixferox.app_tfg_2024.domain.model.Request
import com.lixferox.app_tfg_2024.domain.model.User

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
                                val document = request.documents.first()
                                document.reference.update(updateFields)
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
        db.collection(Tables.requests).whereEqualTo("acceptedByUid", uid).get()
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
                    puntuation = document.getLong("puntuation")?.toInt() ?: 0,
                    affiliated = document.getTimestamp("affiliated") ?: Timestamp.now(),
                    level = document.getLong("level")?.toInt() ?: 0,
                    phone = document.getString("phone") ?: "",
                    address = document.getString("address") ?: ""
                )
                onResult(user)
            }
        }
    }
}

