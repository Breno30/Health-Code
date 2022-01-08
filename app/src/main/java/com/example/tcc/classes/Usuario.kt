package com.example.tcc.classes

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.tcc.MainActivity
import com.example.tcc.auth
import com.example.tcc.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Usuario {
    //verifica se o usuario escreveu ao menos 1

    private var c: Context
    private var auth: FirebaseAuth
    private var senha: String
    private var nome: String
    private var email: String
    private var nasc: String
    private var evit: MutableList<String>

    constructor(c: Context, auth: FirebaseAuth, nome: String, email: String, senha: String, nasc: String, evit: MutableList<String>) {
        this.c = c
        this.auth = auth
        this.senha = senha
        this.nome = nome
        this.email = email
        this.nasc = nasc
        this.evit = evit
    }

    fun cadastrar(){
        cadastrar(c,auth,nome,email,senha,nasc,evit)

    }
}




fun cadastrar(c:Context, auth:FirebaseAuth, nome:String,email:String,senha:String,  data_nascimento:String,  evitaveis:MutableList<String>){
    //caso for cadastro manual, cadastrar no firebase
    if (auth.currentUser?.uid == null) {
        auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener {
                    val ref = FirebaseDatabase.getInstance().reference.child("usuarios/"+ com.example.tcc.auth.currentUser?.uid)
                    cadastrar_dados_adicionais(c,ref,nome, data_nascimento, evitaveis)
                }
    } else {
        val ref = FirebaseDatabase.getInstance().reference.child("usuarios/"+ com.example.tcc.auth.currentUser?.uid)
        cadastrar_dados_adicionais(c,ref,nome, "", evitaveis)
    }

}

fun cadastrar_dados_adicionais(c:Context,ref:DatabaseReference, nome: String, data_nasci:String, evit: MutableList<String>){

    //enviando nome para firebase
    var dados = HashMap<String,Any>()
    dados["nome"] = nome
    dados["data_de_nascimento"]=data_nasci
    dados["evitaveis"] = evit
   ref.setValue(dados)

    //volta para tela inicial, logando
    val intent_tela_gavetas = Intent(c, MainActivity::class.java)
    intent_tela_gavetas.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(c,intent_tela_gavetas,null)


}