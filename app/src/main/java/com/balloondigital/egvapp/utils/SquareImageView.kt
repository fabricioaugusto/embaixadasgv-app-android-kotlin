package com.balloondigital.egvapp.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

class SquareImageView : ImageView {

    // Inherited constructors

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

}