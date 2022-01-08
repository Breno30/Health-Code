package com.example.tcc

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sobre_antes_login.*

class Activity_Sobre_antes_login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sobre_antes_login)

        //escondendo barra
        supportActionBar!!.hide()

        btn_voltar_login.setOnClickListener{
            //voltando para login
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}