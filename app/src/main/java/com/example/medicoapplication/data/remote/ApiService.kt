package com.example.medicoapplication.data.remote

import com.example.medicoapplication.data.remote.DTO.agenda.AgendaRequestDTO
import com.example.medicoapplication.data.remote.DTO.agenda.AgendaResponseDto
import com.example.medicoapplication.data.remote.DTO.bloqueioagenda.BloqueioAgendaCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.bloqueioagenda.BloqueioAgendaPageResponseDto
import com.example.medicoapplication.data.remote.DTO.bloqueioagenda.BloqueioAgendaResponseDto
import com.example.medicoapplication.data.remote.DTO.bloqueioagenda.BloqueioAgendaUpdateRequestDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaPageResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaUpdateRequestDto
import com.example.medicoapplication.data.remote.DTO.consulta.StatusConsulta
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaPageResponseDto
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaUpdateRequestDto
import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioPageResponseDto
import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioResponseDto
import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioUpdateRequestDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadePageResponseDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeResponseDto
import com.example.medicoapplication.data.remote.DTO.local.LocalCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.local.LocalPageResponseDto
import com.example.medicoapplication.data.remote.DTO.local.LocalResponseDto
import com.example.medicoapplication.data.remote.DTO.local.LocalUpdateRequestDto
import com.example.medicoapplication.data.remote.DTO.login.LoginRequestDto
import com.example.medicoapplication.data.remote.DTO.login.LoginResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoPageResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoUpdateRequestDto
import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadeCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadePageResponseDto
import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadeResponseDto
import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadeUpdateRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacientePageResponseDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteResponseDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteUpdateRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    //LOGIN ok
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<LoginResponseDto>

    //PACIENTES ok
    // LISTAR PACIENTES
    @GET("pacientes")
    suspend fun getPacientes(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "nome,asc"
    ): Response<PacientePageResponseDto>

    // BUSCAR PACIENTE POR ID
    @GET("pacientes/{id}")
    suspend fun getPacienteById(
        @Path("id") id: Long
    ): Response<PacienteResponseDto>

    // CRIAR PACIENTE
    @POST("pacientes")
    suspend fun createPaciente(
        @Body paciente: PacienteCreateRequestDto
    ): Response<PacienteResponseDto>

    // ATUALIZAR PACIENTE
    @PUT("pacientes/{id}")
    suspend fun updatePaciente(
        @Path("id") id: Long,
        @Body paciente: PacienteUpdateRequestDto
    ): Response<PacienteResponseDto>

    // DELETAR PACIENTE
    @DELETE("pacientes/{id}")
    suspend fun deletePaciente(
        @Path("id") id: Long
    ): Response<Unit>

//MEDICOS ok
    // LISTAR MÉDICOS
    @GET("medicos")
    suspend fun getMedicos(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "nome,asc"
    ): Response<MedicoPageResponseDto>

    // BUSCAR MÉDICO POR ID
    @GET("medicos/{id}")
    suspend fun getMedicoById(
        @Path("id") id: Long
    ): Response<MedicoResponseDto>

    // CRIAR MÉDICO
    @POST("medicos")
    suspend fun createMedico(
        @Body medico: MedicoCreateRequestDto
    ): Response<MedicoResponseDto>

    // ATUALIZAR MÉDICO
    @PUT("medicos/{id}")
    suspend fun updateMedico(
        @Path("id") id: Long,
        @Body medico: MedicoUpdateRequestDto
    ): Response<MedicoResponseDto>

    // DELETAR MÉDICO
    @DELETE("medicos/{id}")
    suspend fun deleteMedico(
        @Path("id") id: Long
    ): Response<Unit>


//ESPECIALIDADES ok
    // LISTAR ESPECIALIDADES
    @GET("especialidades")
    suspend fun getEspecialidades(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "nome,asc"
    ): Response<EspecialidadePageResponseDto>

    // BUSCAR ESPECIALIDADE POR ID
    @GET("especialidades/{id}")
    suspend fun getEspecialidadeById(
        @Path("id") id: Long
    ): Response<EspecialidadeResponseDto>

