package com.rubens.salonpremiumclient.utils

import com.example.appsalaobelezaclientemvvm.model.dataClasses.Day
import com.rubens.salonpremiumclient.data.models.Appointment
import com.rubens.salonpremiumclient.data.models.Employee
import com.rubens.salonpremiumclient.data.models.Hour
import com.rubens.salonpremiumclient.data.models.Service
import com.rubens.salonpremiumclient.utils.helperdataclasses.DataDeAtendimentoSelecionada
import com.rubens.salonpremiumclient.utils.helperdataclasses.PeriodoDoDiaSelecionado

class AgendamentoHelperImpl : AgendamentoHelper {


    override fun lidarComSelecaoDeItemDeAgendamento(
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
        profissionaisRetirados: (profissionaisRetirados: ArrayList<String>) -> Unit,
        servicosRetirados: (servicosRetirados: ArrayList<Service>) -> Unit,
        precisaRecolocarFuncionarios: (funcionarios: ArrayList<Employee>) -> Unit,
        precisaRecolocarServicos: (servicos: ArrayList<Service>) -> Unit,
        precisaRecolocarDias: (dias: ArrayList<Day>) -> Unit,
        precisaRecolocarHorários: (horários: ArrayList<Hour>) -> Unit
    ) {

        verificarSePrecisaModificarDiasRecyclerView(
            profissionalSelecionado,
            precisaRetirarDiaDaRecyclerview,
            todosDiasAtualmenteNaRecylerView,
            todosOsDias,
            precisaRecolocarDias
        )


        verificarSePrecisaModificarHorasRecyclerView(
            profissionalSelecionado,
            dataDeAtendimentoSelecionada,
            allAppointments,
            precisaRetirarHoraDaRecyclerView,
            todosOsHorários,
            todosHorariosAtualmenteNaRecyclerview,
            precisaRecolocarHorários
        )
        verificarSePrecisaModificarProfissionaisRecyclerView(
            todosFuncionarios,
            dataDeAtendimentoSelecionada,
            allAppointments,
            periodoDoDiaSelecionado,
            servicoSelecionado,
            profissionaisRetirados,
            todosFuncionariosAtualmenteNaRecyclerView,
            precisaRecolocarFuncionarios
        )

        verificarSePrecisaModificarServicosRecyclerView(
            profissionalSelecionado,
            todosServicos,
            servicosRetirados,
            todosServicosAtualmenteNaRecyclerView,
            precisaRecolocarServicos
        )


    }


    private fun verificarSePrecisaModificarServicosRecyclerView(
        profissionalSelecionado: Employee?,
        todosServicos: List<Service>,
        servicosRetirados: (servicosRetirados: ArrayList<Service>) -> Unit,
        todosServicosAtualmenteNaRecyclerView: ArrayList<Service>?,
        precisaRecolocarServicos: (servicos: ArrayList<Service>) -> Unit
    ) {
        if (profissionalSelecionado != null) {
            val listaDeServicosQueOFuncionarioNaoExecuta: ArrayList<Service> = arrayListOf()
            todosServicos.forEach { servico ->
                if (!profissionalSelecionado.servicesList.contains(servico.serviceName)) {
                    listaDeServicosQueOFuncionarioNaoExecuta.add(servico)
                }
            }
            servicosRetirados(listaDeServicosQueOFuncionarioNaoExecuta)

            val listaDeServicosASeremRecolocados = arrayListOf<Service>()
            todosServicos.forEach { servico ->
                //se n tem o servico na recycler view
                servico
                if (!todosServicosAtualmenteNaRecyclerView!!.contains(servico)) {
                    //se n tem o servico na lista de servicos que vão ser excluidos
                    //pode recolocar esse servico na recyclerview
                    if(listaDeServicosQueOFuncionarioNaoExecuta.isNotEmpty()){
                        listaDeServicosQueOFuncionarioNaoExecuta.forEach {
                                servicoQueVaiSerRetirado->
                            if(servico.serviceName != servicoQueVaiSerRetirado.serviceName){
                                if(!listaDeServicosASeremRecolocados.contains(servico)  && profissionalSelecionado.servicesList.contains(servico.serviceName)){
                                    listaDeServicosASeremRecolocados.add(servico)

                                }
                            }
                        }
                    }else{
                        if(!listaDeServicosASeremRecolocados.contains(servico) && profissionalSelecionado.servicesList.contains(servico.serviceName)){
                            listaDeServicosASeremRecolocados.add(servico)

                        }

                    }


                }
            }
            if (listaDeServicosASeremRecolocados.isNotEmpty()) {
                precisaRecolocarServicos(listaDeServicosASeremRecolocados)
            }
        }

    }

