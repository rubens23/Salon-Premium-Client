package com.rubens.salonpremiumclient.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.rubens.month_selector.MonthSelectorHelper
import com.rubens.salonpremiumclient.viewmodel.FragmentAgendaViewModel
import com.rubens.salonpremiumclient.R
import com.rubens.salonpremiumclient.data.models.Appointment
import com.rubens.salonpremiumclient.databinding.FragmentAgendaBinding
import com.rubens.salonpremiumclient.view.adapters.AgendaAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentAgenda : Fragment(), AgendaItemClickListener {



    private lateinit var viewModel: FragmentAgendaViewModel
    private lateinit var binding: FragmentAgendaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAgendaBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initCollectors()
    }

    override fun onResume() {
        super.onResume()

        viewModel.pegarTodasAppointmentsDoCliente()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[FragmentAgendaViewModel::class.java]

    }

    private fun initCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouListaDeAppointments.collectLatest {
                        listaAppointments->
                    changeAppointmentsRv(listaAppointments)

                }
            }
        }

    }

    private fun changeAppointmentsRv(listaAppointments: ArrayList<Appointment>) {
        binding.appointmensRv.adapter = AgendaAdapter(listaAppointments, this)

    }

    override fun onAgendaItemClickListener(appointment: Appointment) {
        var message = ""
        val title = "status de atendimento:"
        val builder = AlertDialog.Builder(requireContext())


        when(appointment.confirmacaoAtendimento){
            "aguardando confirmacao"->{
                message = "Seu atendimento ainda está em análise"
                builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
            }
            "negado"->{
                message = "Seu atendimento foi negado. Reagende para novo horário ou dia"
                builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Reagendar") { dialog, _ ->
                        // Lógica para lidar com o clique em "Reagendar"
                        view?.findNavController()?.navigate(R.id.marcarHorariosFragment)

                        dialog.dismiss()
                    }

            }
            "confirmado"->{
                message = "Seu atendimento está confirmado! Basta comparecer no salão no horário marcado!"
                builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }}
        }



        // Exibe o AlertDialog
        builder.create().show()
    }

    override fun pegarDiaDaSemanaAbreviado(data: String): String {
        return viewModel.pegarDiaDaSemanaAbreviado(data)
    }


}