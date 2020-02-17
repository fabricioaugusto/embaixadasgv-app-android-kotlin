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
import androidx.core.view.isGone
import com.balloondigital.egvapp.utils.MyApplication
import java.net.URLEncoder


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
            return
        }

        if(id == R.id.txtInvitationLink) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label","https://embaixadasgv.app/convite/${mUser.username}")
            clipboard.setPrimaryClip(clip)
            makeToast("Link copiado!")
            return
        }

        val name = mInvite.name_receiver
        val whatsapp_text = "Olá *${name}*, este é um convite para você ter acesso ao aplicativo das Embaixadas GV. Bastar baixar o *EGV App* na Google Play (para Android) ou na AppStore (para iOS), clicar em *CADASTRE-SE* e utilizar o seguinte código de acesso: *${mInvite.invite_code}*. Vamos lá? https://embaixadasgv.app"
        val default_text = "Olá ${name}, este é um convite para você ter acesso ao aplicativo das Embaixadas GV. Bastar baixar o EGV App na Google Play (para Android) ou na AppStore (para iOS), clicar em CADASTRE-SE e utilizar o seguinte código de acesso: ${mInvite.invite_code}. Vamos lá? https://embaixadasgv.app"
        val urlText = URLEncoder.encode(whatsapp_text, "UTF-8")


        if(id == R.id.btInvitationCopy) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label",default_text)
            clipboard.setPrimaryClip(clip)
            makeToast("Link copiado!")
            return
        }

        if(id == R.id.btInvitationWhatsapp) {
            MyApplication.util.openExternalLink(this, "https://wa.me/?text=${urlText}")
            return
        }

        if(id == R.id.btInvitationNewCode) {
            layoutInvitationCode.isGone = true
            layoutInvitationForm.isGone = false
            return
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
        btInvitationNewCode.setOnClickListener(this)
        btInvitationWhatsapp.setOnClickListener(this)
        btInvitationCopy.setOnClickListener(this)
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

                            layoutInvitationForm.isGone = true

                            txtInvitationCode.text = code.toString()

                            layoutInvitationCode.isGone = false

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