    private fun verificarSePrecisaModificarProfissionaisRecyclerView(
        todosFuncionarios: ArrayList<Employee>,
        dataDeAtendimentoSelecionada: DataDeAtendimentoSelecionada?,
        allAppointments: List<Appointment>?,
        periodoDoDiaSelecionado: PeriodoDoDiaSelecionado?,
        servicoSelecionado: Service?,
        profissionaisRetirados: (profissionaisRetirados: ArrayList<String>) -> Unit,
        todosFuncionariosAtualmenteNaRecyclerView: ArrayList<Employee>?,
        precisaRecolocarFuncionarios: (funcionarios: ArrayList<Employee>) -> Unit
    ) {
        var listaComTodosFuncionariosRetirados = arrayListOf<String>()

        //- quando o funcionario estiver de folga num determinado dia
        if (dataDeAtendimentoSelecionada != null) {
            var profissionaisDeFolga: ArrayList<String> = arrayListOf()
            todosFuncionarios.forEach { funcionario ->
                if (dataDeAtendimentoSelecionada.formattedDate == funcionario.dtDayOff) {
                    //funcionario esta de folga e precisa ser excluido da recycler view
                    profissionaisDeFolga.add(funcionario.employeeKey)

                }
            }
            profissionaisRetirados(profissionaisDeFolga)
            listaComTodosFuncionariosRetirados.addAll(profissionaisDeFolga)

        }


        //- quando o funcionario nao estiver disponivel num determinado horario
        if (dataDeAtendimentoSelecionada != null && periodoDoDiaSelecionado != null) {
            val listaDeFuncionariosOcupados = arrayListOf<String>()
            allAppointments?.forEach { appointment ->
                if (appointment.appointmentDayFormatted == dataDeAtendimentoSelecionada.formattedDate && appointment.hour == periodoDoDiaSelecionado.horaDoDia) {
                    //funcionario que executa essa appointment deve ser excluido da recycler view
                    listaDeFuncionariosOcupados.add(appointment.employeeKey)
                }
            }
            profissionaisRetirados(listaDeFuncionariosOcupados)
            listaComTodosFuncionariosRetirados.addAll(listaDeFuncionariosOcupados)

        }


        //- quando o funcionario não executar determinado serviço.
        if (servicoSelecionado != null) {
            val listaDeFuncionariosQueNaoExecutamEsseServico = arrayListOf<String>()
            todosFuncionarios.forEach { funcionario ->
                if (!funcionario.servicesList.contains(servicoSelecionado.serviceName)) {
                    //se n contiver o servico na lista de servicos
                    //que o funcionario executa
                    //esse funcionario deve ser excluido da recycler view
                    listaDeFuncionariosQueNaoExecutamEsseServico.add(funcionario.employeeKey)

                }
            }
            profissionaisRetirados(listaDeFuncionariosQueNaoExecutamEsseServico)
            listaComTodosFuncionariosRetirados.addAll(listaDeFuncionariosQueNaoExecutamEsseServico)

        }

        val listaDeFuncionariosASeremRecolocados = arrayListOf<Employee>()
        todosFuncionarios.forEach { funcionario ->
            //se n tem o horario na recycler view
            if (!todosFuncionariosAtualmenteNaRecyclerView!!.contains(funcionario)) {
                //se n tem o horario na lista de horarios que vão ser excluidos
                //pode recolocar esse horario na recyclerview
                if(listaComTodosFuncionariosRetirados.isNotEmpty()){
                    listaComTodosFuncionariosRetirados.forEach {
                            funcionarioQueVaiSerRetirado->
                        if(funcionario.employeeKey != funcionarioQueVaiSerRetirado){
                            listaDeFuncionariosASeremRecolocados.add(funcionario)
                        }
                    }
                }else{
                    listaDeFuncionariosASeremRecolocados.add(funcionario)

                }


            }
        }
        if (listaDeFuncionariosASeremRecolocados.isNotEmpty()) {
            precisaRecolocarFuncionarios(listaDeFuncionariosASeremRecolocados)
        }

    }

