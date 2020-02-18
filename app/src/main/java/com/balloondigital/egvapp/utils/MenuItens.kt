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
            list.add(MenuItem(myEmbassy, "item", R.drawable.ic_logo_embaixadagv_black))

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
            list.add(MenuItem(membersCodes, "item", R.drawable.ic_menu_keys))
            list.add(MenuItem(invitationRequests, "item", R.drawable.ic_approve_members))
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
            list.add(MenuItem(manageInteresteds, "item", R.drawable.ic_menu_interested))
            list.add(MenuItem(createBulletin, "item", R.drawable.ic_menu_manage_bulletins))
            list.add(MenuItem(sendNotifications, "item", R.drawable.ic_menu_add_notification))
            list.add(MenuItem(report, "item", R.drawable.ic_report_black))

            return list
        }

        fun getInfluencerSection(): MutableList<MenuItem> {

            val list: MutableList<MenuItem> = mutableListOf()
            list.add(MenuItem("Gerenciar Influenciadores", "section"))
            list.add(MenuItem(registeredInfluencers, "item", R.drawable.ic_menu_list_approvation))
            list.add(MenuItem(sendInviteToInfluencers, "item", R.drawable.ic_menu_manage_sponsors))
            list.add(MenuItem(influencersCodes, "item", R.drawable.ic_menu_interested))

            return list
        }

        fun getCounselorSection(): MutableList<MenuItem> {

            val list: MutableList<MenuItem> = mutableListOf()
            list.add(MenuItem("Gerenciar Conselheiros", "section"))
            list.add(MenuItem(registeredCounselors, "item", R.drawable.ic_menu_list_approvation))
            list.add(MenuItem(sendInviteToCounselors, "item", R.drawable.ic_menu_manage_sponsors))
            list.add(MenuItem(counselorsCodes, "item", R.drawable.ic_menu_interested))

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
            list.add(MenuItem(manageInteresteds, "item", R.drawable.ic_menu_manage_sponsors))
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
        const val editProfile = "Editar Perfil"
        const val changeProfilePhoto = "Alterar Foto de Perfil"
        const val changePassword = "Alterar Senha"
        const val editSocialNetwork = "Editar Redes Sociais"
        const val myEmbassy = "Minha Embaixada"
        const val myEnrolledEvents = "Meus Eventos Confirmados"
        const val myFavoriteEvents = "Meus Eventos Favoritos"
        const val newEvent = "Gerenciar Eventos"
        const val sendInvites = "Convidar Membros"
        const val membersCodes = "Lista de Códigos de Acesso"
        const val invitationRequests = "Aprovar Solicitações de Membros"
        const val sentEmbassyPhotos = "Gerenciar Fotos"
        const val editEmbassy = "Editar Dados da Embaixada"
        const val affiliatedEmbassies = "Embaixadas Afiliadas"
        const val embassyForApproval = "Embaixadas para Aprovação"
        const val createBulletin = "Gerenciar Informativos"
        const val sendNotifications = "Enviar Notificações"
        const val manageSponsors = "Gerenciar Padrinhos"
        const val editDashboardPost = "Editar Post de Dashboard"
        const val registeredInfluencers = "Lista de Influenciadores"
        const val sendInviteToInfluencers = "Convidar Influenciadores"
        const val influencersCodes = "Lista de Códigos para Influenciadores"
        const val registeredCounselors = "Lista de Conselheiros"
        const val sendInviteToCounselors = "Convidar Conselheiros"
        const val counselorsCodes= "Lista de Códigos para Conselheiros"
        const val manageInteresteds = "Lista de Interessados"
        const val report = "Informações"
        const val setPrivacy = "Configurações de Privacidade"
        const val policyPrivacy = "Políticas de Privacidade"
        const val embassyList = "Lista de Embaixadas"
        const val aboutEmbassy = "Sobre as Embaixadas"
        const val aboutApp = "Sobre o Aplicativo"
        const val suggestFeatures = "Sugira uma Funcionalidade"
        const val rateApp = "Avalie o Aplicativo"
        const val sendUsMessage = "Envie-nos uma Mensagem"
        const val logout = "Sair"
    }
}