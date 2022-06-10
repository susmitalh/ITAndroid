package com.locatocam.app.data.responses.followers

data class Data(
    val brand_followers: List<BrandFollower>,
    val brand_followers_count: Int,
    val brand_following: List<BrandFollowing>,
    val brand_following_count: Int,
    val influencer_followers: List<InfluencerFollower>,
    val influencer_followers_count: Int,
    val influencer_following: List<InfluencerFollowing>,
    val influencer_following_count: Int,
    val people_followers: List<PeopleFollower>,
    val people_followers_count: Int,
    val people_following: List<PeopleFollowing>,
    val people_following_count: Int
)