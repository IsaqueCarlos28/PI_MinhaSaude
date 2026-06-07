package com.example.medicoapplication.UI.activities.medico.configuracao

import com.example.medicoapplication.UI.activities.configuracao.BaseConfigTextoActivity

class ConfigFaqMedicoActivity : BaseConfigTextoActivity() {
    override val titulo = "FAQ/Ajuda"
    override val subtitulo = "Dúvidas frequentes do médico"
    override val conteudo = """
        1. Como vejo minha agenda?
        Acesse a opção Agenda no menu inferior para visualizar os horários cadastrados e os atendimentos previstos.

        2. Como acompanho minhas consultas?
        Acesse a área de Consultas. Nela serão exibidos os agendamentos feitos pelos pacientes.

        3. Como marco uma consulta como realizada?
        Abra os detalhes da consulta e utilize a opção Marcar como realizada, quando disponível.

        4. Posso cancelar uma consulta?
        Sim. Ao visualizar os detalhes de uma consulta, utilize a opção de cancelamento quando necessário.

        5. Como cadastro horários disponíveis?
        Acesse a área de Consultas Ofertadas ou Agenda, conforme a organização do projeto, e cadastre os horários de atendimento.

        6. O paciente consegue ver meus horários?
        Sim. Os horários ofertados pelo médico podem aparecer para o paciente durante o processo de agendamento.

        7. Como altero meus dados profissionais?
        Acesse Configurações e toque no card do perfil para abrir suas informações profissionais.
    """
}
