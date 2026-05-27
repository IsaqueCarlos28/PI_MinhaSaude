package com.example.medicoapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicoapplication.data.repository.EspecialidadeRepository

class EspecialidadeViewModel(
    private val repository: EspecialidadeRepository = EspecialidadeRepository()
) : ViewModel()