package com.rubens.salonpremiumclient.utils

interface CadastroValidator {
    fun verSeDadosForamPreenchidosCorretamente(email: String,
                                               nome: String,
                                               sobrenome: String,
                                               senha: String,
                                               confirmacaoSenha: String,
    msgValidacao: (mensagens: Map<String, String>)->Unit)
}