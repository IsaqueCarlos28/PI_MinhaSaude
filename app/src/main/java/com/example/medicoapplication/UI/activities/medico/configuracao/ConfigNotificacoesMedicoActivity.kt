package com.example.medicoapplication.UI.activities.medico.configuracao

import com.example.medicoapplication.UI.activities.configuracao.BaseConfigTextoActivity

class ConfigNotificacoesMedicoActivity : BaseConfigTextoActivity() {
    override val titulo = "Notificações"
    override val subtitulo = "Avisos para médicos"
    override val conteudo = """
        Nesta área o médico poderá acompanhar e configurar avisos relacionados à sua agenda e aos atendimentos.

        Sugestões de notificações para o médico:
        • Nova consulta agendada por um paciente;
        • Consulta cancelada ou reagendada;
        • Lembrete de próximo atendimento;
        • Alerta de alteração na agenda;
        • Avisos sobre horários disponíveis cadastrados.

        Exemplo:
        Você pode receber uma notificação quando um paciente agendar uma consulta em um horário ofertado por você.

        No momento, esta tela está preparada como modelo visual. A ativação real das notificações pode ser integrada futuramente com permissões do Android, Firebase Cloud Messaging ou notificações locais.
    """
}
