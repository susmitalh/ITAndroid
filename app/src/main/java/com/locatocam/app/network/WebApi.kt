package com.locatocam.app.network

import com.locatocam.app.ModalClass.*
import com.locatocam.app.data.requests.*
import com.locatocam.app.data.requests.reqUserProfile.ReqBlockedUser
import com.locatocam.app.data.requests.reqUserProfile.ReqProfileData
import com.locatocam.app.data.requests.reqUserProfile.ReqViewApproval
import com.locatocam.app.data.requests.viewApproval.ReqApprove
import com.locatocam.app.data.requests.viewApproval.ReqCompanyApprove
import com.locatocam.app.data.requests.viewApproval.ReqCompanyReject
import com.locatocam.app.data.requests.viewApproval.ReqReject
import com.locatocam.app.data.responses.*
import com.locatocam.app.data.responses.SearchModal.RespSearch
import com.locatocam.app.data.responses.add_comments.RespComments
import com.locatocam.app.data.responses.address.RespAddress
import com.locatocam.app.data.responses.brand_list.BrandList
import com.locatocam.app.data.responses.comments.RespGetComments
import com.locatocam.app.data.responses.customer_model.Customer
import com.locatocam.app.data.responses.feed.Feed
import com.locatocam.app.data.responses.followers.RespFollowers
import com.locatocam.app.data.responses.like.RespLike
import com.locatocam.app.data.responses.my_activity.RespMyActivity
import com.locatocam.app.data.responses.notification.RespNotification
import com.locatocam.app.data.responses.offers_for_you.RespOffersForYou
import com.locatocam.app.data.responses.playrolls.Rolls
import com.locatocam.app.data.responses.popular_brands.RespPopularBrands
import com.locatocam.app.data.responses.popular_videos.MostPopVideos
import com.locatocam.app.data.responses.register.RespRegister
import com.locatocam.app.data.responses.resp_products_new.RespProductsNew
import com.locatocam.app.data.responses.rolls_and_short_videos.RollsAndShortVideos
import com.locatocam.app.data.responses.saveaddress.RespSaveAddress
import com.locatocam.app.data.responses.selected_brands.RespSelectedBrand
import com.locatocam.app.data.responses.settings.*
import com.locatocam.app.data.responses.settings.Approved.ApprovedPost
import com.locatocam.app.data.responses.settings.companyApproved.companyApproved
import com.locatocam.app.data.responses.settings.companyPending.CompanyPending
import com.locatocam.app.data.responses.settings.companyRejected.companyRejected
import com.locatocam.app.data.responses.settings.pendingPost.RespViewApproval
import com.locatocam.app.data.responses.settings.rejectedPost.ResRejected
import com.locatocam.app.data.responses.social_login.SocialLogin
import com.locatocam.app.data.responses.top_brands.RespTopBrands
import com.locatocam.app.data.responses.top_influencers.TopInfluencer
import com.locatocam.app.data.responses.top_pics.RespTopPics
import com.locatocam.app.data.responses.user_details.RespUserDetails
import com.locatocam.app.data.responses.user_model.User
import com.locatocam.app.data.responses.verify_ptp.RespVerifyOTP
import com.locatocam.app.data.responses.webaudio.RespWebAudio
import com.locatocam.app.network.approvals.RespApproval
import com.locatocam.app.network.approvedData.RespApprovedData
import net.minidev.json.JSONObject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface WebApi {


    @Headers("Accept: application/json")
    @POST("App/top_influencer")
    suspend fun getTopInfluencerfl(
        @Body reqTopInfluencer: ReqTopInfluencer
    ): TopInfluencer

    @Headers("Accept: application/json")
    @POST("App/most_popular_videos")
    suspend fun getMostPopularVideos(
        @Body reqMostPopularVideos: ReqMostPopularVideos
    ): MostPopVideos

    @Headers("Accept: application/json")
    @POST("App/short_videos")
    suspend fun getRollsAndShortVideos(
        @Body reqRollsAndShortVideos: ReqRollsAndShortVideos
    ): RollsAndShortVideos

//    @Headers("Accept: application/json")

    @POST("App/search")
    suspend fun search(@Body jsonObject: JSONObject): RespSearch

    @Headers("Accept: application/json")
    @POST("App/feed")
    suspend fun getPost(
        @Body reqFeed: ReqFeed
    ): Feed

    @Headers("Accept: application/json")
    @POST("App/my_post_rolls_approval_list")
    suspend fun getBrandPending(
        @Body reqFeed: ReqPostApproval
    ): RespApproval

    @Headers("Accept: application/json")
    @POST("App/my_post_rolls_approval_list")
    suspend fun getApprovedApprovals(
        @Body reqFeed: ReqPostApproval
    ): RespApprovedData


    @Headers("Accept: application/json")
    @POST("App/otp")
    suspend fun sendOtp(
        @Body reqFeed: ReqSendOtp
    ): ResOtp

    @Headers("Accept: application/json")
    @POST("App/play_rolls")
    suspend fun getPlayRolls(
        @Body reqFeed: ReqPlayRolls
    ): Rolls

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("App/influencer_login")
    suspend fun login(
        @Field("phone") phone: String,
        @Field("otp") otp: String
    ): LoginResp

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("App/verify_phone")
    suspend fun socialAddNumber(
        @Field("phone") phone: String
    ): SocialAddNumber

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("App/influencer_login")
    suspend fun verifyOTP(
        @Field("phone") phone: String,
        @Field("otp") otp: String
    ): RespVerifyOTP

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("App/social_login")
    suspend fun socialLogin(
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("name") name: String
    ): SocialLogin

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("App/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("influencer_code") influencer_code: String,
        @Field("post_id") post_id: String,
        @Field("otp") otp: String
    ): RespRegister

    @Headers("Accept: application/json")

    @POST("App/upload_document")
    suspend fun uploadDocument(
        @Body multipartBody: MultipartBody
    ): ResDocUpload

    @Headers("Accept: application/json")
    @POST("App/view_address")
    suspend fun getAddress(
        @Body reqAddress: ReqAddress
    ): RespAddress

    @Headers("Accept: application/json")
    @POST("App/add_address")
    suspend fun savetAddress(
        @Body reqSaveAddress: ReqSaveAddress
    ): RespSaveAddress

    @Headers("Accept: application/json")
    @POST("App/likes")
    suspend fun like(
        @Body reqFeed: ReqLike
    ): RespLike

    @Headers("Accept: application/json")
    @POST("App/follow")
    suspend fun follow(
        @Body reqFollow: ReqFollow
    ): RespFollow

    @Headers("Accept: application/json")
    @POST("App/user_details")
    suspend fun userDetails(
        @Body reqUserDetails: ReqUserDetails
    ): RespUserDetails

    @Headers("Accept: application/json")
    @POST("App/view_comments")
    suspend fun view_comments(
        @Body reqGetCommets: ReqGetCommets
    ): RespGetComments

    @Headers("Accept: application/json")
    @POST("App/add_comments")
    suspend fun add_comments(
        @Body reqAddComment: ReqAddComment
    ): RespComments

    @Headers("Accept: application/json")
    @POST("App/settings")
    suspend fun settings(
        @Body reqSettings: ReqSettings
    ): RespSettings

    @Headers("Accept: application/json")
    @POST("App/settings")
    suspend fun settingsData(
        @Body reqSettings: ReqSettings
    ): User

    @Headers("Accept: application/json")
    @POST("App/settings")
    suspend fun companySettingsData(
        @Body reqSettings: ReqSettings
    ): com.locatocam.app.data.responses.company.Company

    @Headers("Accept: application/json")
    @POST("App/settings")
    suspend fun customerSettingsData(
        @Body reqSettings: ReqSettings
    ): Customer


    @Headers("Accept: application/json")
    @POST("App/edit_profile")
    suspend fun editProfile(
        @Body req: ReqProfileData
    ): ResEditProfile

    @Headers("Accept: application/json")
    @POST("App/states")
    suspend fun getStates(): ResState

    @Headers("Accept: application/json")
    @POST("App/cities")
    suspend fun getCities(
        @Body req: ReqCity
    ): ResCity


    @Headers("Accept: application/json")
    @GET("App/list_of_mp3")
    suspend fun list_of_mp3(): Call<RespWebAudio>

    @Headers("Accept: application/json")
    @POST("foodstamart_app/Main_api/selected_brands")
    suspend fun getSelectedBrands(
        @Body reqSelected: ReqSelectedBrand
    ): RespSelectedBrand

    @Headers("Accept: application/json")
    @GET("foodstamart_app/Main_api/offers")
    suspend fun getOffersForYou(): RespOffersForYou

    @Headers("Accept: application/json")
    @POST("foodstamart_app/Main_api/top_brands")
    suspend fun getTopBrands(
        @Body reqTopBrands: ReqTopBrands
    ): RespTopBrands

    @Headers("Accept: application/json")
    @GET("beta/foodstamart_app/Main_api/top_picks")
    suspend fun getTopPicks(): RespTopPics

    @Headers("Accept: application/json")
    @POST("foodstamart_app/Main_api/popular_brands")
    suspend fun getPopularBrands(
        @Body reqTopBrands: ReqPopularBrands
    ): RespPopularBrands

    @Headers("Accept: application/json")
    @POST("foodstamart_app/Main_api/fetch_dishes")
    suspend fun getPoducts(
        @Body reqItems: ReqItems
    ): RespProductsNew

    @Headers("Accept: application/json")
    @POST("foodstamart_app/Main_api/brand_list")
    suspend fun getBrandList(
        @Body reqItems: ReqOffersForYou
    ): BrandList

    @Headers("Accept: application/json")
    @POST("foodstamart_app/Main_api/menu_brands")
    suspend fun getMenuBrands(
        @Body reqItems: ReqItems
    ): RespSelectedBrand

    @Headers("Accept: application/json")
    @POST("App/get_counts")
    suspend fun get_counts(
        @Body reqGetCounts: ReqGetCounts
    ): RespCounts

    @Headers("Accept: application/json")
    @GET("App/post_report_master")
    suspend fun postReportMaster(): RespPostReportMaster

    @Headers("Accept: application/json")
    @POST("App/add_post_report")
    suspend fun addPostReport(
        @Body reqReportPost: ReqReportPost
    ): RespReportPost

    @Headers("Accept: application/json")
    @POST("App/trash_post")
    suspend fun trash(
        @Body reqTrash: ReqTrash
    ): RespTrash

    @Headers("Accept: application/json")
    @POST("App/follower")
    suspend fun getFollowers(
        @Body reqFollowers: ReqFollowers
    ): RespFollowers

    @Headers("Accept: application/json")
    @POST("App/my_activity")
    suspend fun getMyActivity(
        @Body reqMyActivity: ReqMyActivity
    ): RespMyActivity

    @Headers("Accept: application/json")
    @POST("App/notification")
    suspend fun getNotification(
        @Body reqMyActivity: ReqMyActivity
    ): RespNotification


    @POST("App/add_share")
    fun getAddShare(@Body jsonObject: JSONObject): Call<AddShare>?

    @POST("App/add_views")
    fun getAddView(@Body jsonObject: JSONObject): Call<AddView>?

    @POST("App/follow")
    fun getFollow(@Body jsonObject: JSONObject): Call<Follow>?

    @POST("App/play_post")
    suspend fun playPost(@Body jsonObject: JSONObject): PlayPost

    @POST("App/my_posts")
    suspend fun myposts(@Body jsonObject: JSONObject): MyPosts

    @POST("App/my_short_videos")
    suspend fun myshort(@Body jsonObject: JSONObject): MyPosts

    @Headers("Accept: application/json")
    @GET("App/influencer_sop")
    suspend fun getInfluencerSop(): InfluencerSop

    @Headers("Accept: application/json")
    @GET("App/poc_sop")
    suspend fun getPocSop(): InfluencerSop

    @Headers("Accept: application/json")
    @POST("App/view_unblock_user")
    suspend fun getViewBlock(
        @Body reqSettings: ReqSettings
    ): ViewBlockUser
    @Headers("Accept: application/json")
    @POST("App/unblock_user")
    suspend fun postBlockedUser(
        @Body reqBlockedUser: ReqBlockedUser
    ): ResBlockedUser

    @Headers("Accept: application/json")
    @POST("App/admin_post_rolls_approval_list")
    suspend fun getViewApproval(
        @Body reqViewApproval: ReqViewApproval
    ): RespViewApproval

    @Headers("Accept: application/json")
    @POST("App/admin_post_rolls_approval_list")
    suspend fun getViewApproved(
        @Body reqViewApproval: ReqViewApproval
    ): ApprovedPost

    @Headers("Accept: application/json")
    @POST("App/admin_post_rolls_approval_list")
    suspend fun getViewRejected(
        @Body reqViewApproval: ReqViewApproval
    ): ResRejected


    @Headers("Accept: application/json")
    @POST("App/brand_post_rolls_approval_list")
    suspend fun getCompanyPending(
        @Body reqViewApproval: ReqViewApproval
    ): CompanyPending

    @Headers("Accept: application/json")
    @POST("App/brand_post_rolls_approval_list")
    suspend fun getCompanyApproved(
        @Body reqViewApproval: ReqViewApproval
    ): companyApproved

    @Headers("Accept: application/json")
    @POST("App/brand_post_rolls_approval_list")
    suspend fun getCompanyeRejected(
        @Body reqViewApproval: ReqViewApproval
    ): companyRejected

    @Headers("Accept: application/json")
    @POST("App/admin_post_rolls_rejected")
    suspend fun postReject(
        @Body reqReject: ReqReject
    ): StatusApproved
    @Headers("Accept: application/json")
    @POST("App/admin_post_rolls_approve")
    suspend fun postApprove(
        @Body reqApprove: ReqApprove
    ): StatusApproved

    @Headers("Accept: application/json")
    @POST("App/admin_post_rolls_repost")
    suspend fun postRepost(
        @Body reqApprove: ReqApprove
    ): StatusApproved

    @Headers("Accept: application/json")
    @POST("App/online_ordering_help")
    suspend fun FAQ(
        @Body reqFeed: ReqOnlineOrderHelp
    ): FAQ





    @Headers("Accept: application/json")
    @POST("App/brand_post_rolls_reject")
    suspend fun postCompanyReject(
        @Body reqCompanyReject: ReqCompanyReject
    ): StatusApproved
    @Headers("Accept: application/json")
    @POST("App/brand_post_rolls_approve")
    suspend fun postCompanyApprove(
        @Body reqCompanyApprove: ReqCompanyApprove
    ): StatusApproved

    @Headers("Accept: application/json")
    @POST("App/brand_post_rolls_repost")
    suspend fun postCompanyRepost(
        @Body reqCompanyApprove: ReqCompanyApprove
    ): StatusApproved

    @Headers("Accept: application/json")
    @POST("App/my_post_rolls_approval_list")
    suspend fun getMyPostReelPending(
        @Body reqViewApproval: ReqViewApproval
    ): RespViewApproval

    @Headers("Accept: application/json")
    @POST("App/my_post_rolls_approval_list")
    suspend fun getMyPostReelApproved(
        @Body reqViewApproval: ReqViewApproval
    ): ApprovedPost

    @Headers("Accept: application/json")
    @POST("App/my_post_rolls_approval_list")
    suspend fun getMyPostReelRejected(
        @Body reqViewApproval: ReqViewApproval
    ): ResRejected
}