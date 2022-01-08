package com.example.tcc

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tcc.classes.atualizar_banco_local
import com.example.tcc.classes.atualizar_evitaveis_local
import com.example.tcc.classes.tem_internet
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.security.AccessController.getContext


lateinit var googleSignInClient: GoogleSignInClient
private const val RC_SIGN_IN = 9001
var auth: FirebaseAuth = Firebase.auth


open class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pedir_permissao_camera()

        //esconde barra superior
        supportActionBar!!.hide()




        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()


        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //caso uid já estiver cadastrado, entrar na aplicação
        if (auth.uid != null) {

            if(tem_internet(applicationContext)){
                atualizar_banco_local(this)
                atualizar_evitaveis_local(this, auth.currentUser.uid)
            }

            //entrar a aplicação
            val intent = Intent(this, Activity_gavetas::class.java)
            startActivity(intent)
        }


        //DANDO FUNÇÃO AOS BOTÕES

        //ao clicar em logar
        logar!!.setOnClickListener {
            pedir_permissao_camera { logar() }
        }
        //ao clicar no botao do Google
        signInButton!!.setOnClickListener{
            pedir_permissao_camera { signIn() }
        }

        //ao clicar em cadastrar
        cadastro!!.setOnClickListener{
            pedir_permissao_camera { cadastro() }

        }

        //ao clicar em recuperar senha
        recuperar_senha!!.setOnClickListener {
            recuperar_senha()
        }

        //ao clicar em sobre
        info!!.setOnClickListener {
            val intent = Intent(this, Activity_Sobre_antes_login::class.java)
            startActivity(intent)
        }

        //quando botão "feito" do teclado for pressionado
        senha_texto.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                logar()
                true
            }else{
                false
            }
        }



    }






    fun  pedir_permissao_camera(funcao:() -> Unit){
        //se usuário não aceitou permissão da camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){

            //pedindo permissão
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)

        }else{
            //se tiver permissão, usar função
            funcao()
        }

    }

    fun pedir_permissao_camera(){
        //se usuário não aceitou permissão da camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){

            //pedindo permissão
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)

        }
    }

    private fun logar(){
        var email = email_texto.text.toString().trim()
        var senha = senha_texto.text.toString().trim()
        if (email.isEmpty() || senha.isEmpty()){
            //se o email ou a senha forem vazio
            Toast.makeText(this, "Por Favor, digite nos dois campos", Toast.LENGTH_LONG).show()
        }else{
            //se o email e senha estiverem preenchidos loga com email e senha
            auth.signInWithEmailAndPassword(email, senha)
                    //verificando se foi efeituado com sucesso
                .addOnSuccessListener {
                    //se não, recarrega pagina, como o auth.uid sera != null, logará
                    finish()
                    startActivity(intent)
                }

                .addOnFailureListener {
                    if (it.message?.contains("password is invalid") == true){
                        Toast.makeText(this, "Senha errada ou login feito apenas com botão do Google", Toast.LENGTH_SHORT).show()
                    }else{
                        if (it.message?.contains("network") == true){
                            Toast.makeText(this, "Por favor, se conecte a internet", Toast.LENGTH_SHORT).show()

                        }else{
                            Toast.makeText(this, "Usuário não cadastrado", Toast.LENGTH_SHORT).show()

                        }
                    }

                }

        }

    }

    private fun cadastro(){
        //muda para pagina de cadastro
        val intent = Intent(this, Activity_cadastro::class.java)
        startActivity(intent)
    }

    private fun recuperar_senha(){
        val intent = Intent(this, Activity_recuperar_senha::class.java)
        //enviando endereço de email já digitado
        intent.putExtra("email", email_texto.text.toString())
        //muda para pagina de recuperação de senha
        startActivity(intent)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {

                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "ERROR onActivityResult\n $e", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //se for usuario novo vai para tela de cadastro com nome e email pegos do google
                        if (task.result?.additionalUserInfo?.isNewUser == true) {
                            val intent = Intent(applicationContext, Activity_Cadastro_2::class.java)
                            intent.putExtra("nome", auth.currentUser?.displayName.toString())
                            startActivity(intent)
                        }else{
                            //se não, recarrega pagina, como o auth.uid sera != null, logará
                            finish()
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(applicationContext, "Error firebaseAuthWithGoogle", Toast.LENGTH_SHORT).show()
                    }

                }
    }


}

