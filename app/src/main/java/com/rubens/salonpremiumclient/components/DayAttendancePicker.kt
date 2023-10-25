package com.rubens.salonpremiumclient.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.appsalaobelezaclientemvvm.model.dataClasses.Day
import com.rubens.salonpremiumclient.R
import com.rubens.salonpremiumclient.data.models.Hour
import com.rubens.salonpremiumclient.databinding.DayAttendancePickerBinding
import com.rubens.salonpremiumclient.view.adapters.DaysOfMonthAdapter
import com.rubens.salonpremiumclient.view.interfaces.DiaClickListener
import com.rubens.salonpremiumclient.view.interfaces.HoraClickListener
import java.util.HashSet

class DayAttendancePicker(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs) {

    private lateinit var hoursRvAdapter: DaysOfMonthAdapter
    private lateinit var daysRvAdapter: DaysOfMonthAdapter
    private val binding: DayAttendancePickerBinding = DayAttendancePickerBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    init {

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.DayAttendancePicker)
        setMonthText(attributes.getString(R.styleable.DayAttendancePicker_month))
        showBackArrow(attributes.getBoolean(R.styleable.DayAttendancePicker_showBackArrow, false))

    }

    fun showBackArrow(podeMostrar: Boolean) {
        if (podeMostrar){
            binding.arrowPreviousMonth.visibility = View.VISIBLE

        }else{
            binding.arrowPreviousMonth.visibility = View.INVISIBLE
        }
    }

    fun setMonthText(text: String?) {
        binding.tvLabelMonth.text = text

    }

    fun setYearText(text: String?){
        binding.tvLabelYear.text = text
    }

    fun setPeriodOfDayTextView(text: String) {
        binding.tvLabelMonth.text = text
    }


    /**
     * esse setRecyclerView pode servir para:
     * - recycler view de dias e meses
     * - recycler view de periodo do dia
     */
    fun setRecyclerView(listaDeDias: ArrayList<Day>, diaClickListener: DiaClickListener){
        daysRvAdapter = DaysOfMonthAdapter(listOfDays = listaDeDias, listOfHours = null, diaClickListener = diaClickListener)
        listaDeDias

        binding.daysOfMonthRecyclerView.adapter = daysRvAdapter
    }

    fun setHourRecyclerView(listaDeHorasPorPeriodo: ArrayList<Hour>, horaClickListener: HoraClickListener) {

        hoursRvAdapter = DaysOfMonthAdapter(listOfHours = listaDeHorasPorPeriodo, listOfDays = null, horaClickListener = horaClickListener)
        binding.daysOfMonthRecyclerView.adapter = hoursRvAdapter

    }

    fun getAdapterHoursListReference(): ArrayList<Hour>? {
        return hoursRvAdapter.listOfHours
    }

    fun getBackArrowReference(): ImageView {
        return binding.arrowPreviousMonth
    }

    fun getForwardArrowReference(): ImageView{
        return binding.arrowNextMonth
    }

    fun getMonthTextView(): TextView{
        return binding.tvLabelMonth
    }

    fun getYearTextView(): TextView{
        return binding.tvLabelYear
    }

    fun getAdapterListReference(): ArrayList<Day>? {
        daysRvAdapter.listOfDays
        return daysRvAdapter.listOfDays
    }

    fun removerItemDaDayRecyclerView(day: Day) {
        daysRvAdapter.listOfDays?.remove(day)
        daysRvAdapter.notifyDataSetChanged()
    }

    fun removerItemDaHourRecyclerView(hora: Hour) {
        hoursRvAdapter.listOfHours?.remove(hora)
        hoursRvAdapter.notifyDataSetChanged()

    }

    fun addNewDaysToRecyclerview(diasASeremColocados: ArrayList<Day>) {

        for(dia in diasASeremColocados){
            var inserido = false

            for(i in 0 until daysRvAdapter.listOfDays!!.size){
                val diaExistente = daysRvAdapter.listOfDays!![i]

                // Comparar as datas para encontrar a posição correta
                if(dia.dateFormatted < diaExistente.dateFormatted){
                    daysRvAdapter.listOfDays!!.add(i, dia)
                    inserido = true
                    break
                }
            }

            // Se não encontrou uma posição anterior, insira no final
            if(!inserido){
                daysRvAdapter.listOfDays!!.add(dia)

            }
        }



        daysRvAdapter.notifyDataSetChanged()


    }

    fun addNewHoursToHourRv(horasASeremRecolocadas: ArrayList<Hour>) {
        for(hora in horasASeremRecolocadas){
            var inserido = false

            for(i in 0 until hoursRvAdapter.listOfHours!!.size){
                val horaExistente = hoursRvAdapter.listOfHours!![i]

                // Comparar as horas para encontrar a posição correta
                if(hora.hour < horaExistente.hour){
                    hoursRvAdapter.listOfHours!!.add(i, hora)
                    inserido = true
                    break
                }
            }

            // Se não encontrou uma posição anterior, insira no final
            if(!inserido){
                hoursRvAdapter.listOfHours!!.add(hora)

            }
        }

        hoursRvAdapter.notifyDataSetChanged()

    }

    fun mudarCorForwardArrow(pressionado: Boolean){
        if(pressionado){
            binding.arrowNextMonth.setColorFilter(Color.WHITE)
        }else{
            binding.arrowNextMonth.colorFilter = null

        }
    }

    fun mudarCorBackArrow(pressionado: Boolean){
        if(pressionado){
            binding.arrowPreviousMonth.setColorFilter(Color.WHITE)
        }else{
            binding.arrowPreviousMonth.colorFilter = null

        }
    }

    fun showForwardArrowBackground(podeMostrar: Boolean){
        if(podeMostrar){
            binding.backgroundForwardArrow.visibility = View.VISIBLE
        }else{
            binding.backgroundForwardArrow.visibility = View.INVISIBLE

        }

    }

    fun showBackArrowBackground(podeMostrar: Boolean){
        if(podeMostrar){
            binding.backgroundBackArrow.visibility = View.VISIBLE
        }else{
            binding.backgroundBackArrow.visibility = View.INVISIBLE


        }

    }


}