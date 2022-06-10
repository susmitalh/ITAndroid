package com.locatocam.app.utils

interface UserDataMapper<Entity,DomainModel>  {

    fun mapFromCompanyUser(entity: Entity) : DomainModel

    fun mapToCompanyUser(domainModel: DomainModel) : Entity

}