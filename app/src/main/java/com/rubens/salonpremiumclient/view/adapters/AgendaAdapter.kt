package com.rubens.salonpremiumclient.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rubens.salonpremiumclient.R
import com.rubens.salonpremiumclient.data.models.Appointment
import com.rubens.salonpremiumclient.databinding.ItemAgendaBinding
import com.rubens.salonpremiumclient.view.AgendaItemClickListener

class AgendaAdapter(private val listaAppointments: ArrayList<Appointment>, val agendaItemClickListener: AgendaItemClickListener): RecyclerView.Adapter<AgendaAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAgendaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return listaAppointments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindAppointment(listaAppointments[position])
    }

    inner class ViewHolder(val binding: ItemAgendaBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindAppointment(app: Appointment){
            binding.tvNumeroDia.text = app.day
            binding.tvHoraAtendimento.text = app.hour
            binding.tvMonthName.text = app.month
            binding.tvNomeServico.text = app.service
            binding.tvNomeAtendente.text = app.employee
            binding.tvDiaSemana.text = agendaItemClickListener.pegarDiaDaSemanaAbreviado(app.appointmentDayFormatted)
            when(app.confirmacaoAtendimento){
                "aguardando confirmacao"->{binding.statusAtendimento.setBackgroundResource(R.color.light_yellow)}
                "negado"->{binding.statusAtendimento.setBackgroundResource(R.color.vermelho)}
                "confirmado"->{binding.statusAtendimento.setBackgroundResource(R.color.verde)}
            }
            binding.root.setOnClickListener {
                agendaItemClickListener.onAgendaItemClickListener(app)
            }

        }

    }
}

