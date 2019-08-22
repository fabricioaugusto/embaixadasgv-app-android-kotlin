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
import com.balloondigital.egvapp.activity.MainActivity
import com.balloondigital.egvapp.model.Invite
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_invites.*


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
    }

    fun setListeners() {
        btRegister.setOnClickListener(this)
        tvToLogin.setOnClickListener(this)
    }

    fun startCheckAuthActivity() {
        val intent: Intent = Intent(this, CheckAuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun registerUser() {

        val name = etRegisterName.text.toString()
        val email = etRegisterEmail.text.toString()
        val pass = etRegisterPass.text.toString()
        val passConfirm = etRegisterPassConfirm.text.toString()

        if(validateRegister(name, email, pass, passConfirm)) {
            btRegister.startAnimation()


            mDatabase.collection(MyFirebase.COLLECTIONS.APP_INVITATIONS)
                .whereEqualTo("email_receiver", mInvite.email_receiver).get()
                .addOnSuccessListener { querySnapshot ->
                    if(querySnapshot.documents.size > 0) {
                        val documents = querySnapshot.documents
                        val invite = documents[0].toObject(Invite::class.java)
                        if(invite != null) {
                            if(invite.invite_code == mInvite.invite_code) {

                                mAuth.createUserWithEmailAndPassword(email, pass)
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            // Sign in success, update UI with the signed-in user's information
                                            val user = mAuth.currentUser

                                            if(user != null) {
                                                saveUser(user.uid, name, email)
                                            }

                                            btRegister.revertAnimation()
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            btRegister.revertAnimation()
                                            Log.d("createUserWithEmail", task.exception.toString())
                                            Toast.makeText(
                                                this, "Authentication failed.",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }
                                    }

                            } else {
                                makeToast("Você deve cadastrar o mesmo e-mail em que o convite foi enviado")
                                btSendInvite.revertAnimation()
                            }
                        }
                    } else {
                        makeToast("Você deve cadastrar o mesmo e-mail em que o convite foi enviado")
                        btSendInvite.revertAnimation()
                    }
                }

        }
    }

    fun saveUser(id: String, name: String, email: String) {

        val collection = mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
        val embassy = mInvite.embassy_receiver

        val user = HashMap<String, Any>()
        user["id"] = id
        user["name"] = name
        user["email"] = email
        if(embassy != null) {
            user["embassy"] = embassy
            user["embassy_id"] = embassy.id
        }

        collection.document(id).set(user)
            .addOnSuccessListener {
                if(mAuth.currentUser != null) {
                    startCheckAuthActivity()
                }

        }
            .addOnFailureListener {

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
