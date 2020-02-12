package com.balloondigital.egvapp.fragment.menu


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.adapter.ManageItemsDialogAdapter
import com.balloondigital.egvapp.adapter.UserBasicAdapter
import com.balloondigital.egvapp.api.MyFirebase
import com.balloondigital.egvapp.model.*
import com.balloondigital.egvapp.utils.MyApplication
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.firebase.firestore.FirebaseFirestore
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.DialogPlusBuilder
import com.orhanobut.dialogplus.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_interested_list.btBackPress
import kotlinx.android.synthetic.main.fragment_list_members_code.*
import java.net.URLEncoder

/**
 * A simple [Fragment] subclass.
 */
class ListMembersCodeFragment : Fragment(), OnItemClickListener, View.OnClickListener {

    private lateinit var mUser: User
    private lateinit var mToolbar: Toolbar
    private lateinit var mContext: Context
    private lateinit var mCurrentInterested: UserBasic
    private lateinit var mCurrentInvitation: Invite
    private lateinit var mDatabase: FirebaseFirestore
    private lateinit var mInvitationList: MutableList<Invite>
    private lateinit var mUsersList: MutableList<UserBasic>
    private lateinit var mAdapter: UserBasicAdapter
    private lateinit var mCPDialog: DialogPlus
    private lateinit var mAdapterDialog: ManageItemsDialogAdapter
    private lateinit var mSkeletonScreen: RecyclerViewSkeletonScreen

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_list_members_code, container, false)

        val bundle: Bundle? = arguments
        if (bundle != null) {
            mUser = bundle.getSerializable("user") as User
        }

        mToolbar = view.findViewById(R.id.listMembersCodesToolbar)
        mToolbar.title = ""

        mDatabase = MyFirebase.database()
        mInvitationList = mutableListOf()
        mUsersList = mutableListOf()
        mContext = view.context

        val menuList: List<MenuItem> = listOf(
            MenuItem("Enviar por Whatsapp", "item", R.drawable.ic_whatsapp_black),
            MenuItem("Enviar por e-mail", "item", R.drawable.ic_email_black),
            MenuItem("Copiar", "item", R.drawable.ic_email_black),
            MenuItem("Excluir", "item", R.drawable.ic_delete_black)
        )

        mAdapterDialog = ManageItemsDialogAdapter(mContext, false, 3, menuList)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        getInvitationsList()
        setRecyclerView(mUsersList)
    }


    override fun onItemClick(dialog: DialogPlus?, item: Any?, view: View?, position: Int) {

        mCPDialog.dismiss()

        val name = mCurrentInvitation.name_receiver
        val whatsapp_text = "Olá *${name}*, este é um convite para você ter acesso ao aplicativo das Embaixadas GV. Bastar baixar o *EGV App* na Google Play (para Android) ou na AppStore (para iOS), clicar em *CADASTRE-SE* e utilizar o seguinte código de acesso: *${mCurrentInvitation.invite_code}*. Vamos lá? https://embaixadasgv.app"
        val default_text = "Olá ${name}, este é um convite para você ter acesso ao aplicativo das Embaixadas GV. Bastar baixar o EGV App na Google Play (para Android) ou na AppStore (para iOS), clicar em CADASTRE-SE e utilizar o seguinte código de acesso: ${mCurrentInvitation.invite_code}. Vamos lá? https://embaixadasgv.app"
        val urlText = URLEncoder.encode(whatsapp_text, "UTF-8")
        if(position == 0) {
            MyApplication.util.openExternalLink(mContext, "https://wa.me/?text=${urlText}")
        }

        if(position == 1) {
            MyApplication.util.openExternalLink(mContext, "mailto:${mCurrentInvitation.email_receiver}")
        }

        if(position == 1) {
            val clipboard = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label",default_text)
            clipboard.setPrimaryClip(clip)
            makeToast("Link copiado!")
        }

        if(position == 3) {
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

    private fun getInvitationsList() {

        mDatabase.collection(MyFirebase.COLLECTIONS.APP_INVITATIONS)
            .whereEqualTo("email_sender", mUser.email)
            .orderBy("name_receiver")
            .get().addOnSuccessListener { documentSnapshot ->

                mUsersList.clear()

                if(documentSnapshot != null) {
                    if(documentSnapshot.size() > 0) {
                        for(document in documentSnapshot) {
                            val invitation : Invite? = document.toObject(Invite::class.java)
                            if(invitation != null) {

                                invitation.id = document.id
                                mInvitationList.add(invitation)

                                val user: UserBasic = UserBasic()
                                user.id = invitation.id
                                user.name = invitation.name_receiver
                                user.description = invitation.invite_code.toString()
                                mUsersList.add(user)


                            }
                        }
                    }
                }
                makeToast("${mInvitationList.size} códigos")
                mAdapter.notifyDataSetChanged()
                mSkeletonScreen.hide()
            }.addOnFailureListener {
                Log.d("EGVAPPLOG", it.message.toString())
            }
    }


    private fun setRecyclerView(users: MutableList<UserBasic>) {

        mAdapter = UserBasicAdapter(users)
        rvMembersCodes.layoutManager = LinearLayoutManager(mContext)

        mSkeletonScreen = Skeleton.bind(rvMembersCodes)
            .adapter(mAdapter)
            .load(R.layout.item_skeleton_user)
            .shimmer(true).show()

        mAdapter.onItemClick = {
                user, pos ->
            mCurrentInterested = user
            mCurrentInvitation = mInvitationList[pos]
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
            .setTitle("Remover da lista")
            .setMessage("Tem certeza que deseja remover ${mCurrentInterested.name}?")
            .setPositiveButton("Sim") { dialog, which ->
                deleteRequest()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun deleteRequest() {
        mDatabase.collection(MyFirebase.COLLECTIONS.APP_INVITATIONS)
            .document(mCurrentInvitation.id)
            .delete()
            .addOnCompleteListener {
                getInvitationsList()
            }
    }


    private fun makeToast(text: String) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show()
    }


}
