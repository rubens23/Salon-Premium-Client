package com.rubens.salonpremiumclient.utils

import com.example.appsalaobelezaclientemvvm.model.dataClasses.Day
import com.rubens.salonpremiumclient.data.models.Appointment
import com.rubens.salonpremiumclient.data.models.Employee
import com.rubens.salonpremiumclient.data.models.Hour
import com.rubens.salonpremiumclient.data.models.Service
import com.rubens.salonpremiumclient.utils.helperdataclasses.DataDeAtendimentoSelecionada
import com.rubens.salonpremiumclient.utils.helperdataclasses.PeriodoDoDiaSelecionado

interface AgendamentoHelper {
    /**
     * ItemDeAgendamento: (servico, dia, horario e funcionario)
     */
    fun lidarComSelecaoDeItemDeAgendamento(
        profissionalSelecionado: Employee?,
        dataDeAtendimentoSelecionada: DataDeAtendimentoSelecionada?,
        servicoSelecionado: Service?,
        periodoDoDiaSelecionado: PeriodoDoDiaSelecionado?,
        allAppointments: List<Appointment>?,
        todosFuncionarios: ArrayList<Employee>,
        todosOsDias: ArrayList<Day>?,
        todosOsHorários: ArrayList<Hour>?,
        todosServicos: List<Service>,
        todosFuncionariosAtualmenteNaRecyclerView: ArrayList<Employee>?,
        todosServicosAtualmenteNaRecyclerView: ArrayList<Service>?,
        todosDiasAtualmenteNaRecylerView: ArrayList<Day>?,
        todosHorariosAtualmenteNaRecyclerview: ArrayList<Hour>?,
        precisaRetirarDiaDaRecyclerview: (dataASerRetirada: String) -> Unit,
        precisaRetirarHoraDaRecyclerView: (horasASeremRetiradas: List<Map<String, String>>) -> Unit,
        profissionaisRetirados: (profissionaisRetirados: ArrayList<String>)->Unit,
        servicosRetirados: (servicosRetirados: ArrayList<Service>)->Unit,
        precisaRecolocarFuncionarios: (funcionarios: ArrayList<Employee>) -> Unit,
        precisaRecolocarServicos: (servicos: ArrayList<Service>) -> Unit,
        precisaRecolocarDias: (dias: ArrayList<Day>) -> Unit,
        precisaRecolocarHorários: (horários: ArrayList<Hour>) -> Unit
    )

    /**
     * um item foi retirado da recyclerview em uma selecao anterior
     * nova seleção foi feita e itens que não deveriam ser mostrados anteriormente agora
     * necessitam ser mostrados
     *
     * o metodo abaixo vai lidar com isso
     */
//    fun recolocarItensNaRecyclerView(
//        todosFuncionarios: ArrayList<Employee>?, todosServicos: ArrayList<Service>?,
//        todosOsDias: ArrayList<Day>?,
//        todosOsHorários: ArrayList<Hour>?,
//        todosFuncionariosAtualmenteNaRecyclerView: ArrayList<Employee>?,
//        todosServicosAtualmenteNaRecyclerView: ArrayList<Service>?,
//        todosDiasAtualmenteNaRecylerView: ArrayList<Day>?,
//        todosHorariosAtualmenteNaRecyclerview: ArrayList<Hour>?,
//        precisaRecolocarFuncionarios: (funcionarios: ArrayList<Employee>) -> Unit,
//        precisaRecolocarServicos: (servicos: ArrayList<Service>) -> Unit,
//        precisaRecolocarDias: (dias: ArrayList<Day>) -> Unit,
//        precisaRecolocarHorários: (horários: ArrayList<Hour>) -> Unit
//    )
}