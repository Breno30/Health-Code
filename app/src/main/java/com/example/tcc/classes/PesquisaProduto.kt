package com.example.tcc.classes

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PesquisaProduto {
    private var v:View
    private var context:Context
    private var numIdListView:Int
    private var limite:Int


    constructor(v: View,  numId:Int, limite:Int) {
        this.v = v
        this.context = v.context
        this.numIdListView = numId
        this.limite = limite

    }
    fun pesquisar(myCallback: MyCallback, textoPesquisa: String){

        pesquisar(object :MyCallback(){
            override fun onCallback(value:DataSnapshot){
                myCallback.onCallback(value)
            }
        }
           ,context, textoPesquisa,limite)
    }


}


private fun pesquisar(myCallback: MyCallback, context: Context, textoPesquisa: String,limite:Int){

    val valuelistener2 = object : ValueEventListener {

        override fun onDataChange(param1: DataSnapshot) {
            //retorna resultados
            myCallback.onCallback(param1)

        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
        }
    }
    //pesquisa produto no firebase
    var texto = textoPesquisa.toLowerCase()
    var database_his = FirebaseDatabase.getInstance().reference.child("produtos")
    database_his.orderByChild("Nome").startAt(texto).endAt("$texto\uf8ff").limitToFirst(limite).addValueEventListener(valuelistener2)

}