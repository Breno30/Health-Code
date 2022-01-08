package com.example.tcc.funcoes

import android.content.Context
import android.widget.Toast
import com.example.tcc.auth

fun recuperar_senha(email_reset:String, cont:Context){
    if (email_reset.isEmpty()){
        //se o email for vazio
        Toast.makeText(cont, "Por Favor, digite o email", Toast.LENGTH_LONG).show()
    }else {
        //se o email estiver preenchido
        auth.sendPasswordResetEmail(email_reset)
                //verificando se foi efeituado com sucesso
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        Toast.makeText(cont, "email para enviado para \n$email_reset", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(cont, "ERROR\nemail não cadastrado\n" + task.exception, Toast.LENGTH_LONG).show()
                    }
                }
    }
}