package com.rubens.salonpremiumclient.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appsalaobelezaclientemvvm.model.dataClasses.Day
import com.rubens.salonpremiumclient.R
import com.rubens.salonpremiumclient.data.models.Hour
import com.rubens.salonpremiumclient.databinding.DayItemBinding
import com.rubens.salonpremiumclient.view.interfaces.DiaClickListener
import com.rubens.salonpremiumclient.view.interfaces.HoraClickListener

class DaysOfMonthAdapter(
    val listOfDays: ArrayList<Day>? = null,
    val listOfHours: ArrayList<Hour>? = null,
    val diaClickListener: DiaClickListener? = null,
    val horaClickListener: HoraClickListener? = null
) :
    RecyclerView.Adapter<DaysOfMonthAdapter.ViewHolder>() {

    var diaSelecionado: Int? = null
    var horaSelecionada: Int? = null


    inner class ViewHolder(val binding: DayItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindDayItem(day: Day, position: Int) {
            setDayTextView(day)
            modifyDayBackgroundIfSelected(day)

            binding.tvNumberDay.setOnClickListener {
                lidarComOCliqueNumDia(day, position)
                clicouNumDia(day)
            }

            binding.tvLabelDay.setOnClickListener {
                lidarComOCliqueNumDia(day, position)
                clicouNumDia(day)


            }
        }



        fun bindHourItem(hour: Hour, position: Int) {
            setHourTextView(hour)
            modifyHourBackgroundIfSelected(hour)

            binding.tvNumberDay.setOnClickListener {
                lidarComOCliqueNumaHora(hour, position)
                clicouNumaHora(hour)
            }

        }

        private fun clicouNumaHora(hour: Hour) {
            horaClickListener?.onHourClick(hour)
        }

        private fun clicouNumDia(day: Day) {
            diaClickListener?.onDayClickListener(day)

        }


        private fun setHourTextView(hour: Hour) {
            binding.tvLabelDay.visibility = View.GONE
            binding.tvNumberDay.text = hour.hour
        }


        private fun setDayTextView(day: Day) {
            binding.tvLabelDay.text = day.labelDay
            binding.tvNumberDay.text = day.numberDay
        }


        private fun modifyDayBackgroundIfSelected(day: Day) {
            if (day.isSelected) {
                binding.ivNumberBackground.visibility = View.VISIBLE
                binding.tvNumberDay.setTextColor(binding.root.context.resources.getColor(R.color.white))
            } else {
                binding.ivNumberBackground.visibility = View.INVISIBLE
                binding.tvNumberDay.setTextColor(binding.root.context.resources.getColor(R.color.alt_black2))
            }
        }

        private fun modifyHourBackgroundIfSelected(hour: Hour) {
            if (hour.isSelected) {
                binding.ivNumberBackground.setImageResource(R.drawable.time_background)
                binding.ivNumberBackground.visibility = View.VISIBLE
                binding.tvNumberDay.setTextColor(binding.root.context.resources.getColor(R.color.white))
            } else {
                binding.ivNumberBackground.visibility = View.INVISIBLE
                binding.tvNumberDay.setTextColor(binding.root.context.resources.getColor(R.color.alt_black2))
            }
        }


        private fun lidarComOCliqueNumDia(day: Day, position: Int) {
//            diaSelecionado?.let {
//                if (listOfDays != null) {
//                    listOfDays[it].isSelected = false
//                    notifyDataSetChanged()
//                }
//
//            }

            listOfDays?.forEach {
                    dia ->
                if(dia != day){
                    if(dia.isSelected){
                        dia.isSelected = false
                        notifyDataSetChanged()
                    }
                }
            }


            day.isSelected = true
            binding.ivNumberBackground.visibility = View.VISIBLE
            binding.tvNumberDay.setTextColor(binding.root.context.resources.getColor(R.color.white))


        }

        private fun lidarComOCliqueNumaHora(hour: Hour, position: Int) {
//            horaSelecionada?.let {
//                if (listOfHours != null) {
//                    listOfHours[it].isSelected = false
//                    notifyDataSetChanged()
//                }
//
//            }

            listOfHours?.forEach {
                    hora ->
                if(hora != hour){
                    if(hora.isSelected){
                        hora.isSelected = false
                        notifyDataSetChanged()
                    }
                }
            }


            hour.isSelected = true
            binding.ivNumberBackground.setImageResource(R.drawable.time_background)
            binding.ivNumberBackground.visibility = View.VISIBLE
            binding.tvNumberDay.setTextColor(binding.root.context.resources.getColor(R.color.white))



        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (listOfDays != null){
            holder.bindDayItem(listOfDays[position], position)


        }
        if(listOfHours != null){
            holder.bindHourItem(listOfHours[position], position)

        }
    }

    override fun getItemCount(): Int{
        if (listOfDays != null){
            return listOfDays.size


        }
        if(listOfHours != null){
            return listOfHours.size

        }
        return 0

    }





}