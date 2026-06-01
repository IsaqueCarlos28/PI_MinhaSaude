package com.example.medicoapplication.viewmodel.medico

import androidx.lifecycle.ViewModel
import com.example.medicoapplication.data.repository.MedicoRepository

class MedicoViewModel(
    private val repository: MedicoRepository = MedicoRepository()
) : ViewModel()