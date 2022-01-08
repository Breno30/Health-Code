package com.example.tcc.classes

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.text.Layout
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.StrictMode
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.tcc.R
import com.example.tcc.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject
import pl.droidsonroids.gif.GifImageView
import java.lang.reflect.Method
import java.net.URL
import android.graphics.drawable.Drawable
import androidx.annotation.Nullable
import androidx.core.graphics.alpha
import com.bumptech.glide.request.target.Target


//https://tccprojeto-9cd31-default-rtdb.firebaseio.com/produtos/.json

class dadosProduto {

    private var v:View
    private var num_id:Int
    private var codigo:String
    private var inseguro:Boolean = false
    private lateinit var nome_prod:String
    private lateinit var nome_marca:String
    private lateinit var ingredientes:String
    private lateinit var img_prod:String
    private lateinit var activity: Activity

    constructor(v: View, num_id: Int, codigo: String, activity: Activity){
        this.v = v
        this.num_id = num_id
        this.codigo = codigo
        this.activity = activity
    }
    fun dados_do_produto_com_codigo(){
        var infos:HashMap<String,Any> = HashMap()

        dados_do_produto_com_codigo(object: MyCallback() {
            override fun onCallback(value: HashMap<String,Any>) {
                infos = value
                inseguro = infos["inseguro"] as Boolean
                nome_prod = infos["nomeProd"] as String
                nome_marca = infos["nomeMarca"] as String
                ingredientes = infos["ingredientes"] as String
                img_prod = infos["imgProd"] as String
                criar_quadrado(v,num_id,codigo,inseguro,nome_prod,nome_marca,ingredientes,img_prod)
            }
        },v,activity, codigo)


    }

}

open class MyCallback {
    open fun onCallback(value: HashMap<String,Any>){
    }
    open fun onCallback(value: DataSnapshot){
    }
}


fun dados_do_produto_com_codigo(myCallback : MyCallback,compat: View, activity:Activity, codigo: String){


    val BancoDeDados = activity.getSharedPreferences("label", 0).getString("banco", "null")
    val BancoDeEvitaveis = activity.getSharedPreferences("label", 0).getString("evitaveis", "null")

    val banco_de_dados = JSONObject(BancoDeDados)
    val evitavies_usuario = BancoDeEvitaveis!!.replace("[","").replace("]","").replace('"','\u0000').split(",")


   var inseguro = false


    var nome_prod: String = banco_de_dados.getJSONObject(codigo).get("Nome").toString()

    var img_prod: String = banco_de_dados.getJSONObject(codigo).get("Img").toString()

    var nome_marca: String = banco_de_dados.getJSONObject(codigo).get("Marca").toString()

    var string_ingredientes = banco_de_dados.getJSONObject(codigo).get("Ingredientes").toString()
    var array_ingredientes = string_ingredientes.replace("[","").replace("]","").replace('"','\u0000').split(",")

    //criando variavel com ingredientes
    var ingredientes_noc = ""
    var ingredientes_seg = ""
    var seguro: Boolean

    for (ingred in array_ingredientes) {
        seguro = true

        for (evitavel in evitavies_usuario) {
            //adiciona ingrediente a string html, em vermelho
            if (ingred.toUpperCase().contains(evitavel.toUpperCase())) {
                ingredientes_noc += "<font color=#dd8888> - ${ingred.toUpperCase()}<br><br> </font>"
                seguro = false
                inseguro = true
            }

        }

        //adiciona ingrediente a string html, sem cor
        if (seguro){
            //adiciona ingrediente seguro a string
            ingredientes_seg += " - ${ingred.toUpperCase()}<br><br>"
        }


    }

    //mostra os nocivos primeiro
    var ingredientes = ingredientes_noc+ingredientes_seg



    //criando layout com todas informações
    var infos:HashMap<String,Any> = HashMap()
    infos["ingredientes"] = ingredientes
    infos["inseguro"] = inseguro
    infos["nomeProd"] = nome_prod
    infos["nomeMarca"] = nome_marca
    infos["imgProd"] = img_prod

    //retorna valores
    myCallback.onCallback(infos)


}


fun tem_internet(c:Context): Boolean {
    val connectivityManager = c?.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    // In here we return true if network is not null and Network is connected
    return networkInfo != null && networkInfo.isConnected

}


fun atualizar_banco_local(activity: Activity){


    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)

    //PEGA TODOS PRODUTOS DO FIREBASE E COLOCA NA MEMORIA
    val mPrefs = activity.getSharedPreferences("label", 0).edit()
    mPrefs.putString("banco", URL("https://tccprojeto-9cd31-default-rtdb.firebaseio.com/produtos/.json").readText()).commit()

}
fun atualizar_evitaveis_local(activity: FragmentActivity, id_user:String){

    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)

    //PEGA TODOS PRODUTOS DO FIREBASE E COLOCA NA MEMORIA
    val mPrefs = activity.getSharedPreferences("label", 0).edit()
    mPrefs.putString("evitaveis", URL("https://tccprojeto-9cd31-default-rtdb.firebaseio.com/usuarios/$id_user/evitaveis/.json").readText()).commit()

}



