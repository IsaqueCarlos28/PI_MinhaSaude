package com.example.medicoapplication.data.remote

import com.example.medicoapplication.data.remote.DTO.notification.RegistrarFcmTokenRequest
import com.example.medicoapplication.data.remote.DTO.agenda.AgendaRequestDTO
import com.example.medicoapplication.data.remote.DTO.agenda.AgendaResponseDto
import com.example.medicoapplication.data.remote.DTO.agenda.DisponibilidadeSemanaDTO
import com.example.medicoapplication.data.remote.DTO.auth.AlterarSenhaRequestDto
import com.example.medicoapplication.data.remote.DTO.auth.EsqueceuSenhaRequestDto
import com.example.medicoapplication.data.remote.DTO.auth.LoginRequestDto
import com.example.medicoapplication.data.remote.DTO.auth.LoginResponseDto
import com.example.medicoapplication.data.remote.DTO.auth.TokenDeRecuperacaoDto
import com.example.medicoapplication.data.remote.DTO.auth.ValidarTokenRequestDto
import com.example.medicoapplication.data.remote.DTO.bloqueioagenda.BloqueioAgendaCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.bloqueioagenda.BloqueioAgendaResponseDto
import com.example.medicoapplication.data.remote.DTO.bloqueioagenda.BloqueioAgendaUpdateRequestDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaStatusRequestDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaUpdateRequestDto
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaUpdateRequestDto
import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioPageResponseDto
import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioResponseDto
import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioUpdateRequestDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadePageResponseDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeResponseDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeUpdateRequestDto
import com.example.medicoapplication.data.remote.DTO.local.LocalCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.local.LocalPageResponseDto
import com.example.medicoapplication.data.remote.DTO.local.LocalResponseDto
import com.example.medicoapplication.data.remote.DTO.local.LocalUpdateRequestDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoEditRequestDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoPageResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadeCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadeResponseDto
import com.example.medicoapplication.data.remote.DTO.notification.DesativarFcmTokenRequest
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteEditRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacientePageResponseDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ─── AUTH ────────────────────────────────────────────────────────────────────

    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequestDto
    ): Response<LoginResponseDto>

    @POST("auth/esqueceu-a-senha")
    suspend fun esqueceuSenha(
        @Body request: EsqueceuSenhaRequestDto
    ): Response<Unit>

    @POST("auth/validar-codigo")
    suspend fun validarCodigo(
        @Body request: ValidarTokenRequestDto
    ): Response<TokenDeRecuperacaoDto>

    @PUT("auth/alterar-senha")
    suspend fun alterarSenha(
        @Body request: AlterarSenhaRequestDto
    ): Response<Unit>

    // ─── PACIENTES ───────────────────────────────────────────────────────────────

    @GET("pacientes")
    suspend fun getPacientes(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "nome,asc"
    ): Response<PacientePageResponseDto>

    @GET("pacientes/{id}")
    suspend fun getPacienteById(
        @Path("id") id: Long
    ): Response<PacienteResponseDto>

    @POST("pacientes")
    suspend fun createPaciente(
        @Body paciente: PacienteCreateRequestDto
    ): Response<PacienteResponseDto>

    @PUT("pacientes/{id}")
    suspend fun updatePaciente(
        @Path("id") id: Long,
        @Body paciente: PacienteEditRequestDto
    ): Response<PacienteResponseDto>

    @DELETE("pacientes/{id}")
    suspend fun deletePaciente(
        @Path("id") id: Long
    ): Response<Unit>

    // ─── MÉDICOS ─────────────────────────────────────────────────────────────────

    @GET("medicos")
    suspend fun getMedicos(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<MedicoPageResponseDto>

    @GET("medicos/{id}")
    suspend fun getMedicoById(
        @Path("id") id: Long
    ): Response<MedicoResponseDto>


    @POST("medicos")
    suspend fun createMedico(
        @Body medico: MedicoCreateRequestDto
    ): Response<MedicoResponseDto>

    @PUT("medicos/{id}")
    suspend fun updateMedico(
        @Path("id") id: Long,
        @Body medico: MedicoEditRequestDto
    ): Response<MedicoResponseDto>

    @DELETE("medicos/{id}")
    suspend fun deleteMedico(
        @Path("id") id: Long
    ): Response<Unit>

    // ─── MÉDICO-ESPECIALIDADE ────────────────────────────────────────────────────

    @GET("medicos/{idMedico}/especialidades")
    suspend fun getMedicoEspecialidades(
        @Path("idMedico") idMedico: Long
    ): Response<List<MedicoEspecialidadeResponseDto>>

    @POST("medicos/{idMedico}/especialidades")
    suspend fun addMedicoEspecialidade(
        @Path("idMedico") idMedico: Long,
        @Body request: MedicoEspecialidadeCreateRequestDto
    ): Response<MedicoEspecialidadeResponseDto>

    @DELETE("medicos/{idMedico}/especialidades/{id}")
    suspend fun deleteMedicoEspecialidade(
        @Path("idMedico") idMedico: Long,
        @Path("id") id: Long
    ): Response<Unit>

    // ─── ESPECIALIDADES ──────────────────────────────────────────────────────────

    @GET("especialidades")
    suspend fun getEspecialidades(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "nome,asc"
    ): Response<EspecialidadePageResponseDto>

    @GET("especialidades/{id}")
    suspend fun getEspecialidadeById(
        @Path("id") id: Long
    ): Response<EspecialidadeResponseDto>

    @POST("especialidades")
    suspend fun createEspecialidade(
        @Body especialidade: EspecialidadeCreateRequestDto
    ): Response<EspecialidadeResponseDto>

    @PUT("especialidades/{id}")
    suspend fun updateEspecialidade(
        @Path("id") id: Long,
        @Body especialidade: EspecialidadeUpdateRequestDto
    ): Response<EspecialidadeResponseDto>

    @DELETE("especialidades/{id}")
    suspend fun deleteEspecialidade(
        @Path("id") id: Long
    ): Response<Unit>

    // ─── CONVÊNIOS ───────────────────────────────────────────────────────────────

    @GET("convenio")
    suspend fun getConvenios(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "nome,asc"
    ): Response<ConvenioPageResponseDto>

    @GET("convenio/{id}")
    suspend fun getConvenioById(
        @Path("id") id: Long
    ): Response<ConvenioResponseDto>

    @POST("convenio")
    suspend fun createConvenio(
        @Body convenio: ConvenioCreateRequestDto
    ): Response<ConvenioResponseDto>

    @PUT("convenio/{id}")
    suspend fun updateConvenio(
        @Path("id") id: Long,
        @Body convenio: ConvenioUpdateRequestDto
    ): Response<ConvenioResponseDto>

    @DELETE("convenio/{id}")
    suspend fun deleteConvenio(
        @Path("id") id: Long
    ): Response<Unit>

    // ─── LOCAIS ──────────────────────────────────────────────────────────────────

    @GET("locais")
    suspend fun getLocais(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "nome,asc"
    ): Response<LocalPageResponseDto>

    @GET("locais/{id}")
    suspend fun getLocalById(
        @Path("id") id: Long
    ): Response<LocalResponseDto>

    @POST("locais")
    suspend fun createLocal(
        @Body local: LocalCreateRequestDto
    ): Response<LocalResponseDto>

    @PUT("locais/{id}")
    suspend fun updateLocal(
        @Path("id") id: Long,
        @Body local: LocalUpdateRequestDto
    ): Response<LocalResponseDto>

    @DELETE("locais/{id}")
    suspend fun deleteLocal(
        @Path("id") id: Long
    ): Response<Unit>

    // ─── BLOQUEIO DE AGENDA ──────────────────────────────────────────────────────

    @GET("medicos/{idMedico}/bloqueio_agenda")
    suspend fun getBloqueiosAgenda(
        @Path("idMedico") idMedico: Long
    ): Response<List<BloqueioAgendaResponseDto>>

    @POST("medicos/{idMedico}/bloqueio_agenda")
    suspend fun createBloqueioAgenda(
        @Path("idMedico") idMedico: Long,
        @Body bloqueio: BloqueioAgendaCreateRequestDto
    ): Response<BloqueioAgendaResponseDto>

    @PUT("medicos/{idMedico}/bloqueio_agenda/{idBloqueio}")
    suspend fun updateBloqueioAgenda(
        @Path("idMedico") idMedico: Long,
        @Path("idBloqueio") idBloqueio: Long,
        @Body bloqueio: BloqueioAgendaUpdateRequestDto
    ): Response<BloqueioAgendaResponseDto>

    @DELETE("medicos/{idMedico}/bloqueio_agenda/{idBloqueio}")
    suspend fun deleteBloqueioAgenda(
        @Path("idMedico") idMedico: Long,
        @Path("idBloqueio") idBloqueio: Long
    ): Response<Unit>

    // ─── CONSULTAS OFERTADAS ─────────────────────────────────────────────────────

    @GET("medicos/{idMedico}/consultas-ofertadas")
    suspend fun getConsultasOfertadas(
        @Path("idMedico") idMedico: Long
    ): Response<List<ConsultaOfertadaResponseDto>>

    @GET("medicos/{idMedico}/consultas-ofertadas/{id}")
    suspend fun getConsultaOfertadaById(
        @Path("idMedico") idMedico: Long,
        @Path("id") id: Long
    ): Response<ConsultaOfertadaResponseDto>

    @POST("medicos/{idMedico}/consultas-ofertadas")
    suspend fun createConsultaOfertada(
        @Path("idMedico") idMedico: Long,
        @Body consulta: ConsultaOfertadaCreateRequestDto
    ): Response<ConsultaOfertadaResponseDto>

    @PUT("medicos/{idMedico}/consultas-ofertadas/{id}")
    suspend fun updateConsultaOfertada(
        @Path("idMedico") idMedico: Long,
        @Path("id") id: Long,
        @Body consulta: ConsultaOfertadaUpdateRequestDto
    ): Response<ConsultaOfertadaResponseDto>

    @DELETE("medicos/{idMedico}/consultas-ofertadas/{id}")
    suspend fun deleteConsultaOfertada(
        @Path("idMedico") idMedico: Long,
        @Path("id") id: Long
    ): Response<Unit>

    // ─── AGENDA ──────────────────────────────────────────────────────────────────

    @GET("medicos/{idMedico}/consultas-ofertadas/{idConsulta}/agenda")
    suspend fun getAgendas(
        @Path("idMedico") idMedico: Long,
        @Path("idConsulta") idConsulta: Long
    ): Response<List<AgendaResponseDto>>

    @GET("medicos/{idMedico}/consultas-ofertadas/{idConsulta}/agenda/{idAgenda}")
    suspend fun getAgendaById(
        @Path("idMedico") idMedico: Long,
        @Path("idConsulta") idConsulta: Long,
        @Path("idAgenda") idAgenda: Long
    ): Response<AgendaResponseDto>

    @GET("medicos/{idMedico}/consultas-ofertadas/{idConsulta}/agenda/disponibilidade")
    suspend fun getDisponibilidade(
        @Path("idMedico") idMedico: Long,
        @Path("idConsulta") idConsulta: Long,
        @Query("semanas") semanas: Int = 4
    ): Response<List<DisponibilidadeSemanaDTO>>

    @POST("medicos/{idMedico}/consultas-ofertadas/{idConsulta}/agenda")
    suspend fun createAgenda(
        @Path("idMedico") idMedico: Long,
        @Path("idConsulta") idConsulta: Long,
        @Body agenda: AgendaRequestDTO
    ): Response<AgendaResponseDto>

    @PUT("medicos/{idMedico}/consultas-ofertadas/{idConsulta}/agenda/{idAgenda}")
    suspend fun updateAgenda(
        @Path("idMedico") idMedico: Long,
        @Path("idConsulta") idConsulta: Long,
        @Path("idAgenda") idAgenda: Long,
        @Body agenda: AgendaRequestDTO
    ): Response<AgendaResponseDto>

    @DELETE("medicos/{idMedico}/consultas-ofertadas/{idConsulta}/agenda/{idAgenda}")
    suspend fun deleteAgenda(
        @Path("idMedico") idMedico: Long,
        @Path("idConsulta") idConsulta: Long,
        @Path("idAgenda") idAgenda: Long
    ): Response<Unit>

    // ─── EVENTO CONSULTA — PERSPECTIVA PACIENTE ──────────────────────────────────

    @GET("pacientes/{idPaciente}/consultas")
    suspend fun getConsultasByPaciente(
        @Path("idPaciente") idPaciente: Long
    ): Response<List<ConsultaResponseDto>>

    @GET("pacientes/{idPaciente}/consultas/{idEvento}")
    suspend fun getConsultaByIdPaciente(
        @Path("idPaciente") idPaciente: Long,
        @Path("idEvento") idEvento: Long
    ): Response<ConsultaResponseDto>

    @POST("pacientes/{idPaciente}/consultas")
    suspend fun agendarConsulta(
        @Path("idPaciente") idPaciente: Long,
        @Body consulta: ConsultaCreateRequestDto
    ): Response<ConsultaResponseDto>

    @PUT("pacientes/{idPaciente}/consultas/{idEvento}")
    suspend fun reagendarConsulta(
        @Path("idPaciente") idPaciente: Long,
        @Path("idEvento") idEvento: Long,
        @Body consulta: ConsultaUpdateRequestDto
    ): Response<ConsultaResponseDto>

    @PATCH("pacientes/{idPaciente}/consultas/{idEvento}/status")
    suspend fun atualizarStatusPeloPaciente(
        @Path("idPaciente") idPaciente: Long,
        @Path("idEvento") idEvento: Long,
        @Body status: ConsultaStatusRequestDto
    ): Response<ConsultaResponseDto>

    // ─── EVENTO CONSULTA — PERSPECTIVA MÉDICO ────────────────────────────────────

    @GET("medicos/{idMedico}/consultas-agendadas")
    suspend fun getConsultasByMedico(
        @Path("idMedico") idMedico: Long
    ): Response<List<ConsultaResponseDto>>

    @GET("medicos/{idMedico}/consultas-agendadas/{idEvento}")
    suspend fun getConsultaByIdMedico(
        @Path("idMedico") idMedico: Long,
        @Path("idEvento") idEvento: Long
    ): Response<ConsultaResponseDto>

    @PATCH("medicos/{idMedico}/consultas-agendadas/{idEvento}/status")
    suspend fun atualizarStatusPeloMedico(
        @Path("idMedico") idMedico: Long,
        @Path("idEvento") idEvento: Long,
        @Body status: ConsultaStatusRequestDto
    ): Response<ConsultaResponseDto>

    // ─── NOTIFICAÇÕES ────────────────────────────────────

    @POST("notificacoes/fcm-token")
    suspend fun registrarFcmToken(
        @Body request: RegistrarFcmTokenRequest
    ):Response<Unit>

    @POST("notificacoes/fcm-token/desativar")
    suspend fun desativarFcmToken(
        @Body request: DesativarFcmTokenRequest
    ):Response<Unit>

}
