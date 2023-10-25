package com.rubens.salonpremiumclient.utils

class CadastroValidatorImpl: CadastroValidator {
    override fun verSeDadosForamPreenchidosCorretamente(
        email: String,
        nome: String,
        sobrenome: String,
        senha: String,
        confirmacaoSenha: String,
        msgValidacao: (mensagens: Map<String, String>) -> Unit
    ) {
        val mensagensErro = mutableMapOf<String, String>()

        if (email.isEmpty()) {
            mensagensErro["email"] = "O campo e-mail está vazio."
        }

        if (nome.isEmpty()) {
            mensagensErro["nome"] = "O campo nome está vazio."
        }

        if (sobrenome.isEmpty()) {
            mensagensErro["sobrenome"] = "O campo sobrenome está vazio."
        }

        if (senha.isEmpty()) {
            mensagensErro["senha"] = "O campo senha está vazio."
        }

        if (confirmacaoSenha.isEmpty()) {
            mensagensErro["confirmacaoSenha"] = "O campo confirmação de senha está vazio."
        }

        if (senha != confirmacaoSenha) {
            mensagensErro["confirmacaoSenha"] = "As senhas não coincidem."
        }

        ///se nao o correu nenhum erro, continua
        ///para a verificacao da força da senha
        if(mensagensErro.isEmpty()){
            if(senha.length < 8){
                mensagensErro["senhaCurta"] = "A senha tem que ter pelo menos 6 dígitos"
            }else{
                if(!isSenhaSegura(senha))
                {
                    mensagensErro["senhaInsegura"] = "A senha não é segura o suficiente"
                }
            }


        }

        msgValidacao(mensagensErro)
    }

    private fun isSenhaSegura(senha: String): Boolean {
        // - pelo menos 8 caracteres
        // - Pelo menos 1 letra maiuscula
        // - pelo menos 1 letra minuscula
        // - pelo menos 1 numero
        // - pelo menos 1 caractere especial
        val regexSenhaSegura = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,}\$".toRegex()
        return senha.matches(regexSenhaSegura)


    }
}