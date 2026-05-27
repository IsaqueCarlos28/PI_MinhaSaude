package com.example.medicoapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicoapplication.data.repository.LocalRepository

class LocalViewModel(
    private val repository: LocalRepository = LocalRepository()
) : ViewModel()