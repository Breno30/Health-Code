package com.example.tcc.ui.home

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.bumptech.glide.Glide
import com.example.tcc.*
import com.example.tcc.classes.Produto
import com.example.tcc.funcoes.adicionar_evit
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_cadastro_produto.*
import java.io.ByteArrayOutputStream
import android.graphics.drawable.BitmapDrawable
import android.graphics.BitmapFactory
import com.example.tcc.classes.atualizar_banco_local


var email_usuario: String = "Não foi possível encontrar Email"
var selecinou_imagem = false

class CadastroProduto : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_produto)

        supportActionBar?.hide()

        val img:ImageView = findViewById(R.id.imagem_produto)
        val adic_prod:Button = findViewById(R.id.adic_cadast_prod)
        val cancelar:Button = findViewById(R.id.cancelar_cadast_prod)
        val cadastrar:Button = findViewById(R.id.cadastrar_prod)
        val lista_cadast:ListView = findViewById(R.id.lista_cadast_prod)


        var codigo = intent.getStringExtra("codigo")


        //adiciona codigo na parte superior
        var cod:TextView = findViewById(R.id.cd)

        cod.text = codigo

        val evitaveis_cadast_prod: MutableList<String> = ArrayList()

        val adaptador_cadast_prod:ArrayAdapter<*> = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                evitaveis_cadast_prod
        )

        val adaptador_recomendacoes:ArrayAdapter<*> = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                evitaveis_lista
        )

        nova_composicao_cadast_prod.setAdapter(adaptador_recomendacoes)



        adic_prod.setOnClickListener {

            val txt_novo_item = this.findViewById<AppCompatAutoCompleteTextView>(R.id.nova_composicao_cadast_prod).text.toString().trim()

            if (txt_novo_item.isEmpty()){
                Toast.makeText(this, "Digite algo", Toast.LENGTH_SHORT).show()
            }else {
                //verifica se item já esta na lista
                if (!evitaveis_cadast_prod.contains(txt_novo_item)) {
                    //se não estiver na lista,
                    //adiciona item
                    evitaveis_cadast_prod.add(txt_novo_item)

                    //atualizando lista
                    this.findViewById<ListView>(R.id.lista_cadast_prod).adapter = adaptador_cadast_prod

                    //limpando entrada de texto
                    this.findViewById<AppCompatAutoCompleteTextView>(R.id.nova_composicao_cadast_prod).setText("")
                } else {
                    Toast.makeText(this, "ingredientes já existente", Toast.LENGTH_SHORT).show()
                }

            }
        }

        //quando clicar na lista, apaga item
        lista_cadast.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView, view, posicao, arg3 -> //alerta

                    //removendo posicao
                    evitaveis_cadast_prod.removeAt(posicao)

                    //atualizando lista
                    lista_cadast!!.adapter = adaptador_cadast_prod
                }


        cadastrar.setOnClickListener {
            //verifica se o codigo veio corretamente
            if (codigo != null) {
                salvar(img, codigo, evitaveis_cadast_prod)
            }
        }

        cancelar.setOnClickListener {
            //voltando para leitor
            val home = Intent(this, Activity_gavetas::class.java)
            startActivity(home)
            finish()
        }


        img.setOnClickListener {

            selecinou_imagem = true
            if (auth!=null){
                email_usuario = auth.currentUser.email.toString()
            }

            val galeria_intent = Intent(Intent.ACTION_GET_CONTENT, null)
            galeria_intent.type = "image/*"

            val camera_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val escolha = Intent(Intent.ACTION_CHOOSER)
            escolha.putExtra(Intent.EXTRA_INTENT, galeria_intent)
            escolha.putExtra(Intent.EXTRA_TITLE, "Selecione a Imagem do Produto:")

            val intentArray = arrayOf(camera_intent)
            escolha.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
            startActivityForResult(escolha, 111)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val img: ImageView = findViewById(R.id.imagem_produto)

        try {
            //TENTA LER COMO FOTO DA CÂMERA
            val extras: Bundle = data?.extras!!
            val bmp = extras["data"] as Bitmap?

            val stream = ByteArrayOutputStream()
            bmp?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            Glide.with(this)
                .load(stream.toByteArray())
                .into(img)
        }catch (e:Exception){
            //LÊ COMO IMAGEM DA GALERIA
            Glide.with(this)
                .load(data?.data)
                .into(img)
        }

    }

    fun salvar(img: ImageView, codigo: String, ingredientes: MutableList<String>){

        var nome = nome_prod_txt.text.toString().trim()

        var marca = marca_prod_texto_txt.text.toString().trim()

        //se tiver data de nascimento e não estiver correta, avisar
        if (marca.isEmpty()){
            Toast.makeText(this, "Digite Marca, Por favor", Toast.LENGTH_LONG).show()
        }else {

            //se nome for vazio, avisa
            if (nome.isEmpty()) {
                Toast.makeText(this, "Digite o nome do Produto, Por favor", Toast.LENGTH_LONG).show()
            } else {

                //se usuario nao preencheu nada
                if (ingredientes.isEmpty()){
                    Toast.makeText(this, "Digite selecione ao menos 1 Ingrediente, Por favor", Toast.LENGTH_LONG).show()
                }else{


                        var prod = Produto(applicationContext, nome,codigo,marca,email_usuario,ingredientes,img)
                        prod.cadastrarProduto()



                }


            }
        }

    }
}
