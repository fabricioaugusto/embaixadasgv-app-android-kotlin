package com.balloondigital.egvapp.activity.Edit

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Auth.CheckAuthActivity
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.model.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_choose_embassy.*

class ChooseEmbassyActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mUser: User
    private lateinit var mBrazilCollection: CollectionReference
    private lateinit var mCitiesCollection: CollectionReference
    private lateinit var mEmbassiesCollection: CollectionReference
    private lateinit var mEmbassy: Embassy
    private lateinit var mStateId: String
    private lateinit var mStateName: String
    private lateinit var mCityId: String
    private lateinit var mCityName: String
    private lateinit var mEmbassyId: String
    private lateinit var mEmbassyName: String
    private lateinit var mListOptions: MutableList<String>
    private lateinit var mListOptionsID: MutableList<String>
    private lateinit var mAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_embassy)

        mListOptions = mutableListOf()
        mListOptionsID = mutableListOf()
        mDatabase = MyFirebase.database()
        mBrazilCollection = mDatabase.collection(MyFirebase.COLLECTIONS.LOCATIONS)
            .document("cBnu0Eb45uaMixV05Qnd")
            .collection("states")

        val bundle: Bundle? = intent.extras
        if(bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        getStates()
        setListeners()
    }


    fun getStates() {
        mBrazilCollection.get().addOnSuccessListener { documents ->
            for(document in documents) {
                mListOptions.add(document.data["name"].toString())
                mListOptionsID.add(document.id)
            }
            btChooseEmbasyState.isVisible = true
            pbChooseEmbassy.isGone = true
            createStateDialog()
        }
    }

    fun getCities() {

        mListOptions.clear()
        mListOptionsID.clear()

        mCitiesCollection = mBrazilCollection.document(mStateId).collection("cities")

        mCitiesCollection.get().addOnSuccessListener { documents ->
            for(document in documents) {
                mListOptions.add(document.data["name"].toString())
                mListOptionsID.add(document.id)
            }
            btChooseEmbasyCity.isVisible = true
            pbChooseEmbassy.isGone = true
            createCityDialog()
        }
    }

    fun getEmbassies() {

        mListOptions.clear()
        mListOptionsID.clear()

        mEmbassiesCollection = mCitiesCollection.document(mCityId)
            .collection("embassies")

        mEmbassiesCollection.get().addOnSuccessListener { documents ->
            for(document in documents) {
                Log.d("FirebaseLog", document.data.toString())
                mListOptions.add(document.data["name"].toString())
                mListOptionsID.add(document.data["id"].toString())

            }
            btChooseEmbasyEmbassy.isVisible = true
            pbChooseEmbassy.isGone = true
            createEmbassyDialog()
        }
    }

    override fun onClick(view: View) {

        val id = view.id

        if(id == R.id.btChooseEmbasyState) {
            mAlertDialog.show()
        }

        if(id == R.id.btChooseEmbasyCity) {
            mAlertDialog.show()
        }

        if(id == R.id.btChooseEmbasyEmbassy) {
            mAlertDialog.show()
        }

        if(id == R.id.btSaveEmbassy) {
            saveEmbassy()
        }

    }

    fun setListeners() {
        btChooseEmbasyState.setOnClickListener(this)
        btChooseEmbasyCity.setOnClickListener(this)
        btChooseEmbasyEmbassy.setOnClickListener(this)
        btSaveEmbassy.setOnClickListener(this)
    }


    fun createStateDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Escolha um estado")
        builder.setSingleChoiceItems(mListOptions.toTypedArray(), -1, DialogInterface.OnClickListener
        { dialogInterface, i ->
            mStateId = mListOptionsID[i]
            mStateName = mListOptions[i]
        })
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
            btChooseEmbasyState.text = mStateName
            pbChooseEmbassy.isGone = false
            getCities()
        })
        builder.setNegativeButton("Cancelar", null)
        mAlertDialog = builder.create()
    }

    fun createCityDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Escolha um cidade")
        builder.setSingleChoiceItems(mListOptions.toTypedArray(), -1, DialogInterface.OnClickListener
        { dialogInterface, i ->
            mCityId = mListOptionsID[i]
            mCityName = mListOptions[i]
        })
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
            btChooseEmbasyCity.text = mCityName
            pbChooseEmbassy.isGone = false
            getEmbassies()
        })
        builder.setNegativeButton("Cancelar", null)
        mAlertDialog = builder.create()
    }

    fun createEmbassyDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Escolha uma embaixada")
        builder.setSingleChoiceItems(mListOptions.toTypedArray(), -1, DialogInterface.OnClickListener
        { dialogInterface, i ->
            mEmbassyId = mListOptionsID[i]
            mEmbassyName = mListOptions[i]
        })
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
            btChooseEmbasyEmbassy.text = mEmbassyName
            btSaveEmbassy.isGone = false
        })
        builder.setNegativeButton("Cancelar", null)
        mAlertDialog = builder.create()
    }

    fun saveEmbassy() {

        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
            .whereEqualTo("id", mEmbassyId).get()
            .addOnSuccessListener { querySnapshot ->

                val documents =  querySnapshot.documents

                if(documents.size > 0) {
                    mEmbassy = documents[0].toObject(Embassy::class.java)!!
                }
            }

        btSaveEmbassy.startAnimation()

        val collection = mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
        collection.document(mUser.id).set(mUser.toMap())
            .addOnSuccessListener {
                Log.d("FirebaseLog", "Embaixada Adicionada com Sucesso!")
                startCheckAuthActivity()
                finish()
            }
    }

    fun startCheckAuthActivity() {
        val intent: Intent = Intent(this, CheckAuthActivity::class.java)
        startActivity(intent)
    }
}
