package com.example.medicoapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicoapplication.data.repository.BloqueioAgendaRepository

class BloqueioAgendaViewModel(
    private val repository: BloqueioAgendaRepository = BloqueioAgendaRepository()
) : ViewModel()