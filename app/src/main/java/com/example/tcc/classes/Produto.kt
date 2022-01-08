package com.example.tcc.classes

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable
import com.example.tcc.Activity_gavetas
import com.example.tcc.MainActivity
import com.example.tcc.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import android.app.Activity




class Produto {

    private var c:Context
    private var nome: String
    private var codigo: String
    private var marca: String
    private var uploader: String
    private var ingred: MutableList<String>
    private var img:ImageView

    constructor(c:Context, nome:String, codigo:String,marca:String,uploader:String, ingred:MutableList<String>, img:ImageView){
        this.c =c
        this.nome = nome
        this.codigo = codigo
        this.marca = marca
        this.uploader = uploader
        this.ingred = ingred
        this.img = img
    }

    fun cadastrarProduto(){
        cadastrarProduto(c,codigo,nome,marca,uploader,ingred,img)
    }


}

fun cadastrarProduto( c:Context, codigo:String,  nome: String,  marca: String,uploader:String,  ingred:MutableList<String>, img: ImageView){
    //adiciona ingredientes
    val ref = FirebaseDatabase.getInstance().reference.child("produtos/"+codigo)

    //adiciona dados
    var dados                       = HashMap<String,Any>()
    dados["Nome"]                   = nome.toLowerCase()
    dados["Marca"]                  = marca.toLowerCase()
    dados["Ingredientes"]           = ingred
    dados["eviado_pelo_usuario"]    = uploader
    enviar_imagem(c, img, ref, codigo)
    ref.setValue(dados)


}

fun enviar_imagem(c: Context, img: ImageView, ref: DatabaseReference, codigo: String) {

    //colhendo bitmap do ImageView que contém a foto
    val bmp = img.drawable.current.toBitmap()

    //convertendo para ByteArray
    val stream = ByteArrayOutputStream()
    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val byteArray = stream.toByteArray()

    val caminho_firebase = Firebase.storage.reference.child("imagens_produtos/${codigo}")
    caminho_firebase.putBytes(byteArray)

    //colhendo bitmap do ImageView que contém a foto

   // val bmp = (img.drawable.current as GlideBitmapDrawable).bitmap





    //upload imagem
    caminho_firebase.putBytes(byteArray)
        .addOnSuccessListener {
            //pegando link da imagem

            caminho_firebase.downloadUrl.addOnSuccessListener {
                //caso link tenha funcionado, quardar link da imagem na ficha do produto

                ref.child("Img").setValue(it.toString()).addOnSuccessListener {

                    //caso tenha guardado link, guardar email do usuario que fez o upload
                            Toast.makeText(c, "Produto Cadastrado com Sucesso", Toast.LENGTH_LONG).show()
                            //voltando para leitor
                            val home = Intent(c, MainActivity::class.java)
                            home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            c.startActivity(home)
                            (c as Activity?)?.finish()


                }
            }

        }


}