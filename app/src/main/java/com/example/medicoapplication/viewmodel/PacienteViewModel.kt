package com.example.medicoapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicoapplication.data.repository.PacienteRepository

class PacienteViewModel(
    private val repository: PacienteRepository = PacienteRepository()
) : ViewModel()