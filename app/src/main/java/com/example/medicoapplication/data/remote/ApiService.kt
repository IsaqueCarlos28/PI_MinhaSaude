package com.example.medicoapplication.data.remote

import com.example.medicoapplication.data.remote.DTO.auth.LoginRequestDto
import com.example.medicoapplication.data.remote.DTO.auth.LoginResponseDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.evento_consulta.EventoConsultaRequestDto
import com.example.medicoapplication.data.remote.DTO.evento_consulta.EventoConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.evento_consulta.EventoConsultaStatusDto
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Login
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequestDto): Response<LoginResponseDto>

    // Paciente
    @GET("pacientes/{id}")
    suspend fun getPacienteById(@Path("id") id: Long): Response<PacienteDto>

    @POST("pacientes")
    suspend fun cadastrarPaciente(@Body paciente: PacienteCreateRequestDto): Response<Void>

    // Médico
    @POST("medicos")
    suspend fun cadastrarMedico(@Body medico: MedicoCreateRequestDto): Response<Void>

    // Consultas
    @GET("pacientes/{id}/consultas")
    suspend fun getConsultasByPaciente(@Path("id") idPaciente: Long): Response<List<EventoConsultaResponseDto>>

    @PUT("pacientes/{id}/consultas/{idEvento}")
    suspend fun reagendarConsulta(
        @Path("id") idPaciente: Long,
        @Path("idEvento") idEvento: Long,
        @Body consulta: EventoConsultaRequestDto
    ): Response<EventoConsultaResponseDto>

    @PATCH("pacientes/{id}/consultas/{idEvento}/status")
    suspend fun atualizarStatusConsulta(
        @Path("id") idPaciente: Long,
        @Path("idEvento") idEvento: Long,
        @Body status: EventoConsultaStatusDto
    ): Response<EventoConsultaResponseDto>
}
