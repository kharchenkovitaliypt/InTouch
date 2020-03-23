package com.kharchenkovitaliy.intouch.shared

import android.content.Context
import android.view.View
import com.airbnb.epoxy.EpoxyHolder
import kotlinx.android.extensions.LayoutContainer

class SyntheticEpoxyHolder : EpoxyHolder(), LayoutContainer {

    override var containerView: View? = null

    override fun bindView(itemView: View) {
        this.containerView = itemView
    }
}

val SyntheticEpoxyHolder.view: View
    get() = containerView!!

val SyntheticEpoxyHolder.context: Context
    get() = containerView!!.context
