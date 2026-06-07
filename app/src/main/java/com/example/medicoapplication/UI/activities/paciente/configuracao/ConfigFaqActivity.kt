package com.example.medicoapplication.UI.activities.paciente.configuracao

import com.example.medicoapplication.UI.activities.configuracao.BaseConfigTextoActivity

class ConfigFaqActivity : BaseConfigTextoActivity() {
    override val titulo = "FAQ/Ajuda"
    override val subtitulo = "Dúvidas frequentes do paciente"
    override val conteudo = """
        1. Como faço para agendar uma consulta?
        Acesse a área de médicos, escolha o profissional desejado, selecione uma data e horário disponível e confirme o agendamento.

        2. Onde vejo minhas consultas marcadas?
        Acesse a opção Consultas no menu inferior. Nessa tela serão exibidas suas consultas agendadas e seus respectivos status.

        3. Posso cancelar uma consulta?
        Sim. Ao abrir os detalhes da consulta, utilize a opção de cancelamento quando ela estiver disponível.

        4. O que significa o status da consulta?
        O status indica a situação atual do agendamento, como pendente, confirmado, cancelado ou realizado.

        5. O aplicativo substitui atendimento médico?
        Não. O Minha Saúde serve para organização e agendamento. Ele não substitui consulta, diagnóstico ou orientação médica profissional.

        6. Meus dados estão seguros?
        O aplicativo utiliza seus dados apenas para o funcionamento da plataforma, como cadastro, login, agendamento e acompanhamento de consultas.

        7. Como altero meus dados?
        Acesse Configurações e toque no card do seu perfil para editar as informações disponíveis.
    """
}
