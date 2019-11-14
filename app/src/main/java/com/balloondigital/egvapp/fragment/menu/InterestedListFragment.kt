package com.balloondigital.egvapp.fragment.menu


import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.ManageItemsDialogAdapter
import com.balloondigital.egvapp.adapter.UserListAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.InvitationRequest
import com.balloondigital.egvapp.model.MenuItem
import com.balloondigital.egvapp.model.User
import com.balloondigital.egvapp.utils.MyApplication
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.firestore.FirebaseFirestore
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.DialogPlusBuilder
import com.orhanobut.dialogplus.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_interested_list.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class InterestedListFragment : Fragment(), OnItemClickListener, View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mToolbar: Toolbar
    private lateinit var mContext: Context
    private lateinit var mCurrentInterested: User
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mSwipeLayoutFeed: SwipeRefreshLayout
    private lateinit var mUsersList: MutableList<User>
    private lateinit var mAdapter: UserListAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mCPDialog: DialogPlus
    private lateinit var mAdapterDialog: ManageItemsDialogAdapter
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View =  inflater.inflate(R.layout.fragment_interested_list, container, false)
        val bundle: Bundle? = arguments
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mToolbar = view.findViewById(R.id.interestedListToolbar)
        mToolbar.title = ""

        mDatabase = MyFirebase.database()
        mUsersList = mutableListOf()
        mContext = view.context
        mRecyclerView = view.findViewById(R.id.rvInterestedList)
        mSwipeLayoutFeed = view.findViewById(R.id.swipeLayoutInterestedList)

        val menuList: List<MenuItem> = listOf(
            MenuItem("Chamar no Whatsapp", "item", R.drawable.ic_whatsapp_black),
            MenuItem("Enviar e-mail", "item", R.drawable.ic_email_black),
            MenuItem("Excluir", "item", R.drawable.ic_delete_black)
        )

        mAdapterDialog = ManageItemsDialogAdapter(mContext, false, 3, menuList)

        mSwipeLayoutFeed.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { getRequestorsList() })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        getRequestorsList()
        setRecyclerView(mUsersList)
    }


    override fun onItemClick(dialog: DialogPlus?, item: Any?, view: View?, position: Int) {

        mCPDialog.dismiss()

        if(position == 0) {
            MyApplication.util.openExternalLink(mContext, "https://wa.me/+55${mCurrentInterested.whatsapp}")
        }

        if(position == 1) {
            MyApplication.util.openExternalLink(mContext, "mailto:${mCurrentInterested.email}")
        }

        if(position == 2) {
            confirmDeleteDialog()
        }
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

    private fun getRequestorsList() {

        mDatabase.collection(MyFirebase.COLLECTIONS.INTERESTED)
            .get().addOnSuccessListener { documentSnapshot ->

                mUsersList.clear()

                if(documentSnapshot != null) {
                    if(documentSnapshot.size() > 0) {
                        for(document in documentSnapshot) {
                            val user : User? = document.toObject(User::class.java)
                            if(user != null) {
                                mUsersList.add(user)
                            }
                        }
                    }
                }

                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
                mSwipeLayoutFeed.isRefreshing = false
            }
    }


    private fun setRecyclerView(users: MutableList<User>) {

        mAdapter = UserListAdapter(users)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_user)
            .shimmer(true).show()

        mAdapter.onItemClick = {
                user ->
            mCurrentInterested = user
            setManageItemsDialog()
        }
    }


    private fun setManageItemsDialog() {

        val dialogBuilder: DialogPlusBuilder? = DialogPlus.newDialog(mContext)
        if(dialogBuilder != null) {
            dialogBuilder.adapter = mAdapterDialog
            dialogBuilder.onItemClickListener = this
            dialogBuilder.setPadding(16, 32, 16, 32)
            dialogBuilder.setGravity(Gravity.CENTER)
            mCPDialog = dialogBuilder.create()
            mCPDialog.show()
        }
    }

    private fun confirmDeleteDialog() {
        AlertDialog.Builder(mContext)
            .setIcon(R.drawable.ic_warning_yellow)
            .setTitle("Remover padrinho")
            .setMessage("Tem certeza que deseja remover a solicitação de ${mCurrentInterested.name}?")
            .setPositiveButton("Sim") { dialog, which ->
                deleteRequest()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun deleteRequest() {
        mDatabase.collection(MyFirebase.COLLECTIONS.INTERESTED)
            .document(mCurrentInterested.id)
            .delete()
            .addOnCompleteListener {
                getRequestorsList()
            }
    }


    private fun makeToast(text: String) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show()
    }


}
