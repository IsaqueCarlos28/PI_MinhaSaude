# MinhaSaude API – Documentação Funcional para Desenvolvimento do Aplicativo

## Objetivo

Este documento descreve as regras de negócio observadas na API, os contratos principais de entrada e saída e o comportamento esperado pelo aplicativo.

O foco é ajudar desenvolvedores mobile, web e futuras IAs a entenderem como o backend deve ser consumido.

---

# Conceitos do Domínio

## Paciente

Usuário que agenda consultas.

Pode:

- autenticar-se
- visualizar consultas
- agendar consultas
- reagendar consultas
- cancelar consultas
- receber notificações

---

## Médico

Usuário responsável por disponibilizar consultas.

Pode:

- configurar consultas ofertadas
- configurar agendas recorrentes
- bloquear horários específicos
- visualizar consultas agendadas
- concluir consultas
- cancelar consultas

---

## Consulta Ofertada

Representa um serviço médico disponível para agendamento.

Exemplos:

- Consulta clínica geral presencial
- Consulta cardiológica online
- Retorno presencial

Possui:

- especialidade
- tipo de consulta
- local
- duração
- preço
- convênios aceitos
- regra de atendimento particular

---

## Agenda

Representa horários recorrentes disponíveis.

Exemplo:

- Segunda 08:00–12:00
- Quarta 13:00–18:00

A agenda não representa consultas reais.

Ela apenas define quando consultas podem ser criadas.

---

## EventoConsulta

Representa uma consulta efetivamente agendada.

Contém:

- paciente
- médico
- data
- horário
- convênio
- status

---

## BloqueioAgenda

Representa indisponibilidade do médico.

Exemplos:

- férias
- almoço
- congresso
- licença

---

# Fluxo Principal

1. Médico cria consulta ofertada.
2. Médico cria agendas.
3. Médico cria bloqueios quando necessário.
4. Paciente consulta disponibilidade.
5. Paciente agenda.
6. Sistema envia notificações.
7. Médico realiza ou cancela.
8. Paciente acompanha status.

---

# Autenticação

## POST /auth/login

### Entrada

```json
{
  "email": "usuario@email.com",
  "senha": "12345678"
}
```

### Regras

- email obrigatório
- email válido
- senha obrigatória
- mínimo 8 caracteres

### Retorno

JWT + informações do usuário.

---

# Recuperação de Senha

## POST /auth/esqueceu-a-senha

Solicita envio de código de recuperação.

### Entrada

```json
{
  "email": "usuario@email.com"
}
```

---

## POST /auth/validar-codigo

Valida código enviado por email.

Retorna token temporário de recuperação.

---

## PUT /auth/alterar-senha

Altera senha usando token de recuperação.

---

# Consultas Ofertadas

## GET /medicos/{idMedico}/consultas-ofertadas

Lista serviços disponíveis do médico.

---

## POST /medicos/{idMedico}/consultas-ofertadas

### Entrada

```json
{
  "idEspecialidade": 1,
  "idLocal": 1,
  "tipoConsulta": "PRESENCIAL",
  "valorConsulta": 150.00,
  "aceitaParticular": true,
  "duracaoMinutos": 30,
  "conveniosAceitosIds": [1,2]
}
```

### Regras

- especialidade obrigatória
- duração > 0
- valor >= 0
- tipo obrigatório
- lista de convênios válida
- aceitaParticular obrigatório

### Impacto

A duração é usada para:

- cálculo de disponibilidade
- conflitos de horário
- definição do horário final

---

# Agenda

## POST /medicos/{idMedico}/consultas-ofertadas/{idConsulta}/agenda

### Entrada

```json
{
  "diaSemana": "SEGUNDA",
  "horaInicio": "08:00",
  "horaFim": "12:00"
}
```

### Regras

- dia obrigatório
- hora inicial obrigatória
- hora final obrigatória
- início deve ser anterior ao fim

### Observação

A agenda define apenas disponibilidade recorrente.

Não cria consultas.

---

## GET disponibilidade

### Endpoint

```text
GET /medicos/{idMedico}/consultas-ofertadas/{idConsulta}/agenda/disponibilidade
```

### Parâmetro

```text
?semanas=4
```

### Retorna

Horários efetivamente disponíveis.

### O backend considera

- agendas
- duração da consulta
- bloqueios
- consultas existentes

### Regra para o App

Nunca calcular disponibilidade localmente.

Sempre utilizar o resultado da API.

---

# Bloqueio de Agenda

## POST /medicos/{idMedico}/bloqueio_agenda

### Entrada

```json
{
  "data": "2026-07-10",
  "horaInicio": "12:00",
  "horaFim": "14:00"
}
```

### Regras

- data obrigatória
- data futura ou atual
- horário obrigatório

### Efeito

O período bloqueado deixa de aparecer como disponível.

