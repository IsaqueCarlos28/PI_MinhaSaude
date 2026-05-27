package com.example.medicoapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicoapplication.data.repository.ConvenioRepository

class ConvenioViewModel(
    private val repository: ConvenioRepository = ConvenioRepository()
) : ViewModel()