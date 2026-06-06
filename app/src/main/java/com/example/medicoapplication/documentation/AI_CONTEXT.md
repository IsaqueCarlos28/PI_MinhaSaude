# AI_CONTEXT.md

> Documento de contexto para desenvolvedores e IAs que forem trabalhar no projeto.

---

# Sobre o Projeto

Minha Saúde é um aplicativo Android desenvolvido em Kotlin que conecta pacientes e médicos.

Principais funcionalidades:

* Autenticação
* Cadastro
* Perfil de paciente
* Perfil de médico
* Busca de médicos
* Agendamento de consultas
* Gerenciamento de consultas
* Agenda médica
* Controle de sessão

---

# Stack Tecnológica

## Linguagem

```text
Kotlin
```

## Arquitetura

```text
MVVM + Repository Pattern
```

## UI

```text
Android XML Views
RecyclerView
Activities
```

## Concorrência

```text
Coroutines
StateFlow
Flow
```

## Rede

```text
Retrofit
Gson
```

## Persistência

```text
Jetpack DataStore
```

---

# Fluxo Arquitetural

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

A Activity nunca deve acessar Retrofit diretamente.

---

# Estrutura do Projeto

## data/local

Responsável por persistência local.

Classes principais:

```text
SessionManager
AppDependencies
```

### SessionManager

Responsável por:

* Login atual
* Usuário atual
* Logout
* Persistência de sessão

Utiliza:

```text
Jetpack DataStore
```

---

## data/remote

Responsável pela integração com a API.

Contém:

* RetrofitClient
* ApiService
* DTOs
* NetworkError

---

## data/repository

Responsável pelo acesso aos dados.

Repositories existentes:

```text
AuthRepository
PacienteRepository
MedicoRepository
ConsultaRepository
ConsultaOfertadaRepository
AgendaRepository
BloqueioAgendaRepository
ConvenioRepository
EspecialidadeRepository
LocalRepository
```

Nenhuma chamada HTTP deve sair desta camada.

---

## ViewModels

Responsáveis pela lógica de apresentação.

Organizados por domínio:

```text
viewmodel
├── auth
├── medico
└── paciente
```

Cada tela deve possuir seu próprio ViewModel.

---

# Tratamento de Erros

O projeto utiliza:

```kotlin
Result<T>
```

e:

```kotlin
NetworkException
NetworkError
```

Sempre reutilizar a infraestrutura existente.

Exemplo:

```kotlin
repository.buscar()
    .onSuccess { }
    .onFailure { }
```

---

# Estados da UI

Preferir:

```kotlin
sealed class UiState
```

com:

```text
Idle
Loading
Success
Error
```

Evitar múltiplos booleans independentes.

---

# BaseRepository

Existe uma abstração comum para repositories.

Responsabilidades:

* Recuperar usuário logado
* Compartilhar lógica comum
* Evitar duplicação

Exemplo:

```kotlin
requireUserId()
```

Sempre reutilizar quando possível.

---

# Safe API Call

Toda chamada remota deve preferencialmente passar por:

```kotlin
safeApiCall()
```

Objetivos:

* Padronizar erros
* Padronizar Result<T>
* Reduzir código duplicado

---

# Regras para Desenvolvedores e IAs

Antes de criar código:

1. Procurar ViewModel relacionado.
2. Procurar Repository relacionado.
3. Procurar DTO semelhante.
4. Reutilizar SessionManager.
5. Reutilizar NetworkError.
6. Reutilizar safeApiCall.
7. Seguir MVVM atual.

---

# Não Fazer

❌ Retrofit em Activities

❌ Retrofit em Adapters

❌ Retrofit em ViewModels

❌ Regras de negócio em Activities

❌ Duplicação de SessionManager

❌ Duplicação de lógica já existente

---

# Sempre Fazer

✅ Activity → ViewModel → Repository

✅ StateFlow para estado da UI

✅ Coroutines para operações assíncronas

✅ Result<T> para retorno de operações

✅ Reutilização dos componentes existentes

---

# Melhorias Futuras

## Injeção de Dependência

Atual:

```text
AppDependencies
```

Planejado:

```text
Hilt
```

ou

```text
Koin
```

---

## Use Cases

Futuro:

```text
ViewModel
    ↓
UseCase
    ↓
Repository
```

---

## Domain Models

Futuro:

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

Ferramentas recomendadas:

* JUnit
* MockK
* Mockito

---

# Resumo Executivo

Arquitetura atual:

```text
MVVM
+ Repository Pattern
+ Retrofit
+ Coroutines
+ StateFlow
+ DataStore
```

Objetivo principal:

Manter separação clara entre UI, estado, regras de negócio e acesso a dados, reduzindo acoplamento e facilitando manutenção e evolução do projeto.
