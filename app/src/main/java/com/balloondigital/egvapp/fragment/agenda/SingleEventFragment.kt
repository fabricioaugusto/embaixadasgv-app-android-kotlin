package com.balloondigital.egvapp.fragment.agenda


import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.view.isGone
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.fragment.feed.UsersListFragment
import com.balloondigital.egvapp.fragment.search.SingleUserFragment
import com.balloondigital.egvapp.model.Enrollment
import com.balloondigital.egvapp.model.Event
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.Converters
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_single_event.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SingleEventFragment : Fragment(), OnMapReadyCallback, View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var mUser: User
    private lateinit var mEvent: Event
    private lateinit var mEventId: String
    private var mRootView: Int = 0
    private lateinit var mCurrentUser: FirebaseUser
    private lateinit var mUserEnrollment: Enrollment
    private lateinit var mEnrollmentList: MutableList<Enrollment>
    private lateinit var mUserEnrollmentList: MutableList<Enrollment>
    private var mPlaceLat: Double = 0.0
    private var mPlaceLng: Double = 0.0
    private var mEnrollProcessing  = false
    private var isReadMoreClicked = false
    private lateinit var mPlaceName: String
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mBtEnrollEvent: Button
    private lateinit var mBtBack: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_single_event, container, false)

        mContext = view.context

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mEventId = bundle.getString("eventId", "defaultId")
            mPlaceLat = bundle.getDouble("placeLat", -33.852)
            mPlaceLng = bundle.getDouble("placeLng",151.211)
            mPlaceName = bundle.getString("placeName","defaultPlace")
            mRootView = bundle.getInt("rootViewer")
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        mBtEnrollEvent = view.findViewById(R.id.btEnrollEvent)
        mBtBack = view.findViewById(R.id.btBackPress)
        mDatabase = MyFirebase.database()
        mEnrollmentList = mutableListOf()
        mUserEnrollmentList = mutableListOf()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(MyFirebase.auth().currentUser != null) {
            mCurrentUser = MyFirebase.auth().currentUser!!
            getCurrentUserDetails(mCurrentUser.uid)
        }

        setListeners()
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

        if(id == R.id.btBackPress) {
            activity!!.onBackPressed()
        }

        if(id == R.id.txtCountEnrolled) {
            startEnrollmentUserList()
        }

        if(id == R.id.layoutEventModerator) {
            startUserProfileActivity()
        }

        if(id == R.id.txtEventReadMore) {
            if(!isReadMoreClicked) {
                isReadMoreClicked = true
                txtEventReadMore.text = "Voltar"
                txtEventDescription.maxLines = Integer.MAX_VALUE
                txtEventDescription.ellipsize = null
            } else {
                isReadMoreClicked = false
                txtEventReadMore.text = "Leia mais"
                txtEventDescription.maxLines = 3
                txtEventDescription.ellipsize = TextUtils.TruncateAt.END
            }

        }

    }

    private fun setListeners() {
        layoutEventModerator.setOnClickListener(this)
        txtCountEnrolled.setOnClickListener(this)
        txtEventReadMore.setOnClickListener(this)
        mBtEnrollEvent.setOnClickListener(this)
        mBtBack.setOnClickListener(this)
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

        if(mEvent.cover_img != null) {
            Glide.with(this)
                .load(mEvent.cover_img)
                .into(imgEventCover)
        } else {
            imgEventCover.resources.getDrawable(R.drawable.bg_default_cover)
        }

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

        if(mEvent.moderator_1?.occupation != null) {
            txtModeratorProfession1.text = mEvent.moderator_1?.occupation
        } else {
            txtModeratorProfession1.text = "Eu sou GV!"
        }

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

    private fun startEnrollmentUserList() {

        val bundle = Bundle()
        bundle.putString("type", "enrollments")
        bundle.putString("obj_id", mEventId)
        bundle.putInt("rootViewer", mRootView)

        val nextFrag = UsersListFragment()
        nextFrag.arguments = bundle

        activity!!.supportFragmentManager.beginTransaction()
            .add(mRootView, nextFrag, "$mRootView:enrollmentUsers")
            .addToBackStack(null)
            .commit()
    }

    private fun startUserProfileActivity() {

        if(mEvent.moderator_1 != null) {

            val bundle = Bundle()
            bundle.putSerializable("user", mEvent.moderator_1)

            val nextFrag = SingleUserFragment()
            nextFrag.arguments = bundle

            activity!!.supportFragmentManager.beginTransaction()
                .add(mRootView, nextFrag, "$mRootView:singleUser")
                .addToBackStack(null)
                .commit()
        }
    }
}
