package com.example.medicoapplication.UI.activities.paciente.configuracao

import com.example.medicoapplication.UI.activities.configuracao.BaseConfigTextoActivity

open class ConfigSobreActivity : BaseConfigTextoActivity() {
    override val titulo = "Sobre o App"
    override val subtitulo = "Minha Saúde"
    override val conteudo = """
        O Minha Saúde é um aplicativo desenvolvido com o objetivo de facilitar o processo de agendamento e acompanhamento de consultas médicas.

        A plataforma permite que pacientes encontrem profissionais de saúde, visualizem especialidades, escolham horários disponíveis e acompanhem suas consultas agendadas de maneira simples e rápida.

        Além disso, o sistema também oferece funcionalidades para médicos, permitindo a visualização da agenda, consultas marcadas e gerenciamento de atendimentos.

        Principais funcionalidades:
        • Cadastro e login de pacientes e médicos;
        • Visualização de médicos e especialidades;
        • Agendamento de consultas;
        • Acompanhamento de consultas agendadas;
        • Visualização da agenda médica;
        • Atualização de status de atendimento.

        Este aplicativo foi desenvolvido como parte de um projeto acadêmico, com foco no aprendizado de desenvolvimento mobile, integração com API e construção de uma solução prática para a área da saúde.

        Versão do aplicativo: 1.0
        Desenvolvido por: Equipe Minha Saúde
    """
}
