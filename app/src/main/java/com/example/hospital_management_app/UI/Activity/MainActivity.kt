package com.example.hospital_management_app.UI.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hospital_management_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}