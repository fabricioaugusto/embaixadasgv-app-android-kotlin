package com.balloondigital.egvapp.utils

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Edit.EditProfileActivity
import com.balloondigital.egvapp.model.MenuItem

class MenuItens private constructor() {
    companion object {

        fun getAccountSection(): MutableList<MenuItem> {

            val list: MutableList<MenuItem> = mutableListOf()
            list.add(MenuItem(profile, "profile"))
            list.add(MenuItem("Configurações de Conta", "section"))
            list.add(MenuItem(editProfile, "item", R.drawable.ic_menu_edit_profile))
            list.add(MenuItem(changeProfilePhoto, "item", R.drawable.ic_menu_change_photo))
            list.add(MenuItem(changePassword, "item", R.drawable.ic_menu_change_pass))
            list.add(MenuItem(editSocialNetwork, "item", R.drawable.ic_menu_edit_social))
            list.add(MenuItem(myEmbassy, "item", R.drawable.ic_menu_myembassy))

            return list
        }

        fun getPrivacySection(): MutableList<MenuItem> {

            val list: MutableList<MenuItem> = mutableListOf()
            list.add(MenuItem("Privacidade", "section"))
            list.add(MenuItem(setPrivacy, "item", R.drawable.ic_menu_policy_privacy))

            return list
        }

        fun getLeaderSection(): MutableList<MenuItem> {

            val list: MutableList<MenuItem> = mutableListOf()
            list.add(MenuItem("Líderes", "section"))
            list.add(MenuItem(newEvent, "item", R.drawable.ic_menu_manage_event))
            list.add(MenuItem(sentEmbassyPhotos, "item", R.drawable.ic_menu_add_picture))
            list.add(MenuItem(sendInvites, "item", R.drawable.ic_menu_invite_user))
            list.add(MenuItem(editEmbassy, "item", R.drawable.ic_menu_edit_embassy))

            return list
        }

        fun getSponsorSection(): MutableList<MenuItem> {

            val list: MutableList<MenuItem> = mutableListOf()
            list.add(MenuItem("Padrinhos", "section"))
            list.add(MenuItem(affiliatedEmbassies, "item", R.drawable.ic_menu_affiliates))

            return list
        }

        fun getManagerSection(): MutableList<MenuItem> {

            val list: MutableList<MenuItem> = mutableListOf()
            list.add(MenuItem("Gestores", "section"))
            list.add(MenuItem(embassyForApproval, "item", R.drawable.ic_menu_list_approvation))
            list.add(MenuItem(manageSponsors, "item", R.drawable.ic_menu_manage_sponsors))
            list.add(MenuItem(createBulletin, "item", R.drawable.ic_menu_manage_bulletins))

            return list
        }

        fun getMoreOptionsSection(): MutableList<MenuItem> {

            val list: MutableList<MenuItem> = mutableListOf()
            list.add(MenuItem("Mais Opções", "section"))
            list.add(MenuItem(embassyList, "item", R.drawable.ic_menu_list_embassy))
            list.add(MenuItem(aboutEmbassy, "item", R.drawable.ic_my_embassy))
            list.add(MenuItem(suggestFeatures, "item", R.drawable.ic_menu_suggestions))
            list.add(MenuItem(rateApp, "item", R.drawable.ic_menu_rate_app))
            list.add(MenuItem(sendUsMessage, "item", R.drawable.ic_menu_send_message))
            list.add(MenuItem(logout, "item", R.drawable.ic_menu_logout))

            return list
        }


        fun getList(): MutableList<MenuItem> {
            val list: MutableList<MenuItem> = mutableListOf()

            list.add(MenuItem(profile, "profile"))
            list.add(MenuItem("Configurações de Conta", "section"))
            list.add(MenuItem(editProfile, "item", R.drawable.ic_menu_edit_profile))
            list.add(MenuItem(changeProfilePhoto, "item", R.drawable.ic_menu_change_photo))
            list.add(MenuItem(changePassword, "item", R.drawable.ic_menu_change_pass))
            list.add(MenuItem(editSocialNetwork, "item", R.drawable.ic_menu_edit_social))
            list.add(MenuItem(myEmbassy, "item", R.drawable.ic_menu_myembassy))
            list.add(MenuItem("Privacidade", "section"))
            list.add(MenuItem(setPrivacy, "item", R.drawable.ic_menu_policy_privacy))
            list.add(MenuItem("Mais Opções", "section"))
            list.add(MenuItem(embassyList, "item", R.drawable.ic_menu_list_embassy))
            list.add(MenuItem(aboutEmbassy, "item", R.drawable.ic_my_embassy))
            list.add(MenuItem(suggestFeatures, "item", R.drawable.ic_menu_suggestions))
            list.add(MenuItem(rateApp, "item", R.drawable.ic_menu_rate_app))
            list.add(MenuItem(sendUsMessage, "item", R.drawable.ic_menu_send_message))
            list.add(MenuItem(logout, "item", R.drawable.ic_menu_logout))

            return list
        }


        fun getLeaderList(): MutableList<MenuItem> {
            val list: MutableList<MenuItem> = mutableListOf()

            list.add(MenuItem(profile, "profile"))
            list.add(MenuItem("Configurações de Conta", "section"))
            list.add(MenuItem(editProfile, "item", R.drawable.ic_menu_edit_profile))
            list.add(MenuItem(changeProfilePhoto, "item", R.drawable.ic_menu_change_photo))
            list.add(MenuItem(changePassword, "item", R.drawable.ic_menu_change_pass))
            list.add(MenuItem(editSocialNetwork, "item", R.drawable.ic_menu_edit_social))
            list.add(MenuItem(myEmbassy, "item", R.drawable.ic_menu_myembassy))
            list.add(MenuItem("Líderes", "section"))
            list.add(MenuItem(newEvent, "item", R.drawable.ic_menu_manage_event))
            list.add(MenuItem(sentEmbassyPhotos, "item", R.drawable.ic_menu_add_picture))
            list.add(MenuItem(sendInvites, "item", R.drawable.ic_menu_invite_user))
            list.add(MenuItem(editEmbassy, "item", R.drawable.ic_menu_edit_embassy))
            list.add(MenuItem("Privacidade", "section"))
            list.add(MenuItem(setPrivacy, "item", R.drawable.ic_menu_policy_privacy))
            list.add(MenuItem("Mais Opções", "section"))
            list.add(MenuItem(embassyList, "item", R.drawable.ic_menu_list_embassy))
            list.add(MenuItem(aboutEmbassy, "item", R.drawable.ic_my_embassy))
            list.add(MenuItem(suggestFeatures, "item", R.drawable.ic_menu_suggestions))
            list.add(MenuItem(rateApp, "item", R.drawable.ic_menu_rate_app))
            list.add(MenuItem(sendUsMessage, "item", R.drawable.ic_menu_send_message))
            list.add(MenuItem(logout, "item", R.drawable.ic_menu_logout))

            return list
        }

        fun getManagerList(): MutableList<MenuItem> {
            val list: MutableList<MenuItem> = mutableListOf()

            list.add(MenuItem(profile, "profile"))
            list.add(MenuItem("Configurações de Conta", "section"))
            list.add(MenuItem(editProfile, "item", R.drawable.ic_menu_edit_profile))
            list.add(MenuItem(changeProfilePhoto, "item", R.drawable.ic_menu_change_photo))
            list.add(MenuItem(changePassword, "item", R.drawable.ic_menu_change_pass))
            list.add(MenuItem(editSocialNetwork, "item", R.drawable.ic_menu_edit_social))
            list.add(MenuItem(myEmbassy, "item", R.drawable.ic_menu_myembassy))
            list.add(MenuItem("Líderes", "section"))
            list.add(MenuItem(newEvent, "item", R.drawable.ic_menu_manage_event))
            list.add(MenuItem(sentEmbassyPhotos, "item", R.drawable.ic_menu_add_picture))
            list.add(MenuItem(sendInvites, "item", R.drawable.ic_menu_invite_user))
            list.add(MenuItem(editEmbassy, "item", R.drawable.ic_menu_edit_embassy))
            list.add(MenuItem("Gestores", "section"))
            list.add(MenuItem(embassyForApproval, "item", R.drawable.ic_menu_list_approvation))
            list.add(MenuItem(manageSponsors, "item", R.drawable.ic_menu_manage_sponsors))
            list.add(MenuItem(createBulletin, "item", R.drawable.ic_menu_manage_bulletins))
            list.add(MenuItem("Privacidade", "section"))
            list.add(MenuItem(setPrivacy, "item", R.drawable.ic_menu_policy_privacy))
            list.add(MenuItem("Mais Opções", "section"))
            list.add(MenuItem(embassyList, "item", R.drawable.ic_menu_list_embassy))
            list.add(MenuItem(aboutEmbassy, "item", R.drawable.ic_my_embassy))
            list.add(MenuItem(suggestFeatures, "item", R.drawable.ic_menu_suggestions))
            list.add(MenuItem(rateApp, "item", R.drawable.ic_menu_rate_app))
            list.add(MenuItem(sendUsMessage, "item", R.drawable.ic_menu_send_message))
            list.add(MenuItem(logout, "item", R.drawable.ic_menu_logout))

            return list
        }


        const val profile = "profile"
        const val editProfile = "Editar perfil"
        const val changeProfilePhoto = "Alterar foto de perfil"
        const val changePassword = "Alterar senha"
        const val editSocialNetwork = "Editar redes sociais"
        const val myEmbassy = "Minha embaixada"
        const val myEnrolledEvents = "Meus eventos confirmados"
        const val myFavoriteEvents = "Meus eventos favoritos"
        const val newEvent = "Gerenciar eventos"
        const val sendInvites = "Convidar Membros"
        const val sentEmbassyPhotos = "Gerenciar fotos"
        const val editEmbassy = "Editar dados da embaixada"
        const val affiliatedEmbassies = "Embaixadas Afiliadas"
        const val embassyForApproval = "Embaixadas para aprovação"
        const val createBulletin = "Gerenciar Informativos"
        const val manageSponsors = "Gerenciar Padrinhos"
        const val setPrivacy = "Configurações de privacidade"
        const val policyPrivacy = "Políticas de privacidade"
        const val embassyList = "Lista de embaixadas"
        const val aboutEmbassy = "Sobre as embaixadas"
        const val aboutApp = "Sobre o aplicativo"
        const val suggestFeatures = "Sugira uma funcionalidade"
        const val rateApp = "Avalie o aplicativo"
        const val sendUsMessage = "Envie-nos uma mensagem"
        const val logout = "Sair"
    }
}