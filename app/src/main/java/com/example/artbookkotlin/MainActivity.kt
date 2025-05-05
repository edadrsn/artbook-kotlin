package com.example.artbookkotlin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.artbookkotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    //MENÜYÜ MAİNE BAĞLAMAK

    //Bağlama işini yapıcaz
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //inflater
        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.art_menu,menu)
        return super.onCreateOptionsMenu(menu)

    }

    //Menüye tıklanırsa ne yapıcaz
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.add_art_item){
            val intent= Intent(this@MainActivity,MainActivity2::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}