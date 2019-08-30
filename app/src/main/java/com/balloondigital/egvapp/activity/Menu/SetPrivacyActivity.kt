package com.balloondigital.egvapp.activity.Menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import kotlinx.android.synthetic.main.activity_set_privacy.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import androidx.core.view.isGone
import com.algolia.search.saas.Client
import com.algolia.search.saas.Index
import com.algolia.search.saas.Query
import com.balloondigital.egvapp.activity.Auth.CheckAuthActivity
import kotlin.system.exitProcess


class SetPrivacyActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mCollection: MyFirebase.COLLECTIONS
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mBatch: WriteBatch
    private lateinit var mIndex: Index
    private lateinit var mClient: Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_privacy)

        supportActionBar!!.title = "Configurar Privacidade"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mDatabase = MyFirebase.database()
        mAuth = MyFirebase.auth()
        mBatch = mDatabase.batch()
        mCollection = MyFirebase.COLLECTIONS
        mClient = Client("2IGM62FIAI", "042b50ac3860ac597be1fbefad09b9d4")
        mIndex = mClient.getIndex("users")

        setListeners()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> true
        }
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btExcludeAccount) {
            confirmDialog("Excluir conta", "Tem certeza que deseja excluir sua conta? Esta ação é irreversível.")
        }
    }

    private fun setListeners() {
        btExcludeAccount.setOnClickListener(this)
    }

    private fun confirmDialog(dialogTitle: String, dialogMessage: String) {

        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setPositiveButton("Sim") { dialog, which ->
                pbExcludeAccount.isGone = false
                mBatch.delete(mDatabase.collection(mCollection.USERS).document(mUser.id))
                deleteUserLikes()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteUserLikes() {

        mDatabase.collection(mCollection.POST_LIKES)
            .whereEqualTo("user_id", mUser.id)
            .get()
            .addOnSuccessListener { query ->
                for(document in query.documents) {
                    mBatch.delete(document.reference)
                }
                deleteUserComments()
            }
    }

    private fun deleteUserComments() {

        mDatabase.collection(mCollection.POST_COMMENTS)
            .whereEqualTo("user_id", mUser.id)
            .get()
            .addOnSuccessListener { query ->
                for(document in query.documents) {
                    mBatch.delete(document.reference)
                }
                deletePosts()
            }
    }

    private fun deletePosts() {

        mDatabase.collection(mCollection.POSTS)
            .whereEqualTo("user_id", mUser.id)
            .get()
            .addOnSuccessListener { query ->
                for(document in query.documents) {
                    mBatch.delete(document.reference)
                }
                deleteEnrollments()
            }
    }

    private fun deleteEnrollments() {

        mDatabase.collection(mCollection.ENROLLMENTS)
            .whereEqualTo("user_id", mUser.id)
            .get()
            .addOnSuccessListener { query ->
                for(document in query.documents) {
                    mBatch.delete(document.reference)
                }
                deleteAuthUser()
            }
    }

    private fun deleteAuthUser() {
        mBatch.commit().addOnSuccessListener {
            val currentUser = mAuth.currentUser
            currentUser?.delete()?.addOnSuccessListener {
                restartApp()
            }
        }
    }

    private fun restartApp() {
        finish()
        exitProcess(0)
    }
}
