package com.javabobo.reddit.models

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Post")
class Post : Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @get:Exclude
    var postId: String = ""

    @ColumnInfo(name = "userId")
    var userId: String? = ""

    @ColumnInfo(name = "title")
    var title: String? = null

    @ColumnInfo(name = "desc")
    var desc: String? = null

    @get:Exclude
    @Ignore
    var imageUri: Uri? =null


    @ColumnInfo(name = "timestamp")
    var timestamp: Long = 0


}