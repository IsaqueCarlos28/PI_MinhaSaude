package com.example.medicoapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicoapplication.data.repository.ConsultaOfertadaRepository

class ConsultaOfertadaViewModel(
    private val repository: ConsultaOfertadaRepository = ConsultaOfertadaRepository()
) : ViewModel()