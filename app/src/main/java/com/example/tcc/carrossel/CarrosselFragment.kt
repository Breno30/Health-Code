package com.example.tcc.carrossel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tcc.R
import kotlinx.android.synthetic.main.fragment_carrossel.*


class CarrosselFragment : Fragment() {

    var pageTitle:Int = R.drawable.tutorial_cadastrar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_carrossel, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragment_imagem.setImageResource(pageTitle)
    }

    fun setTitle(title:Int){
        pageTitle = title
    }

}