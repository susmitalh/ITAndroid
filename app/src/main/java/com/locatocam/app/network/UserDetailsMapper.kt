package com.locatocam.app.network



import com.locatocam.app.utils.EntityMapper
import javax.inject.Inject

class UserDetailsMapper
@Inject
constructor() : EntityMapper<com.locatocam.app.data.responses.company.UserDetails, com.locatocam.app.data.responses.customer_model.UserDetails> {
    override fun mapFromEntity(entity: com.locatocam.app.data.responses.company.UserDetails): com.locatocam.app.data.responses.customer_model.UserDetails {
        return com.locatocam.app.data.responses.customer_model.UserDetails(

            name = entity.name,
            address = entity.address,
            adhaar_no = entity.adhaar_no,
            bank_ac_name = entity.bank_ac_name,
            bank_ac_no = entity.bank_ac_no,
            bank_branch = entity.bank_branch,
            bank_ifsc = entity.bank_ifsc,
            bank_name = entity.bank_name,
            city_id = entity.city_id,
            city_name = entity.city_name,
            dob = entity.dob,
            documents = entity.documents,
            email = entity.email,
            gender = entity.gender,
            influencer_code = entity.influencer_code,
            is_admin = entity.is_admin,
            pan_no = entity.pan_no,
            phone = entity.phone,
            pincode = entity.pincode,
            social_details = entity.social_details,
            state_id = entity.state_id,
            state_name = entity.state_name,
            user_id = entity.user_id,
            user_type = entity.user_type

                )
    }

    override fun mapToEntity(entity: com.locatocam.app.data.responses.customer_model.UserDetails): com.locatocam.app.data.responses.company.UserDetails {
        return com.locatocam.app.data.responses.company.UserDetails(

            name = entity.name,
            address = entity.address,
            adhaar_no = entity.adhaar_no,
            bank_ac_name = entity.bank_ac_name,
            bank_ac_no = entity.bank_ac_no,
            bank_branch = entity.bank_branch,
            bank_ifsc = entity.bank_ifsc,
            bank_name = entity.bank_name,
            city_id = entity.city_id,
            city_name = entity.city_name,
            dob = entity.dob,
            documents = entity.documents,
            email = entity.email,
            gender = entity.gender,
            influencer_code = entity.influencer_code,
            is_admin = entity.is_admin,
            pan_no = entity.pan_no,
            phone = entity.phone,
            pincode = entity.pincode,
            social_details = entity.social_details,
            state_id = entity.state_id,
            state_name = entity.state_name,
            user_id = entity.user_id,
            user_type = entity.user_type

        )
    }
}