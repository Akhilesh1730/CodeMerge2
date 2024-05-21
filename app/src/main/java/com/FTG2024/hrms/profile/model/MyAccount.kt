package com.FTG2024.hrms.profile.model

import android.os.Parcel
import android.os.Parcelable

data class MyAccount(
    val username: String,
    val email: String,
    val mobNo: String,
    val address: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeString(mobNo)
        parcel.writeString(address)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MyAccount> {
        override fun createFromParcel(parcel: Parcel): MyAccount = MyAccount(parcel)
        override fun newArray(size: Int): Array<MyAccount?> = arrayOfNulls(size)
    }
}
