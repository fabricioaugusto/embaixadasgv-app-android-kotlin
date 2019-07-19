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
import com.balloondigital.egvapp.adapter.UserListAdapter
import com.balloondigital.egvapp.model.User
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

    private lateinit var mContext: Context
    private lateinit var mAdapter: UserListAdapter
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_users, container, false)

        val listUsers: MutableList<User> = mutableListOf()
        listUsers.add(User(id = "001", name="Fabrício Augusto", email= "fabricio.mata@gmail.com", status = "Eu sou GV!", gender = "M"))
        listUsers.add(User(id = "001", name="Iara Fortado", email= "fabricio.mata@gmail.com", status = "Eu sou GV!", gender = "F"))
        listUsers.add(User(id = "001", name="George da Mata", email= "fabricio.mata@gmail.com", status = "Eu sou GV!", gender = "M"))

        mContext = view.context
        mRecyclerView = view.findViewById(R.id.usersRecyclerView)

        setRecyclerView(listUsers)

        return view
    }

    private fun setRecyclerView(users: MutableList<User>) {

        mAdapter = UserListAdapter(users)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)

        mAdapter.onItemClick = {
                user ->

        }
    }


}
