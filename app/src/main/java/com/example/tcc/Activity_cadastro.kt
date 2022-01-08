package com.example.tcc

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tcc.funcoes.data_correta
import com.example.tcc.funcoes.normalizar_data
import kotlinx.android.synthetic.main.activity_cadastro_usuario.*

class Activity_cadastro : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_usuario)


        //muda texto da barra superior
        supportActionBar!!.title = "Cadastro"


        //quando a data perder o foco
        data_de_nascimento_texto2!!.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            val data_usuario = data_de_nascimento_texto2!!.text.toString().trim()
            if (!hasFocus) {
                data_de_nascimento_texto2!!.setText(normalizar_data(data_usuario))
            }
        }

        //quando senha perder o foco
        senha2!!.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                //se não tiver foco e tiver menos de 8 caracteres
                if (!hasFocus && senha_texto2.text.toString().trim().length < 8) {
                    Toast.makeText(
                            applicationContext,
                            "Senha com no mínimo 8 caracteres",
                            Toast.LENGTH_SHORT).show()

                }
            }


        //quando botão "feito" do teclado for pressionado
        senha_confirmacao_texto2.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                verificar_dados()
                true
            }else{
                false
            }
        }

        //proxima pagina de cadastro
        cadastro2!!.setOnClickListener {
            verificar_dados()
         }



    }

    fun verificar_dados(){

        //DECLARANDO VARIAVEIS
        val nome = nome_texto2.text.toString().trim()
        val data = data_de_nascimento_texto2.text.toString().trim()
        val email = email_texto2.text.toString().trim()
        val senha = senha_texto2.text.toString().trim()
        val senha_confir = senha_confirmacao_texto2.text.toString().trim()



        //VERIFICANDO DADOS ANTES DE ENVIAR
        if (    nome.isEmpty() || email.isEmpty()){
            //se email ou senha forem vazios
            Toast.makeText(this, "Por Favor, digite todos os  campos", Toast.LENGTH_LONG).show()
        }else {
            if (senha != senha_confir){
                Toast.makeText(this, "Digite as duas senhas iguas\n", Toast.LENGTH_LONG).show()

            } else {
                if (senha.length<8){
                    //se senhas nao estiverem corretas
                    Toast.makeText(this, "Senhas de no mínimo 8 caracteres\n", Toast.LENGTH_LONG).show()
                }else{
                    //se senhas estiverem corretas
                    if (!data_correta(data)) {
                        //se a data estiver incorreta
                        Toast.makeText(this, "digite a data corretamente", Toast.LENGTH_LONG).show()
                    } else {

                        //se tudo estiver corretamente preenchido, envia dados para segunda tela
                        enviar_dados_segunda_tela(nome, data, email, senha)

                    }
                }

            }

        }
    }

    fun enviar_dados_segunda_tela(nome: String, data: String, email: String, senha: String){
        //PASSANDO DADOS PARA SEGUNDA TELA
        val intent = Intent(this, Activity_Cadastro_2::class.java)

        intent.putExtra("nome", nome)
        intent.putExtra("data", data)
        intent.putExtra("email", email)
        intent.putExtra("senha", senha)
        startActivity(intent)
    }




}