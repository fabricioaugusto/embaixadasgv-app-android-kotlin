package com.balloondigital.egvapp.utils

class MenuItens private constructor() {
    companion object {
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
        const val inviteLeader = "Enviar convite para líderes"
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
            myEnrolledEvents,
            myFavoriteEvents,
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
            "Eventos",
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
            myEnrolledEvents,
            myFavoriteEvents,
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
            "Eventos",
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
            myEnrolledEvents,
            myFavoriteEvents,
            "section",
            newEvent,
            sendInvites,
            sentEmbassyPhotos,
            editEmbassy,
            "section",
            inviteLeader,
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
            "Eventos",
            "Líderes",
            "Gestores",
            "Privacidade",
            "Sobre")
    }
}