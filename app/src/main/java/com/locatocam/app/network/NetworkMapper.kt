package com.locatocam.app.network

import com.locatocam.app.data.PendingPost
import com.locatocam.app.network.approvals.Detail
import com.locatocam.app.utils.EntityMapper
import javax.inject.Inject

class NetworkMapper
@Inject
    constructor() : EntityMapper<Detail, PendingPost>
{
    override fun mapFromEntity(entity: Detail): PendingPost {


        return PendingPost(
            posted_by = entity.posted_by,
            posted_on = entity.posted_on,
            file = entity.file,
            header = entity.header,
            sub_header = entity.sub_header,
            description = entity.description,
            brand = entity.brand,
            file_type = entity.file_type,
            brand_approve = entity.brand_approve,
            id = entity.id,
            is_social = entity.is_social,
            screenshot = entity.screenshot
        )
    }



    fun mapFromEntityList(entities : List<Detail>) : List<PendingPost>{
        return entities.map { mapFromEntity(it) }
    }



    override fun mapToEntity(domainModel: PendingPost): Detail {
        TODO("Not yet implemented")
    }
}