// LISTAR RELAÇÕES MÉDICO-ESPECIALIDADE - ok
    @GET("/medicos/{idMedico}/especialidades")
    suspend fun getMedicoEspecialidades(
        @Path("idMedico") idMedico: Long
    ): Response<MedicoEspecialidadePageResponseDto>

    // CRIAR RELAÇÃO
    @POST("/medicos/{idMedico}/especialidades")
    suspend fun createMedicoEspecialidade(
        @Path("idMedico") idMedico: Long,
        @Body medicoEspecialidade: MedicoEspecialidadeCreateRequestDto
    ): Response<MedicoEspecialidadeResponseDto>

    // DELETAR RELAÇÃO
    @DELETE("/medicos/{idMedico}/especialidades/{id}")
    suspend fun deleteMedicoEspecialidade(
        @Path("idMedico") idMedico: Long,
        @Path("id") id: Long
    ): Response<Unit>

//LOCAL -ok
    // LISTAR LOCAIS
    @GET("locais")
    suspend fun getLocais(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "nome,asc"
    ): Response<LocalPageResponseDto>

    // BUSCAR LOCAL POR ID
    @GET("locais/{id}")
    suspend fun getLocalById(
        @Path("id") id: Long
    ): Response<LocalResponseDto>

    // CRIAR LOCAL
    @POST("locais")
    suspend fun createLocal(
        @Body local: LocalCreateRequestDto
    ): Response<LocalResponseDto>

    // ATUALIZAR LOCAL
    @PUT("locais/{id}")
    suspend fun updateLocal(
        @Path("id") id: Long,
        @Body local: LocalUpdateRequestDto
    ): Response<LocalResponseDto>

    // DELETAR LOCAL
    @DELETE("locais/{id}")
    suspend fun deleteLocal(
        @Path("id") id: Long
    ): Response<Unit>

//BLOQUEIOAGENDA-ok
// LISTAR BLOQUEIOS DE AGENDA
    @GET("/medicos/{idMedico}/bloqueio_agenda")
    suspend fun getBloqueiosAgenda(
        @Path("id") idMedico: Long
    ): Response<List<BloqueioAgendaPageResponseDto>>

    // BUSCAR BLOQUEIO POR ID
    @GET("/medicos/{idMedico}/bloqueio_agenda/{idBloqueio}")
    suspend fun getBloqueioAgendaById(
        @Path("id") idMedico: Long,
        @Path("idBloqueio") idBloqueio: Long
    ): Response<BloqueioAgendaResponseDto>

    // CRIAR BLOQUEIO
    @POST("/medicos/{idMedico}/bloqueio_agenda")
    suspend fun createBloqueioAgenda(
        @Path("id") idMedico: Long,
        @Body bloqueio: BloqueioAgendaCreateRequestDto
    ): Response<BloqueioAgendaResponseDto>

    // ATUALIZAR BLOQUEIO
    @PUT("/medicos/{idMedico}/bloqueio_agenda/{idBloqueio}")
    suspend fun updateBloqueioAgenda(
        @Path("id") idMedico: Long,
        @Path("idBloqueio") idBloqueio: Long,
        @Body bloqueio: BloqueioAgendaUpdateRequestDto
    ): Response<BloqueioAgendaResponseDto>

    // DELETAR BLOQUEIO
    @DELETE("/medicos/{idMedico}/bloqueio_agenda/{idBloqueio}")
    suspend fun deleteBloqueioAgenda(
        @Path("id") idMedico: Long,
        @Path("idBloqueio") idBloqueio: Long
    ): Response<Unit>

//CONVENIOS -ok
// LISTAR CONVÊNIOS
    @GET("convenios")
    suspend fun getConvenios(
    ): Response<List<ConvenioPageResponseDto>>

    // BUSCAR CONVÊNIO POR ID
    @GET("convenios/{id}")
    suspend fun getConvenioById(
        @Path("id") id: Long
    ): Response<ConvenioResponseDto>

    // CRIAR CONVÊNIO
    @POST("convenios")
    suspend fun createConvenio(
        @Body convenio: ConvenioCreateRequestDto
    ): Response<ConvenioResponseDto>

    // ATUALIZAR CONVÊNIO
    @PUT("convenios/{id}")
    suspend fun updateConvenio(
        @Path("id") id: Long,
        @Body convenio: ConvenioUpdateRequestDto
    ): Response<ConvenioResponseDto>

    // DELETAR CONVÊNIO
    @DELETE("convenios/{id}")
    suspend fun deleteConvenio(
        @Path("id") id: Long
    ): Response<Unit>

