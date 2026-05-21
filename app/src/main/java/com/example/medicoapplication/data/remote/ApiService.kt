package com.example.medicoapplication.data.remote

import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadePageResponseDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeResponseDto
import com.example.medicoapplication.data.remote.DTO.login.LoginRequestDto
import com.example.medicoapplication.data.remote.DTO.login.LoginResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoPageResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoUpdateRequestDto
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
}