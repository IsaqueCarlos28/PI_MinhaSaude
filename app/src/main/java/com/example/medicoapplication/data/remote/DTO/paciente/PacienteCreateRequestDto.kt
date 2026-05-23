package com.example.medicoapplication.data.remote.DTO.paciente

<<<<<<< HEAD
=======
import com.example.medicoapplication.data.remote.DTO.Genero

>>>>>>> 2ace8766d38c78a681c89f28b8086b9ad78212c2
data class PacienteCreateRequestDto(
    val nome: String,
    val cpf: String,
    val email: String,
    val telefone: String,
<<<<<<< HEAD
    val senha: String,
    val genero: String,
    val dataNascimento: String,
    val uf: String
=======
    val genero: Genero,
    val dataNascimento: String,  // format: "yyyy-MM-dd"
    val senha: String
>>>>>>> 2ace8766d38c78a681c89f28b8086b9ad78212c2
)
