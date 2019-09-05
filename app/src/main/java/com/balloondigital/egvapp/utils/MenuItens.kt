package com.balloondigital.egvapp.utils

import com.balloondigital.egvapp.R
import com.balloondigital.egvapp.activity.Edit.EditProfileActivity
import com.balloondigital.egvapp.model.MenuItem

class MenuItens private constructor() {
    companion object {

        fun getList(): MutableList<MenuItem> {
            val list: MutableList<MenuItem> = mutableListOf()

            list.add(MenuItem(profile, "profile"))
            list.add(MenuItem("Configurações de Conta", "section"))
            list.add(MenuItem(editProfile, "item", R.drawable.ic_menu_edit_profile))
            list.add(MenuItem(changeProfilePhoto, "item", R.drawable.ic_menu_change_photo))
            list.add(MenuItem(changePassword, "item", R.drawable.ic_menu_change_pass))
            list.add(MenuItem(editSocialNetwork, "item", R.drawable.ic_menu_edit_social))
            list.add(MenuItem(myEmbassy, "item", R.drawable.ic_date_blue))
            list.add(MenuItem("Privacidade", "section"))
            list.add(MenuItem(setPrivacy, "item", R.drawable.ic_menu_policy_privacy))
            list.add(MenuItem("Mais Opções", "section"))
            list.add(MenuItem(embassyList, "item", R.drawable.ic_date_blue))
            list.add(MenuItem(aboutEmbassy, "item", R.drawable.ic_date_blue))
            list.add(MenuItem(aboutApp, "item", R.drawable.ic_date_blue))
            list.add(MenuItem(suggestFeatures, "item", R.drawable.ic_date_blue))
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
            list.add(MenuItem(myEmbassy, "item", R.drawable.ic_date_blue))
            list.add(MenuItem("Líderes", "section"))
            list.add(MenuItem(newEvent, "item", R.drawable.ic_date_blue))
            list.add(MenuItem(sendInvites, "item", R.drawable.ic_date_blue))
            list.add(MenuItem(sentEmbassyPhotos, "item", R.drawable.ic_menu_add_picture))
            list.add(MenuItem(editEmbassy, "item", R.drawable.ic_menu_edit_embassy))
            list.add(MenuItem("Privacidade", "section"))
            list.add(MenuItem(setPrivacy, "item", R.drawable.ic_menu_policy_privacy))
            list.add(MenuItem("Mais Opções", "section"))
            list.add(MenuItem(embassyList, "item", R.drawable.ic_date_blue))
            list.add(MenuItem(aboutEmbassy, "item", R.drawable.ic_date_blue))
            list.add(MenuItem(aboutApp, "item", R.drawable.ic_date_blue))
            list.add(MenuItem(suggestFeatures, "item", R.drawable.ic_date_blue))
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
            list.add(MenuItem(myEmbassy, "item", R.drawable.ic_date_blue))
            list.add(MenuItem("Líderes", "section"))
            list.add(MenuItem(newEvent, "item", R.drawable.ic_menu_create_event))
            list.add(MenuItem(sendInvites, "item", R.drawable.ic_date_blue))
            list.add(MenuItem(sentEmbassyPhotos, "item", R.drawable.ic_menu_add_picture))
            list.add(MenuItem(editEmbassy, "item", R.drawable.ic_menu_edit_embassy))
            list.add(MenuItem("Gestores", "section"))
            list.add(MenuItem(embassyForApproval, "item", R.drawable.ic_date_blue))
            list.add(MenuItem(createBulletin, "item", R.drawable.ic_menu_create_bulletin))
            list.add(MenuItem("Privacidade", "section"))
            list.add(MenuItem(setPrivacy, "item", R.drawable.ic_menu_policy_privacy))
            list.add(MenuItem("Mais Opções", "section"))
            list.add(MenuItem(embassyList, "item", R.drawable.ic_date_blue))
            list.add(MenuItem(aboutEmbassy, "item", R.drawable.ic_date_blue))
            list.add(MenuItem(aboutApp, "item", R.drawable.ic_date_blue))
            list.add(MenuItem(suggestFeatures, "item", R.drawable.ic_date_blue))
            list.add(MenuItem(rateApp, "item", R.drawable.ic_menu_rate_app))
            list.add(MenuItem(sendUsMessage, "item", R.drawable.ic_menu_send_message))
            list.add(MenuItem(logout, "item", R.drawable.ic_menu_logout))

            return list
        }


        const val profile = "profile"
        const val editProfile = "Editar perfil"
        const val changeProfilePhoto =  "Alerar foto de perfil"
        const val changePassword = "Alterar senha"
        const val editSocialNetwork = "Minhas redes sociais"
        const val myEmbassy = "Minha embaixada"
        const val myEnrolledEvents = "Meus eventos confirmados"
        const val myFavoriteEvents = "Meus eventos favoritos"
        const val newEvent = "Criar evento"
        const val sendInvites = "Enviar convite"
        const val sentEmbassyPhotos = "Adicionar foto"
        const val editEmbassy = "Editar dados da embaixada"
        const val embassyForApproval = "Embaixadas para aprovação"
        const val createBulletin = "Novo Informativo"
        const val setPrivacy = "Configurações de privacidade"
        const val policyPrivacy = "Políticas de privacidade"
        const val embassyList = "Lista de embaixadas"
        const val aboutEmbassy = "Sobre as embaixadas"
        const val aboutApp = "Sobre o aplicativo"
        const val suggestFeatures = "Sugira uma funcionalidade"
        const val rateApp = "Avalie o aplicativo"
        const val sendUsMessage = "Envie-nos uma mensagem"
        const val logout = "Sair"

        val menuList: List<String> = listOf(
            profile,
            "section",
            editProfile,
            changeProfilePhoto,
            changePassword,
            editSocialNetwork,
            myEmbassy,
            "section",
            setPrivacy,
            policyPrivacy,
            "section",
            embassyList,
            aboutEmbassy,
            aboutApp,
            suggestFeatures,
            rateApp,
            sendUsMessage,
            logout)

        val menuSectionList: List<String> = listOf(
            "Configurações de Perfil",
            "Privacidade",
            "Sobre")

        val menuListLeader: List<String> = listOf(
            profile,
            "section",
            editProfile,
            changeProfilePhoto,
            changePassword,
            editSocialNetwork,
            myEmbassy,
            "section",
            newEvent,
            sendInvites,
            sentEmbassyPhotos,
            editEmbassy,
            "section",
            setPrivacy,
            policyPrivacy,
            "section",
            embassyList,
            aboutEmbassy,
            aboutApp,
            suggestFeatures,
            rateApp,
            sendUsMessage,
            logout)

        val menuSectionListLeader: List<String> = listOf(
            "Configurações de Perfil",
            "Líderes",
            "Privacidade",
            "Sobre")

        val menuListManager: List<String> = listOf(
            profile,
            "section",
            editProfile,
            changeProfilePhoto,
            changePassword,
            editSocialNetwork,
            myEmbassy,
            "section",
            newEvent,
            sendInvites,
            sentEmbassyPhotos,
            editEmbassy,
            "section",
            embassyForApproval,
            createBulletin,
            "section",
            setPrivacy,
            policyPrivacy,
            "section",
            embassyList,
            aboutEmbassy,
            aboutApp,
            suggestFeatures,
            rateApp,
            sendUsMessage,
            logout)

        val menuSectionListManager: List<String> = listOf(
            "Configurações de Perfil",
            "Líderes",
            "Gestores",
            "Privacidade",
            "Sobre")
    }

}