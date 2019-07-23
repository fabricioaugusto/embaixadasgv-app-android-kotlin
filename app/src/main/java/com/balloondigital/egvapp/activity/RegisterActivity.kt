package com.balloondigital.egvapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import com.google.firebase.auth.FirebaseUser
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore


class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Init vars
        mAuth = MyFirebase.auth()
        mDatabase = MyFirebase.database()
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

    fun startMainActivity() {
        val intent: Intent = Intent(this, MainActivity::class.java)
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
        }
    }

    fun saveUser(id: String, name: String, email: String) {

        val collection = mDatabase.collection(MyFirebase.COLLECTIONS.USERS)

        val user = HashMap<String, Any>()
        user["id"] = id
        user["name"] = name
        user["email"] = email

        collection.document(id).set(user)
            .addOnSuccessListener {
                if(mAuth.currentUser != null) {
                    startMainActivity()
                }

        }
            .addOnFailureListener {

            }

    }

    fun validateRegister (name: String, email: String, pass: String, passConfirm: String): Boolean {

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
