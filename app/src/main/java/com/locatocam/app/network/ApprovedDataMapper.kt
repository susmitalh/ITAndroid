package com.locatocam.app.network

import com.locatocam.app.data.ApprovedPosts
import com.locatocam.app.data.PendingPost
import com.locatocam.app.network.approvedData.Detail
import com.locatocam.app.utils.EntityMapper
import javax.inject.Inject

class ApprovedDataMapper @Inject constructor() : EntityMapper<Detail,ApprovedPosts> {
    override fun mapFromEntity(entity: Detail): ApprovedPosts {
        return ApprovedPosts(
            posted_by = entity.posted_by,
            posted_on = entity.posted_on,
            file = entity.file,
            header = entity.header,
            sub_header = entity.sub_header,
            description = entity.description,
            brand = entity.brand,
            file_type = entity.file_type,
            brand_status = entity.brand_status,
            id = entity.id,
            is_social = entity.is_social,
            screenshot = entity.screenshot,
            approved_time = entity.approved_time,
            approved_by = entity.approved_by,
            rejected_time = entity.rejected_time,
            rejected_reason = entity.rejected_reason,
            rejected_by = entity.rejected_by

        )
    }

    fun mapFromEntityList(entities : List<Detail>) : List<ApprovedPosts>{
        return entities.map { mapFromEntity(it) }
    }

    override fun mapToEntity(domainModel: ApprovedPosts): Detail {
        TODO("Not yet implemented")
    }
}