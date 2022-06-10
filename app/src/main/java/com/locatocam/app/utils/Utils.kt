package com.locatocam.app.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Editable
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.coroutines.coroutineContext


class Utils {
    companion object{
        fun toEditable(source : String): Editable {
           return Editable.Factory.getInstance().newEditable(source)
        }

        fun hasPermissions(context: Context?, vararg permissions: Array<String>): Boolean {
            if (context != null && permissions != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            permission!!.toString()
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
            }
            return true
        }


        fun isEmailValid(email : String) : Boolean{
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isPhoneLengthMatches(phone : String) : Boolean{
            return phone.length == 10
        }

        fun isPinLengthMatches(pin : String) : Boolean{
            return pin.length == 6
        }

        fun getMultiPartFile(file : File) : MultipartBody.Part{

            val requestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            return MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        fun getActualPath(uri : Uri,context: Context?) : String{

            val wholeID = DocumentsContract.getDocumentId(uri)

            val id = wholeID.split(":".toRegex()).toTypedArray()[1]

            val column = arrayOf(MediaStore.Images.Media.DATA)

            val sel = MediaStore.Images.Media._ID + "=?"

            val cursor: Cursor = context!!.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null
            )!!

            var filePath = ""

            val columnIndex: Int = cursor.getColumnIndex(column[0])

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }

            cursor.close()

            return filePath
        }

        fun toBase64(path : String) : String{
            Log.e("path",path)
            val byteArrayOutputStream = ByteArrayOutputStream()
            val bitmap = BitmapFactory.decodeFile(path)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(imageBytes, Base64.DEFAULT)
        }


        fun getImageRequestBody(sourceFile: File) : RequestBody? {
            var requestBody: RequestBody? = null
            Thread {
                val mimeType = getMimeType(sourceFile);
                if (mimeType == null) {
                    Log.e("file error", "Not able to get mime type")
                    return@Thread
                }
                try {
                    requestBody = sourceFile.path.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Log.e("File upload", "failed")
                }
            }.start()

            return requestBody;
        }

        // url = file path or whatever suitable URL you want.
        private fun getMimeType(file: File): String? {
            var type: String? = null
            val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }
            return type
        }


         fun pickDocument(activity: Activity): Intent {
              var dataIntent : Intent = Intent()
            Dexter.withContext(activity)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                        ImagePicker.with(activity)
//                            .compress(1024)         //Final image size will be less than 1 MB(Optional)
//                            .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                            .createIntent { intent ->
                                dataIntent = intent
                            }



                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    }

                }
                ).check()

             return dataIntent

        }
    }
    fun ImageView.loadUrl(url:String){
        Glide.with(this)
            .load(url)
            .into(this)
    }


}