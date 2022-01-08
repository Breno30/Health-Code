package com.example.tcc.ui.historico

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.example.tcc.R
import com.example.tcc.auth
import com.example.tcc.classes.dadosProduto
import com.example.tcc.classes.tem_internet
import com.example.tcc.funcoes.aviso
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import pl.droidsonroids.gif.GifImageView


class HistoricoFragment : Fragment() {


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_historico, container, false)

        //repetir a cada 3 segundo
//        val mainHandler = Handler(Looper.getMainLooper())
//        mainHandler.post(object : Runnable {
//            override fun run() {

                if (tem_internet(root.context)) {

                    var database_hist = FirebaseDatabase.getInstance().reference.child("usuarios/${auth.currentUser?.uid}/historico")
                    val deletar_tudo: FloatingActionButton = root.findViewById(R.id.deletar_todo_historico)


                    deletar_tudo.setOnClickListener {
                        database_hist.removeValue()
                            .addOnSuccessListener {
                                //mostra aviso de histórico vazio
                                aviso(root, R.id.historico_caixas, "Histórico vazio")

                            }
                    }

                    //pegando codigos do historico
                    montando_tela_historico(root, database_hist)
                }else{
                    aviso(root, R.id.historico_caixas, "Por favor, \nConecte-se à Internet\n e tente novamente")
                    val scroll_hist:NestedScrollView = root.findViewById(R.id.scrollView_historico)
                    scroll_hist.isVisible = true
                }

//                mainHandler.postDelayed(this, 3000)
//            }
//        })




        return root
    }

    fun montando_tela_historico(root: View, database: DatabaseReference) {

        database.get()
        .addOnSuccessListener { it ->
            if (it.childrenCount.toInt() == 0) {
                //nenhum produto lido ainda
                aviso(root, R.id.historico_caixas, "Histórico vazio")
            } else {
                var limiteProdutoHistorico:Int = 10
                var cont = 0
                val invertido = it.children.reversed()
                for ( dt in invertido) {
                    //se for um dos 10 ultimos lidos, mostra
                    if (cont < limiteProdutoHistorico){
                        var dados = dadosProduto(root, R.id.historico_caixas, dt.value.toString(),requireActivity())
                        dados.dados_do_produto_com_codigo()

                    }else{
                        // se não, excluir
                        database.child(dt.key.toString()).removeValue()
                    }
                    cont++
                }
            }

            mostrar_lista_carregada(root)


        }
        .addOnFailureListener { it->
            Toast.makeText(context, "erro firebase historico...\n$it", Toast.LENGTH_LONG).show()
        }
    }

    private fun mostrar_lista_carregada(v: View) {
        val carregamento:GifImageView = v.findViewById(R.id.gif_carregamento)
        val btn_del: FloatingActionButton = v.findViewById(R.id.deletar_todo_historico)
        val scroll_hist:NestedScrollView = v.findViewById(R.id.scrollView_historico)

        //esconde gif carregamento
        carregamento.isVisible = false
        //mostra botão deletar histórico
        btn_del.isVisible = true
        //mostra histórico
        scroll_hist.isVisible = true

    }
}
