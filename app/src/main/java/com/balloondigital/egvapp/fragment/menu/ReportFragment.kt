package com.balloondigital.egvapp.fragment.menu


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.Embassy
import com.balloondigital.egvapp.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_report.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ReportFragment : Fragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mUsersList: MutableList<User>
    private lateinit var mLeadersList: MutableList<User>
    private lateinit var mSponsorsList: MutableList<User>
    private lateinit var mEmbassyList: MutableList<Embassy>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_report, container, false)

        mContext = view.context
        mDatabase = MyFirebase.database()
        mUsersList = mutableListOf()
        mLeadersList = mutableListOf()
        mSponsorsList = mutableListOf()
        mEmbassyList = mutableListOf()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUsersReport()
        getEmbassiesReport()
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

    private fun getUsersReport() {
        mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
            .get()
            .addOnSuccessListener {
                querySnapshot ->
                if(querySnapshot.size() > 0) {
                    for(document in querySnapshot.documents) {
                        val user = document.toObject(User::class.java)
                        if(user != null) {
                            mUsersList.add(user)
                            if(user.leader) {
                                mLeadersList.add(user)
                            }
                            if(user.sponsor) {
                                mSponsorsList.add(user)
                            }
                        }
                    }
                    txtReportUserCount.text = "${mUsersList.size} usuários cadastradas"
                    txtReportLeaderCount.text = mLeadersList.size.toString()
                    txtReportSponsorCount.text = mSponsorsList.size.toString()
                }
            }
    }

    private fun getEmbassiesReport() {
        mDatabase.collection(MyFirebase.COLLECTIONS.EMBASSY)
            .get()
            .addOnSuccessListener {
                    querySnapshot ->
                if(querySnapshot.size() > 0) {
                    for(document in querySnapshot.documents) {
                        val embassy = document.toObject(Embassy::class.java)
                        if(embassy != null) {
                            mEmbassyList.add(embassy)
                        }
                    }
                    txtReportEmbassyCount.text = "${mEmbassyList.count()} embaixadas cadastradas"
                }
            }
    }
}
