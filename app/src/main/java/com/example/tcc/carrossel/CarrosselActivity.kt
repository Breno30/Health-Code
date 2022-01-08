package com.example.tcc.carrossel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.tcc.MainActivity
import com.example.tcc.R
import com.google.android.material.color.MaterialColors
import kotlinx.android.synthetic.main.activity_carrossel.*
import java.util.ArrayList

class CarrosselActivity : AppCompatActivity() {

    val fragment1 = CarrosselFragment()
    val fragment2 = CarrosselFragment()
    val fragment3 = CarrosselFragment()
    val fragment4 = CarrosselFragment()
    lateinit var adapter : myPageAdapter
    lateinit var activity : Activity

    lateinit var preference :SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrossel)

        //esconde barra superior
        supportActionBar!!.hide()

        activity = this
        preference = getSharedPreferences("introSlider",Context.MODE_PRIVATE)

        if(!preference.getBoolean("intro", true)){
            //se for falso, vai direto para pagina inicial
            var intent_entrar = Intent(activity,MainActivity::class.java)
            startActivity(intent_entrar)
            finish()
        }

        //configura cada imagem para cada fragment
        fragment1.setTitle(R.drawable.tutorial_cadastrar)
        fragment2.setTitle(R.drawable.tutorial_escanear)
        fragment3.setTitle(R.drawable.tutorial_pesquisar)
        fragment4.setTitle(R.drawable.tutorial_adicionar)


        adapter = myPageAdapter(supportFragmentManager)
        adapter.list.add(fragment1)
        adapter.list.add(fragment2)
        adapter.list.add(fragment3)
        adapter.list.add(fragment4)

        view_pager.adapter = adapter
        //botão próximo adiciona um
        btn_next.setOnClickListener {
            view_pager.currentItem++
        }
        //botão pular entra na aplicação
        btn_skip.setOnClickListener {
            EntrarAplicativo()
        }
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            @SuppressLint("ResourceType")
            override fun onPageSelected(position: Int) {
                if (position == adapter.list.size-1){
                    //ultima página
                    btn_next.text = "COMEÇAR"
                    btn_next.setOnClickListener {
                        EntrarAplicativo()
                    }
                }else{
                    //tem próximo
                    btn_next.text = "PRÓXIMO"
                    btn_next.setOnClickListener {
                        view_pager.currentItem++
                    }

                }


                when(view_pager.currentItem){
                    0->{
                        indicator1.setTextColor(MaterialColors.getColor(view_pager, R.attr.colorPrimary))
                        indicator2.setTextColor(Color.parseColor("#808080"))
                        indicator3.setTextColor(Color.parseColor("#808080"))
                        indicator4.setTextColor(Color.parseColor("#808080"))
                    }
                    1->{
                        indicator1.setTextColor(Color.parseColor("#808080"))
                        indicator2.setTextColor(MaterialColors.getColor(view_pager, R.attr.colorPrimary))
                        indicator3.setTextColor(Color.parseColor("#808080"))
                        indicator4.setTextColor(Color.parseColor("#808080"))
                    }
                    2->{
                        indicator1.setTextColor(Color.parseColor("#808080"))
                        indicator2.setTextColor(Color.parseColor("#808080"))
                        indicator3.setTextColor(MaterialColors.getColor(view_pager, R.attr.colorPrimary))
                        indicator4.setTextColor(Color.parseColor("#808080"))
                    }
                    3->{
                        indicator1.setTextColor(Color.parseColor("#808080"))
                        indicator2.setTextColor(Color.parseColor("#808080"))
                        indicator3.setTextColor(Color.parseColor("#808080"))
                        indicator4.setTextColor(MaterialColors.getColor(view_pager, R.attr.colorPrimary))
                    }
                }
            }

        })
    }

    fun EntrarAplicativo(){
        var intent_entrar = Intent(activity,MainActivity::class.java)
        startActivity(intent_entrar)
        finish()

        val editor = preference.edit()
        editor.putBoolean("intro",false)
        editor.apply()
    }

    class myPageAdapter(manager:FragmentManager) : FragmentPagerAdapter(manager){
        val list:MutableList<Fragment> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return list[position]
        }

        override fun getCount(): Int {
            return list.size
        }



    }
}