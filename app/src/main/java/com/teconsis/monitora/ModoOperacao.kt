package com.teconsis.monitora

data class ModoOperacao (
    val id: Int,
    val descricao: String,
    val modoOperacao: Int
) {
    companion object {
        fun ligarDesligar(id: Int, ligado: Boolean): ModoOperacao {
            val descricao = if (ligado) "Ligado" else "Desligado"
            val modoOperacao = if (ligado) 1 else 0
            return ModoOperacao(id, descricao, modoOperacao)
        }
    }
}