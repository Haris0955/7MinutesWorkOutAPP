package com.example.a7minwork

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.a7minwork.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding:ActivityMainBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //We inflate the late file by calling inflate on the Binding name
        binding= ActivityMainBinding.inflate(layoutInflater)

        //Then replace the setContentView parameter with binding?.root
        setContentView(binding?.root)

        binding?.FLStart?.setOnClickListener {
            val intent = Intent(this,ExerciseActivity::class.java)
            startActivity(intent)
        }
        binding?.FLBMI?.setOnClickListener{
            val intent = Intent(this@MainActivity ,BMIActivity::class.java )
            startActivity(intent)
        }

        binding?.FLHistory?.setOnClickListener{
            val intent = Intent(this@MainActivity ,HistoryActivity::class.java )
            startActivity(intent)
        }
    }

    //TO avoid memory leak we unassign the binding once the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        binding= null
    }
}