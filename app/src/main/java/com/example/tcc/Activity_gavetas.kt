package com.example.tcc

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_gavetas.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*


class Activity_gavetas : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gavetas)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)


        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_historico, R.id.nav_edit_cadastro, R.id.nav_sair), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //esconde barra
        supportActionBar!!.hide()


        //botao gaveta
        btn_gaveta.setOnClickListener{
            //abre gaveta quando clicar no botao
            drawer_layout.setScrimColor(resources.getColor(android.R.color.transparent))
            drawer_layout.openDrawer(GravityCompat.START)

            //abaixa teclado se estiver aberto
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(navView.getWindowToken(), 0)
        }




    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        foto_usuario.clipToOutline = true
        //adicioando informações do usuário a gaveta
        var database = FirebaseDatabase.getInstance().reference.child("usuarios/${auth.uid}/nome").get()
        database.addOnSuccessListener { it->
            nome_gaveta.text = it.value.toString()
        }

        //pegando email do auth
        email_gaveta.text = auth.currentUser?.email.toString()

        //colocando imagem do usuario
        Glide.with(applicationContext)
                .load(auth.currentUser?.photoUrl)
                .into(foto_usuario)

        return super.onCreateOptionsMenu(menu)
    }



    override fun onBackPressed() {
        //abre gaveta quando clicar em voltar
        drawer_layout.openDrawer(GravityCompat.START)

    }


    override fun onSupportNavigateUp(): Boolean {

        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}


