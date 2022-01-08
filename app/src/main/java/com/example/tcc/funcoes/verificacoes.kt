package com.example.tcc.funcoes

fun normalizar_data(data_usuario: String): String {

    //deixando apenas numeros
    val data_sem_caracteres = data_usuario.replace(".", "").replace("-", "").replace("/", "")
    //fazendo array com os numeros
    val data_array = data_sem_caracteres.toCharArray()
    var data_normal = ""

    // adicionando barras
    for (cont in data_array.indices) {
        if (cont == 2 || cont == 4) {
            data_normal += "/"
        }
        data_normal += data_array[cont]
    }

    //retornando data formatada
    return data_normal
}

fun data_correta(data: String): Boolean {
    var result:Boolean = false

    if (data.length == 10){
        result = true
    }
    return result
}

