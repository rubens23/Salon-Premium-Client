package com.rubens.salonpremiumclient.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.rubens.salonpremiumclient.R
import com.rubens.salonpremiumclient.databinding.FragmentLoginBinding
import com.rubens.salonpremiumclient.viewmodel.SharedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentLogin : Fragment() {


    private lateinit var viewModel: FragmentLoginViewModel
    private lateinit var binding: FragmentLoginBinding
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initCollectors()
        onClicklisteners()
    }

    private fun onClicklisteners() {
        binding.tvLabelRegistrar.setOnClickListener {
            mudarAparenciaDaViewPorModo("cadastro")
        }

        binding.tvLabelEsqueceuSenha.setOnClickListener {
        }

        binding.arrowBackToLogin.setOnClickListener {
            mudarAparenciaDaViewPorModo("login")

        }

        binding.btnLogin.setOnClickListener {
            if(binding.btnLogin.text == "Cadastrar"){
                cadastrarNovoCliente()
            }
            if(binding.btnLogin.text == "Login"){
                viewModel.fazerLogin(binding.etEmailLogin.text.toString(), binding.etSenha.text.toString())
            }
        }
    }

    private fun cadastrarNovoCliente() {
        val email = binding.etEmailCadastro.text.toString()
        val nome = binding.etEmailLogin.text.toString()
        val sobrenome = binding.etSenha.text.toString()
        val senha = binding.etCadastroSenha.text.toString()
        val confirmacaoSenha = binding.etCadastroSenhaConfirmacao.text.toString()
        viewModel.cadastrarNovoCliente(email, nome, sobrenome, senha, confirmacaoSenha)
    }


    /**
     * modo == (login, cadastro ou esqueceu senha)
     */
    private fun mudarAparenciaDaViewPorModo(modo: String) {
        when(modo){
            "login"->{
                changeViewsParaLogin()

            }
            "cadastro"->{
                changeViewsParaCadastro()

            }
        }

    }

    private fun changeViewsParaLogin() {
        binding.tvLabelLogin.text = "e-mail"
        binding.tvLabelSenha.text = "senha"
        binding.etSenha.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.etEmailLogin.setText("")
        binding.etSenha.setText("")
        binding.arrowBackToLogin.visibility = View.GONE
        binding.tvLabelCadastroEmail.visibility = View.GONE
        binding.etEmailCadastro.visibility = View.GONE
        binding.tvLabelCadastroSenha.visibility = View.GONE
        binding.etCadastroSenha.visibility = View.GONE
        binding.tvLabelCadastroSenhaConfirmacao.visibility = View.GONE
        binding.etCadastroSenhaConfirmacao.visibility = View.GONE
        binding.btnLogin.text = "Login"
        binding.tvLabelEsqueceuSenha.visibility = View.VISIBLE
        binding.tvLabelRegistrar.visibility = View.VISIBLE
    }

    private fun initCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouMsgsDeErro.collectLatest {
                    msgsDeErro->
                    for ((campo, mensagem) in msgsDeErro){
                        when(campo){
                            "email" -> {
                                binding.etEmailCadastro.error = mensagem
                            }
                            "nome" -> {
                                binding.etEmailLogin.error = mensagem
                            }
                            "sobrenome" -> {
                                binding.etSenha.error = mensagem
                            }
                            "senha" -> {
                                binding.etCadastroSenha.error = mensagem
                            }
                            "confirmacaoSenha" -> {
                                binding.etCadastroSenhaConfirmacao.error = mensagem
                            }
                            "senhaCurta"->{
                                binding.etCadastroSenha.error = mensagem
                            }
                            "senhaInsegura"->{
                                binding.etCadastroSenha.error = "$mensagem!\nDicas para uma senha segura:\n" +
                                        "- Pelo menos 8 caracteres\n" +
                                        "- Pelo menos 1 letra maiúscula\n" +
                                        "- Pelo menos 1 letra minúscula\n" +
                                        "- Pelo menos 1 número\n" +
                                        "- Pelo menos 1 caracter especial"

                            }

                        }
                    }
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loginResult.collectLatest{
                        result->

                    if (result == "Login bem sucedido!"){
                        //showToast(result)

                        view?.findNavController()?.popBackStack()


                        view?.findNavController()?.navigate(R.id.marcarHorariosFragment)
                        sharedViewModel.mostrarBottomNavigationDepoisDoLogin()


                    }else{
                        //nao logou
                        showToast(result)

                    }

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loginMsg.collectLatest{
                        msg->
                    if(msg == "There is no user record corresponding to this identifier. The user may have been deleted."){
                        showToast("Não tem nenhum cliente cadastrado com esse e-mail")

                    }



                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.msgResultadoCadastro.collectLatest {
                        msgDeCadastro->
                        when(msgDeCadastro){
                            "Esse endereço de email já é usado em outra conta!" -> {
                                showToast(msgDeCadastro)
                            }
                            "Ocorreu alguma falha no cadastro" -> {
                                showToast(msgDeCadastro)

                            }
                            "erro no cadastro" -> {
                                showToast(msgDeCadastro)

                            }
                            "cadastrado com sucesso!" -> {
                                showToast(msgDeCadastro)
                                changeViewsParaLogin()

                            }


                        }

                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[FragmentLoginViewModel::class.java]

    }

    private fun changeViewsParaCadastro(){
        binding.tvLabelLogin.text = "nome"
        binding.tvLabelSenha.text = "sobrenome"
        binding.etSenha.inputType = InputType.TYPE_CLASS_TEXT
        binding.arrowBackToLogin.visibility = View.VISIBLE
        binding.tvLabelCadastroEmail.visibility = View.VISIBLE
        binding.etEmailLogin.setText("")
        binding.etSenha.setText("")
        binding.etEmailCadastro.visibility = View.VISIBLE
        binding.tvLabelCadastroSenha.visibility = View.VISIBLE
        binding.etCadastroSenha.visibility = View.VISIBLE
        binding.tvLabelCadastroSenhaConfirmacao.visibility = View.VISIBLE
        binding.etCadastroSenhaConfirmacao.visibility = View.VISIBLE
        binding.btnLogin.text = "Cadastrar"
        binding.tvLabelEsqueceuSenha.visibility = View.GONE
        binding.tvLabelRegistrar.visibility = View.GONE
    }



}