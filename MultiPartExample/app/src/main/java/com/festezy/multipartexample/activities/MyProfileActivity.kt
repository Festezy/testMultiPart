package com.festezy.multipartexample.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.festezy.multipartexample.R
import com.festezy.multipartexample.databinding.ActivityMyProfileBinding
import com.festezy.multipartexample.services.network.ApiClient
import com.festezy.multipartexample.services.network.ApiInterface
import com.festezy.multipartexample.services.response.UpdateProfileResponse
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.nio.charset.Charset

class MyProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    private val serverInterface: ApiInterface = ApiClient().getApiClient()!!
        .create(ApiInterface::class.java)
    private var token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiNWZlNmFlMjY2OWQ1Njg1ZjJiY2Q2MmNiNGU5NDA0ZTgzNTliMTRlMjQ5Y2U3M2RjZmRlNGFkNDk1NDM4N2NkYzQ1OThiNmZkNzEyMTY5OTUiLCJpYXQiOjE2NzczOTU2NzIuNTAxMjE1LCJuYmYiOjE2NzczOTU2NzIuNTAxMjE2LCJleHAiOjE3MDg5MzE2NzIuNDk4MzY0LCJzdWIiOiIyIiwic2NvcGVzIjpbXX0.JLVQi-tCpxCSxKXrqbikXZADZaxw3dh1HGPwc8le5Kalmli5WzJ_uqH-Pr3-N7rb1KAps40_16cz8laWUVGYesUu4NCK_lel-73dzNwhNF11ZMzIZkTfVOUdYs6LcbMRXSx3IAtpOAn_xiQwS878c-ORSuQYUn8IDhAbZlfvchWllE0wV1Ezb51z22tW6ArbGLnhZgDWcojP2gvhL6XQxlOzNsRzKU93lrHKDUx5MY5JyoKkxattypGyjHR2QDb89cSN7Z1kiy4ERHOQEzOemy66lfzIbDcpj1-RZFholCdKw8ujKqeJ-mPuXVbYCUGOJb2VYV5zaiBzFJgyUysUC3g37GhiHmWmCbHOsnw2hJ5BIVN9GDekNREzbUk27pdtUiXBZEqqHUmFfE6N_RGSsy3OOqGszm3tVW_nU1gYKuaIcaeY7SyF9qWBqhmyhzVFAT_FE9uU7PLI3ofiPiJbr7ZjwJw28l3RyD4cGV5KwYzGeyEkDo0wFyniyenzk6jY1O8xlpMT9iJ0L_dVW03nU1DQuyeS-8mobD-XA3HHWYfg2Zp-0knFfvlRC3xByZ-wEWT4t5t7AIZYluyCuCcblnvq49zMyFw9SsL1fE7YupfBrQ2_0LlRMXA-vmBXCz2RXJPfu1Dw-Uykex45KFm2dZDfrM2znIO_qlVA7D1qO_k"

    var pickedPhoto: Uri? = null
    var pickedBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myProfileImg = intent.getStringExtra("KEY_MYPROFILEIMG")
        val myProfileName = intent.getStringExtra("KEY_MYPROFILENAME")
        val myProfilleEmail = intent.getStringExtra("KEY_MYPROFILEEMAIL")

        Glide.with(this@MyProfileActivity).load(myProfileImg).into(binding.profileProfileImage)
        binding.detailEtName.setText(myProfileName)
        binding.detailEtEmail.setText(myProfilleEmail)

        binding.ProfileEditButton.setOnClickListener {
            pickPhoto()
        }
    }

    private fun pickPhoto(){
        val grantedPermission = PackageManager.PERMISSION_GRANTED
        val readExternalStorage = android.Manifest.permission.READ_EXTERNAL_STORAGE
        val externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val checkSelfPermission = ContextCompat.checkSelfPermission(this@MyProfileActivity,
            readExternalStorage)

        if (checkSelfPermission != grantedPermission){
            ActivityCompat.requestPermissions(this@MyProfileActivity,
                arrayOf(readExternalStorage),1)

        } else {
            val galeryIntent = Intent(Intent.ACTION_PICK, externalContentUri)
            startActivityForResult(galeryIntent, 2)
        }
    }


    private fun updateDataToServer(token: String, avatar: Uri, name: String){
//        val file = File(avatar.path!!)
//
        val mediaType = "text/plain".toMediaTypeOrNull()
//        val requestBody = name.toRequestBody(mediaType)
//
//        Log.d("requestBody", requestBody.toString())
//
////        val requestFile = RequestBody.create(contentResolver.getType(avatar)
////            ?.let { it.toMediaTypeOrNull() }, file)
//
//        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
//
//        val avatarPart = MultipartBody.Part.createFormData("avatar", file.name, requestFile)

        val contentResolver = this@MyProfileActivity.contentResolver
        val uri = Uri.parse("$avatar")
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val filePath = cursor.getString(column_index)
            cursor.close()
        }


        val data: String = name
        val charset: Charset? = Charset.forName("UTF-8")
        val nameRequestBody: RequestBody = RequestBody.create(mediaType, data.toByteArray(charset!!))

        serverInterface.updateProfile("Bearer $token", avatarPart, nameRequestBody).enqueue(object : Callback<UpdateProfileResponse>{
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                Log.d("updateProfile sucess", response.body()!!.data.toString())
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                Log.d("updateProfile Fail", t.message.toString())
            }
        })


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        if (requestCode == 1 ){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val galeryIntent = Intent(Intent.ACTION_PICK, externalContentUri)
                startActivityForResult(galeryIntent, 2)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data!= null){
            pickedPhoto = data.data

            if (pickedPhoto != null){
                if (Build.VERSION.SDK_INT >= 28){
                    val sourceImg = ImageDecoder.createSource(this.contentResolver, pickedPhoto!!)
                    pickedBitmap = ImageDecoder.decodeBitmap(sourceImg)
                    binding.profileProfileImage.setImageBitmap(pickedBitmap)

                    updateDataToServer(token, pickedPhoto!!, "arr")
                }else {
                    pickedBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, pickedPhoto)
                    binding.profileProfileImage.setImageBitmap(pickedBitmap)
                    updateDataToServer(token, pickedPhoto!!, "arr")
                }
            }
        }
    }
}