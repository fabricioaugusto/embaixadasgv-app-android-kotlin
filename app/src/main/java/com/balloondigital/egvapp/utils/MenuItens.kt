package com.balloondigital.egvapp.utils

class MenuItens private constructor() {
    companion object {
        val profile = "profile"
        val editProfile = "Editar perfil"
        val changeProfilePhoto =  "Alerar foto de perfil"
        val changePassword = "Alterar senha"
        val editSocialNetwork = "Minhas redes sociais"
        val myEmbassy = "Minha embaixada"
        val myEnrolledEvents = "Meus eventos confirmados"
        val myFavoriteEvents = "Meus eventos favoritos"
        val newEvent = "Criar evento"
        val sendInvites = "Enviar convites"
        val sentEmbassyPhotos = "Enviar foto de encontro da embaixada"
        val setPrivacy = "Configurações de privacidade"
        val policyPrivacy = "Políticas de privacidade"
        val embassyList = "Lista de embaixadas"
        val aboutEmbassy = "Sobre as embaixadas"
        val abaoutApp = "Sobre o aplicativo"
        val suggestFeatures = "Sugira uma funcionalidade"
        val rateApp = "Avalie o aplicativo"
        val sendUsMessage = "Envie-nos uma mensagem"
        val logout = "Sair"

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
            MenuItens.myFavoriteEvents,
            "section",
            newEvent,
            sendInvites,
            sentEmbassyPhotos,
            "section",
            setPrivacy,
            policyPrivacy,
            "section",
            embassyList,
            aboutEmbassy,
            abaoutApp,
            suggestFeatures,
            rateApp,
            sendUsMessage,
            logout)

        val menuSectionList: List<String> = listOf(
            "Configurações de Perfil",
            "Eventos",
            "Líderes",
            "Privacidade",
            "Sobre")
    }
}