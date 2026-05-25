# Nota Técnica

Decisões de arquitetura, trade-offs e melhorias futuras do Attus Task Manager.

---

## Decisões de Arquitetura

### SQLite como banco de dados
O SQLite foi escolhido por ser leve, embarcado e não exigir instalação ou configuração de servidor externo. Para o contexto deste teste técnico, ele permite que qualquer avaliador clone o repositório e suba o projeto imediatamente sem dependências externas. O arquivo `taskmanager.db` é criado automaticamente na raiz do projeto na primeira execução.

### Gradle como build tool
O projeto foi gerado via Spring Initializr com Gradle por ser mais conciso e performático que o Maven em projetos modernos. O wrapper (`gradlew`) garante que o projeto rode sem necessidade de instalar o Gradle globalmente.

### Separação de DTOs (Request e Response)
A separação entre `TaskRequestDTO` e `TaskResponseDTO` foi uma decisão deliberada:
- **Request**: controla exatamente o que o cliente pode enviar, evitando over-posting
- **Response**: desacopla o contrato da API da entidade JPA, permitindo evoluir o banco sem quebrar o contrato com o cliente
- A entidade `Task` nunca é exposta diretamente na API

### PATCH exclusivo para mudança de status
O endpoint `PATCH /api/tasks/{id}/status` foi criado separadamente do `PUT /api/tasks/{id}` por uma razão semântica clara: o `PUT` atualiza dados descritivos da task (título e descrição), enquanto o `PATCH` representa uma transição de estado; uma operação com regra de negócio própria. Misturar os dois no mesmo endpoint tornaria a validação de transição ambígua e o contrato da API menos expressivo.

### Regra de transição no Enum
A lógica de transição de status foi colocada diretamente no `TaskStatus` (enum) através do método `canTransitionTo()`. Isso garante que a regra fique próxima do domínio, seja facilmente testável de forma isolada e evite duplicação caso outros serviços precisem validar transições no futuro.

### Injeção por construtor
Todos os componentes usam injeção por construtor em vez de `@Autowired` no campo. Isso torna as dependências explícitas, facilita os testes unitários com Mockito e segue as boas práticas recomendadas pelo próprio Spring.

### Logging em duas camadas
- **`LoggingInterceptor`**: loga todas as requisições HTTP (método, rota, status, duração) de forma centralizada, sem poluir os controllers
- **`TaskService`**: loga eventos de negócio (criação, atualização, erros esperados) com níveis adequados (`INFO` para operações normais, `WARN` para erros esperados como not found)

---

## Trade-offs

| Decisão | Vantagem | Limitação |
|---|---|---|
| SQLite | Zero configuração, portátil | Não suporta múltiplas escritas concorrentes |
| `ddl-auto=update` | Tabelas criadas automaticamente | Não recomendado em produção, usar migrations |
| Sem autenticação | Simplicidade para o teste | API aberta, sem controle de acesso |
| Testes unitários com Mockito | Rápidos e isolados | Não cobrem a integração real com o banco |
| `@CrossOrigin("*")` | Facilita integração com o front-end | Permissivo demais para produção |

---

## Melhorias Futuras

### Curto prazo
- **Migrations**: substituir o `ddl-auto=update` por controle versionado do schema
- **Paginação**: adicionar `Pageable` no `GET /api/tasks` para listar grandes volumes

### Médio prazo
- **Autenticação e autorização**: JWT com Spring Security, associando tasks a usuários
- **Banco relacional completo**: migrar para PostgreSQL em ambiente de produção
- **Docker**: containerizar a aplicação com `Dockerfile` e `docker-compose.yml`

### Longo prazo
- **Documentação automática**: Swagger/OpenAPI com SpringDoc
- **Observabilidade**: métricas com Spring Actuator
- **CI/CD**: pipeline com GitHub Actions para rodar testes a cada push
