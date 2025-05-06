package com.example.artbookkotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.artbookkotlin.databinding.ActivityMain2Binding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)


        registerLauncher()


    }

    fun saveButtonClicked(view: View) {

        //VERİ KAYDI
        val artName = binding.artName.text.toString()
        val artistName = binding.artistName.text.toString()
        val year = binding.year.text.toString()

        if (selectedBitmap != null) {
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 300)

            //Resmi 0 ve 1 lere çevirip veritabanına kaydetmek için yaptık
            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteArray = outputStream.toByteArray()

            try {
                val database = this.openOrCreateDatabase("Arts", MODE_PRIVATE, null)
                database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY,artname VARCHAR,artistname VARCHAR,year VARCHAR,image BLOB)")

                val sqlString = "INSERT INTO arts(artname,artistname,year,image) VALUES(?,?,?,?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1, artName)
                statement.bindString(2, artistName)
                statement.bindString(3, year)
                statement.bindBlob(4, byteArray)
                statement.execute()
            } catch (e: Exception) {
                e.printStackTrace()
            }

           val intent= Intent(this@MainActivity2,MainActivity::class.java)
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)   //açık olan tüm activityleri kapat maine git
           startActivity(intent)



            //Veritabanından yukardaki byte dizisini çekip görsele dönüştürücez
        }


    }

    //GÖRSELİ KÜÇÜLTMEK
    private fun makeSmallerBitmap(image: Bitmap, maximumSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio: Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1) {
            //Resim yatay-landscape
            width = maximumSize
            val scaleHeight = width / bitmapRatio
            height = scaleHeight.toInt()

        } else {
            //Resim dikey-portrait
            height = maximumSize
            val scaleWidth = height * bitmapRatio
            width = scaleWidth.toInt()

        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }


    //RESİM SEÇME & KAMERA İZNİ
    fun selectImage(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //Android 33+ -> READ_MEDIA_IMAGES
            //Daha önceden izin verilmiş mi kontrol et
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) { // -->izin verilmedi demek
                //İzin iste ve kullanıcıya izin isteme mantığını  açıkla
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                ) {
                    //-rationale-
                    Snackbar.make(view, "Permission Needed For Gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission", View.OnClickListener {
                            //request permission-izin iste
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                        }).show()
                } else {
                    //request permission-izin iste
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else { //--> izin verildi demek
                //Galeriye git
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }
        } else {

            //Android 32 - -> READ_EXTERNAL_STORAGE
            //Daha önceden izin verilmiş mi kontrol et
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) { // -->izin verilmedi demek
                //İzin iste ve kullanıcıya izin isteme mantığını  açıkla
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    //-rationale-
                    Snackbar.make(view, "Permission Needed For Gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission", View.OnClickListener {
                            //request permission-izin iste
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                        }).show()
                } else {
                    //request permission-izin iste
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else { //--> izin verildi demek
                //Galeriye git
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }
        }


    }


    //REGISTERLAUNCHER: Intent Sonucu Almak için
    //PERMISSIONLAUNCHER: İzin İstemek için
    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val imageData = intentFromResult.data
                        //binding.image.setImageURI(imageData)
                        if (imageData != null) {
                            try {
                                if (Build.VERSION.SDK_INT >= 28) {
                                    val source = ImageDecoder.createSource(
                                        this@MainActivity2.contentResolver,
                                        imageData
                                    )
                                    selectedBitmap = ImageDecoder.decodeBitmap(source)
                                    binding.image.setImageBitmap(selectedBitmap)
                                } else {
                                    selectedBitmap = MediaStore.Images.Media.getBitmap(
                                        contentResolver,
                                        imageData
                                    )
                                    binding.image.setImageBitmap(selectedBitmap)
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }
                }
            }
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    //permission granted-izin verildi
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    //permission denied-izin verilmedi
                    Toast.makeText(this@MainActivity2, "Permission needed!", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }


}