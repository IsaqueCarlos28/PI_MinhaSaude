package com.example.medicoapplication.UI.activities

import android.content.Intent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.medico.agenda.AgendaMedicoActivity
import com.example.medicoapplication.UI.activities.medico.ConfiguracoesMedicoActivity
import com.example.medicoapplication.UI.activities.medico.consultas.ConsultasMedicoActivity
import com.example.medicoapplication.UI.activities.medico.HomeMedicoActivity
import com.example.medicoapplication.UI.activities.medico.perfil.PerfilMedicoActivity
import com.example.medicoapplication.UI.activities.paciente.medicos.BuscaMedicosActivity
import com.example.medicoapplication.UI.activities.paciente.HomePacienteActivity
import com.example.medicoapplication.UI.activities.paciente.consultas.MinhasConsultasActivity
import com.example.medicoapplication.UI.activities.paciente.perfil.PerfilPacienteActivity
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
                R.id.nav_inicio_paciente -> {
                    if (this !is HomePacienteActivity) {
                        startActivity(
                            Intent(this, HomePacienteActivity::class.java)
                        )
                    }
                    true
                }
                R.id.nav_consultas_paciente -> {
                    if (this !is MinhasConsultasActivity) {
                        startActivity(
                            Intent(this, MinhasConsultasActivity::class.java)
                        )
                    }
                    true
                }
                R.id.nav_medicos_paciente -> {
                    if (this !is BuscaMedicosActivity) {
                        startActivity(
                            Intent(this, BuscaMedicosActivity::class.java)
                        )
                    }
                    true
                }
                R.id.nav_perfil_paciente -> {
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

                R.id.nav_inicio_medico -> {
                    if (this !is HomeMedicoActivity) {
                        startActivity(
                            Intent(this, HomeMedicoActivity::class.java)
                        )
                    }
                    true
                }

                R.id.nav_agenda_medico -> {
                    if (this !is AgendaMedicoActivity) {
                        startActivity(
                            Intent(this, AgendaMedicoActivity::class.java)
                        )
                    }
                    true
                }

                R.id.nav_consultas_medico -> {
                    if (this !is ConsultasMedicoActivity) {
                        startActivity(
                            Intent(this, ConsultasMedicoActivity::class.java)
                        )
                    }
                    true
                }

                R.id.nav_perfil_medico -> {
                    if (this !is PerfilMedicoActivity) {
                        startActivity(
                            Intent(this, PerfilMedicoActivity::class.java)
                        )
                    }
                    true
                }
                R.id.nav_config_medico -> {
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
