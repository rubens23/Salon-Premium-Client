package com.rubens.salonpremiumclient.data.repositories

import android.net.Uri
import com.rubens.salonpremiumclient.data.models.Appointment
import com.rubens.salonpremiumclient.data.models.Cliente
import com.rubens.salonpremiumclient.data.models.Employee
import com.rubens.salonpremiumclient.data.models.Service
import kotlin.reflect.KFunction1

interface FirebaseRepository {
    fun updateListaDeServicos(funcionarioKey: String, listaDeServicosAtualizada: (listaServicos: ArrayList<String>)->Unit)
    fun updateServicosDoFuncionario(childKey: String, funcionario: Employee, atualizouListaDeServicos: (atualizouServicos: Boolean)->Unit)
    fun removerServicoDoFuncionario(funcionarioKey: String, servicoFuncionario: String, deletouServicoDoFuncionario: (deletou: Boolean, servico: String)->Unit)
    fun pegarTodosFuncionarios(pegouTodosOsFuncionarios: (todosOsFuncionarios: ArrayList<Employee>)->Unit)
    fun sendPhotoToStorage(imgUri: Uri, fileExtension: String?, enviouFotoParaOStorage: (url: String?)->Unit)
    fun saveFuncionarioInDatabase(urlFoto: String, salvouFuncionarioNoDatabase: (salvou: Boolean)->Unit, corteCabelo: String,
                                pinturaCabelo: String,
                                manicure: String,
                                pedicure: String,
                                nomeFuncionario: String,
                                cargoFuncionario: String)

    fun pegarFolgas(funcionarioKey: String, pegouFolga: (folgaDoFuncionario: String)->Unit)

    fun salvarNovaFolga(funcionarioKey: String, dataFolga: String, salvouNovaFolga: (salvou: Boolean)->Unit)
    fun saveServicoInDatabase(urlFoto: String?, nomeServico: String, salvouNovoServico: (salvou: Boolean)->Unit)

    fun pegarTodosServicos(pegouTodosOsServicos: (servicos: ArrayList<Service>)->Unit)

    fun pegarTodosAppointments(pegouTodosAppointments: (listaAppointments: ArrayList<Appointment>)->Unit)

    fun sendAppointmentToFirebase(appointment: Appointment, salvouAppointment: (salvou: Boolean)->Unit)
    fun cadastrarNovoUsuario(
        email: String,
        nome: String,
        sobrenome: String,
        senha: String,
        confirmacaoSenha: String,
        cadastrouComSucesso: (msgResultadoCadastro: String) -> Unit
    )

    fun login(email: String, senha: String, logou: (logou: Boolean, msgErro: String)->Unit)

    fun pegarDadosDoUser(pegouDados: (cliente: Cliente)->Unit)

    fun pegarTodosAppointmentsDoCliente(pegouTodosAppointments: (listaAppointments: ArrayList<Appointment>)->Unit)
    fun getUserUid(): String?
    fun salvarImagemNoFirebase(imagemEscolhida: Uri, salvouImagemNoFirebase: (salvou: Boolean)->Unit)


}