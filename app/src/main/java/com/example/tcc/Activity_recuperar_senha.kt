package com.example.tcc

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.example.tcc.funcoes.recuperar_senha
import kotlinx.android.synthetic.main.activity_recuperar_senha.*

class Activity_recuperar_senha : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_senha)

        //recebendo endereço de email já digitado
        var email:String = intent.getStringExtra("email").toString()
        email_recuperacao_texto.setText(email)


        //mudando nome da barra
        supportActionBar!!.title = "Recuperação de Conta"

        //ao clicar em recuperar senha
        btn_recuperar_senha.setOnClickListener {
            recuperar_senha(email_recuperacao_texto.text.toString().trim(), this.applicationContext)
        }
        //quando botão "feito" do teclado for pressionado
        email_recuperacao_texto.setOnEditorActionListener {
            v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                recuperar_senha(email_recuperacao_texto.text.toString().trim(), this.applicationContext)
                true
            }else{
                false
            }


        }

    }



}


