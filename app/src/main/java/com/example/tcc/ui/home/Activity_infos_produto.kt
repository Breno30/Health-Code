package com.example.tcc.ui.home

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.tcc.Activity_gavetas
import com.example.tcc.MainActivity
import com.example.tcc.R
import com.example.tcc.auth
import com.example.tcc.classes.atualizar_banco_local
import com.example.tcc.classes.atualizar_evitaveis_local
import com.example.tcc.classes.dadosProduto
import com.example.tcc.classes.tem_internet
import com.example.tcc.funcoes.aviso
import com.example.tcc.funcoes.botao
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_infos_produto.*

class Activity_infos_produto : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infos_produto)

        //esconde barra
        supportActionBar!!.hide()

        val BancoDeDados = this.getSharedPreferences("label", 0).getString("banco", "null")
        var codigo = intent.getStringExtra("codigo").toString()

        //se produto não for cadastrado, mostra botao de cadastrar
        if (BancoDeDados!!.contains(codigo)) {
            //se for cadastrado adiciona ao historico e mostra produto

            //adicionando codigo ao historico
            FirebaseDatabase.getInstance().reference.child("usuarios/" + auth.uid + "/historico").push().setValue(codigo)

            //criando tela de informações
            var dado = dadosProduto(this.findViewById(R.id.infos), R.id.infos, codigo, this)
            dado.dados_do_produto_com_codigo()


        }else{
            //avisa que o produto não esta cadastrado
            aviso(this.findViewById(R.id.infos), R.id.infos, "DESCULPE, PRODUTO NÃO CADASTRADO")

            if(tem_internet(applicationContext)){
                botao(this.findViewById(R.id.layout_info), R.id.layout_info, "Desejo Cadastrar esse Prodduto", codigo)


            }
        }







        btn_voltar_gavetas.setOnClickListener {
            //ao clicar, volta a pagina de gavetas
            val intent = Intent(this, Activity_gavetas::class.java)
            startActivity(intent)
            finish()
        }
    }


}