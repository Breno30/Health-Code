package com.example.tcc.ui.sair

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tcc.R
import com.example.tcc.funcoes.sair

class SairFragment : Fragment() {


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_sair, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sair(view.context)
    }


}