package com.balloondigital.egvapp.activity.Edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.api.UserService
import com.balloondigital.egvapp.utils.Converters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_change_pass.*
import kotlinx.android.synthetic.main.activity_login.*

class ChangePassActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private var mCurrentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pass)

        supportActionBar!!.title = "Alterar senha"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mAuth = MyFirebase.auth()
        mCurrentUser = UserService.authCurrentUser()

        setListeners()
    }

    override fun onClick(view: View) {
        val id = view.id
        if(id == R.id.btChangePassSavaData) {
            setPassword()
        }
    }

    private fun setListeners() {
        btChangePassSavaData.setOnClickListener(this)
    }

    private fun setPassword() {

        val currentPass: String = etChangePassCurrent.text.toString()
        val newPass: String = etChangePassNew.text.toString()
        val confirmPass: String = etChangePassConfirm.text.toString()

        if(newPass.isEmpty() || confirmPass.isEmpty()) {
            makeToast("Os campos devem ser preenchidos!")
            return
        }

        if(newPass != confirmPass) {
            makeToast("As senhas não conferem!")
            return
        }

        btChangePassSavaData.startAnimation()

        if(mCurrentUser != null) {

            val credentials = mAuth.signInWithEmailAndPassword(mCurrentUser!!.email!!, currentPass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth.currentUser
                        user?.updatePassword(newPass)?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                makeToast("Senha alterada com sucesso!")
                                btChangePassSavaData.doneLoadingAnimation(
                                    resources.getColor(com.balloondigital.egvapp.R.color.colorGreen),
                                    Converters.drawableToBitmap(resources.getDrawable(com.balloondigital.egvapp.R.drawable.ic_check_grey_light))
                                )
                            }
                        }

                    } else {
                        btLogin.revertAnimation()
                        // If sign in fails, display a message to the user.
                        makeToast("Senha atual incorreta!")

                    }

                    // ...
                }

        }
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

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
