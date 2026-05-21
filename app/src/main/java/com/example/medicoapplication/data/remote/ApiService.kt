package com.example.medicoapplication.data.remote

import com.example.medicoapplication.data.remote.DTO.bloqueioagenda.BloqueioAgendaCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.bloqueioagenda.BloqueioAgendaPageResponseDto
import com.example.medicoapplication.data.remote.DTO.bloqueioagenda.BloqueioAgendaResponseDto
import com.example.medicoapplication.data.remote.DTO.bloqueioagenda.BloqueioAgendaUpdateRequestDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaPageResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaUpdateRequestDto
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
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    //LOGIN
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<LoginResponseDto>

    //PACIENTES
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

//MEDICOS
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

//MEDICO-ESPECIALIDADE
// LISTAR RELAÇÕES MÉDICO-ESPECIALIDADE
    @GET("medico-especialidade")
    suspend fun getMedicoEspecialidades(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<MedicoEspecialidadePageResponseDto>

    // BUSCAR RELAÇÃO POR ID
    @GET("medico-especialidade/{id}")
    suspend fun getMedicoEspecialidadeById(
        @Path("id") id: Long
    ): Response<MedicoEspecialidadeResponseDto>

    // CRIAR RELAÇÃO
    @POST("medico-especialidade")
    suspend fun createMedicoEspecialidade(
        @Body medicoEspecialidade: MedicoEspecialidadeCreateRequestDto
    ): Response<MedicoEspecialidadeResponseDto>

    // ATUALIZAR RELAÇÃO
    @PUT("medico-especialidade/{id}")
    suspend fun updateMedicoEspecialidade(
        @Path("id") id: Long,
        @Body medicoEspecialidade: MedicoEspecialidadeUpdateRequestDto
    ): Response<MedicoEspecialidadeResponseDto>

    // DELETAR RELAÇÃO
    @DELETE("medico-especialidade/{id}")
    suspend fun deleteMedicoEspecialidade(
        @Path("id") id: Long
    ): Response<Unit>

//ESPECIALIDADES
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

//LOCAL
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

//BLOQUEIOAGENDA
// LISTAR BLOQUEIOS DE AGENDA
@GET("bloqueio-agenda")
suspend fun getBloqueiosAgenda(
    @Query("page") page: Int = 0,
    @Query("size") size: Int = 20,
    @Query("sort") sort: String = "dataInicio,asc"
): Response<BloqueioAgendaPageResponseDto>

    // BUSCAR BLOQUEIO POR ID
    @GET("bloqueio-agenda/{id}")
    suspend fun getBloqueioAgendaById(
        @Path("id") id: Long
    ): Response<BloqueioAgendaResponseDto>

    // CRIAR BLOQUEIO
    @POST("bloqueio-agenda")
    suspend fun createBloqueioAgenda(
        @Body bloqueio: BloqueioAgendaCreateRequestDto
    ): Response<BloqueioAgendaResponseDto>

    // ATUALIZAR BLOQUEIO
    @PUT("bloqueio-agenda/{id}")
    suspend fun updateBloqueioAgenda(
        @Path("id") id: Long,
        @Body bloqueio: BloqueioAgendaUpdateRequestDto
    ): Response<BloqueioAgendaResponseDto>

    // DELETAR BLOQUEIO
    @DELETE("bloqueio-agenda/{id}")
    suspend fun deleteBloqueioAgenda(
        @Path("id") id: Long
    ): Response<Unit>

//CONVENIOS
// LISTAR CONVÊNIOS
    @GET("convenios")
    suspend fun getConvenios(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "nome,asc"
    ): Response<ConvenioPageResponseDto>

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

//AGENDA
// LISTAR CONSULTAS OFERTADAS
    @GET("consultas-ofertadas")
    suspend fun getConsultasOfertadas(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "dataHoraInicio,asc"
    ): Response<ConsultaOfertadaPageResponseDto>

    // BUSCAR CONSULTA OFERTADA POR ID
    @GET("consultas-ofertadas/{id}")
    suspend fun getConsultaOfertadaById(
        @Path("id") id: Long
    ): Response<ConsultaOfertadaResponseDto>

    // CRIAR CONSULTA OFERTADA
    @POST("consultas-ofertadas")
    suspend fun createConsultaOfertada(
        @Body consulta: ConsultaOfertadaCreateRequestDto
    ): Response<ConsultaOfertadaResponseDto>

    // ATUALIZAR CONSULTA OFERTADA
    @PUT("consultas-ofertadas/{id}")
    suspend fun updateConsultaOfertada(
        @Path("id") id: Long,
        @Body consulta: ConsultaOfertadaUpdateRequestDto
    ): Response<ConsultaOfertadaResponseDto>

    // DELETAR CONSULTA OFERTADA
    @DELETE("consultas-ofertadas/{id}")
    suspend fun deleteConsultaOfertada(
        @Path("id") id: Long
    ): Response<Unit>

//CONSULTA
    // LISTAR CONSULTAS
    @GET("consultas")
    suspend fun getConsultas(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "id,desc"
    ): Response<ConsultaPageResponseDto>

    // BUSCAR CONSULTA POR ID
    @GET("consultas/{id}")
    suspend fun getConsultaById(
        @Path("id") id: Long
    ): Response<ConsultaResponseDto>

    // CRIAR CONSULTA
    @POST("consultas")
    suspend fun createConsulta(
        @Body consulta: ConsultaCreateRequestDto
    ): Response<ConsultaResponseDto>

    // ATUALIZAR CONSULTA
    @PUT("consultas/{id}")
    suspend fun updateConsulta(
        @Path("id") id: Long,
        @Body consulta: ConsultaUpdateRequestDto
    ): Response<ConsultaResponseDto>

    // DELETAR CONSULTA
    @DELETE("consultas/{id}")
    suspend fun deleteConsulta(
        @Path("id") id: Long
    ): Response<Unit>
}