fun criar_quadrado(v: View, num_id: Int, frase_topo: String, inseguro:Boolean,nome: String, marca: String, ingred: String, img_url: String){
    var altura_tela =  Resources.getSystem().displayMetrics.heightPixels
    var largura_tela = Resources.getSystem().displayMetrics.widthPixels

    val ll: LinearLayout = v.findViewById(num_id)

    ll.addView( criar_frase_topo_textview(v.context, frase_topo))

    ll.addView(criar_nome_textview(v.context,inseguro,nome,marca) )

    var prod_img = criar_img(v.context, largura_tela)



    Glide.with(v.context)
        .load(img_url)
        .error(R.drawable.sem_foto_produto)
        .override(100, 100)
        .into(prod_img)

    ll.addView(prod_img)

    var sc = criar_sc(v.context,altura_tela,largura_tela)
    ll.addView(sc)

    ll.addView(criar_ingred_textview(v.context,sc,ingred) )

    ll.addView( criarEspaco(v.context,altura_tela) )

    adiciona_imagem(v.context,img_url, prod_img)


}


fun adiciona_imagem(c: Context?, img_url: String,prodImg: GifImageView?) {
    Glide.with(c)
            .load(img_url)
            .error(R.drawable.sem_foto_produto)
            .override(500, 500)
            .into(prodImg)

}

fun criar_ingred_textview(c:Context, sc:NestedScrollView, ingred: String): TextView? {
    val ingred_seg_textview = TextView(c)
    ingred_seg_textview.textSize = 20F
    ingred_seg_textview.text = "Ver Ingredientes"
    ingred_seg_textview.textAlignment = View.TEXT_ALIGNMENT_CENTER
    ingred_seg_textview.setTextColor(Color.GRAY)
    //se clicar, mudar texto do botao para ingredientes
    ingred_seg_textview.setOnClickListener {

        //layout
        val sub_ll = LinearLayout(c)
        sub_ll.orientation = LinearLayout.VERTICAL
        //sub_ll.background = ContextCompat.getDrawable(v.context, R.drawable.gradiente)


        //todos ingredientes
        val list_ingre = TextView(c)
        list_ingre.textSize = 19F
        list_ingre.text = HtmlCompat.fromHtml(ingred, HtmlCompat.FROM_HTML_MODE_LEGACY)
        list_ingre.setTextColor(Color.GRAY)

        //adiciona seguros a ScrollView
        sub_ll.addView(list_ingre)


        //adiciona linearlayout ao scrollview
        sc?.addView(sub_ll)


        //esconde texto e mostra scrollview
        ingred_seg_textview.isVisible = false
        //começa a descer lentamente, velocidade de acordo com altura
       // sc?.post(Runnable { sc.smoothScrollTo(0,sub_ll.measuredHeight,sub_ll.measuredHeight*22) })
        //aparece para o usúario
        sc?.isVisible = true
    }
    return ingred_seg_textview
}

fun criar_sc(c:Context, altura_tela: Int, largura_tela: Int): NestedScrollView {
    val sc = NestedScrollView(c)

    //sc.setPadding(0,0,0,(altura_tela*0.3).toInt())
    sc.layoutParams = LinearLayout.LayoutParams(
            //largura 90%
            (largura_tela * 0.9).toInt(),
            //altura de 20% da tela
            (altura_tela * 0.2).toInt()
    )
    //centraliza scrollview
    sc.setPadding((largura_tela * 0.1).toInt(), 10, 0, 10)
    //começa invisivel
    sc.isVisible = false
//SCROLL
    return sc
}

fun criar_img(c:Context, largura_tela:Int): GifImageView? {
    val prod_img = GifImageView(c)
    prod_img.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            (largura_tela*0.5).toInt()
    )
    //prod_img.background = ContextCompat.getDrawable(c,R.drawable.borda_produtos)
    //adiciona imagem de carregando
    prod_img.setImageResource(R.drawable.loading3)
    return prod_img
}

fun criar_nome_textview(c:Context, inseguro: Boolean, nome: String, marca: String): View {
    //cria textview com NOME
    val nome_textview = TextView(c)
    nome_textview.textSize = 25F
    nome_textview.setTextColor(Color.GRAY)
    //caso seja inseguro, texto vermelho
    if (inseguro) {
        nome_textview.text = "$nome \n$marca\n(Inseguro para consumo)"
        nome_textview.setTextColor(0xffdd8888.toInt())

    }else{
        nome_textview.text = "$nome \n$marca\n(Seguro para consumo)"
        nome_textview.setTextColor(0xff6baf92.toInt())

    }
    nome_textview.textAlignment = View.TEXT_ALIGNMENT_CENTER
    return nome_textview

}

fun criar_frase_topo_textview(c:Context, frase_topo: String): View {
    //cria textview com CÓDIGO
    val frase_topo_textview = TextView(c)
    frase_topo_textview.textSize = 20F
    //frase_topo_textview.setTextColor(0xff60a0e0.toInt())
    frase_topo_textview.text = "\n$frase_topo"
    frase_topo_textview.setPadding(15, 0, 15, 0)
    frase_topo_textview.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
    frase_topo_textview.setTextColor(Color.GRAY)
    return frase_topo_textview

}

fun criarEspaco(c: Context, altura_tela:Int): View {
    var esp = View(c)
    esp.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            //altura de 35% da tela
            (altura_tela * 0.35).toInt()
    )
    return esp


}
