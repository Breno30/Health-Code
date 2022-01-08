package com.example.tcc

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.example.tcc.classes.Usuario
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_cadastro_usuario_2.*

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

val evitaveis_lista = arrayOf(
    "Leite",
    "Ovo",
    "Trigo",
    "Soja",
    "Amendoim",
    "Aveia",
    "Avelã",
    "Camarão",
    "Castanha de caju",
    "Centeio",
    "Lagosta",
    "Nozes",
    "Peixe",
    "Pistache")

class Activity_Cadastro_2 : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_usuario_2)


        //mudando titulo da barra
        supportActionBar!!.title = "Componentes a se Evitar"

        //RECEBENDO DADOS DA PRIMEIRA TELA
        var nome = intent.getStringExtra("nome").toString()
        var email = intent.getStringExtra("email").toString()
        var senha = intent.getStringExtra("senha").toString()
        var data_nascimento = intent.getStringExtra("data").toString()


        val evitaveis: MutableList<String> = ArrayList()


        //adicionando evitaveis na lista
        val adaptador: ArrayAdapter<*> = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                evitaveis
        )
        lista!!.adapter = adaptador

        //configurando recomendações na caixa de texto
        val adaptador_recomendacoes: ArrayAdapter<*> = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                evitaveis_lista
        )
        nova_composicao!!.setAdapter(adaptador_recomendacoes)

        //quando clicar na lista, apaga item
        lista!!.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView, view, posicao, arg3 -> //alerta

                    //removendo posicao
                    evitaveis.removeAt(posicao)

                    //atualizando lista
                    lista!!.adapter = adaptador
                }


        //quando clicar em adicionar
        adic!!.setOnClickListener {View->
            //adicionar_evit(View, R.id.lista, R.id.nova_composicao, evitaveis, adaptador)
            val txt_novo_item = this.findViewById<AppCompatAutoCompleteTextView>(R.id.nova_composicao).text.toString().trim()

            if (txt_novo_item.isEmpty()){
                Toast.makeText(this, "Digite algo", Toast.LENGTH_SHORT).show()
            }else {
                //verifica se item já esta na lista
                if (!evitaveis.contains(txt_novo_item)) {
                    //se não estiver na lista,
                    //adiciona item
                    evitaveis.add(txt_novo_item)

                    //atualizando lista
                    this.findViewById<ListView>(R.id.lista)!!.adapter = adaptador

                    //limpando entrada de texto
                    this.findViewById<AppCompatAutoCompleteTextView>(R.id.nova_composicao).setText("")
                } else {
                    Toast.makeText(this, "ingredientes já existente", Toast.LENGTH_SHORT).show()
                }

            }
        }

        //quando clicar em ENVIAR
        enviar!!.setOnClickListener {
            if (evitaveis.isEmpty()) {
                Toast.makeText(this, "Por Favor, selecione ao menos 1", Toast.LENGTH_LONG).show()
            } else {
                var user = Usuario(applicationContext,auth, nome,email,senha, data_nascimento, evitaveis)
                user.cadastrar()
//                //CHANGE HERE
//                val intent = Intent(this,MainActivity::class.java)
//                this.startActivity(intent)
            }


        }
    }





}
