package com.balloondigital.egvapp.fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.MainActivity
import com.balloondigital.egvapp.activity.UserProfileActivity
import com.balloondigital.egvapp.adapter.UserListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_users.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class UsersFragment : Fragment() {

    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mContext: Context
    private lateinit var mAdapter: UserListAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mListUsers: MutableList<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_users, container, false)

        mDatabase = MyFirebase.database()
        mListUsers = mutableListOf()
        mContext = view.context
        mRecyclerView = view.findViewById(R.id.usersRecyclerView)

        getListUsers()
        setRecyclerView()

        return view
    }

    fun getListUsers() {

        mDatabase.collection(MyFirebase.COLLECTIONS.USERS)
            .get()
            .addOnSuccessListener {
                val documents = it.documents
                for (document in documents) {
                    val user: User? = document.toObject(User::class.java)
                    if(user != null) {
                        mListUsers.add(user)
                    }
                }

                mAdapter.notifyDataSetChanged()
            }

    }

    private fun setRecyclerView() {

        mAdapter = UserListAdapter(mListUsers)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mAdapter.onItemClick = {user -> startUserProfileActivity(user)}
    }

    fun startUserProfileActivity(singleUser: User) {

        val intent: Intent = Intent(mContext, UserProfileActivity::class.java)
        intent.putExtra("user", singleUser)
        startActivity(intent)
    }

}
