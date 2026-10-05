# API Biblioteca — Carreira 03

API CRUD de livros em Spring Boot (Java 17, Maven, banco H2 em memória).
Sobe em `http://localhost:8080`. Os dados são perdidos a cada reinício.

## Endpoints

| Método | Rota | Sucesso | Erros |
|---|---|---|---|
| POST | `/books` | 201 | 400 |
| GET | `/books` (filtro opcional `?disponivel=true\|false`) | 200 | 400 (filtro inválido) |
| GET | `/books/{id}` | 200 | 404, 400 (id inválido) |
| PUT | `/books/{id}` (parcial) | 200 | 400, 404 |
| DELETE | `/books/{id}` | 204 | 404 |
| POST | `/books/{id}/emprestar` | 200 | 404, 409 |
| POST | `/books/{id}/devolver` | 200 | 404, 409 |

O código fica na pasta `crudproject/`.

> Guia para testar em outra máquina e roteiro do dia da prova: [`PASSO_A_PASSO.md`](PASSO_A_PASSO.md).
> Simulado com o contrato de exemplo do professor (`/tasks`): [`simulado-tasks/`](simulado-tasks/).

## Subir com Docker (caminho da correção)

Requisito: Docker (ou Podman) instalado. A partir da raiz do repositório:

```bash
docker build -t biblioteca crudproject/
docker run -d -p 8080:8080 --name bib biblioteca
```

Para parar e remover o container:

```bash
docker rm -f bib
```

## Subir localmente (desenvolvimento)

Requisitos: Java 17 e Maven instalados.

```bash
cd crudproject
mvn spring-boot:run
```

No IntelliJ: abrir o `crudproject/pom.xml` como projeto e executar a classe `CrudprojectApplication`.

## Rodar a suíte de testes

Os testes públicos (`public_tests.py`) são fornecidos pelo professor e não ficam neste
repositório. `hidden_tests.py` é uma simulação própria dos testes escondidos, para treino.

Com a API no ar em `http://localhost:8080`, em outro terminal:

```bash
python3 -m venv .venv
source .venv/bin/activate        # Windows: .venv\Scripts\activate
pip install pytest requests
cd crudproject
pytest hidden_tests.py
```

Reinicie a API antes de cada execução completa: alguns testes esperam a base vazia e ids começando em 1.
