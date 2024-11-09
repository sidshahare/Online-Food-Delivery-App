


package com.example.wavesoffood.model



import java.io.Serializable
import android.os.Parcel
import android.os.Parcelable

data class OrderDetails(
    var userUid: String? = null,
    var userName: String? = null,
    var foodNames: MutableList<String>? = null,
    var foodImages: MutableList<String>? = null,
    var foodPrices: MutableList<String>? = null,
    var foodQuantities: MutableList<Int>? = null,
    var foodDescriptions: MutableList<String>? = null,
    var foodIngredients: MutableList<String>? = null,
    var address: String? = null,
    var totalPrice: String? = null,
    var phoneNumber: String? = null,
    var orderAccepted: Boolean = false,
    var paymentReceived: Boolean = false,
    var itemPushKey: String? = null,
    var currentTime: Long = 0
) : Serializable, Parcelable {

    constructor(parcel: Parcel) : this() {
        userUid = parcel.readString()
        userName = parcel.readString()
        foodNames = parcel.createStringArrayList()
        foodImages = parcel.createStringArrayList()
        foodPrices = parcel.createStringArrayList()
        foodQuantities = parcel.createIntArray()?.toMutableList()
        foodDescriptions = parcel.createStringArrayList()
        foodIngredients = parcel.createStringArrayList()
        address = parcel.readString()
        totalPrice = parcel.readString()
        phoneNumber = parcel.readString()
        orderAccepted = parcel.readByte() != 0.toByte()
        paymentReceived = parcel.readByte() != 0.toByte()
        itemPushKey = parcel.readString()
        currentTime = parcel.readLong()
    }

    constructor(
        userUid: String,
        userName: String,
        foodNames: ArrayList<String>,
        foodImages: ArrayList<String>,
        foodPrices: ArrayList<String>,
        foodQuantities: ArrayList<Int>,
        foodDescriptions: ArrayList<String>,
        foodIngredients: ArrayList<String>,
        address: String,
        totalPrice: String,
        phoneNumber: String,
        orderAccepted: Boolean,
        paymentReceived: Boolean,
        itemPushKey: String?,
        currentTime: Long
    ) : this() {
        this.userUid = userUid
        this.userName = userName
        this.foodNames = foodNames
        this.foodPrices = foodPrices
        this.foodImages = foodImages
        this.foodQuantities = foodQuantities
        this.foodDescriptions = foodDescriptions
        this.foodIngredients = foodIngredients
        this.address = address
        this.totalPrice = totalPrice
        this.phoneNumber = phoneNumber
        this.currentTime = currentTime
        this.itemPushKey = itemPushKey
        this.orderAccepted = orderAccepted
        this.paymentReceived = paymentReceived
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userUid)
        parcel.writeString(userName)
        parcel.writeStringList(foodNames)
        parcel.writeStringList(foodImages)
        parcel.writeStringList(foodPrices)
        parcel.writeIntArray(foodQuantities?.toIntArray())
        parcel.writeStringList(foodDescriptions)
        parcel.writeStringList(foodIngredients)
        parcel.writeString(address)
        parcel.writeString(totalPrice)
        parcel.writeString(phoneNumber)
        parcel.writeByte(if (orderAccepted) 1 else 0)
        parcel.writeByte(if (paymentReceived) 1 else 0)
        parcel.writeString(itemPushKey)
        parcel.writeLong(currentTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderDetails> {
        override fun createFromParcel(parcel: Parcel): OrderDetails {
            return OrderDetails(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetails?> {
            return arrayOfNulls(size)
        }
    }
}
