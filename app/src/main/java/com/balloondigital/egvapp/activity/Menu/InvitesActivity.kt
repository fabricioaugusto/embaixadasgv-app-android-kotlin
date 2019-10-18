package com.balloondigital.egvapp.activity.Menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Invite
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_invites.*
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context



class InvitesActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mInvite: Invite
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mCollections: MyFirebase.COLLECTIONS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invites)

        supportActionBar!!.title = "Enviar convites"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mDatabase = MyFirebase.database()
        mCollections = MyFirebase.COLLECTIONS

        txtInvitationLink.text = "https://embaixadasgv.app/convite/${mUser.username}"

        setListeners()

    }

    override fun onClick(view: View) {
        val id = view.id
        if(id == R.id.btSendInvite) {
            saveData()
        }

        if(id == R.id.txtInvitationLink) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label","https://embaixadasgv.app/convite/${mUser.username}")
            clipboard.setPrimaryClip(clip)
            makeToast("Link copiado!")
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

    private fun setListeners() {
        txtInvitationLink.setOnClickListener(this)
        btSendInvite.setOnClickListener(this)
    }

    private fun saveData() {
        val name: String = etSendNameInvite.text.toString()
        val email: String = etSendEmailInvite.text.toString()

        if(name.isEmpty() || email.isEmpty()) {
            makeToast("Você deve preencher todos os campos")
            return
        }

        btSendInvite.startAnimation()

        val code: Int = (100000..999999).random()

        mInvite = Invite(
            name_sender = mUser.name,
            email_sender = mUser.email,
            name_receiver = name,
            email_receiver = email,
            embassy_receiver = mUser.embassy,
            invite_code = code
        )

        mDatabase.collection(MyFirebase.COLLECTIONS.APP_INVITATIONS)
            .whereEqualTo("email_receiver", email).get()
            .addOnSuccessListener { querySnapshot ->
                if(querySnapshot.documents.size > 0) {
                    makeToast("Um convite já foi enviado para este e-mail")
                    btSendInvite.revertAnimation()
                } else {
                    mDatabase.collection(MyFirebase.COLLECTIONS.APP_INVITATIONS)
                        .document(code.toString())
                        .set(mInvite.toMap())
                        .addOnSuccessListener {

                            etSendNameInvite.setText("")
                            etSendEmailInvite.setText("")

                            makeToast("Convite enviado!")
                            btSendInvite.doneLoadingAnimation(
                                resources.getColor(R.color.colorGreen),
                                Converters.drawableToBitmap(resources.getDrawable(R.drawable.ic_check_grey_light)))

                            Handler().postDelayed({
                                btSendInvite.revertAnimation()
                            }, 1000)
                        }
                }
            }
    }



    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }


}
