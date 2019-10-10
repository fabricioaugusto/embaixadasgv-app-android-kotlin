package com.balloondigital.egvapp.activity.Auth

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_request_code.*
import java.util.*

class RequestCodeActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mPlaceFields: List<Place.Field>
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var mPlacesClient: PlacesClient
    private var mIsPartOfEmbassy: Boolean = false
    private lateinit var mCity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_code)


        mDatabase = MyFirebase.database()

        Places.initialize(this, "AIzaSyDu9n938_SYxGcdZQx5hLC91vFa-wf-JoY")
        mPlacesClient = Places.createClient(this)
        mPlaceFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.TYPES)

        setListeners()
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btEmbassyYes) {
            mIsPartOfEmbassy = true
            txtInstructionText.text = "Se você é um(a) líder de embaixada, é necessário cadastra-la no site embaixadasgv.app. Ao realizar o cadastro, aguarde a sua embaixada ser aprovada para receber o convite com o código no seu e-mail. Caso você seja um participante, o líder da embaixada que você participa precisa estar cadastrado no aplicativo e lhe enviar o convite com o código através do próprio app."
            txtGoToSite.isGone = false
            layoutThirdQuestion.isGone = true
            layoutTextNewEmbassy.isGone = false
        }

        if(id == R.id.btEmbassyNo) {
            mIsPartOfEmbassy = false
            layoutFirstQuestion.isGone = true
            layoutChooseCity.isGone = false
        }

        if(id == R.id.btEmbassyParticipate) {
            txtInstructionText.text = "Entre no site embaixadasgv.app, confira a listas das embaixadas localizadas em $mCity e entre em contato com o líder da embaixada mais próxima de sua região!"
            txtGoToSite.isGone = false
            layoutThirdQuestion.isGone = true
            layoutTextNewEmbassy.isGone = false
        }

        if(id == R.id.btEmbassyFound) {
            txtInstructionText.text = "Preencha o formulário abaixo com os seus dados que em breve entraremos em contato para lhe fornecer o suporte necessário na abertura de sua embaixada."
            layoutThirdQuestion.isGone = true
            layoutTextNewEmbassy.isGone = false
            layoutForm.isGone = false
        }

        if(id == R.id.etSelectCity) {
            startPlacesActivity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == this.AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                val place = Autocomplete.getPlaceFromIntent(data!!)
                val addressComponents = place.addressComponents
                val city = place.name
                etSelectCity.text = city
                mCity = city.toString()
                pbSearchingCity.isGone = false

                mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
                    .whereEqualTo("city", city)
                    .get()
                    .addOnSuccessListener {
                        querySnapshot ->
                        if(querySnapshot.size() > 0) {
                            layoutChooseCity.isGone = true
                            layoutThirdQuestion.isGone = false
                            pbSearchingCity.isGone = true
                        } else {
                            txtInstructionText.text = "Não foi encontrada nenhuma embaixada ativa em $mCity, mas você pode fundar a primeira em sua cidade! Preencha o formulário abaixo com os seus dados que em breve entraremos em contato para lhe fornecer o suporte necessário na abertura de sua embaixada:"
                            layoutChooseCity.isGone = true
                            layoutTextNewEmbassy.isGone = false
                            layoutForm.isGone = false
                            pbSearchingCity.isGone = true
                        }
                    }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.i("GooglePlaceLog", status.statusMessage)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private fun setListeners() {
        btEmbassyYes.setOnClickListener(this)
        btEmbassyNo.setOnClickListener(this)
        btEmbassyParticipant.setOnClickListener(this)
        btEmbassyLeader.setOnClickListener(this)
        btEmbassyParticipate.setOnClickListener(this)
        btEmbassyFound.setOnClickListener(this)
        txtGoToSite.setOnClickListener(this)
        etSelectCity.setOnClickListener(this)
    }

    private fun startPlacesActivity() {

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, mPlaceFields
        )
            .setTypeFilter(TypeFilter.CITIES)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        etSelectCity.clearFocus()
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
