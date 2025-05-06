package com.example.artbookkotlin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.artbookkotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var artList:ArrayList<Art>
    private lateinit var artAdapter:ArtAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        artList=ArrayList<Art>()

        artAdapter=ArtAdapter(artList)
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        binding.recyclerView.adapter=artAdapter

        //VERİ ÇEKMEK
        try{
            val database=this.openOrCreateDatabase("Arts", MODE_PRIVATE,null)
            val cursor=database.rawQuery("SELECT * FROM arts",null)
            val artNameIx=cursor.getColumnIndex("artname")
            val idIx=cursor.getColumnIndex("id")

            while (cursor.moveToNext()){
                val name=cursor.getString(artNameIx)
                val id=cursor.getInt(idIx)
                val art=Art(name,id)
                artList.add(art)
            }

            //Art adapter,veri seti değişti yeni verileri göster
            artAdapter.notifyDataSetChanged()

            cursor.close()

        }catch (e:Exception){
            e.printStackTrace()
        }

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