//CONSULTA - OFERTADA
// LISTAR CONSULTAS OFERTADAS
    @GET("/medicos/{idMedico}/consultas-ofertadas")
    suspend fun getConsultasOfertadas(
        @Path("idMedico") idMedico: Long
    ): Response<ConsultaOfertadaPageResponseDto>

    // BUSCAR CONSULTA OFERTADA POR ID
    @GET("/medicos/{idMedico}/consultas-ofertadas/{id}")
    suspend fun getConsultaOfertadaById(
        @Path("idMedico") idMedico: Long,
        @Path("id") id: Long
    ): Response<ConsultaOfertadaResponseDto>

    // CRIAR CONSULTA OFERTADA
    @POST("/medicos/{idMedico}/consultas-ofertadas")
    suspend fun createConsultaOfertada(
        @Path("idMedico") idMedico: Long,
        @Body consulta: ConsultaOfertadaCreateRequestDto
    ): Response<ConsultaOfertadaResponseDto>

    // ATUALIZAR CONSULTA OFERTADA
    @PUT("/medicos/{idMedico}/consultas-ofertadas/{id}")
    suspend fun updateConsultaOfertada(
        @Path("idMedico") idMedico: Long,
        @Path("id") id: Long,
        @Body consulta: ConsultaOfertadaUpdateRequestDto
    ): Response<ConsultaOfertadaResponseDto>

    // DELETAR CONSULTA OFERTADA
    @DELETE("/medicos/{idMedico}/consultas-ofertadas/{id}")
    suspend fun deleteConsultaOfertada(
        @Path("id") id: Long
    ): Response<Unit>

//EVENTO - CONSULTA
    //PELO PACIENTE
    @GET("/pacientes/{idPaciente}/consultas")
    suspend fun getConsultas(
        @Path("idPaciente") idPaciente: Long
    ): Response<ConsultaPageResponseDto>

    // BUSCAR CONSULTA POR ID
    @GET("/pacientes/{idPaciente}/consultas/{id}")
    suspend fun getConsultaById(
        @Path("idPaciente") idPaciente: Long,
        @Path("id") id: Long
    ): Response<ConsultaResponseDto>

    // CRIAR CONSULTA
    @POST("/pacientes/{idPaciente}/consultas")
    suspend fun createConsulta(
        @Path("idPaciente") idPaciente: Long,
        @Body consulta: ConsultaCreateRequestDto
    ): Response<ConsultaResponseDto>

    // ATUALIZAR CONSULTA
    @PUT("/pacientes/{idPaciente}/consultas/{id}")
    suspend fun updateConsulta(
        @Path("idPaciente") idPaciente: Long,
        @Path("id") id: Long,
        @Body consulta: ConsultaUpdateRequestDto
    ): Response<ConsultaResponseDto>

    // CANCELAR CONSULTA
    @PATCH("/pacientes/{idPaciente}/consultas/{id}/status")
    suspend fun pacienteCancelarConsulta(
        @Path("idPaciente") idPaciente: Long,
        @Path("id") id: Long,
        @Field("status") status: StatusConsulta = StatusConsulta.CANCELADA
    ): Response<ConsultaResponseDto>

    //PELO MEDICO

    @GET("/medicos/{idMedico}/consultas-agendadas")
    suspend fun medicoGetConsultas(
        @Path("idPaciente") idPaciente: Long
    ): Response<ConsultaPageResponseDto>

    @GET("/medicos/{idMedico}/consultas-agendadas/{id}")
    suspend fun medicoGetConsultaById(
        @Path("idPaciente") idPaciente: Long,
        @Path("id") id: Long,
    ): Response<ConsultaPageResponseDto>

    @PATCH("/medicos/{idMedico}/consultas-agendadas/{id}/status")
    suspend fun medicoStatusConsulta(
        @Path("idPaciente") idPaciente: Long,
        @Path("id") id: Long,
        @Field("status") status: StatusConsulta
    ): Response<ConsultaResponseDto>
//AGENDA

    @GET("agenda")
    suspend fun getAgendas(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<AgendaResponseDto>

    // BUSCAR AGENDA POR ID
    @GET("agendas/{id}")
    suspend fun getAgendaById(
        @Path("id") id: Long
    ): Response<AgendaResponseDto>

    // CRIAR AGENDA
    @POST("agendas")
    suspend fun createAgenda(
        @Body agenda: AgendaRequestDTO
    ): Response<AgendaResponseDto>

    // ATUALIZAR AGENDA
    @PUT("agendas/{id}")
    suspend fun updateAgenda(
        @Path("id") id: Long,
        @Body agenda: AgendaRequestDTO
    ): Response<AgendaResponseDto>

    // DELETAR AGENDA
    @DELETE("agendas/{id}")
    suspend fun deleteAgenda(
        @Path("id") id: Long
    ): Response<Unit>
}