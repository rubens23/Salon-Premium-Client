package com.rubens.salonpremiumclient.data.repositories

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rubens.salonpremiumclient.data.models.Appointment
import com.rubens.salonpremiumclient.data.models.Cliente
import com.rubens.salonpremiumclient.data.models.Employee
import com.rubens.salonpremiumclient.data.models.Service


class FirebaseRepositoryImpl: FirebaseRepository {
    private lateinit var databaseRef: DatabaseReference
    private lateinit var databaseRefServicos: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var databaseRefHorariosMarcados: DatabaseReference
    private lateinit var databaseRefClientes: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage


    init {
        initFirebaseInstances()
    }

    private fun initFirebaseInstances() {
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads")
        storageRef = FirebaseStorage.getInstance().getReference("uploads")
        databaseRefServicos = FirebaseDatabase.getInstance().getReference("servicos")
        databaseRefHorariosMarcados = FirebaseDatabase.getInstance().getReference("appointments")
        auth = FirebaseAuth.getInstance()
        databaseRefClientes = FirebaseDatabase.getInstance().getReference("clientes")
        storage = FirebaseStorage.getInstance()

    }

    override fun updateListaDeServicos(funcionarioKey: String, listaDeServicosAtualizada: (listaServicos: ArrayList<String>) -> Unit) {
        val listaServicosDoFuncionario: ArrayList<String> = arrayListOf()

        databaseRef.orderByChild("childKey").equalTo(funcionarioKey)
            .addListenerForSingleValueEvent(
                object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            it.children.forEach {
                                if(it.key == "listaServicos"){
                                    it.children.forEach {
                                        servico->
                                        listaServicosDoFuncionario.add(servico.value as String)

                                    }
                                }
                            }
                        }
                        listaDeServicosAtualizada(listaServicosDoFuncionario)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("dblistenererror", error.message)

                    }

                }
            )
    }

    override fun updateServicosDoFuncionario(
        childKey: String,
        funcionario: Employee,
        atualizouListaDeServicos: (atualizouServicos: Boolean) -> Unit
    ) {
        databaseRef.child(childKey)
            .setValue(funcionario)
            .addOnSuccessListener {
                atualizouListaDeServicos(true)
            }.addOnFailureListener {
                atualizouListaDeServicos(false)

            }
    }

    override fun removerServicoDoFuncionario(
        funcionarioKey: String,
        servicoFuncionario: String,
        deletouServicoDoFuncionario: (deletou: Boolean, servico: String)->Unit
    ) {
        databaseRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    if(it.key == funcionarioKey){
                        it.children.forEach {
                            if(it.hasChildren()){
                                it.children.forEach {
                                    servico->
                                    if(servico.value == servicoFuncionario){
                                        servico.ref.removeValue().addOnSuccessListener {
                                            deletouServicoDoFuncionario(true, servicoFuncionario)
                                        }.addOnFailureListener {
                                            deletouServicoDoFuncionario(false, "")
                                        }


                                    }
                                }
                            }
                        }

                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("testeremove", "to no onCancelled")
            }

        })

    }

    override fun pegarTodosFuncionarios(pegouTodosOsFuncionarios: (todosOsFuncionarios: ArrayList<Employee>) -> Unit) {
        val listaDeFuncionarios: ArrayList<Employee> = arrayListOf()
        databaseRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val childValue = it.getValue() as? Map<String, Any>

                    val employee = fazerEmployeeAPartirDeSnapshot(childValue)
                    if (employee != null){
                        if(!listaDeFuncionarios.contains(employee)){
                            listaDeFuncionarios.add(employee)

                        }

                    }
                }
                pegouTodosOsFuncionarios(listaDeFuncionarios)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("getfuncionarios", "onCancelled: ${error.message}")
            }

        })
    }

    private fun fazerEmployeeAPartirDeSnapshot(childValue: Map<String, Any>?): Employee {
        return Employee(
            name = childValue!!.get("nome") as String,
            job = childValue.get("cargo") as String,
            imageLink = childValue.get("linkImagem") as String,
            employeeKey = childValue.get("childKey") as String,
            servicesList = childValue.get("listaServicos") as ArrayList<String>,
            dtDayOff = childValue.get("dataFolga") as String
        )
    }


    private fun pegarServicoAtravesDoSnapshot(childValue: Map<String, Any>?): Service {
        return Service(
            serviceName = childValue!!.get("nomeServico") as String,
            imageLink = childValue.get("linkImagem") as String

        )
    }

    private fun pegarAppointmentsPorSnapshot(childValue: Map<String, Any>?): Appointment {
        return Appointment(
            day = childValue!!["day"] as String,
            month = childValue["month"] as String,
            employee = childValue["employee"] as String,
            hour = childValue["hour"] as String,
            service = childValue["service"] as String,
            clientName = childValue["clientName"] as String,
            employeeKey = childValue["employeeKey"] as String,
            appointmentDayFormatted = childValue["appointmentDayFormatted"] as String,
            confirmacaoAtendimento = childValue["confirmacaoAtendimento"] as String,
            appointmentId = childValue["appointmentId"] as String,
            clientUid = childValue["clientUid"] as String?
        )

    }

    override fun sendPhotoToStorage(imgUri: Uri,
                                    fileExtension: String?,
                                    enviouFotoParaOStorage: (url: String?)->Unit) {
        val fileReference = storageRef.child(
            "uploads/" + System.currentTimeMillis().toString()
                    + "." + fileExtension
        )

        fileReference.putFile(imgUri).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                downloadUrl->
                enviouFotoParaOStorage(downloadUrl.toString())

            }.addOnFailureListener {
                enviouFotoParaOStorage("")

            }
        }.addOnFailureListener {
            enviouFotoParaOStorage("")

        }
    }

    override fun saveFuncionarioInDatabase(
        urlFoto: String,
        salvouFuncionarioNoDatabase: (salvou: Boolean) -> Unit,
        corteCabelo: String,
        pinturaCabelo: String,
        manicure: String,
        pedicure: String,
        nomeFuncionario: String,
        cargoFuncionario: String

    ) {
        val listaDeServicos = fazerListaDeServicos(corteCabelo,
        pinturaCabelo,
        manicure,
        pedicure)

        val uploadId = databaseRef.push().key

        val funcionario = Employee(
            name = nomeFuncionario,
            job = cargoFuncionario,
            imageLink = urlFoto,
            employeeKey = uploadId!!,
            servicesList = listaDeServicos
        )

        databaseRef.child(uploadId).setValue(funcionario)
            .addOnSuccessListener {
                salvouFuncionarioNoDatabase(true)

            }.addOnFailureListener {
                salvouFuncionarioNoDatabase(false)
            }


    }

    override fun pegarFolgas(
        funcionarioKey: String,
        pegouFolga: (folgaDoFuncionario: String) -> Unit
    ) {
        databaseRef.orderByChild("childKey").equalTo(funcionarioKey)
            .addListenerForSingleValueEvent(
                object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            it.children.forEach {
                                funcionario->
                                if(funcionario.key == "dataFolga"){
                                    val dataFolga = funcionario.value as String
                                    pegouFolga(dataFolga)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("dblistenererror", error.message)
                    }

                }
            )
    }

    override fun salvarNovaFolga(
        funcionarioKey: String,
        dataFolga: String,
        salvouNovaFolga: (salvou: Boolean) -> Unit
    ) {
        databaseRef.child(funcionarioKey).child("dataFolga")
            .setValue(dataFolga)
            .addOnSuccessListener {
                salvouNovaFolga(true)
            }.addOnFailureListener {
                salvouNovaFolga(false)
            }
    }

    override fun saveServicoInDatabase(
        urlFoto: String?,
        nomeServico: String,
        salvouNovoServico: (salvou: Boolean) -> Unit
    ) {
        val servico = Service(serviceName = nomeServico, imageLink = urlFoto!!)
        val servicoId = databaseRefServicos.push().key
        databaseRefServicos.child(servicoId!!).setValue(servico).addOnSuccessListener {
            salvouNovoServico(true)
        }.addOnFailureListener {
            salvouNovoServico(false)
        }
    }

    override fun pegarTodosServicos(pegouTodosOsServicos: (servicos: ArrayList<Service>) -> Unit) {
        val listaDeServicos: ArrayList<Service> = arrayListOf()

        databaseRefServicos.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val childValue = it.getValue() as? Map<String, Any>
                    val servico = pegarServicoAtravesDoSnapshot(childValue)
                    listaDeServicos.add(servico)

                }
                pegouTodosOsServicos(listaDeServicos)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("errogetserv", error.message.toString())

            }

        })
    }

    override fun pegarTodosAppointments(pegouTodosAppointments: (listaAppointments: ArrayList<Appointment>) -> Unit) {
        val listaAppointments: ArrayList<Appointment> = arrayListOf()
        databaseRefHorariosMarcados.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val childValue = it.getValue() as? Map<String, Any>
                    val appointment = pegarAppointmentsPorSnapshot(childValue)
                    if(!listaAppointments.contains(appointment)){
                        listaAppointments.add(appointment)
                    }
                }
                pegouTodosAppointments(listaAppointments)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("erroappoint", error.message)
            }

        })
    }

    override fun pegarTodosAppointmentsDoCliente(pegouTodosAppointments: (listaAppointments: ArrayList<Appointment>) -> Unit) {
        val currentUserUid = auth.currentUser?.uid
        if(currentUserUid != null){
            databaseRefHorariosMarcados.orderByChild("clientUid").equalTo(currentUserUid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val appointmentsList = ArrayList<Appointment>()
                    for (appointmentSnapshot in snapshot.children) {
                        val childValue = appointmentSnapshot.getValue() as? Map<String, Any>
                        val appointment = pegarAppointmentsPorSnapshot(childValue)
                        if(!appointmentsList.contains(appointment)){
                            appointmentsList.add(appointment)
                        }

                    }
                    pegouTodosAppointments(appointmentsList)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Trate o erro aqui
                }
            })
        }

    }


    override fun sendAppointmentToFirebase(
        appointment: Appointment,
        salvouAppointment: (salvou: Boolean) -> Unit
    ) {
        val uploadId = databaseRefHorariosMarcados.push().key

        if (uploadId != null) {
            appointment.appointmentId = uploadId
        }
        databaseRefHorariosMarcados.child(uploadId!!).setValue(appointment)
            .addOnSuccessListener { salvouAppointment(true) }
            .addOnFailureListener { salvouAppointment(false) }
    }

    override fun cadastrarNovoUsuario(
        email: String,
        nome: String,
        sobrenome: String,
        senha: String,
        confirmacaoSenha: String,
        cadastrouComSucesso: (msgResultadoCadastro: String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener {
            cadastro->
            if(cadastro.isSuccessful){
                mandarInformacoesDoClienteParaORealtimeDatabase(email, nome, sobrenome, cadastrouComSucesso)
            }
        }.addOnFailureListener {
            if (it.message.toString() == "The email address is already in use by another account.") {
                cadastrouComSucesso("Esse endereço de email já é usado em outra conta!")
            }else{
                cadastrouComSucesso("Ocorreu alguma falha no cadastro")

            }
        }



    }

    private fun mandarInformacoesDoClienteParaORealtimeDatabase(
        email: String,
        nome: String,
        sobrenome: String,
        cadastrouComSucesso: (msgResultadoCadastro: String) -> Unit
    ) {


        val novaChave = auth.currentUser?.uid
        val cliente = novaChave?.let { Cliente(nomeCliente = nome, sobrenomeCliente = sobrenome, emailCliente = email, keyCliente = it) }

        novaChave?.let {
            databaseRefClientes.child(it).setValue(cliente).addOnSuccessListener {
                cadastrouComSucesso("cadastrado com sucesso!")
            }.addOnFailureListener {
                cadastrouComSucesso("erro no cadastro")



            }
        }

    }


    private fun fazerListaDeServicos(corteCabelo: String, pinturaCabelo: String, manicure: String, pedicure: String): ArrayList<String> {
        val listaServicos: ArrayList<String> = arrayListOf()

        if (corteCabelo == "Corte de Cabelo") {
            listaServicos.add(corteCabelo)
        }
        if (pinturaCabelo == "Pintura de Cabelo") {
            listaServicos.add(pinturaCabelo)

        }
        if (manicure == "Manicure") {
            listaServicos.add(manicure)
        }
        if (pedicure == "Pedicure") {
            listaServicos.add(pedicure)
        }
        return listaServicos
    }

    override fun login(email: String, senha: String, logou: (logou: Boolean, msgErro: String)->Unit){
        auth.signInWithEmailAndPassword(email, senha).addOnSuccessListener {
                resultadoLogin->
            if(resultadoLogin.user != null){
                logou(true, "")

            }else{
                logou(false, "")
            }
        }.addOnFailureListener {
            if(it.message == "There is no user record corresponding to this identifier. The user may have been deleted."){
                logou(false, it.message!!)
            }
        }



    }

    override fun pegarDadosDoUser(pegouDados: (cliente: Cliente) -> Unit) {
        if(auth.currentUser?.uid != null){
            val reference = databaseRefClientes.child(auth.currentUser!!.uid)

            reference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val cliente = snapshot.getValue(Cliente::class.java)
                        if (cliente != null) {
                            pegouDados(cliente)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("dadosusererro", error.message)
                }

            })
        }


    }


    override fun getUserUid(): String? {
        return auth.currentUser?.uid
    }

    override fun salvarImagemNoFirebase(
        imagemEscolhida: Uri,
        salvouImagemNoFirebase: (salvou: Boolean) -> Unit
    ) {
        //salvar imagem no firebase storage
        val storageRef = storage.reference.child("profile_images/${auth.currentUser?.uid}.jpg")

        val uploadTask = storageRef.putFile(imagemEscolhida)

            uploadTask.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { link ->
                    val imageUrl = link.toString()
                    val userUid = auth.currentUser?.uid
                    if(userUid != null){
                        databaseRefClientes.child(userUid).child("linkFotoCliente")
                            .setValue(imageUrl)
                            .addOnSuccessListener {
                                salvouImagemNoFirebase(true)
                            }.addOnFailureListener {
                                salvouImagemNoFirebase(false)
                            }

                    }




                }
            }.addOnFailureListener {
                Log.d("errostorage", "${it.message}")
            }


    }

}