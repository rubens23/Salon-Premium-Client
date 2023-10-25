package com.rubens.salonpremiumclient.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.appsalaobelezaclientemvvm.model.dataClasses.Day
import com.rubens.salonpremiumclient.data.models.Cliente
import com.rubens.salonpremiumclient.viewmodel.FragmentAgendarAtendimentosViewModel
import com.rubens.salonpremiumclient.data.models.Employee
import com.rubens.salonpremiumclient.data.models.Hour
import com.rubens.salonpremiumclient.data.models.Service
import com.rubens.salonpremiumclient.databinding.FragmentAgendarAtendimentosBinding
import com.rubens.salonpremiumclient.utils.helperdataclasses.DataDeAtendimentoSelecionada
import com.rubens.salonpremiumclient.utils.helperdataclasses.PeriodoDoDiaSelecionado
import com.rubens.salonpremiumclient.view.interfaces.DiaClickListener
import com.rubens.salonpremiumclient.view.interfaces.EmployeeClickListener
import com.rubens.salonpremiumclient.view.interfaces.HoraClickListener
import com.rubens.salonpremiumclient.view.interfaces.ServiceClickListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentAgendarAtendimentos : Fragment(), ServiceClickListener, EmployeeClickListener, DiaClickListener, HoraClickListener {

    private lateinit var binding: FragmentAgendarAtendimentosBinding


    private lateinit var viewModel: FragmentAgendarAtendimentosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgendarAtendimentosBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initViewModel()
        initCollectors()
        viewModel.pegarDadosDoUser()

        //configuracao do componente de escolher dia do atendimento
        initMonthTextViews()
        initVariables()


        changeDayRecyclerView(viewModel.obterListaDeDias(viewModel.mesSelecionado, viewModel.anoSelecionado))
        binding.dayAttendancePicker.showBackArrow(false)

        //configuracao do componente de escolher o horario do atendimento
        initPeriodOfDayTextView()
        changeHoursOfAttendanceRecyclerView(pegarListaDeHorariosPorPeriodo())
        binding.hourAttendancePicker.showBackArrow(false)
        viewModel.todosHorarios = pegarListaDeHorariosPorPeriodo()

        //iniciarRecyclerView de funcionarios e serviços
        pegarTodosOsFuncionarios()
        pegarTodosOsServicos()





        onClickListeners()


    }

    private fun pegarListaComTodosOsDiasDoMes(): ArrayList<Day> {
        return viewModel.transformDayListOfMapsToDayListOfDays(
            viewModel.obterListaDeDias(viewModel.mesSelecionado, viewModel.anoSelecionado)
        )
    }

    private fun pegarTodosOsServicos() {
        viewModel.pegarTodosOsServicos()
    }

    private fun pegarTodosOsFuncionarios() {
        viewModel.getAllEmployees()

    }

    private fun initViewModel() {
        viewModel =
            ViewModelProvider(requireActivity())[FragmentAgendarAtendimentosViewModel::class.java]
    }


    private fun onClickListeners() {
        binding.dayAttendancePicker.getBackArrowReference().setOnClickListener {
            val lista = viewModel.monthSelector.subtractOneMonth(
                binding.dayAttendancePicker.getMonthTextView(),
                binding.dayAttendancePicker.getYearTextView()
            )
            textViewsSetters(lista)
            //lista[0] é o mes e lista[1] é o ano
            setSelectedMonth(lista[0])
            setSelectedYear(lista[1])
            dealWithBackArrowVisibility(lista)
            changeDayRecyclerView(
                viewModel.obterListaDeDias(
                    viewModel.mesSelecionado,
                    viewModel.anoSelecionado
                )
            )

            Toast.makeText(requireContext(), "cliquei na seta de retornar", Toast.LENGTH_SHORT).show()
            showDayBackArrowBackground(true)
            showDayForwardArrowBackground(false)



        }

        binding.dayAttendancePicker.getForwardArrowReference().setOnClickListener {
            val lista = viewModel.monthSelector.addOneMonth(
                binding.dayAttendancePicker.getMonthTextView(),
                binding.dayAttendancePicker.getYearTextView()
            )
            textViewsSetters(lista)
            //lista[0] é o mes e lista[1] é o ano
            setSelectedMonth(lista[0])
            setSelectedYear(lista[1])
            //todo fazer o algoritmo de mostrar o background das arrows
            dealWithBackArrowVisibility(lista)
            changeDayRecyclerView(
                viewModel.obterListaDeDias(
                    viewModel.mesSelecionado,
                    viewModel.anoSelecionado
                )
            )

            Toast.makeText(requireContext(), "cliquei na seta de avancar", Toast.LENGTH_SHORT).show()
            showDayForwardArrowBackground(true)
            showDayBackArrowBackground(false)


        }

        binding.hourAttendancePicker.getForwardArrowReference().setOnClickListener {
            val periodoDoDia = viewModel.avancarPeriodoDoDia()
            setPeriodOfDayTextView(periodoDoDia)
            //todo mudar a recyclerview dos horarios
            changeHoursOfAttendanceRecyclerView(pegarListaDeHorariosPorPeriodo())
            dealWithPeriodOfDayBackArrow(periodoDoDia)
            dealWithPeriodOfDayForwardArrow(periodoDoDia)

            showHourForwardArrowBackground(true)
            showHourBackArrowBackground(false)


        }

        binding.hourAttendancePicker.getBackArrowReference().setOnClickListener {
            val periodoDoDia = viewModel.retrocederPeriodoDoDia()
            setPeriodOfDayTextView(periodoDoDia)
            changeHoursOfAttendanceRecyclerView(pegarListaDeHorariosPorPeriodo())
            dealWithPeriodOfDayBackArrow(periodoDoDia)
            dealWithPeriodOfDayForwardArrow(periodoDoDia)

            showHourForwardArrowBackground(false)
            showHourBackArrowBackground(true)
        }

        binding.btnSetAppointment.setOnClickListener {
            viewModel.sendAppointmentToDatabase()
        }

        binding.imgProfileIcon.setOnClickListener {
            abrirImagePickerFromGallery()
        }

        binding.imgAvatar.setOnClickListener {
            abrirImagePickerFromGallery()
        }
    }

    private fun abrirImagePickerFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
            if(result.resultCode == Activity.RESULT_OK){
                if(result.data != null){
                    val data: Intent = result.data!!
                    mostrarImagemProvisoria(data.data)
                    viewModel.salvarImagemEscolhidaNoFirebase(data.data!!)
                }
            }
        }

    private fun mostrarImagemProvisoria(imageUri: Uri?) {
        imageUri?.let {
            binding.imgProfileIcon.visibility = View.GONE
            Picasso.get().load(imageUri).into(binding.imgAvatar)

            //binding.profileImageChange.setImageURI(it)


        }
    }

    private fun showDayBackArrowBackground(showBackground: Boolean) {
        //se a back arrow estiver visivel, muda o estilo dela, se n, volta pro estilo original
        if(showBackground){
            if(binding.dayAttendancePicker.getBackArrowReference().isVisible){
                binding.dayAttendancePicker.mudarCorBackArrow(true)
                binding.dayAttendancePicker.showBackArrowBackground(true)
            }else{
                binding.dayAttendancePicker.mudarCorBackArrow(false)
                binding.dayAttendancePicker.showBackArrowBackground(false)
            }
        }else{
            //esconde o background se ele não deve ser mostrado
            if(binding.dayAttendancePicker.getBackArrowReference().isVisible){
                binding.dayAttendancePicker.mudarCorBackArrow(false)
                binding.dayAttendancePicker.showBackArrowBackground(false)}
        }


    }

    private fun showDayForwardArrowBackground(showBackground: Boolean){
        //se a forward arrow estiver visivel, muda o estilo dela, se n, volta pro estilo original
        if(showBackground){
            if(binding.dayAttendancePicker.getForwardArrowReference().isVisible){
                binding.dayAttendancePicker.mudarCorForwardArrow(true)
                binding.dayAttendancePicker.showForwardArrowBackground(true)
            }else{
                binding.dayAttendancePicker.mudarCorForwardArrow(false)
                binding.dayAttendancePicker.showForwardArrowBackground(false)
            }
        }else{
            //esconde o background se ele não deve ser mostrado
            if(binding.dayAttendancePicker.getForwardArrowReference().isVisible){
                binding.dayAttendancePicker.mudarCorForwardArrow(false)
                binding.dayAttendancePicker.showForwardArrowBackground(false)
            }
        }


    }

    private fun showHourBackArrowBackground(showBackground: Boolean) {
        //se a back arrow estiver visivel, muda o estilo dela, se n, volta pro estilo original
        if(showBackground){
            if(binding.hourAttendancePicker.getBackArrowReference().isVisible){
                binding.hourAttendancePicker.mudarCorBackArrow(true)
                binding.hourAttendancePicker.showBackArrowBackground(true)
            }else{
                binding.hourAttendancePicker.mudarCorBackArrow(false)
                binding.hourAttendancePicker.showBackArrowBackground(false)
            }
        }else{
            //esconde o background se ele não deve ser mostrado
            if(binding.hourAttendancePicker.getBackArrowReference().isVisible){
                binding.hourAttendancePicker.mudarCorBackArrow(false)
                binding.hourAttendancePicker.showBackArrowBackground(false)}
        }


    }

    private fun showHourForwardArrowBackground(showBackground: Boolean){
        //se a forward arrow estiver visivel, muda o estilo dela, se n, volta pro estilo original
        if(showBackground){
            if(binding.hourAttendancePicker.getForwardArrowReference().isVisible){
                binding.hourAttendancePicker.mudarCorForwardArrow(true)
                binding.hourAttendancePicker.showForwardArrowBackground(true)
            }else{
                binding.hourAttendancePicker.mudarCorForwardArrow(false)
                binding.hourAttendancePicker.showForwardArrowBackground(false)
            }
        }else{
            //esconde o background se ele não deve ser mostrado
            if(binding.hourAttendancePicker.getForwardArrowReference().isVisible){
                binding.hourAttendancePicker.mudarCorForwardArrow(false)
                binding.hourAttendancePicker.showForwardArrowBackground(false)
            }
        }


    }



    private fun dealWithPeriodOfDayForwardArrow(selectedPeriodOfDay: String) {
        when(selectedPeriodOfDay){
            "Manhã"->{
                //show forward arrow
                hourForwardArrowVisibility(true)
            }
            "Tarde"->{
                //show forward arrow
                hourForwardArrowVisibility(true)
            }
            "Noite"->{
                //hide forward arrow
                hourForwardArrowVisibility(false)
            }
        }
    }

    private fun dealWithPeriodOfDayBackArrow(selectedPeriodOfDay: String) {
        when(selectedPeriodOfDay){
            "Manhã"->{
                //hide back arrow
                hourBackArrowVisibility(false)
            }
            "Tarde"->{
                //show back arrow
                hourBackArrowVisibility(true)
            }
            "Noite"->{
                //show backArrow
                hourBackArrowVisibility(true)
            }
        }
    }

    private fun hourBackArrowVisibility(podeMostrar: Boolean){
        if(podeMostrar){
            binding.hourAttendancePicker.getBackArrowReference().visibility = View.VISIBLE
        }else{
            binding.hourAttendancePicker.getBackArrowReference().visibility = View.INVISIBLE

        }
    }

    private fun hourForwardArrowVisibility(podeMostrar: Boolean){
        if(podeMostrar){
            binding.hourAttendancePicker.getForwardArrowReference().visibility = View.VISIBLE
        }else{
            binding.hourAttendancePicker.getForwardArrowReference().visibility = View.INVISIBLE

        }
    }

    private fun dealWithBackArrowVisibility(lista: ArrayList<String>) {
        val selectedMon = lista[0]
        val selectedYe = lista[1]
        if (selectedMon == viewModel.mesAtual.capitalize() && selectedYe == viewModel.anoAtual) {
            binding.dayAttendancePicker.showBackArrow(false)


        } else {
            binding.dayAttendancePicker.showBackArrow(true)
        }

    }


    private fun textViewsSetters(lista: ArrayList<String>) {
        setMonthTextView(lista[0])
        setYearTextView(lista[1])
    }

    private fun changeDayRecyclerView(listaDeDias: List<Map<String, String>>) {
        val lstDeDias =   viewModel.transformDayListOfMapsToDayListOfDays(
            listaDeDias
        )
        viewModel.todosDias = ArrayList(lstDeDias)
        binding.dayAttendancePicker.setRecyclerView(
          lstDeDias, this
        )


    }

    private fun changeHoursOfAttendanceRecyclerView(listaDeHorasPorPeriodo: ArrayList<Hour>) {
        binding.hourAttendancePicker.setHourRecyclerView(
            listaDeHorasPorPeriodo, this
        )

        viewModel.todosHorarios = listaDeHorasPorPeriodo
    }

    private fun pegarListaDeHorariosPorPeriodo(): ArrayList<Hour> {
        return viewModel.obterListaDeHorasPorPeriodo(viewModel.selectedPeriodOfDay)
    }






    private fun initVariables() {
        setSelectedMonth(viewModel.monthSelector.getCurrentMonth())
        setSelectedYear(viewModel.monthSelector.getCurrentYear())
        setCurrentMonth(viewModel.monthSelector.getCurrentMonth())
        setCurrentYear(viewModel.monthSelector.getCurrentYear())
    }

    private fun setSelectedYear(text: String) {
        viewModel.anoSelecionado = text

    }

    private fun setSelectedMonth(text: String) {
        viewModel.mesSelecionado = text
    }

    private fun setCurrentYear(text: String) {
        viewModel.anoAtual = text
    }

    private fun setCurrentMonth(text: String) {
        viewModel.mesAtual = text
    }

    private fun initCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouTodosOsFuncionarios.collectLatest {
                        listaFuncionarios->
                    viewModel.todosFuncionarios?.clear()
                    viewModel.todosFuncionarios = ArrayList(listaFuncionarios)
                    changeEmployeeRecyclerView(listaFuncionarios)





                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.colocouNovoAppointment.collectLatest {
                        msg->
                    showToast(msg)

                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouTodosOsServicos.collectLatest {
                        listaDeServicos->
                    changeServicesRecyclerView(listaDeServicos)

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouDadosDoCliente.collectLatest {
                        dadosCliente->
                    mostrarDadosDoClienteNasViews(dadosCliente)

                }
            }
        }



        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouDataASerRetirada.collectLatest {
                        dataASerRetirada->
                    dataASerRetirada
                    binding.dayAttendancePicker.getAdapterListReference()?.let {
                        listaDeDias->
                        val itemToRemove = listaDeDias.find { it.dateFormatted ==  dataASerRetirada}

                        itemToRemove?.let {
                            binding.dayAttendancePicker.removerItemDaDayRecyclerView(it)

                        }
                    }


                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pegouDiasQuePrecisamSerRecolocadosNaRv.collectLatest { diasASeremColocados ->
                    binding.dayAttendancePicker.addNewDaysToRecyclerview(diasASeremColocados)

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouFuncionariosQuePrecisamSerRetirados.collectLatest {
                        todosOsFuncionariosASeremRetirados->
                    excluirFuncionariosDaRecyclerView(todosOsFuncionariosASeremRetirados)



                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouServicosQuePrecisamSerRetirados.collectLatest {
                        pegouServicosQuePrecisamSerRetirados->
                    excluirServicosDaRecyclerView(pegouServicosQuePrecisamSerRetirados)



                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouHorasASeremRetiradas.collectLatest {
                        horasASeremRetiradas->
                    binding.hourAttendancePicker.getAdapterHoursListReference()?.let {
                            listaDeHoras->
                        listaDeHoras.forEach {
                            horaDaListaDeHoras->


                            horasASeremRetiradas.forEach {
                                horaASerRetirada->

                                if(horaDaListaDeHoras.hour == horaASerRetirada["hora"]){
                                    binding.hourAttendancePicker.removerItemDaHourRecyclerView(horaDaListaDeHoras)
                                }
                            }

                        }

                    }


                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouHorasQuePrecisamSerRecolocadasNaRv.collectLatest {
                        horasASeremRecolocadas->
                    binding.hourAttendancePicker.addNewHoursToHourRv(horasASeremRecolocadas)


                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouFuncionariosQuePrecisamSerRecolocadosNaRv.collectLatest {
                        funcionariosASeremRecolocadas->
                    binding.professionalsCircleItemView.addNewEmployeesToRv(funcionariosASeremRecolocadas)


                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouServicosQuePrecisamSerRecolocadosNaRv.collectLatest {
                        servicosASeremRecolocadas->
                    binding.servicesCircleItemView.addNewServicesToRv(servicosASeremRecolocadas)


                }
            }
        }
    }

    private fun mostrarDadosDoClienteNasViews(dadosCliente: Cliente) {
        binding.tvLabelClientName.text = "Olá, ${dadosCliente.nomeCliente.capitalize()}"



        if(dadosCliente.linkFotoCliente.isNotEmpty()){
            binding.imgProfileIcon.visibility = View.GONE
            Picasso.get().load(dadosCliente.linkFotoCliente).into(binding.imgAvatar)

        }

    }

    private fun excluirServicosDaRecyclerView(pegouServicosQuePrecisamSerRetirados: List<Service>) {
        binding.servicesCircleItemView.removerServicosDaRecyclerview(pegouServicosQuePrecisamSerRetirados)

    }

    private fun excluirFuncionariosDaRecyclerView(todosOsFuncionariosASeremRetirados: ArrayList<String>) {
        binding.professionalsCircleItemView.removerFuncionariosDaRecyclerView(todosOsFuncionariosASeremRetirados)

    }

    private fun changeEmployeeRecyclerView(listaFuncionarios: ArrayList<Employee>) {
        binding.professionalsCircleItemView.setProfessionalsRecyclerView(listaFuncionarios, this)

    }

    private fun changeServicesRecyclerView(listaServicos: ArrayList<Service>){
        binding.servicesCircleItemView.setServicesRecyclerView(listaServicos, this)
    }

    private fun initMonthTextViews() {
        setMonthTextView(viewModel.monthSelector.getCurrentMonth())
        setYearTextView(viewModel.monthSelector.getCurrentYear())
    }

    private fun initPeriodOfDayTextView() {
        setPeriodOfDayTextView(viewModel.selectedPeriodOfDay)
    }



    private fun setYearTextView(text: String) {
        binding.dayAttendancePicker.setYearText(text)

    }

    private fun setMonthTextView(text: String) {
        binding.dayAttendancePicker.setMonthText(text)

    }

    private fun setPeriodOfDayTextView(selectedPeriodOfDay: String) {
        if(selectedPeriodOfDay != ""){
            binding.hourAttendancePicker.setPeriodOfDayTextView(selectedPeriodOfDay)
        }

    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()

    }



    private fun lidarComCliqueEmItemDeAtendimento() {

        viewModel.lidarComCliqueEmIemDeAgendamento(binding.professionalsCircleItemView.getProfessionalsRecyclerviewItems(),
            binding.servicesCircleItemView.getServicesRecyclerviewItems(),
            binding.dayAttendancePicker.getAdapterListReference(),
            binding.hourAttendancePicker.getAdapterHoursListReference())

    }

    override fun onEmployeeClick(employee: Employee) {
        viewModel.employee = employee
        lidarComCliqueEmItemDeAtendimento()

    }


    override fun onServiceClick(service: Service) {
        viewModel.service = service
        lidarComCliqueEmItemDeAtendimento()


    }

    override fun onDayClickListener(day: Day) {
        viewModel.dataDeAtendimentoSelecionada = DataDeAtendimentoSelecionada(viewModel.mesSelecionado,
            viewModel.anoSelecionado,
            day.numberDay, viewModel.obterDataFormatada(day.numberDay,viewModel.mesSelecionado,
            viewModel.anoSelecionado))
        lidarComCliqueEmItemDeAtendimento()



    }

    override fun onHourClick(hora: Hour) {
        viewModel.periodoDoDiaSelecionado = PeriodoDoDiaSelecionado(viewModel.selectedPeriodOfDay,
            hora.hour)
        lidarComCliqueEmItemDeAtendimento()


    }





}