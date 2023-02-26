package com.festezy.multipartexample.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import com.bumptech.glide.Glide
import com.festezy.multipartexample.databinding.ActivityMainBinding
import com.festezy.multipartexample.services.network.ApiInterface
import com.festezy.multipartexample.services.network.ApiClient
import com.festezy.multipartexample.services.response.GetProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiNWZlNmFlMjY2OWQ1Njg1ZjJiY2Q2MmNiNGU5NDA0ZTgzNTliMTRlMjQ5Y2U3M2RjZmRlNGFkNDk1NDM4N2NkYzQ1OThiNmZkNzEyMTY5OTUiLCJpYXQiOjE2NzczOTU2NzIuNTAxMjE1LCJuYmYiOjE2NzczOTU2NzIuNTAxMjE2LCJleHAiOjE3MDg5MzE2NzIuNDk4MzY0LCJzdWIiOiIyIiwic2NvcGVzIjpbXX0.JLVQi-tCpxCSxKXrqbikXZADZaxw3dh1HGPwc8le5Kalmli5WzJ_uqH-Pr3-N7rb1KAps40_16cz8laWUVGYesUu4NCK_lel-73dzNwhNF11ZMzIZkTfVOUdYs6LcbMRXSx3IAtpOAn_xiQwS878c-ORSuQYUn8IDhAbZlfvchWllE0wV1Ezb51z22tW6ArbGLnhZgDWcojP2gvhL6XQxlOzNsRzKU93lrHKDUx5MY5JyoKkxattypGyjHR2QDb89cSN7Z1kiy4ERHOQEzOemy66lfzIbDcpj1-RZFholCdKw8ujKqeJ-mPuXVbYCUGOJb2VYV5zaiBzFJgyUysUC3g37GhiHmWmCbHOsnw2hJ5BIVN9GDekNREzbUk27pdtUiXBZEqqHUmFfE6N_RGSsy3OOqGszm3tVW_nU1gYKuaIcaeY7SyF9qWBqhmyhzVFAT_FE9uU7PLI3ofiPiJbr7ZjwJw28l3RyD4cGV5KwYzGeyEkDo0wFyniyenzk6jY1O8xlpMT9iJ0L_dVW03nU1DQuyeS-8mobD-XA3HHWYfg2Zp-0knFfvlRC3xByZ-wEWT4t5t7AIZYluyCuCcblnvq49zMyFw9SsL1fE7YupfBrQ2_0LlRMXA-vmBXCz2RXJPfu1Dw-Uykex45KFm2dZDfrM2znIO_qlVA7D1qO_k"

    private val serverInterface: ApiInterface = ApiClient().getApiClient()!!
        .create(ApiInterface::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 24) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)

            getProfile(token)
        }


    }

    private fun getProfile(token: String) {


        serverInterface.getProfile("Bearer $token").enqueue(object: Callback<GetProfileResponse>{
            override fun onResponse(
                call: Call<GetProfileResponse>,
                response: Response<GetProfileResponse>
            ) {
                val getProfileResponse = response.body()!!
                val myData = getProfileResponse.data!!
                val myProfileImg = myData.avatar
                val myProfileName = myData.name
                val myProfileEmail = myData.email

                Glide.with(this@MainActivity).load(myProfileImg).into(binding.profileProfileImage)
                binding.profileUsername.text = myProfileImg

                binding.profileProfileSaya.setOnClickListener {
                    Intent(this@MainActivity, MyProfileActivity::class.java).also {
                        it.putExtra("KEY_MYPROFILEIMG", myProfileImg)
                        it.putExtra("KEY_MYPROFILENAME", myProfileName)
                        it.putExtra("KEY_MYPROFILEEMAIL", myProfileEmail)
                        startActivity(it)
                    }
                }
                Log.d("getProfile Sucess", myData.toString())
            }

            override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {
                Log.d("getProfile Fail", t.message.toString())
            }
        })
    }
}