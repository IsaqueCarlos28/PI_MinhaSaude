package com.example.medicoapplication.UI.activities

import android.content.Intent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.medico.AgendaMedicoActivity
import com.example.medicoapplication.UI.activities.medico.ConfiguracoesMedicoActivity
import com.example.medicoapplication.UI.activities.medico.ConsultasMedicoActivity
import com.example.medicoapplication.UI.activities.medico.HomeMedicoActivity
import com.example.medicoapplication.UI.activities.medico.PerfilMedicoActivity
import com.example.medicoapplication.UI.activities.paciente.BuscaMedicosActivity
import com.example.medicoapplication.UI.activities.paciente.HomePacienteActivity
import com.example.medicoapplication.UI.activities.paciente.MinhasConsultasActivity
import com.example.medicoapplication.UI.activities.paciente.PerfilPacienteActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.UI.common.mappers.ErrorMapper
import com.example.medicoapplication.data.remote.NetworkError
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity() {
    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    protected open fun handleError(error: NetworkError) {
        showToast(ErrorMapper.getMessage(error))
    }

    protected fun showValidationError(
        field: EditText,
        message: String
    ) {
        field.error = message
        field.requestFocus()
    }

    protected open val menuType = BottomMenuType.DISABLED

    protected fun setupBottomNavigation(
        currentItem: Int
    ) {
        val bottomNav: BottomNavigationView = when (menuType) {
            BottomMenuType.PACIENTE ->
                findViewById(R.id.bottomNavPaciente)

            BottomMenuType.MEDICO ->
                findViewById(R.id.bottomNavMedico)

            BottomMenuType.DISABLED ->
                return
        }

        bottomNav.selectedItemId = currentItem

        when (menuType) {
            BottomMenuType.PACIENTE ->
                setupPacienteMenu(bottomNav)

            BottomMenuType.MEDICO ->
                setupMedicoMenu(bottomNav)

            BottomMenuType.DISABLED -> Unit
        }
    }

    private fun setupPacienteMenu(
        bottomNav: BottomNavigationView
    ) {
        bottomNav.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.nav_home -> {
                    if (this !is HomePacienteActivity) {
                        startActivity(
                            Intent(this, HomePacienteActivity::class.java)
                        )
                    }
                    true
                }
                R.id.nav_consultas -> {
                    if (this !is MinhasConsultasActivity) {
                        startActivity(
                            Intent(this, MinhasConsultasActivity::class.java)
                        )
                    }
                    true
                }
                R.id.nav_medicos -> {
                    if (this !is BuscaMedicosActivity) {
                        startActivity(
                            Intent(this, BuscaMedicosActivity::class.java)
                        )
                    }
                    true
                }
                R.id.nav_perfil -> {
                    if (this !is PerfilPacienteActivity) {
                        startActivity(
                            Intent(this, PerfilPacienteActivity::class.java)
                        )
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun setupMedicoMenu(
        bottomNav: BottomNavigationView
    ) {
        bottomNav.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_inicio -> {
                    if (this !is HomeMedicoActivity) {
                        startActivity(
                            Intent(this, HomeMedicoActivity::class.java)
                        )
                    }
                    true
                }

                R.id.nav_agenda -> {
                    if (this !is AgendaMedicoActivity) {
                        startActivity(
                            Intent(this, AgendaMedicoActivity::class.java)
                        )
                    }
                    true
                }

                R.id.nav_consultas_med -> {
                    if (this !is ConsultasMedicoActivity) {
                        startActivity(
                            Intent(this, ConsultasMedicoActivity::class.java)
                        )
                    }
                    true
                }

                R.id.nav_usuario -> {
                    if (this !is PerfilMedicoActivity) {
                        startActivity(
                            Intent(this, PerfilMedicoActivity::class.java)
                        )
                    }
                    true
                }
                R.id.nav_config -> {
                    if (this !is ConfiguracoesMedicoActivity) {
                        startActivity(
                            Intent(this, ConfiguracoesMedicoActivity::class.java)
                        )
                    }
                    true
                }
                else -> false
            }
        }
    }
}
