package com.example.tcc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tcc.carrossel.CarrosselActivity
import kotlinx.android.synthetic.main.activity_logo.*
import android.animation.AnimatorSet

import android.animation.ObjectAnimator




class ActivityLogo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)

        //esconde barra superior
        supportActionBar!!.hide()

        logo_inicial.alpha = 0.05F
        logo_inicial.scaleY = 0.85F
        logo_inicial.scaleX = 0.85F

        //animação que dura 2 segundos

        //imagem aumenta
        logo_inicial.animate().setDuration(1000).alpha(1F).scaleY(1F).scaleX(1F).withEndAction {

            //imagem diminui um pouco
            logo_inicial.animate().setDuration(1000).alpha(0.74F).scaleY(0.96F).scaleX(0.96F).withEndAction {

                //após seu final, entra no carrosel, se tiver
                val intent_entrar = Intent(this, CarrosselActivity::class.java)
                startActivity(intent_entrar)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }


    }
}