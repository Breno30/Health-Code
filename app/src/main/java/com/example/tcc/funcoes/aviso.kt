package com.example.tcc.funcoes

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import com.example.tcc.R
import com.example.tcc.ui.home.CadastroProduto
import com.google.android.material.button.MaterialButton

@SuppressLint("ResourceAsColor")
fun aviso(v: View, num_id:Int, texto:String){
    val base_hist:LinearLayout = v.findViewById(num_id)
    base_hist.removeAllViews()
    val aviso = TextView(v.context)
    aviso.textSize = 25F
    aviso.setPadding(0,500,0,0)
    aviso.text = texto
    aviso.textAlignment = View.TEXT_ALIGNMENT_CENTER
    aviso.setTextColor(Color.GRAY)
    base_hist.addView(aviso)
}

fun botao(v: View, num_id:Int, texto:String, codigo:String){

    val base_hist: ConstraintLayout = v.findViewById(num_id)
    //faz um botão com a opção de cadastro
    val botao_aceitar = MaterialButton(v.context)

    var altura_tela = Resources.getSystem().displayMetrics.heightPixels.toFloat()
    var comprimento_tela = Resources.getSystem().displayMetrics.widthPixels.toFloat()

    //altura do botão será 70%
    botao_aceitar.y = (altura_tela * 0.7).toFloat()
    //centraliza botão
    botao_aceitar.width = (comprimento_tela * 0.8).toInt()
    botao_aceitar.x = (comprimento_tela * 0.1).toFloat()

    botao_aceitar.text = texto

    //ao clicar abrirá tela de cadastro do produto, mandando o código
    botao_aceitar.setOnClickListener {
        var intent_cadastrar_produto = Intent(v.context, CadastroProduto::class.java)
        intent_cadastrar_produto.putExtra("codigo", codigo)
        v.context.startActivity(intent_cadastrar_produto)
    }
    base_hist.addView(botao_aceitar)



}