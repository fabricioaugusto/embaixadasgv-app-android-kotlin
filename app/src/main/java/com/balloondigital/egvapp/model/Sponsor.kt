package com.balloondigital.egvapp.model

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup



class Sponsor(title: String, items: List<BasicEmbassy>) : ExpandableGroup<BasicEmbassy>(title, items)