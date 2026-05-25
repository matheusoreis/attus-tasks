# Attus Task Manager

Sistema de gerenciamento de tarefas com controle de fluxo de status.

---

## Estrutura do Repositório

```
attus-tasks/
├── backend/    → SpringBoot
└── frontend/   → React + Vite
```

---

## Tecnologias

### Back-end
- Java 17
- Spring Boot 4.0.6
- Spring Data JPA
- SQLite
- Gradle
- JUnit 5 + Mockito

### Front-end *(em desenvolvimento)*
- React + Vite
- TypeScript

---

## Como executar

### Back-end

**Pré-requisitos**
- Java 17+

```bash
# Clone o repositório
git clone https://github.com/matheusoreis/attus-tasks.git

# Entre na pasta do back-end
cd attus-tasks/backend

# Suba o projeto
./gradlew bootRun
```

> Windows: use `gradlew.bat bootRun`

A API estará disponível em: `http://localhost:8080`

O banco de dados SQLite será criado automaticamente na raiz do projeto como `taskmanager.db`.

### Front-end *(em desenvolvimento)*

> Em breve.

---

## Rodando os testes

```bash
cd backend
./gradlew test
```

14 testes unitários cobrindo CRUD completo, filtros e transições de status.

---

## Estrutura do Back-end

```
src/main/java/br/gov/sp/attus/backend/
├── configs/ → Configuração do interceptor HTTP
├── controllers/ → Endpoints
├── dtos/ → Objetos de entrada e saída da API
├── enums/ → TaskStatus com regras de transição
├── exceptions/ → Exceções customizadas e handler global
├── loggings/ → Interceptor de log por requisição
├── models/ → Entidade JPA
├── repositories/ → Acesso ao banco de dados
└── services/ → Regras de negócio
```

---

## Endpoints da API

Base URL: `http://localhost:8080/api/tasks`

### Listar todas as tasks
```
GET /api/tasks
GET /api/tasks?status=TODO
```

**Response 200:**
```json
[
  {
    "id": 1,
    "title": "Minha Task",
    "description": "Descrição da Task",
    "status": "TODO",
    "createdAt": "2026-05-25T17:40:26.015",
    "updatedAt": "2026-05-25T17:40:26.015"
  }
]
```

---

### Buscar task por ID
```
GET /api/tasks/{id}
```

**Response 200:**
```json
{
    "id": 1,
    "title": "Minha Task",
    "description": "Descrição da Task",
    "status": "TODO",
    "createdAt": "2026-05-25T17:40:26.015",
    "updatedAt": "2026-05-25T17:40:26.015"
  }
```

**Response 404:**
```json
{
  "timestamp": "2026-05-25T17:40:26.015",
  "status": 404,
  "message": "Task not found with id: 1"
}
```

---

### Criar task
```
POST /api/tasks
```

**Request body:**
```json
{
  "title": "Minha Task",
  "description": "Descrição da Task",
}
```

**Response 201:**
```json
{
  "id": 1,
  "title": "Minha Task",
  "description": "Descrição da Task",
  "status": "TODO",
  "createdAt": "2026-05-25T17:40:26.015",
  "updatedAt": "2026-05-25T17:40:26.015"
}
```

**Response 400 (validação):**
```json
{
  "timestamp": "2026-05-25T17:40:26.015",
  "status": 400,
  "errors": {
    "title": "Title is required"
  }
}
```

---

### Atualizar task
```
PUT /api/tasks/{id}
```

**Request body:**
```json
{
  "title": "Minha Task",
  "description": "Descrição da Task",
}
```

**Response 200:**
```json
{
  "id": 1,
  "title": "Minha Task",
  "description": "Descrição da Task",
  "status": "TODO",
  "createdAt": "2026-05-25T17:40:26.015",
  "updatedAt": "2026-05-25T17:40:26.015"
}
```

**Response 404:**
```json
{
  "timestamp": "2026-05-25T17:40:26.015",
  "status": 404,
  "message": "Task not found with id: 1"
}
```

---

### Atualizar status
```
PATCH /api/tasks/{id}/status
```

**Request body:**
```json
{
  "status": "IN_PROGRESS"
}
```

**Response 200:**
```json
{
  "id": 1,
  "title": "Minha Task",
  "description": "Descrição da Task",
  "status": "TODO",
  "createdAt": "2026-05-25T17:40:26.015",
  "updatedAt": "2026-05-25T17:40:26.015"
}
```

**Response 422 (transição inválida):**
```json
{
  "timestamp": "2026-05-25T17:40:26.015",
  "status": 422,
  "message": "Invalid status transition from TODO to DONE"
}
```

---

### Deletar task
```
DELETE /api/tasks/{id}
```

**Response 204:** sem corpo

**Response 404:**
```json
{
  "timestamp": "2026-05-25T17:40:26.015",
  "status": 404,
  "message": "Task not found with id: 1"
}
```

---

## Fluxo de Status

```
TODO → IN_PROGRESS → DONE
 ↑________________________| reabrir é permitido
```

| De | Para | Permitido |
|---|---|---|
| TODO | IN_PROGRESS | ✅ |
| IN_PROGRESS | DONE | ✅ |
| DONE | TODO | ✅ reabrir |
| TODO | DONE | ❌ 422 |
| IN_PROGRESS | TODO | ❌ 422 |

---

## Cobertura de Testes

| Cenário | Status |
|---|---|
| Criar task | ✅ |
| Listar todas | ✅ |
| Listar com filtro de status | ✅ |
| Buscar por ID | ✅ |
| Buscar ID inexistente | ✅ |
| Atualizar task | ✅ |
| Atualizar task inexistente | ✅ |
| Deletar task | ✅ |
| Deletar task inexistente | ✅ |
| TODO → IN_PROGRESS | ✅ |
| IN_PROGRESS → DONE | ✅ |
| DONE → TODO (reabrir) | ✅ |
| TODO → DONE (inválido) | ✅ |
| IN_PROGRESS → TODO (inválido) | ✅ |

---

## Documentos

- [`TECHNICAL_NOTE.md`](./TECHNICAL_NOTE.md) — Decisões técnicas e trade-offs
- [`INCIDENT_ANALYSIS.md`](./INCIDENT_ANALYSIS.md) — Análise de incidente