---

# Agendamento de Consulta

## POST /pacientes/{idPaciente}/consultas

### Entrada

```json
{
  "idConsultaOfertada": 15,
  "idConvenio": 2,
  "data": "2026-08-20",
  "horaInicio": "09:00"
}
```

---

## Validações de Negócio

### Consulta ofertada deve existir

Caso contrário retorna erro.

---

### Data não pode estar no passado

A própria DTO exige:

```text
FutureOrPresent
```

---

### Horário deve existir na agenda

O slot deve ser compatível com:

- agenda cadastrada
- duração da consulta

---

### Horário não pode estar bloqueado

Bloqueios invalidam o agendamento.

---

### Médico não pode ter conflito

O médico não pode possuir outra consulta sobreposta.

---

### Paciente não pode ter conflito

O paciente não pode possuir outra consulta sobreposta.

---

### Convênio

Quando informado:

- deve existir
- deve ser aceito pela consulta ofertada

Quando não informado:

- consulta precisa aceitar particular

---

## Resposta

```json
{
  "id": 1,
  "idPaciente": 10,
  "nomePaciente": "João",
  "idConsultaOfertada": 15,
  "idUsuarioMedico": 5,
  "nomeMedico": "Dra Maria",
  "idConvenio": 2,
  "nomeConvenio": "Unimed",
  "data": "2026-08-20",
  "horaInicio": "09:00",
  "horaFim": "09:30",
  "status": "AGENDADA"
}
```

---

# Reagendamento

## PUT /pacientes/{idPaciente}/consultas/{idEvento}

Mesmo payload do agendamento.

### Regras

Todas as validações são executadas novamente.

Incluindo:

- disponibilidade
- convênio
- conflitos
- bloqueios

---

# Consulta do Paciente

## GET /pacientes/{idPaciente}/consultas

Retorna todas as consultas do paciente.

---

## GET /pacientes/{idPaciente}/consultas/{idEvento}

Retorna detalhes de uma consulta específica.

---

# Consulta do Médico

## GET /medicos/{idMedico}/consultas-agendadas

Lista consultas do médico.

---

## GET /medicos/{idMedico}/consultas-agendadas/{idEvento}

Detalhes de uma consulta.

---

# Status das Consultas

## Status conhecidos

```text
AGENDADA
CANCELADA
REALIZADA
```

---

# Alteração pelo Paciente

## PATCH /pacientes/{idPaciente}/consultas/{idEvento}/status

### Entrada

```json
{
  "status": "CANCELADA"
}
```

### Regra Principal

Paciente apenas cancela.

Não deve conseguir marcar:

- REALIZADA
- AGENDADA

---

# Alteração pelo Médico

## PATCH /medicos/{idMedico}/consultas-agendadas/{idEvento}/status

### Entrada

```json
{
  "status": "REALIZADA"
}
```

ou

```json
{
  "status": "CANCELADA"
}
```

### Regras

Médico pode:

- cancelar
- concluir

Médico não deve reagendar através deste endpoint.

---

# Notificações Push

## Registrar Token

### Endpoint

```text
POST /notificacoes/fcm-token
```

### Entrada

```json
{
  "usuarioId": 10,
  "token": "FCM_TOKEN"
}
```

### Comportamento

- token associado ao usuário
- tokens antigos podem ser desativados
- apenas tokens ativos recebem mensagens

---

## Logout

O aplicativo deve solicitar desativação do token quando disponível.

Isso evita envio para dispositivos antigos.

---

# Erros que o App Deve Tratar

## Conflitos

```text
Horário indisponível
Paciente já possui consulta
Médico já possui consulta
```

---

## Convênio

```text
Convênio não aceito
Convênio inexistente
```

---

## Recursos inexistentes

```text
Paciente não encontrado
Médico não encontrado
Consulta não encontrada
Especialidade não encontrada
```

---

## Validação

```text
Campo obrigatório
Data inválida
Email inválido
Senha inválida
```

---

# Estratégia Recomendada para o Aplicativo

## Tela de Agendamento

Fluxo ideal:

1. Buscar consultas ofertadas.
2. Usuário seleciona consulta.
3. Buscar disponibilidade.
4. Selecionar data.
5. Selecionar horário.
6. Agendar.
7. Atualizar lista de consultas.

---

## Tela Minhas Consultas

Exibir:

- médico
- especialidade
- data
- horário
- convênio
- status

---

## Sincronização

Após qualquer operação:

- agendar
- reagendar
- cancelar
- concluir

Atualizar dados diretamente da API.

Nunca assumir estado local como verdade absoluta.

---

# Fonte de Verdade

A API é a autoridade final para:

- disponibilidade
- conflitos
- status
- validade de convênio
- duração
- regras de negócio

O aplicativo deve atuar apenas como cliente e não duplicar essas validações localmente.
