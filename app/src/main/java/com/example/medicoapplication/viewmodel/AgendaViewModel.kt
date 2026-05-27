package com.example.medicoapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicoapplication.data.repository.AgendaRepository

class AgendaViewModel(
    private val repository: AgendaRepository = AgendaRepository()
) : ViewModel()