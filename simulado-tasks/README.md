# API de Tarefas — Simulado Carreira 03

Simulado de prova: o projeto `crudproject/` (livros) adaptado para o contrato de exemplo
do professor (`contrato.md`). Spring Boot (Java 17, Maven, H2 em memória).
Sobe em `http://localhost:8080`. Os dados são perdidos a cada reinício.

## Endpoints

| Método | Rota | Sucesso | Erros |
|---|---|---|---|
| POST | `/tasks` | 201 | 400 (titulo ausente ou vazio) |
| GET | `/tasks` | 200 | — |
| GET | `/tasks/{id}` | 200 | 404, 400 (id inválido) |
| PUT | `/tasks/{id}` (parcial) | 200 | 400, 404 |
| DELETE | `/tasks/{id}` | 204 | 404 |

## Subir com Docker

Requisito: Docker (ou Podman) instalado. A partir da raiz do repositório:

```bash
docker build -t tarefas simulado-tasks/
docker run -d -p 8080:8080 --name tarefas tarefas
```

Para parar e remover: `docker rm -f tarefas`

## Subir localmente

Requisitos: Java 17 e Maven instalados.

```bash
cd simulado-tasks
mvn spring-boot:run
```

## Rodar os testes

Com a API no ar, em outro terminal:

```bash
pip install pytest requests
cd simulado-tasks
pytest hidden_tests.py public_tests.py
```

`public_tests.py` e `contrato.md` são o exemplo público do professor; `hidden_tests.py` é treino próprio.
Reinicie a API antes de cada execução: alguns testes esperam a base vazia e ids começando em 1.

## O que mudou em relação ao `crudproject/`

| Arquivo | Mudança |
|---|---|
| `model/Livro.java` → `model/Tarefa.java` | campos `titulo`, `descricao`, `concluida` |
| `model/LivroIn.java` → `model/TarefaIn.java` | campos do JSON de entrada |
| `service/LivroService.java` → `service/TarefaService.java` | só `titulo` obrigatório; `descricao` vira `""` se omitida; sem emprestar/devolver |
| `controller/LivroController.java` → `controller/TarefaController.java` | rota `/tasks`; sem filtro e sem emprestar/devolver |
| `repository/LivroRepository.java` → `repository/TarefaRepository.java` | sem `findByDisponivel` |

`Dockerfile`, `pom.xml` e `application.properties` não mudaram.
