package com.locatocam.app.network

import com.locatocam.app.data.responses.user_model.Document
import com.locatocam.app.data.responses.user_model.SocialDetail
import com.locatocam.app.utils.EntityMapper
import javax.inject.Inject

class SocialDetailMapper @Inject constructor(): EntityMapper<SocialDetail,com.locatocam.app.data.responses.customer_model.SocialDetail> {
    override fun mapFromEntity(entity: SocialDetail): com.locatocam.app.data.responses.customer_model.SocialDetail {
        return com.locatocam.app.data.responses.customer_model.SocialDetail(
            follower = entity.follower,
            id = entity.id,
            link = entity.link,
            name = entity.name
        )
    }

    override fun mapToEntity(domainModel: com.locatocam.app.data.responses.customer_model.SocialDetail): SocialDetail {
        TODO("Not yet implemented")
    }
    fun mapFromEntityList(entities : List<SocialDetail>) : List<com.locatocam.app.data.responses.customer_model.SocialDetail>{
        return entities.map { mapFromEntity(it) }
    }
}