package com.festezy.multipartexample.services.network

import com.festezy.multipartexample.services.response.GetProfileResponse
import com.festezy.multipartexample.services.response.UpdateProfileResponse
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    /** GET REQUEST*/

    @GET("profile")
    fun getProfile(@Header("Authorization") authHeader: String): Call<GetProfileResponse>


    @Multipart
    @POST("profile")
    fun updateProfile(
        @Header("Authorization") authHeader: String,
        @Part avatar: MultipartBody.Part,
        @Part("name") name: RequestBody
    ): Call<UpdateProfileResponse>
}