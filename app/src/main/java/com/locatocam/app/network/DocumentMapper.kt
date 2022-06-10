package com.locatocam.app.network

import com.locatocam.app.data.responses.user_model.Document
import com.locatocam.app.utils.EntityMapper
import javax.inject.Inject

class DocumentMapper @Inject constructor(): EntityMapper<Document,com.locatocam.app.data.responses.customer_model.Document> {
    override fun mapFromEntity(entity: Document): com.locatocam.app.data.responses.customer_model.Document {
        return com.locatocam.app.data.responses.customer_model.Document(
            doc_description = entity.doc_description,
            doc_id = entity.doc_id,
            doc_location = entity.doc_location,
            doc_name = entity.doc_name
        )
    }

    override fun mapToEntity(domainModel: com.locatocam.app.data.responses.customer_model.Document): Document {
        return Document(
            doc_description = domainModel.doc_description,
            doc_id = domainModel.doc_id,
            doc_location = domainModel.doc_location,
            doc_name = domainModel.doc_name
        )
    }



    fun mapFromEntityList(entities : List<Document>) : List<com.locatocam.app.data.responses.customer_model.Document>{
        return entities.map { mapFromEntity(it) }
    }

    fun mapToEntityList(entities : List<com.locatocam.app.data.responses.customer_model.Document>) : List<Document>{
        return entities.map { mapToEntity(it) }
    }


}