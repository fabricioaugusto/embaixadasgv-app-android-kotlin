package com.balloondigital.egvapp.fragment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.EventListAdapter
import com.balloondigital.egvapp.adapter.UserListAdapter
import com.balloondigital.egvapp.model.Event
import com.balloondigital.egvapp.model.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class AgendaFragment : Fragment() {

    private lateinit var mContext: Context
    private lateinit var mAdapter: EventListAdapter
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_agenda, container, false)

        val mEventList: MutableList<Event> = mutableListOf()
        mEventList.add(Event("01","Gestão Financeira", "Encontro da Embaixada",
            "Vai ser um encontro top", "21 de Julho", "19:00", "Shopping do Vale"))
        mEventList.add(Event("01","Planejamento x Fazejamento", "Encontro da Embaixada",
            "Vai ser um encontro top", "21 de Julho", "19:00", "IDHEA - Cidade Nobre"))

        mContext = view.context
        mRecyclerView = view.findViewById(R.id.eventsRecyclerView)

        setRecyclerView(mEventList)

        return view
    }

    private fun setRecyclerView(events: MutableList<Event>) {

        mAdapter = EventListAdapter(events)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)

        mAdapter.onItemClick = {
                event ->

        }
    }
}