    private fun verificarSePrecisaModificarHorasRecyclerView(
        profissionalSelecionado: Employee?,
        dataDeAtendimentoSelecionada: DataDeAtendimentoSelecionada?,
        allAppointments: List<Appointment>?,
        precisaRetirarHoraDaRecyclerView: (horasASeremRetiradas: List<Map<String, String>>) -> Unit,
        todosOsHorários: ArrayList<Hour>?,
        todosHorariosAtualmenteNaRecyclerview: ArrayList<Hour>?,
        precisaRecolocarHorários: (horários: ArrayList<Hour>) -> Unit
    ) {
        if (profissionalSelecionado != null && dataDeAtendimentoSelecionada != null) {
            var listaDeAtendimentos = arrayListOf<Map<String, String>>()
            allAppointments?.let { listaAppointments ->
                listaAppointments.forEach { appointment ->
                    if (appointment.employee == profissionalSelecionado.name) {
                        if (appointment.appointmentDayFormatted == dataDeAtendimentoSelecionada.formattedDate) {
                            //funcionario dessa appointment é o mesmo que o user escolheu
                            //e funcionario tem atendimento nesse dia
                            //tira todas as horas que o funcionario nao esta disponivel
                            listaDeAtendimentos.add(
                                mapOf(
                                    "data" to appointment.appointmentDayFormatted,
                                    "hora" to appointment.hour
                                )
                            )

                        }
                    }
                }
                if (listaDeAtendimentos.isNotEmpty()) {
                    precisaRetirarHoraDaRecyclerView(listaDeAtendimentos)

                }
            }



            val listaDeHorariosASeremRecolocados = arrayListOf<Hour>()
            todosOsHorários?.forEach { horario ->
                //se n tem o horario na recycler view

                    if (!todosHorariosAtualmenteNaRecyclerview!!.any{it.hour == horario.hour}) {
                    //se n tem o horario na lista de horarios que vão ser excluidos
                    //pode recolocar esse horario na recyclerview
                    if(listaDeAtendimentos.isNotEmpty()){
                        listaDeAtendimentos.forEach {
                                horarioQueVaiSerRetirado->
                            if(horario.hour != horarioQueVaiSerRetirado["hora"]){
                                listaDeHorariosASeremRecolocados.add(horario)
                            }
                        }
                    }else{
                        listaDeHorariosASeremRecolocados.add(horario)

                    }


                }
            }
            if (listaDeHorariosASeremRecolocados.isNotEmpty()) {
                precisaRecolocarHorários(listaDeHorariosASeremRecolocados)
            }

        }

    }

    private fun verificarSePrecisaModificarDiasRecyclerView(
        profissionalSelecionado: Employee?,
        precisaRetirarDiaDaRecyclerview: (dataASerRetirada: String) -> Unit,
        todosDiasAtualmenteNaRecylerView: ArrayList<Day>?,
        todosOsDias: ArrayList<Day>?,
        precisaRecolocarDias: (dias: ArrayList<Day>) -> Unit
    ) {
        if (profissionalSelecionado != null) {
            //profissional com dtDayOff empty quer dizer que ele nao tem nenhuma folga cadastrada
            if (profissionalSelecionado.dtDayOff != "") {
                //retira o dia da folga da lista de dias
                precisaRetirarDiaDaRecyclerview(profissionalSelecionado.dtDayOff)
                profissionalSelecionado
                profissionalSelecionado.dtDayOff


            }

            val listaDeDiasASeremRecolocados = arrayListOf<Day>()
            todosOsDias?.forEach { dia ->
                dia
                if(!todosDiasAtualmenteNaRecylerView!!.any{it.dateFormatted == dia.dateFormatted}){
                    if (dia.dateFormatted != profissionalSelecionado.dtDayOff) {
                        /**
                         * dia nao esta na recyclerview, mas deve estar
                         * coloca o dia de volta na lista de dias
                         */
                        listaDeDiasASeremRecolocados.add(dia)

                    }
                }

            }
            if (listaDeDiasASeremRecolocados.isNotEmpty()) {
                precisaRecolocarDias(listaDeDiasASeremRecolocados)
            }

            listaDeDiasASeremRecolocados
            todosDiasAtualmenteNaRecylerView



        }



    }


}