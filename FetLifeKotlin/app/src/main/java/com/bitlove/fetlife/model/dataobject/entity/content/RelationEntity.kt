package com.bitlove.fetlife.model.dataobject.entity.content

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "relations",
        foreignKeys = arrayOf(
        ForeignKey(
                entity = MemberEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("relatedMemberId"),
                onDelete = ForeignKey.CASCADE),
        ForeignKey(
                entity = MemberEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("memberId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.RESTRICT),
        ForeignKey(
                entity = GroupEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("groupId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.RESTRICT),
        ForeignKey(
                entity = EventEntity::class,
                parentColumns = arrayOf("dbId"),
                childColumns = arrayOf("eventId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.RESTRICT)
))
data class RelationEntity(
        @SerializedName("related_member_id") var relatedMemberId: String = "",
        @SerializedName("member") var memberId: String = "",
        @SerializedName("group_id") var groupId: String? = "",
        @SerializedName("event_id") var eventId: String? = "",
        @SerializedName("action") var type: String? = "",
        @SerializedName("created_at") var createdAt: String? = ""
) : DataEntity {
    @PrimaryKey
    var dbId: String = UUID.randomUUID().toString()
}