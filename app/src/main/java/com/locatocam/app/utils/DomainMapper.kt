package com.locatocam.app.utils

interface DomainMapper <Entity,DomainModel>  {

    fun mapFromEntity(entity: Entity) : DomainModel

    fun mapToEntity(domainModel: DomainModel) : Entity

}