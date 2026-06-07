package com.example.medicoapplication.UI.activities.paciente.configuracao

import com.example.medicoapplication.UI.activities.configuracao.BaseConfigTextoActivity

open class ConfigTermosActivity : BaseConfigTextoActivity() {
    override val titulo = "Termos"
    override val subtitulo = "Termos de Uso e Privacidade"
    override val conteudo = """
        Bem-vindo ao aplicativo Minha Saúde.

        Ao utilizar este aplicativo, o usuário concorda com os presentes Termos de Uso e com as diretrizes de Privacidade descritas nesta página. Caso não concorde com algum dos termos, recomendamos não utilizar os serviços oferecidos pela plataforma.

        1. Objetivo do aplicativo
        O Minha Saúde é uma plataforma voltada ao agendamento e gerenciamento de consultas médicas, permitindo a interação entre pacientes e profissionais de saúde de forma prática e organizada.

        2. Cadastro do usuário
        Para utilizar determinadas funcionalidades, o usuário deverá fornecer informações verdadeiras, completas e atualizadas. O usuário é responsável pela confidencialidade de sua conta e de seus dados de acesso.

        3. Uso adequado da plataforma
        O usuário compromete-se a utilizar o aplicativo de forma ética e responsável, não sendo permitido informar dados falsos, tentar acessar áreas restritas, comprometer a segurança da aplicação ou utilizar a plataforma para finalidades indevidas.

        4. Agendamento de consultas
        As consultas dependem da agenda e disponibilidade dos profissionais cadastrados. O agendamento, cancelamento ou alteração podem seguir regras definidas pela clínica, sistema ou profissional responsável.

        5. Limitações do serviço
        O aplicativo não substitui atendimento médico, diagnóstico profissional ou orientação de emergência. Em situações urgentes, o usuário deve procurar atendimento médico imediatamente.

        6. Privacidade e proteção de dados
        Os dados fornecidos pelo usuário serão utilizados para autenticação, agendamento, gerenciamento de consultas e melhoria da experiência na plataforma. O aplicativo busca adotar boas práticas de segurança para proteção das informações.

        7. Atualizações dos termos
        Estes termos podem ser atualizados periodicamente para adequação funcional, legal ou de segurança.

        8. Aceitação
        Ao utilizar o aplicativo Minha Saúde, o usuário declara estar ciente e de acordo com estes Termos de Uso e Privacidade.
    """
}
