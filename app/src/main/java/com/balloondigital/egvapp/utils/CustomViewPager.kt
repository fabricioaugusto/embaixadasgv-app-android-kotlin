package com.balloondigital.egvapp.utils

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.viewpager.widget.ViewPager
import android.view.MotionEvent


class CustomViewPager : ViewPager {

    private var isPagingEnabled: Boolean = false

    constructor(context: Context) : super(context) {
        this.isPagingEnabled = true
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.isPagingEnabled = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return this.isPagingEnabled && super.onTouchEvent(event)
    }

    //for samsung phones to prevent tab switching keys to show on keyboard
    override fun executeKeyEvent(event: KeyEvent): Boolean {
        return isPagingEnabled && super.executeKeyEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event)
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.isPagingEnabled = enabled
    }
}