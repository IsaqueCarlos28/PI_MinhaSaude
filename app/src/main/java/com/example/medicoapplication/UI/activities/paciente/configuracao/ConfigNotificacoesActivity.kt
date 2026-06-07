package com.example.medicoapplication.UI.activities.paciente.configuracao

import com.example.medicoapplication.UI.activities.configuracao.BaseConfigTextoActivity

class ConfigNotificacoesActivity : BaseConfigTextoActivity() {
    override val titulo = "Notificações"
    override val subtitulo = "Avisos para pacientes"
    override val conteudo = """
        Nesta área o paciente poderá acompanhar e configurar os principais avisos relacionados às suas consultas.

        Sugestões de notificações para o paciente:
        • Lembrete de consulta agendada;
        • Aviso de confirmação da consulta;
        • Aviso de cancelamento ou alteração de horário;
        • Lembrete no dia da consulta;
        • Avisos sobre atualização de dados do perfil.

        Exemplo:
        Você pode receber uma notificação algumas horas antes da consulta para lembrar o horário, o médico e o local de atendimento.

        No momento, esta tela está preparada como modelo visual. A ativação real das notificações pode ser integrada futuramente com permissões do Android, Firebase Cloud Messaging ou notificações locais.
    """
}
