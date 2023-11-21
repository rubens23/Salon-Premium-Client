package com.rubens.salonpremiumclient.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsalaobelezaclientemvvm.model.dataClasses.Day
import com.rubens.month_selector.MonthSelectorHelper
import com.rubens.salonpremiumclient.data.models.Appointment
import com.rubens.salonpremiumclient.data.models.Cliente
import com.rubens.salonpremiumclient.data.models.Employee
import com.rubens.salonpremiumclient.data.models.Hour
import com.rubens.salonpremiumclient.data.models.Service
import com.rubens.salonpremiumclient.data.repositories.FirebaseRepository
import com.rubens.salonpremiumclient.utils.AgendamentoHelper
import com.rubens.salonpremiumclient.utils.CalendarHelper
import com.rubens.salonpremiumclient.utils.TypeConverter
import com.rubens.salonpremiumclient.utils.helperdataclasses.DataDeAtendimentoSelecionada
import com.rubens.salonpremiumclient.utils.helperdataclasses.PeriodoDoDiaSelecionado
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentAgendarAtendimentosViewModel @Inject constructor(
    val monthSelector: MonthSelectorHelper,
    private val typeConverter: TypeConverter,
    private val calendarHelper: CalendarHelper,
    private val firebaseRepository: FirebaseRepository,
    private val agendamentoHelper: AgendamentoHelper
) : ViewModel() {

    var selectedPeriodOfDay = "Manhã"
    var mesSelecionado: String = ""
    var anoSelecionado: String = ""
    lateinit var mesAtual: String
    lateinit var anoAtual: String
    lateinit var userData: Cliente


    /**
     * essas variaveis abaixo são as que serao usadas no metodo de
     * lidar com o clique em um item de atendimento e atualizar as
     * recycler views de acordo com as regras de agendamento
     */
    var employee: Employee? = null
    var service: Service? = null
    var dataDeAtendimentoSelecionada: DataDeAtendimentoSelecionada? = null
    var periodoDoDiaSelecionado: PeriodoDoDiaSelecionado? = null
    var todosAppointments: ArrayList<Appointment>? = null
    var todosFuncionarios: ArrayList<Employee>? = null
    var todosServicos: ArrayList<Service>? = null
    var todosDias: ArrayList<Day>? = null
    var todosHorarios: ArrayList<Hour>? = null

    private val _pegouTodosOsFuncionarios: MutableSharedFlow<ArrayList<Employee>> =
        MutableSharedFlow(replay = 0)
    val pegouTodosOsFuncionarios: SharedFlow<ArrayList<Employee>> = _pegouTodosOsFuncionarios

    //uma data é retirada da recyclerview se o funcionario
    //escolhido estiver de folga naquela data
    private val _pegouDataASerRetirada: MutableSharedFlow<String> = MutableSharedFlow(replay = 0)
    val pegouDataASerRetirada: SharedFlow<String> = _pegouDataASerRetirada

    //horas sao retiradas da recyclerview se o funcionario
    //escolhido tiver serviços naquele dia e horario
    private val _pegouHorasASeremRetiradas: MutableSharedFlow<List<Map<String, String>>> =
        MutableSharedFlow(replay = 0)
    val pegouHorasASeremRetiradas: SharedFlow<List<Map<String, String>>> =
        _pegouHorasASeremRetiradas

    private val _pegouFuncionariosQuePrecisamSerRetirados: MutableSharedFlow<ArrayList<String>> =
        MutableSharedFlow(replay = 0)
    val pegouFuncionariosQuePrecisamSerRetirados: SharedFlow<ArrayList<String>> =
        _pegouFuncionariosQuePrecisamSerRetirados

    private val _pegouServicosQuePrecisamSerRetirados: MutableSharedFlow<List<Service>> =
        MutableSharedFlow(replay = 0)
    val pegouServicosQuePrecisamSerRetirados: SharedFlow<List<Service>> =
        _pegouServicosQuePrecisamSerRetirados


    private val _pegouTodosOsServicos: MutableSharedFlow<ArrayList<Service>> =
        MutableSharedFlow(replay = 0)
    val pegouTodosOsServicos: SharedFlow<ArrayList<Service>> = _pegouTodosOsServicos

    private val _pegouDiasQuePrecisamSerRecolocadosNaRv: MutableSharedFlow<ArrayList<Day>> = MutableSharedFlow(replay = 0)
    val pegouDiasQuePrecisamSerRecolocadosNaRv = _pegouDiasQuePrecisamSerRecolocadosNaRv

    private val _pegouHorasQuePrecisamSerRecolocadasNaRv: MutableSharedFlow<ArrayList<Hour>> = MutableSharedFlow(replay = 0)
    val pegouHorasQuePrecisamSerRecolocadasNaRv = _pegouHorasQuePrecisamSerRecolocadasNaRv

    private val _pegouFuncionariosQuePrecisamSerRecolocadosNaRv: MutableSharedFlow<ArrayList<Employee>> = MutableSharedFlow(replay = 0)
    val pegouFuncionariosQuePrecisamSerRecolocadosNaRv = _pegouFuncionariosQuePrecisamSerRecolocadosNaRv

    private val _pegouServicosQuePrecisamSerRecolocadosNaRv: MutableSharedFlow<ArrayList<Service>> = MutableSharedFlow(replay = 0)
    val pegouServicosQuePrecisamSerRecolocadosNaRv = _pegouServicosQuePrecisamSerRecolocadosNaRv

    private val _pegouDadosDoCliente: MutableSharedFlow<Cliente> = MutableSharedFlow(replay = 0)
    val pegouDadosDoCliente = _pegouDadosDoCliente

    private val _colocouNovoAppointment: MutableSharedFlow<String> = MutableSharedFlow(replay = 0)
    val colocouNovoAppointment = _colocouNovoAppointment


    init {
        pegarAllAppointments()
    }

    private fun pegarAllAppointments() {
        firebaseRepository.pegarTodosAppointments {
            todosAppointments = it
        }
    }

    fun transformDayListOfMapsToDayListOfDays(listaDeDias: List<Map<String, String>>): ArrayList<Day> {
        //lista de dias formatados é uma lista de strings no formato dd/MM/yyyy
        var listaDeDateFormatted = fazerListaDeDiasForamatados(listaDeDias)
        return typeConverter.transformDayListOfMapsToDayListOfDays(
            listaDeDias,
            listaDeDateFormatted
        )

    }

    private fun fazerListaDeDiasForamatados(listaDeDias: List<Map<String, String>>): List<String> {
        val listaDeDiasFormatados = arrayListOf<String>()
        listaDeDias.forEach {
            listaDeDiasFormatados.add(
                obterDataFormatada(
                    it["numero"]!!,
                    mesSelecionado,
                    anoSelecionado
                )
            )
        }
        var listaFinalFormatada =
            calendarHelper.obterListaDeDiasComDiaDeDoisDigitos(listaDeDiasFormatados)
        listaFinalFormatada
        return listaFinalFormatada
    }


    fun obterListaDeDias(
        mesSelecionado: String,
        anoSelecionado: String
    ): List<Map<String, String>> {

        return calendarHelper.obterListaDeDias(mesSelecionado, anoSelecionado)
    }

    fun obterListaDeHorasPorPeriodo(selectedPeriodOfDay: String): ArrayList<Hour> {
        return calendarHelper.getHoursListByPeriodOfDay(selectedPeriodOfDay)

    }

    fun avancarPeriodoDoDia(): String {
        when (selectedPeriodOfDay) {
            "Manhã" -> {
                setPeriodOfDay("Tarde")
            }

            "Tarde" -> {
                setPeriodOfDay("Noite")
            }

            "Noite" -> {
                setPeriodOfDay("")
            }
        }
        return selectedPeriodOfDay

    }

    private fun setPeriodOfDay(period: String) {
        selectedPeriodOfDay = period
    }

    fun retrocederPeriodoDoDia(): String {
        when (selectedPeriodOfDay) {
            "Manhã" -> {
                setPeriodOfDay("")
            }

            "Tarde" -> {
                setPeriodOfDay("Manhã")

            }

            "Noite" -> {
                setPeriodOfDay("Tarde")
            }
        }
        return selectedPeriodOfDay

    }

    fun getAllEmployees() {
        firebaseRepository.pegarTodosFuncionarios(pegouTodosOsFuncionarios = ::pegouTodosOsFuncionarios)

    }

    private fun pegouTodosOsFuncionarios(funcionarios: ArrayList<Employee>) {
        viewModelScope.launch {
            _pegouTodosOsFuncionarios.emit(funcionarios)
        }
    }

    fun pegarTodosOsServicos() {
        firebaseRepository.pegarTodosServicos(pegouTodosOsServicos = ::pegouOsServicos)
    }

    private fun pegouOsServicos(servicos: ArrayList<Service>) {
        if (servicos.isNotEmpty()) {
            viewModelScope.launch {
                todosServicos = ArrayList(servicos)
                _pegouTodosOsServicos.emit(servicos)
            }
        }

    }

    fun lidarComCliqueEmIemDeAgendamento(profisionaisAtualmenteNaRv: ArrayList<Employee>?,
                                         servicosAtualmenteNaRv: ArrayList<Service>?,
                                         diasAtualmenteNaRv: ArrayList<Day>?,
                                         horasAtualmenteNaRv: ArrayList<Hour>?) {

        agendamentoHelper.lidarComSelecaoDeItemDeAgendamento(employee,
            dataDeAtendimentoSelecionada,
            service,
            periodoDoDiaSelecionado,
            todosAppointments,
            todosFuncionarios!!,
            todosDias!!,
            todosHorarios!!,
            todosServicos!!,
            profisionaisAtualmenteNaRv,
            servicosAtualmenteNaRv,
            diasAtualmenteNaRv,
            horasAtualmenteNaRv,
            { dataASerRetirada ->
                viewModelScope.launch {
                    _pegouDataASerRetirada.emit(dataASerRetirada)
                }
            }, { horasQuePrecisamSerRetiradas ->
                viewModelScope.launch {
                    _pegouHorasASeremRetiradas.emit(horasQuePrecisamSerRetiradas)

                }

            },
            { funcionariosQuePrecisamSerRetirados ->
                viewModelScope.launch {
                    _pegouFuncionariosQuePrecisamSerRetirados.emit(
                        funcionariosQuePrecisamSerRetirados
                    )
                }
            },
            { servicosRetirados ->
                viewModelScope.launch {
                    _pegouServicosQuePrecisamSerRetirados.emit(servicosRetirados)
                }
            },
            { funcionariosASeremRecolocados ->
                viewModelScope.launch {
                    _pegouFuncionariosQuePrecisamSerRecolocadosNaRv.emit(funcionariosASeremRecolocados)
                }

            },
            { servicosASeremRecolocados ->
                servicosASeremRecolocados
                viewModelScope.launch {
                    _pegouServicosQuePrecisamSerRecolocadosNaRv.emit(servicosASeremRecolocados)
                }


            },
            { diasASeremRecolocados ->
                viewModelScope.launch {
                    _pegouDiasQuePrecisamSerRecolocadosNaRv.emit(diasASeremRecolocados)
                }

            },
            { horariosASeremRecolocados ->
                viewModelScope.launch {
                    _pegouHorasQuePrecisamSerRecolocadasNaRv.emit(horariosASeremRecolocados)
                }

            })


    }

    fun obterDataFormatada(
        numberDay: String,
        mesSelecionado: String,
        anoSelecionado: String
    ): String {
        val numeroDoDiaFormatado = calendarHelper.obterDiaComDoisDigitos(numberDay)
        return "$numeroDoDiaFormatado/${calendarHelper.obterNumeroDoMes(mesSelecionado)}/$anoSelecionado"
    }

    fun sendAppointmentToDatabase() {
        if (dataDeAtendimentoSelecionada != null
            && employee != null
            && service != null
            && periodoDoDiaSelecionado != null
        ) {

            val appointment = Appointment(
                day = dataDeAtendimentoSelecionada!!.numberDay,
                month = dataDeAtendimentoSelecionada!!.labelMonth,
                employee = employee!!.name,
                hour = periodoDoDiaSelecionado!!.horaDoDia,
                service = service!!.serviceName,
                clientName = userData.nomeCliente.capitalize(),
                employeeKey = employee!!.employeeKey,
                appointmentDayFormatted = obterDataFormatada(
                    dataDeAtendimentoSelecionada!!.numberDay,
                    dataDeAtendimentoSelecionada!!.labelMonth,
                    anoSelecionado
                ),
                clientUid = firebaseRepository.getUserUid()?: ""
            )

            firebaseRepository.sendAppointmentToFirebase(appointment) { salvou ->
                viewModelScope.launch {
                    if(salvou){
                        _colocouNovoAppointment.emit("Novo agendamento salvo com sucesso! Acompanhe ele na sua agenda!")
                    }else{
                        _colocouNovoAppointment.emit("Aconteceu uma falha no agendamento! tente novamente ou entre em contato com o salão!")

                    }
                }

            }
        }else{
            //falta preencher alguns dados da appointment
            viewModelScope.launch {
                _colocouNovoAppointment.emit("Preencha todos os dados!")
            }
        }
    }

    fun pegarDadosDoUser() {
        firebaseRepository.pegarDadosDoUser{
                cliente->
            viewModelScope.launch {
                userData = cliente
                _pegouDadosDoCliente.emit(cliente)
            }

        }
    }

    fun salvarImagemEscolhidaNoFirebase(imagemEscolhida: Uri) {
        firebaseRepository.salvarImagemNoFirebase(imagemEscolhida){_->}

    }


}