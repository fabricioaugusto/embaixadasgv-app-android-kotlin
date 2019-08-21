package com.balloondigital.egvapp.activity.Single

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.isGone
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Enrollment
import com.balloondigital.egvapp.model.Event
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.bumptech.glide.Glide
import com.ethanhua.skeleton.SkeletonScreen
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_event_profile.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseUser


class EventProfileActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mEvent: Event
    private lateinit var mEventId: String
    private lateinit var mCurrentUser: FirebaseUser
    private lateinit var mUserEnrollment: Enrollment
    private lateinit var mEnrollmentList: MutableList<Enrollment>
    private lateinit var mUserEnrollmentList: MutableList<Enrollment>
    private var mPlaceLat: Double = 0.0
    private var mPlaceLng: Double = 0.0
    private var mEnrollProcessing  = false
    private lateinit var mPlaceName: String
    private lateinit var mDatabase: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_profile)

        supportActionBar!!.title = "Evento"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mEventId = bundle.getString("eventId", "defaultId")
            mPlaceLat = bundle.getDouble("placeLat", -33.852)
            mPlaceLng = bundle.getDouble("placeLng",151.211)
            mPlaceName = bundle.getString("placeName","defaultPlace")
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        mDatabase = MyFirebase.database()
        mEnrollmentList = mutableListOf()
        mUserEnrollmentList = mutableListOf()

        if(MyFirebase.auth().currentUser != null) {
            mCurrentUser = MyFirebase.auth().currentUser!!
            getCurrentUserDetails(mCurrentUser.uid)
        }

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

    override fun onMapReady(googleMap: GoogleMap) {

        val sydney = LatLng(mPlaceLat, mPlaceLng)
        googleMap.addMarker(
            MarkerOptions().position(sydney)
                .title(mPlaceName)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        googleMap.setMinZoomPreference(14F)
    }

    override fun onClick(view: View) {
        val id = view.id
        if(id == R.id.btEnrollEvent) {
            if(btEnrollEvent.isSelected) {
                deleteEnrollment()
            } else {
                saveEnrollment()
            }
        }
    }

    private fun setListeners() {
        btEnrollEvent.setOnClickListener(this)
    }

    private fun getCurrentUserDetails(uid: String) {
        mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
            .document(uid)
            .get()
            .addOnSuccessListener {
                documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                if(user!= null) {
                    mUser = user
                    mDatabase.collection(MyFirebase.COLLECTIONS.ENROLLMENTS)
                        .whereEqualTo("user_id", mUser.id).get()
                        .addOnSuccessListener{
                            querySnapshot ->
                            for(document in querySnapshot.documents) {
                                val enrollment = document.toObject(Enrollment::class.java)
                                if(enrollment != null) {
                                    mUserEnrollmentList.add(enrollment)
                                }
                            }
                            getEvent()
                        }
                }
            }
    }

    private fun getEvent() {
        mDatabase.collection(MyFirebase.COLLECTIONS.EVENTS)
            .document(mEventId)
            .get()
            .addOnSuccessListener {
                documentSnapshot ->
                val event = documentSnapshot.toObject(Event::class.java)
                if(event != null) {
                    mEvent = event
                    getEnrollments()
                }
            }
    }

    private fun getEnrollments() {

        mDatabase.collection(MyFirebase.COLLECTIONS.ENROLLMENTS)
            .whereEqualTo("event_id", mEventId)
            .get()
            .addOnSuccessListener {
                   querySnapshot ->
                if (querySnapshot.size() > 0) {
                    for(document in querySnapshot) {
                        val enrollment = document.toObject(Enrollment::class.java)
                        mEnrollmentList.add(enrollment)
                        if(enrollment.user_id == mUser.id) {
                            btEnrollEvent.isSelected = true
                            mUserEnrollment = enrollment
                        }
                    }
                }
                bindData()
            }
    }

    private fun bindData() {

        val eventDate = Converters.dateToString(mEvent.date!!)

        Glide.with(this)
            .load(mEvent.cover_img)
            .into(imgEventCover)

        Glide.with(this)
            .load(mEvent.moderator_1?.profile_img)
            .into(imgEventModerator1)

        btEnrollEvent.isSelected = mUserEnrollmentList.any { it.event_id == mEvent.id }
        if(btEnrollEvent.isSelected) {
            btEnrollEvent.text = "Presença confirmada"
        }

        bindEnrollment()

        imgEventCover
        txtEventTheme.text = mEvent.theme
        txtEventDescription.text = mEvent.description
        txtModeratorName1.text = mEvent.moderator_1?.name
        txtModeratorProfession1.text = "Eu sou GV"
        txtEventPlace.text = mEvent.place
        txtEventAddress.text = mEvent.address
        txtEventEmassy.text = "${mEvent.embassy!!.name} - ${mEvent.city}, ${mEvent.state_short}"
        txtEventDate.text = eventDate.date
        txtEventMonthAbr.text = eventDate.monthAbr
        txtEventTime.text = "${eventDate.weekday} às ${eventDate.hours}:${eventDate.minutes}"

        pbSingleEvent.isGone = true
        rootViewSingleEvent.isGone = false
    }

    private fun bindEnrollment() {

        val enrollmentSize = mEnrollmentList.size

        if(enrollmentSize == 0){
            txtCountEnrolled.text = "Nenhuma pessoa confirmada até o momento"
        } else if (enrollmentSize == 1){
            txtCountEnrolled.text = "$enrollmentSize pessoa confirmada até o momento"
        } else if(enrollmentSize > 1)
        {txtCountEnrolled.text = "$enrollmentSize pessoas confirmadas até o momento"}


        if(enrollmentSize > 0) {
            profile_enrolled_1.isGone = false
            Glide.with(this)
                .load(mEnrollmentList[0].user.profile_img)
                .into(profile_enrolled_1)
        } else {
            profile_enrolled_1.isGone = true
        }

        if(enrollmentSize > 1) {
            profile_enrolled_2.isGone = false
            Glide.with(this)
                .load(mEnrollmentList[1].user.profile_img)
                .into(profile_enrolled_2)
        } else {
            profile_enrolled_2.isGone = true
        }

        if(enrollmentSize > 2) {
            profile_enrolled_3.isGone = false
            Glide.with(this)
                .load(mEnrollmentList[2].user.profile_img)
                .into(profile_enrolled_3)
        } else {
            profile_enrolled_3.isGone = true
        }
    }

    private fun saveEnrollment() {

        if(!mUserEnrollmentList.any { it.event_id == mEvent.id } && !mEnrollProcessing) {

            btEnrollEvent.isSelected = true
            btEnrollEvent.text = "Presença confirmada"

            mEnrollProcessing = true

            val enrollment: Enrollment = Enrollment(
                event = mEvent,
                user = mUser,
                user_id = mUser.id,
                event_id = mEventId,
                event_date = mEvent.date
            )

            mUserEnrollment = enrollment
            mEnrollmentList.add(enrollment)

            bindEnrollment()

            mDatabase.collection(MyFirebase.COLLECTIONS.ENROLLMENTS)
                .add(enrollment.toMap())
                .addOnSuccessListener {
                    documentReference ->
                    enrollment.id = documentReference.id
                    mUserEnrollmentList.add(enrollment)
                    documentReference.update("id", documentReference.id)
                        .addOnSuccessListener {
                            mEnrollProcessing = false
                        }
                }
        }
    }

    private fun deleteEnrollment() {

        if(mUserEnrollmentList.any { it.event_id == mEvent.id } && !mEnrollProcessing) {

            val list = mUserEnrollmentList.filter { it.event_id == mEvent.id }

            if(list.isNotEmpty()) {

                btEnrollEvent.isSelected = false
                btEnrollEvent.text = "Confirmar presença"

                mEnrollProcessing = true
                mUserEnrollmentList.remove(mUserEnrollment)

                mEnrollmentList.remove(mUserEnrollment)
                bindEnrollment()

                mDatabase.collection(MyFirebase.COLLECTIONS.ENROLLMENTS)
                    .document(list[0].id)
                    .delete()
                    .addOnSuccessListener {
                        mEnrollProcessing = false
                    }
            }


        }


    }

}
