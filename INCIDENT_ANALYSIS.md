# Análise de Incidente

**Sistema:** Attus Task Manager  
**Data:** 2025-05-25  
**Severidade:** Média  
**Status:** Resolvido  

---

## Descrição do Incidente

Foi identificado que tasks estavam tendo seu status alterado livremente através do endpoint `PUT /api/tasks/{id}`, sem respeitar o fluxo de negócio definido. Tasks pulavam de `TODO` diretamente para `DONE` sem passar por `IN_PROGRESS`, corrompendo o estado do sistema silenciosamente, sem nenhum erro retornado.

---

## Logs Observados

```
[INFO]  LoggingInterceptor - Incoming request → PUT /api/tasks/7 | IP: 192.168.1.10
[INFO]  TaskService - Updating task with id: 7
[INFO]  TaskService - Task updated successfully with id: 7
[INFO]  LoggingInterceptor - Completed request → PUT /api/tasks/7 | Status: 200 | Duration: 18ms

[INFO]  LoggingInterceptor - Incoming request → PUT /api/tasks/12 | IP: 192.168.1.10
[INFO]  TaskService - Updating task with id: 12
[INFO]  TaskService - Task updated successfully with id: 12
[INFO]  LoggingInterceptor - Completed request → PUT /api/tasks/12 | Status: 200 | Duration: 14ms
```

> Nenhum erro nos logs, o problema era silencioso. As requisições retornavam `200 OK` mas o status era alterado sem qualquer validação de transição.

---

## Identificação

| Campo | Detalhe |
|---|---|
| **Endpoint afetado** | `PUT /api/tasks/{id}` |
| **Comportamento errado** | Status alterado livremente no mesmo body de título e descrição |
| **Impacto** | Tasks em `TODO` sendo marcadas como `DONE` sem passar por `IN_PROGRESS` |
| **Visibilidade** | Nenhuma, retornava `200 OK` normalmente |

---

## Causa Raiz

O endpoint `PUT /api/tasks/{id}` aceitava o campo `status` no body e o aplicava diretamente na entidade, sem qualquer validação de transição. Isso significava que qualquer cliente podia enviar:

```json
{
  "title": "Minha task",
  "description": "Descrição",
  "status": "DONE"
}
```

E a task pularia de `TODO` para `DONE` diretamente, violando o fluxo de negócio:

```
TODO → DONE ❌ (deveria ser recusado)
```

O problema era estrutural, a responsabilidade de atualizar dados descritivos e mudar o estado da task estavam misturadas no mesmo endpoint.

---

## Correção Aplicada

A solução foi separar as responsabilidades em endpoints distintos:

**`PUT /api/tasks/{id}`**, atualiza apenas dados descritivos:
```json
{
  "title": "Título atualizado",
  "description": "Nova descrição"
}
```

**`PATCH /api/tasks/{id}/status`**, exclusivo para mudança de status, com validação de transição:
```json
{
  "status": "IN_PROGRESS"
}
```

Transições inválidas passaram a retornar `422 Unprocessable Entity`:
```json
{
  "timestamp": "2025-05-25T14:02:11",
  "status": 422,
  "message": "Invalid status transition from TODO to DONE"
}
```

O fluxo correto passou a ser garantido pela API:
```
TODO → IN_PROGRESS → DONE
 ↑________________________| (reabrir permitido)
```

---

## Medidas de Prevenção

### Imediatas
- **Testes unitários** cobrindo todas as transições válidas e inválidas, qualquer regressão agora é detectada automaticamente
- **Separação clara de responsabilidades por endpoint**, `PUT` para dados, `PATCH` para estado

### Curto prazo
- **Monitoramento de volume de `422`** no `PATCH /status`, um pico indica tentativas de transição inválida por parte do cliente
- **Documentação explícita do fluxo de status** no README para alinhar front-end e back-end

### Longo prazo
- **Documentação automática com OpenAPI/Swagger**, tornar o contrato da API e o fluxo de status visíveis e navegáveis
- **Testes de integração** cobrindo o fluxo completo de status ponta a ponta
