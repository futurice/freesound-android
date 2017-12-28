package com.futurice.freesound.helpers

import android.os.Parcel
import android.os.Parcelable


fun <R : Parcelable, T : ParcelableTestContainer<R>> R.testParcel(createContainer: (R) -> T,
                                                                  creator: Parcelable.Creator<T>): R {
    val bytes = marshall(createContainer(this))
    val container = unmarshall(bytes, creator)
    return container.tested
}

inline fun <reified T : Parcelable> Parcel.readTypedObjectCompat(): T =
        readParcelable(T::class.java.classLoader)

private fun marshall(parcelable: Parcelable): ByteArray =
        Parcel.obtain().use {
            parcelable.writeToParcel(it, 0)
            it.marshall()
        }

private fun unmarshall(bytes: ByteArray): Parcel =
        Parcel.obtain().apply {
            unmarshall(bytes, 0, bytes.size)
            setDataPosition(0)
        }

private fun <T> unmarshall(bytes: ByteArray, creator: Parcelable.Creator<T>): T =
        unmarshall(bytes).use(creator::createFromParcel)

private fun <T> Parcel.use(block: (Parcel) -> T): T =
        try {
            block(this)
        } finally {
            this.recycle()
        }

abstract class ParcelableTestContainer<out T : Parcelable>(val tested: T) : Parcelable {
    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(tested, flags)
    }
}

inline fun <reified T> parcelableCreator(
        crossinline create: (Parcel) -> T) =
        object : Parcelable.Creator<T> {
            override fun createFromParcel(source: Parcel) = create(source)
            override fun newArray(size: Int) = arrayOfNulls<T>(size)
        }