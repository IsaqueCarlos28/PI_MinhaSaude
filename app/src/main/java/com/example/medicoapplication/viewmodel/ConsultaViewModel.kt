package com.example.medicoapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicoapplication.data.repository.ConsultaRepository

class ConsultaViewModel(
    private val repository: ConsultaRepository = ConsultaRepository()
) : ViewModel()