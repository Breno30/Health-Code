package com.example.tcc.classes

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.tcc.R
import com.example.tcc.ui.home.Activity_infos_produto
import com.google.firebase.database.DataSnapshot
import pl.droidsonroids.gif.GifImageView

class ResultadoPesquisa {
    private var view: View
    private var value: DataSnapshot

    constructor(v: View, infos: DataSnapshot) {
        this.view = v
        this.value = infos
    }

    fun montarTela(activity: FragmentActivity?): LinearLayout {
        val viewpesquisa = LinearLayout(view.context)
        viewpesquisa.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT

        )
        viewpesquisa.orientation = LinearLayout.VERTICAL

        for (child in value.children) {
            var nomeProd = child.child("Nome").value.toString()
            var imgProd = child.child("Img").value.toString()
            var codigoProd: String = child.key.toString()


            val viewEsquerda = criar_ladoEsquerdo(activity, codigoProd, imgProd)
            val viewDireita = criar_ladoDireito(nomeProd)

            viewEsquerda.addView(viewDireita)

            viewpesquisa.addView(viewEsquerda)

        }

        return viewpesquisa
    }

    private fun criar_ladoDireito(nomeProd: CharSequence): LinearLayout {
        val nome = TextView(view.context)
        nome.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        nome.textSize = 20F
        nome.text = nomeProd
        nome.setTextColor(Color.GRAY)

        val llCantoDireito = LinearLayout(view.context)
        val llCantoDireitoPar = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llCantoDireitoPar.setMargins(40, 0, 10, 10)
        llCantoDireito.orientation = LinearLayout.VERTICAL
        llCantoDireito.layoutParams = llCantoDireitoPar
        llCantoDireito.addView(nome)

        return llCantoDireito

    }

    private fun criar_ladoEsquerdo(activity: FragmentActivity?, codigoProd: String, imgProd: String): LinearLayout {
        val llCantoEsquerdo = LinearLayout(view.context)
        llCantoEsquerdo.orientation = LinearLayout.HORIZONTAL
        llCantoEsquerdo.setPadding(30)
        llCantoEsquerdo.isClickable = true
        llCantoEsquerdo.setOnClickListener {
            //ao clicar, abre paginba do produto
            val intent = Intent(view.context, Activity_infos_produto::class.java)
            intent.putExtra("codigo", codigoProd)
            activity?.startActivity(intent)
        }
        val llCantoEsquerdoPar = LinearLayout.LayoutParams(

                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llCantoEsquerdoPar.setMargins(10, 10, 10, 10)
        llCantoEsquerdo.layoutParams = llCantoEsquerdoPar

        val foto = GifImageView(view.context)
        foto.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        foto.layoutParams = LinearLayout.LayoutParams(
                100,100

        )
        //adiciona imagem
        Glide.with(view.context)
                .load(imgProd)
                .error(R.drawable.sem_foto_produto_pesquisa)
                .override(100, 100)
                .into(foto)
        llCantoEsquerdo.addView(foto)

        return llCantoEsquerdo
    }
}

