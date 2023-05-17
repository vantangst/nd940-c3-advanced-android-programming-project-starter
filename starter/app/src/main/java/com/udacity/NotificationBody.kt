package com.udacity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationBody(
    val name: String = "",
    val status: Boolean = false
) : Parcelable