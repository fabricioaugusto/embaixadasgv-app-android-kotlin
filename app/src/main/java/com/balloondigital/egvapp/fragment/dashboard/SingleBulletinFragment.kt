package com.balloondigital.egvapp.fragment.dashboard


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Bulletin
import com.balloondigital.egvapp.utils.Converters
import com.google.firebase.firestore.FirebaseFirestore
import io.github.mthli.knife.KnifeParser
import kotlinx.android.synthetic.main.fragment_single_bulletin.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 *
 */
class SingleBulletinFragment : Fragment(), View.OnClickListener {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mBulletin: Bulletin
    private lateinit var mBulletinID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_single_bulletin, container, false)

        mDatabase = MyFirebase.database()
        val bundle: Bundle? = arguments
        if(bundle != null ) {
            mBulletinID = bundle.getString("bulletinID", "")
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
        mDatabase.collection(MyFirebase.COLLECTIONS.BULLETIN)
            .document(mBulletinID)
            .get()
            .addOnSuccessListener {
                documentSnapshot ->
                val bulletin = documentSnapshot.toObject(Bulletin::class.java)
                if(bulletin != null) {
                    mBulletin = bulletin
                    bindData()
                }
            }
    }

    private fun bindData() {

        val postDate = Converters.dateToString(mBulletin.date!!)
        txtBulletinDate.text = "${postDate.date}/${postDate.month}/${postDate.fullyear}"

        txtBulletinTitle.text = mBulletin.title
        txtBulletinDescription.text = mBulletin.resume
        txtBulletinText.text = KnifeParser.fromHtml(mBulletin.text)
    }

}
