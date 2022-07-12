package com.icecreamqaq.test.db.entity

import com.icecreamqaq.yudb.jpa.JPADao
import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Embeddable
data class GroupQQKey(
    var groupId: Long? = null,
    var qqId: Long? = null,
) : Serializable

@Entity
class GroupQQ(
    @EmbeddedId
    var id: GroupQQKey? = null,
)

interface GroupQQDao : JPADao<GroupQQ, GroupQQKey> {
}