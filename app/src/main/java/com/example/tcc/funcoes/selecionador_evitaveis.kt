package com.example.tcc.funcoes

import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

fun adicionar_evit(v: View, num_id_list:Int, num_id_nova_comp:Int, evitaveis:MutableList<String>, adaptador: ArrayAdapter<*>) {
    val txt_novo_item = v.findViewById<AppCompatAutoCompleteTextView>(num_id_nova_comp).text.toString().trim()

    if (txt_novo_item.isEmpty()){
        Toast.makeText(v.context, "Digite algo", Toast.LENGTH_SHORT).show()
    }else {
        //verifica se item já esta na lista
        if (!evitaveis.contains(txt_novo_item)) {
            //se não estiver na lista,
            //adiciona item
            evitaveis.add(txt_novo_item)

            //atualizando lista
            v.findViewById<ListView>(num_id_list)!!.adapter = adaptador

            //limpando entrada de texto
            v.findViewById<AppCompatAutoCompleteTextView>(num_id_nova_comp).setText("")
        } else {
            Toast.makeText(v.context, "ingredientes já existente", Toast.LENGTH_SHORT).show()
        }

    }
}

