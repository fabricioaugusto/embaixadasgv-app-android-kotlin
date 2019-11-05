package com.balloondigital.egvapp.activity.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import android.util.Log
import com.balloondigital.egvapp.model.EmbassySponsor
import com.balloondigital.egvapp.model.Invite
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.MyApplication
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import io.github.mthli.knife.KnifeParser
import kotlinx.android.synthetic.main.activity_submit_invite_code.*
import java.text.Normalizer


class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mInvite: Invite
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Init vars
        mAuth = MyFirebase.auth()
        mDatabase = MyFirebase.database()

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mInvite = bundle.getSerializable("invite") as Invite
        }

        txtSeePolicyPrivacy.text = KnifeParser.fromHtml("Ao se cadastrar você está de acordo com as <b>Políticas de Privacidade</b>")

        //set
        setListeners()
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.tvToLogin) {
            finish()
        }

        if (id == R.id.btRegister) {
            registerUser()
        }

        if(id == R.id.txtSeePolicyPrivacy) {
            MyApplication.util.openExternalLink(this, MyApplication.const.urls.policyPrivacy)
        }
    }

    fun setListeners() {
        btRegister.setOnClickListener(this)
        tvToLogin.setOnClickListener(this)
        txtSeePolicyPrivacy.setOnClickListener(this)
    }

    fun startCheckAuthActivity() {
        val intent: Intent = Intent(this, CheckAuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun registerUser() {

        val name = etRegisterName.text.toString()
        val email = etRegisterEmail.text.toString()
        val pass = etRegisterPass.text.toString()
        val passConfirm = etRegisterPassConfirm.text.toString()

        if(validateRegister(name, email, pass, passConfirm)) {

            if(email != mInvite.email_receiver) {
                makeToast("Você deve cadastrar o mesmo e-mail em que o convite foi enviado")
                return
            }

            btRegister.startAnimation()

            mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth.currentUser

                        if(user != null) {
                            saveUser(user.uid, name, email)
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        btRegister.revertAnimation()
                        Toast.makeText(
                            this, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

        }
    }

    private fun saveUser(id: String, name: String, email: String) {

        val collection = mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
        val embassy = mInvite.embassy_receiver

        val user = HashMap<String, Any>()
        user["id"] = id
        user["name"] = name
        user["email"] = email
        user["status"] = "registered"
        user["last_read_notification"] = FieldValue.serverTimestamp()


        if(mInvite.isLeader) {
            user["leader"] = true
        }

        if(embassy != null) {
            user["embassy"] = embassy.toBasicMap()
            user["embassy_id"] = embassy.id
        }

        collection.document(id).set(user)
            .addOnSuccessListener {

                if(mAuth.currentUser != null) {

                    mDatabase.collection(MyFirebase.COLLECTIONS.APP_INVITATIONS)
                        .document(mInvite.id)
                        .delete()

                    val currentUser = User()
                    currentUser.name = name
                    currentUser.id = id
                    currentUser.email = email

                    if(mInvite.isLeader) {
                        setEmbassy(currentUser)
                    } else {
                        setUsername(currentUser)
                    }
                }
        }

    }

    private fun setUsername(currentUser: User) {
        val collection = mDatabase.collection(MyFirebase.COLLECTIONS.USERS)

        val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        val temp = Normalizer.normalize(currentUser.name, Normalizer.Form.NFD)
        val formatted_name = REGEX_UNACCENT.replace(temp, "").toLowerCase()


        val name_array = formatted_name.split(" ").toTypedArray()
        val name_size = name_array.size

        val fist_name = name_array[0]
        var last_name = ""

        if(name_size > 1) {
            last_name = name_array[name_size-1]
        }

        var username = "${fist_name}_${last_name}"

        if(name_size == 1) {
            username = fist_name
        }

        collection.whereEqualTo("username", username)
            .get()
            .addOnSuccessListener {
                querySnapshot ->
                if(querySnapshot.isEmpty) {
                    collection.document(currentUser.id).update("username", username)
                        .addOnSuccessListener {
                            startCheckAuthActivity()
                        }
                } else {
                    collection.document(currentUser.id).update("username", "${username}_${(100..999).random()}")
                        .addOnSuccessListener {
                            startCheckAuthActivity()
                        }
                }
            }
    }

    private fun setEmbassy(currentUser: User) {

        val embassy = mInvite.embassy_receiver

        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
            .document(embassy?.id.toString())
            .update("leader", currentUser.toBasicMap(), "leader_id", currentUser.id)
            .addOnSuccessListener {
                setUsername(currentUser)
            }.addOnFailureListener {
                Log.d("EGVAPPLOG", it.message.toString())
            }
    }

    private fun validateRegister (name: String, email: String, pass: String, passConfirm: String): Boolean {

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || passConfirm.isEmpty()) {
            makeToast("Preencha todos os campos!")
            return false
        }

        if(name.contains("@")) {
            makeToast("Por favor cadastre um nome válido!")
            return false
        }

        if(!email.contains("@")) {
            makeToast("Por favor cadastre um e-mail válido!")
            return false
        }

        if (pass.count() < 6) {
            makeToast("A senha deve possuir mais de 6 caracteres!")
            return false
        }

        if (pass != passConfirm) {
            makeToast("As senhas não conferem")
            return false
        }

        return true
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
