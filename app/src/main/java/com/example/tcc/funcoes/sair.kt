package com.example.tcc.funcoes

import android.content.Context
import android.content.Intent
import com.example.tcc.MainActivity
import com.example.tcc.auth
import com.example.tcc.googleSignInClient

fun sair(c:Context){
    //saindo da conta
    auth.signOut()
    googleSignInClient.signOut()

    //saindo após excluir
    var sair = Intent(c, MainActivity::class.java)
    c.startActivity(sair)


}
