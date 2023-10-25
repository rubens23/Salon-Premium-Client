package com.rubens.salonpremiumclient.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rubens.salonpremiumclient.data.models.Cliente
import com.rubens.salonpremiumclient.data.repositories.FirebaseRepository
import com.rubens.salonpremiumclient.utils.CadastroValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentLoginViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val cadastroValidator: CadastroValidator
): ViewModel() {

    private val _pegouMsgsDeErro: MutableSharedFlow<Map<String, String>> = MutableSharedFlow(replay = 0)
    val pegouMsgsDeErro = _pegouMsgsDeErro

    private val _msgResultadoCadastro: MutableSharedFlow<String> = MutableSharedFlow(replay = 0)
    val msgResultadoCadastro = _msgResultadoCadastro

    private val _loginResult: MutableSharedFlow<String> = MutableSharedFlow<String>(replay = 0)
    val loginResult = _loginResult

    private val _loginMsg: MutableSharedFlow<String> = MutableSharedFlow(replay = 0)
    val loginMsg = _loginMsg




    fun cadastrarNovoCliente(email: String, nome: String, sobrenome: String, senha: String, confirmacaoSenha: String) {


        cadastroValidator.verSeDadosForamPreenchidosCorretamente(
            email,
            nome,
            sobrenome,
            senha,
            confirmacaoSenha
        ){
            msgsValidacao->
            viewModelScope.launch {
                _pegouMsgsDeErro.emit(msgsValidacao)

                if(msgsValidacao.isEmpty()){
                    //se nao tem nenhuma mensagem, significa que
                    //todos os campos foram preenchidos
                    //prossegue com a validação
                    firebaseRepository.cadastrarNovoUsuario(email,
                        nome,
                        sobrenome,
                        senha,
                        confirmacaoSenha){
                            msgResultadoCadastro ->
                        viewModelScope.launch {
                            _msgResultadoCadastro.emit(msgResultadoCadastro)

                        }

                    }
                }
            }

        }


    }

    fun fazerLogin(email: String, senha: String) {
        firebaseRepository.login(email, senha){
            fezLogin, msgLogin->
            viewModelScope.launch {
                if(msgLogin == ""){
                    _loginResult.emit(if(fezLogin) "Login bem sucedido!" else "Falha no login")

                }else{
                    _loginMsg.emit(msgLogin)
                }

            }

        }
    }


}