package com.example.tcc.ui.edit_cadastro

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.tcc.R
import com.example.tcc.auth
import com.example.tcc.classes.atualizar_evitaveis_local
import com.example.tcc.classes.tem_internet
import com.example.tcc.evitaveis_lista
import com.example.tcc.funcoes.*
import com.example.tcc.googleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_edit_cadastro.*


val evitaveis_edit: MutableList<String> = ArrayList()


class Edit_CadastroFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_edit_cadastro, container, false)

        val salva: Button = root.findViewById(R.id.salvar)
        val recuperar: Button = root.findViewById(R.id.recuperar)
        val del_conta: Button = root.findViewById(R.id.deletar_conta)
        val adic_edit: Button = root.findViewById(R.id.adic_edit)
        val lista_edit:ListView = root.findViewById(R.id.lista_edit)
        val barra_adic:AppCompatAutoCompleteTextView = root.findViewById(R.id.nova_composicao_edit)


        //indicando caminho no firebase
        var database_nome = FirebaseDatabase.getInstance().reference.child("usuarios/${auth.uid}/nome")
        var database_data = FirebaseDatabase.getInstance().reference.child("usuarios/${auth.uid}/data_de_nascimento")
        var database_evitaveis = FirebaseDatabase.getInstance().reference.child("usuarios/${auth.uid}/evitaveis")


        val adaptador_lista = ArrayAdapter(
            root.context,
            android.R.layout.simple_list_item_1,
            evitaveis_edit
        )


        val adaptador_recomendacoes_edit: ArrayAdapter<*> = ArrayAdapter(
            root.context,
            android.R.layout.simple_list_item_1,
            evitaveis_lista
        )


        ler_cadastro(database_nome, database_data, database_evitaveis, adaptador_lista, lista_edit)

        //configurando recomendações na caixa de texto
        barra_adic.setAdapter(adaptador_recomendacoes_edit)



        //quando clicar na lista, apaga item
        lista_edit.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView, view, posicao, arg3 -> //alerta

                    //removendo posicao
                    evitaveis_edit.removeAt(posicao)

                    //atualizando lista
                    lista_edit!!.adapter = adaptador_lista
                }


        //adciona quando clicar no botão +
        adic_edit.setOnClickListener {
            adicionar_evit(
                root,
                R.id.lista_edit,
                R.id.nova_composicao_edit,
                evitaveis_edit,
                adaptador_lista
            )
        }

        salva.setOnClickListener {
            if (tem_internet(root.context)) salvar(database_nome, database_data, database_evitaveis)
            else Toast.makeText(root.context, "Por favor, Conecte-se à Internet", Toast.LENGTH_SHORT).show()
        }

        recuperar.setOnClickListener {
            if (tem_internet(root.context)) recuperar_senha(auth.currentUser?.email.toString(), root.context)
            else Toast.makeText(root.context, "Por favor, Conecte-se à Internet", Toast.LENGTH_SHORT).show()

        }

        del_conta.setOnClickListener {
            if (tem_internet(root.context)) verifica_qual_metodo_login(root.context)
            else Toast.makeText(root.context, "Por favor, Conecte-se à Internet", Toast.LENGTH_SHORT).show()

        }

        return root
    }

    private fun verifica_qual_metodo_login(c:Context) {

        //se for logado com firebase
        if (auth.currentUser.providerData.last().providerId == EmailAuthProvider.PROVIDER_ID) mostrar_aviso_firebase(c)
        else mostrar_aviso_google(c)

    }

    private fun mostrar_aviso_firebase(c:Context){
        var txt_senha:String
        val alerta = android.app.AlertDialog.Builder(context)
        alerta.setTitle("Digite sua senha para deletar essa conta")
        //constroi input
        val senha = EditText(context)
        senha.inputType = InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
        senha.textAlignment = View.TEXT_ALIGNMENT_CENTER
        alerta.setView(senha)
        //deletar conta
        alerta.setPositiveButton("DELETAR", DialogInterface.OnClickListener { dialog, which ->
            //ao  clicar, reauntentica
            txt_senha = senha.text.toString()
            var credencial = EmailAuthProvider.getCredential(auth.currentUser.email, txt_senha)
            reautenticar(c, credencial)

        })
        //cancelar
        alerta.setNegativeButton("CANCELAR", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        alerta.show()

    }

    private fun mostrar_aviso_google(c:Context) {

        val alerta = android.app.AlertDialog.Builder(context)

        alerta.setTitle("Deseja realmente deletar essa conta?")


        //deletar conta
        alerta.setPositiveButton("DELETAR", DialogInterface.OnClickListener { dialog, which ->
            //ao  clicar, reauntentica

            val acct = GoogleSignIn.getLastSignedInAccount(context)
            var credencial = GoogleAuthProvider.getCredential(acct!!.idToken, null)
            reautenticar(c, credencial)

        })
        //cancelar
        alerta.setNegativeButton("CANCELAR", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })

        alerta.show()

    }


    private fun reautenticar(c:Context, credencial: AuthCredential) {


        //reautentica
        auth.currentUser.reauthenticate(credencial)
                .addOnSuccessListener {

                    //deleta informações
                    deletar_infos()

                }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Por favor, faça login e tente novamente",
                        Toast.LENGTH_LONG
                    ).show()
                    sair(c)


                }
    }

    private fun deletar_infos() {
        var database_usuario = FirebaseDatabase.getInstance().reference.child("usuarios/${auth.uid}")
        //deleta dados
        database_usuario.removeValue()
                .addOnSuccessListener {

                    //se deletou corretamente, delata conta
                    deletar_conta_firebase()

                }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Algo ocorreu errado ao excluir seus dados \n$it",
                        Toast.LENGTH_LONG
                    ).show()

                }
    }

    fun deletar_conta_firebase(){
        //deleta conta após reautenticar
        auth.currentUser.delete()
                .addOnSuccessListener {

                    //se deletou conta e dados, sair
                    Toast.makeText(
                        context,
                        "Conta deletada, sentiremos sua falta",
                        Toast.LENGTH_LONG
                    ).show()
                    //chama função para sair
                    this.context?.let { it -> sair(it) }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Algo ocorreu errado ao excluir sua conta no firebase \n$it",
                        Toast.LENGTH_LONG
                    ).show()

                }
    }

    fun ler_cadastro(
        database_nome: DatabaseReference, database_data: DatabaseReference,
        database_evitaveis: DatabaseReference, adaptador_lista: ArrayAdapter<*>, lista: ListView
    ) {

        //escrevendo data de nascimento
        database_data.get().addOnSuccessListener { it ->
            data_de_nascimento_texto_edit_cadast_txt.setText(it.value.toString())

            //escrevendo nome
            database_nome.get().addOnSuccessListener { it ->
                nome_edit_cadast_txt.setText(it.value.toString())

                ler_evitaveis_e_desenhar(database_evitaveis, adaptador_lista, lista)
            }
        }



    }

    fun ler_evitaveis_e_desenhar(
        database_evitaveis: DatabaseReference,
        adaptador: ArrayAdapter<*>,
        lista: ListView
    ) {

        //pegando nome do firebase
        database_evitaveis.get().addOnSuccessListener {

            var database_evit = FirebaseDatabase.getInstance().reference.child("usuarios/${auth.uid}/evitaveis").get()
            //le evitaveis
            database_evit.addOnSuccessListener {
                //zera evitaveis
                evitaveis_edit.removeAll(evitaveis_edit)

                //adiciona um por um
                for (item in it.children){
                    evitaveis_edit.add(item.value.toString())
                }

                //atualizando lista
                lista.adapter = adaptador

            }



        }
        .addOnFailureListener { it->
            Toast.makeText(context, "erro firebase editar cadastro...\n$it", Toast.LENGTH_LONG).show()

        }

    }

    fun salvar(
        database_nome: DatabaseReference,
        database: DatabaseReference,
        database_evitaveis: DatabaseReference
    ){
        var data = data_de_nascimento_texto_edit_cadast_txt.text.toString().trim()

        //caso data nao for vazio, normalizar
        if (data.isNotEmpty()){
            data_de_nascimento_texto_edit_cadast_txt!!.setText(normalizar_data(data))
            data = normalizar_data(data)
        }

        var nome = nome_edit_cadast_txt.text.toString().trim()

        //se tiver data de nascimento e não estiver correta, avisar
        if (data.isNotEmpty() && (!data_correta(data))){
            Toast.makeText(context, "Digite a data corretamente", Toast.LENGTH_LONG).show()
        }else {


            //se nome for vazio, avisa
            if (nome.isEmpty()) {
                Toast.makeText(context, "Digite seu nome por favor", Toast.LENGTH_LONG).show()
            } else {

                //ATUALIZE EVIATVEIS ANTES
                database_nome.setValue(nome)
                database.setValue(data)

                //se usuario nao preencheu nada
                if (evitaveis_edit.isEmpty()){
                    Toast.makeText(context, "Digite selecione ao menos 1", Toast.LENGTH_LONG).show()
                }else{
                    //delete antigos
                    database_evitaveis.removeValue()
                    //adiciona novos evitaveis
                    var cont = 0
                    for ( e:String in evitaveis_edit){
                        //correndo pela lista, adicionando um por um
                        database_evitaveis.child(cont.toString()).setValue(e)
                            .addOnSuccessListener {
                                atualizar_evitaveis_local(requireActivity(), auth.currentUser.uid)
                            }
                        cont += 1
                    }



                    Toast.makeText(context, "Edição feita com Sucesso", Toast.LENGTH_LONG).show()
                }


            }
        }
    }


}
