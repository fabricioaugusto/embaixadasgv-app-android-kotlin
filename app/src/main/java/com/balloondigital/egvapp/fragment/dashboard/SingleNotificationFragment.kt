package com.balloondigital.egvapp.fragment.dashboard


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Bulletin
import com.balloondigital.egvapp.model.Notification
import com.balloondigital.egvapp.utils.Converters
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.firestore.FirebaseFirestore
import io.github.mthli.knife.KnifeParser
import kotlinx.android.synthetic.main.fragment_single_notification.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SingleNotificationFragment : Fragment(), View.OnClickListener {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mNotification: Notification
    private lateinit var mNotificationID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_single_notification, container, false)
        mDatabase = MyFirebase.database()
        val bundle: Bundle? = arguments
        if(bundle != null ) {
            mNotificationID = bundle.getString("notificationID", "")
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSingleBulletin()
        setListeners()
    }

    override fun onClick(view: View) {
        val id = view.id

        if(id == R.id.btBackPress) {
            activity!!.onBackPressed()
        }
    }

    private fun setListeners() {
        btBackPress.setOnClickListener(this)
    }

    private fun getSingleBulletin() {
        mDatabase.collection(MyFirebase.COLLECTIONS.NOTIFICATIONS)
            .document(mNotificationID)
            .get()
            .addOnSuccessListener {
                    documentSnapshot ->
                val notification = documentSnapshot.toObject(Notification::class.java)
                if(notification != null) {
                    mNotification = notification
                    bindData()
                }
            }
    }

    private fun bindData() {

        val postDate = Converters.dateToString(mNotification.created_at!!)
        txtNotificationDate.text = "${postDate.date}/${postDate.month}/${postDate.fullyear}"

        if(mNotification.picture.isNotEmpty()) {
            Glide.with(this)
                .load(mNotification.picture.toUri())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgNotificationPicture)
        }

        txtNotificationTitle.text = mNotification.title
        txtNotificationDescription.text = mNotification.description
        txtNotificationText.text = KnifeParser.fromHtml(mNotification.text)
    }

}
