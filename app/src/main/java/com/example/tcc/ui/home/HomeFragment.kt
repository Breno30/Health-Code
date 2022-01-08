package com.example.tcc.ui.home

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tcc.R
import com.example.tcc.classes.HeightProvider
import com.example.tcc.classes.MyCallback
import com.example.tcc.classes.PesquisaProduto
import com.example.tcc.classes.ResultadoPesquisa
import com.google.firebase.database.DataSnapshot
import com.google.zxing.Result
import kotlinx.android.synthetic.main.fragment_home.*
import me.dm7.barcodescanner.zxing.ZXingScannerView


class HomeFragment : Fragment(), ZXingScannerView.ResultHandler {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        HeightProvider(activity).init().setHeightListener { height ->
            val barraPesquisa: EditText? = view?.findViewById(R.id.BarraPesquisa)
            var viewPesquisa: NestedScrollView? = view?.findViewById(R.id.nsPesquisa)
            if (viewPesquisa != null){
                val tamanho_tela: Int = Resources.getSystem().displayMetrics.heightPixels
                val posicao_caixa_pesquisa:Int = viewPesquisa?.y!!.toInt()

                viewPesquisa?.layoutParams?.height = tamanho_tela-(height+posicao_caixa_pesquisa)

                //recarrega o texto
                val texto:String = barraPesquisa?.text.toString()
                barraPesquisa?.setText(texto)
                barraPesquisa?.setSelection(texto.length)

            }

        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //inicia camera
        z_xing_scanner.startCamera()
        val barraPesquisa:EditText = view.findViewById(R.id.BarraPesquisa)
        var viewPesquisa:LinearLayout = view.findViewById(R.id.lvPesquisa)



        var pesq = PesquisaProduto(view, R.id.lvPesquisa, 5)

        barraPesquisa.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewPesquisa.removeAllViews()
            }
        })



        barraPesquisa.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var dados_resultado: DataSnapshot? = null
                var textoPesquisa: String = barraPesquisa.text.trim().toString()
                //barraPesquisa.setText("   "+textoPesquisa)
                //limpa linearlayout

                if (textoPesquisa.isNotEmpty()) {

                    pesq.pesquisar(object : MyCallback() {
                        override fun onCallback(value: DataSnapshot) {
                            if (value != dados_resultado) {
                                //só aualizará view se DataSnapshot for diferente
                                dados_resultado = value
                                var result = ResultadoPesquisa(view, value)
                                val aux: LinearLayout = result.montarTela(activity)
                                viewPesquisa.removeAllViews()
                                viewPesquisa.addView(aux)
                            }

                        }
                    }, textoPesquisa)
                } else {
                    viewPesquisa.removeAllViews()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

        })




    }


    override fun onResume() {
        super.onResume()
        z_xing_scanner.setResultHandler(this)
        z_xing_scanner.startCamera()
    }

    override fun handleResult(resultado: Result?) {
        //se nao tiver 13 numeros, algo deu errado
        if (resultado!!.text.length != 13){
            //reinicia camera
//            var intent_reiniciar = Intent(context, MainActivity::class.java)
//            startActivity(intent_reiniciar)

            z_xing_scanner.stopCameraPreview()
            z_xing_scanner.stopCamera()
            z_xing_scanner.startCamera()
            z_xing_scanner.setResultHandler(this)

        }else{
            //abre pagina de informações do produto
            val intent = Intent(context, Activity_infos_produto::class.java)
            intent.putExtra("codigo", resultado!!.text)
            activity?.startActivity(intent)
        }

    }
}