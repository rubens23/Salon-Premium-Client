package com.rubens.salonpremiumclient.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.rubens.salonpremiumclient.R
import com.rubens.salonpremiumclient.data.models.Employee
import com.rubens.salonpremiumclient.data.models.Service
import com.rubens.salonpremiumclient.databinding.CircularItemsViewBinding
import com.rubens.salonpremiumclient.view.adapters.EmployeeAdapter
import com.rubens.salonpremiumclient.view.interfaces.EmployeeClickListener
import com.rubens.salonpremiumclient.view.interfaces.ServiceClickListener

class CircularItemsView(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs) {

    private lateinit var servicesRvAdapter: EmployeeAdapter
    private lateinit var professionalsRv: EmployeeAdapter
    private val binding: CircularItemsViewBinding = CircularItemsViewBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CircularItemsView)
        setLabelText(attributes.getString(R.styleable.CircularItemsView_labelText))


    }

    fun setLabelText(text: String?){
        binding.tvLabelChooseProfessional2.text = text
    }

    fun setProfessionalsRecyclerView(listaDeProfissionais: ArrayList<Employee>, employeeClickListener: EmployeeClickListener){
        professionalsRv = EmployeeAdapter(employeeList = listaDeProfissionais, servicesList = null, employeeClickListener = employeeClickListener)
        binding.professionalsRecyclerView.adapter = professionalsRv
    }

    fun setServicesRecyclerView(listaDeServicos: ArrayList<Service>, serviceClickListener: ServiceClickListener){
        servicesRvAdapter = EmployeeAdapter(employeeList = null, servicesList = listaDeServicos, serviceClickListener = serviceClickListener)
        binding.professionalsRecyclerView.adapter = servicesRvAdapter
    }

    fun getProfessionalsRecyclerviewItems(): ArrayList<Employee>? {
        return professionalsRv.employeeList
    }

    fun getServicesRecyclerviewItems(): ArrayList<Service>?{
        return servicesRvAdapter.servicesList
    }

    fun removerFuncionariosDaRecyclerView(todosOsFuncionariosASeremRetirados: ArrayList<String>) {
        todosOsFuncionariosASeremRetirados.forEach {
            funcionarioKey->
            val index = professionalsRv.employeeList?.indexOfFirst { it.employeeKey == funcionarioKey }

            if(index != -1){
                if (index != null) {
                    professionalsRv.employeeList?.removeAt(index)
                    professionalsRv.notifyItemRemoved(index)
                }
            }

        }


    }

    fun removerServicosDaRecyclerview(servicosQuePrecisamRetirados: List<Service>) {
        servicosQuePrecisamRetirados.forEach {
            servicesRvAdapter.servicesList?.remove(it)
            servicesRvAdapter.notifyDataSetChanged()

        }


    }

    fun addNewEmployeesToRv(funcionariosASeremRecolocadas: ArrayList<Employee>) {
        professionalsRv.employeeList?.addAll(funcionariosASeremRecolocadas)
        professionalsRv.notifyDataSetChanged()

    }

    fun addNewServicesToRv(servicosASeremRecolocadas: ArrayList<Service>) {
        servicesRvAdapter.servicesList?.addAll(servicosASeremRecolocadas)
        servicesRvAdapter.notifyDataSetChanged()

    }
}