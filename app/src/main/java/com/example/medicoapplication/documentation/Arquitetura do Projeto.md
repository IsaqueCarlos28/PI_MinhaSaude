# Arquitetura do Projeto - Minha Saúde

## Visão Geral

O projeto segue uma arquitetura baseada em **MVVM (Model-View-ViewModel)** com uso de **Repository Pattern** para abstrair o acesso aos dados.

Fluxo principal:

```text
Activity
    ↓
ViewModel
    ↓
Repository
    ↓
Retrofit API
    ↓
Backend
```

---

# Estrutura de Pastas

```text
com.example.medicoapplication
│
├── data
│   ├── local
│   ├── remote
│   │   └── DTO
│   └── repository
│
├── UI
│   ├── activities
│   ├── adapters
│   └── common
│
└── viewmodel
    ├── auth
    ├── medico
    └── paciente
```

---

# Camadas

## UI

Localização:

```text
UI/
```

Responsabilidades:

* Exibir dados ao usuário
* Capturar interações
* Observar estados dos ViewModels
* Navegação entre telas

A UI não deve conter regras de negócio complexas.

Exemplos:

```text
LoginActivity
PerfilPacienteActivity
MinhasConsultasActivity
```

---

## ViewModels

Localização:

```text
viewmodel/
```

Responsabilidades:

* Coordenar regras da tela
* Chamar repositories
* Transformar resultados em estados de UI
* Expor StateFlows para observação

Fluxo típico:

```text
Usuário clica em "Entrar"
    ↓
LoginViewModel.login()
    ↓
AuthRepository.login()
    ↓
Atualiza UiState
```

Padrão recomendado:

```kotlin
sealed class UiState {
    object Idle
    object Loading
    data class Success(...)
    data class Error(...)
}
```

---

## Repositories

Localização:

```text
data/repository
```

Responsabilidades:

* Centralizar acesso aos dados
* Executar chamadas Retrofit
* Tratar erros de rede
* Esconder detalhes da API dos ViewModels

Exemplos:

```text
AuthRepository
PacienteRepository
MedicoRepository
ConsultaRepository
AgendaRepository
```

Os ViewModels nunca devem acessar Retrofit diretamente.

---

## Camada Remota

Localização:

```text
data/remote
```

Contém:

* RetrofitClient
* ApiService
* DTOs
* Classes de erro de rede

Todas as chamadas HTTP devem passar por esta camada.

---

## DTOs

Localização:

```text
data/remote/DTO
```

Responsabilidade:

Representar exatamente os contratos da API.

Exemplos:

```kotlin
LoginRequestDto
LoginResponseDto
PacienteResponseDto
```

DTOs não devem conter lógica de negócio.

---

## Sessão do Usuário

Localização:

```text
data/local
```

Classes principais:

```text
SessionManager
AppDependencies
```

Responsabilidades:

* Armazenar login atual
* Recuperar usuário logado
* Persistir informações de sessão

Inicialização:

```kotlin
AppDependencies.init(context)
```

---

# Tratamento de Erros

O projeto utiliza:

```kotlin
Result<T>
```

e erros customizados de rede.

Padrão:

```kotlin
repository.algumaOperacao()
    .onSuccess { ... }
    .onFailure { ... }
```

Erros conhecidos:

```text
NetworkException
NetworkError
```

---

# Organização por Domínio

## Auth

```text
Login
Cadastro
Logout
Recuperação de senha
```

## Paciente

```text
Perfil
Busca de médicos
Agendamento
Consultas
```

## Médico

```text
Agenda
Consultas ofertadas
Perfil
```

---

# Convenções

## Novas telas

```text
NovaTelaActivity
NovaTelaViewModel
NovaTelaRepository
```

Fluxo obrigatório:

```text
Activity
    ↓
ViewModel
    ↓
Repository
```

---

## Regras de negócio

Devem ficar em:

```text
ViewModel
Repository
```

Nunca diretamente na Activity.

---

## Chamadas HTTP

Devem ficar apenas em:

```text
Repository
```

Nunca em:

```text
Activity
ViewModel
Adapter
```

---

## Estados da UI

Preferir:

```kotlin
sealed class UiState
```

ao invés de múltiplos booleans.

---

# Evoluções Futuras

## Injeção de Dependência

Atual:

```text
AppDependencies
```

Futuro:

```text
Hilt
Koin
```

---

## Use Cases

Arquitetura futura:

```text
ViewModel
    ↓
UseCase
    ↓
Repository
```

Exemplos:

```text
AgendarConsultaUseCase
CancelarConsultaUseCase
```

---

## Domain Models

Arquitetura futura:

```text
DTO
 ↓
Mapper
 ↓
Domain Model
```

---

## Testes

Prioridade:

* ViewModels
* Repositories

Ferramentas:

* JUnit
* Mockito
* MockK

---

# Resumo

* UI apenas exibe dados.
* ViewModels controlam estado.
* Repositories acessam dados.
* Retrofit é a única forma de comunicação com backend.
* SessionManager controla autenticação.
* DTOs representam contratos da API.
* Toda nova funcionalidade deve respeitar o fluxo:

```text
Activity
 ↓
ViewModel
 ↓
Repository
 ↓
API
```
