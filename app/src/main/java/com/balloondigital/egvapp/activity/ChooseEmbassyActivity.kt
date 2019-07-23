package com.balloondigital.egvapp.activity

import android.content.DialogInterface
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_choose_embassy.*

class ChooseEmbassyActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mBrazilCollection: CollectionReference
    private lateinit var mListOptions: MutableList<String>
    private lateinit var mAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_embassy)

        mListOptions = mutableListOf()
        mDatabase = MyFirebase.database()
        mBrazilCollection = mDatabase.collection(MyFirebase.COLLECTIONS.LOCATIONS)
            .document("cBnu0Eb45uaMixV05Qnd")
            .collection("states")

        setListeners()
        getStates()

    }

    fun getStates() {
        mBrazilCollection.get().addOnSuccessListener { documents ->
            for(document in documents) {
                mListOptions.add(document.data["name"].toString())
            }
            btChooseEmbasyState.isVisible = true
            pbChooseEmbassy.isGone = true
            createDialog()
        }
    }

    override fun onClick(view: View) {

        val id = view.id

        if(id == R.id.btChooseEmbasyState) {
            mAlertDialog.show()
        }

    }

    fun setListeners() {
        btChooseEmbasyState.setOnClickListener(this)
        btChooseEmbasyCity.setOnClickListener(this)
        btChooseEmbasyEmbassy.setOnClickListener(this)
    }


    fun createDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Escolha um estado")
        builder.setSingleChoiceItems(mListOptions.toTypedArray(), -1, DialogInterface.OnClickListener
        { dialogInterface, i ->

        })
        builder.setPositiveButton("OK", null)
        builder.setNegativeButton("Cancelar", null)
        mAlertDialog = builder.create()
    }
}
