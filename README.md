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

## Subir com Docker (caminho da correção)

Requisito: Docker (ou Podman) instalado.

```bash
docker build -t biblioteca .
docker run -d -p 8080:8080 --name bib biblioteca
```

Para parar e remover o container:

```bash
docker rm -f bib
```

## Subir localmente (desenvolvimento)

Requisitos: Java 17. O Maven já vem no wrapper (`./mvnw`).

```bash
./mvnw spring-boot:run
```

Se o Maven estiver instalado, também funciona `mvn spring-boot:run`.
No IntelliJ: abrir o `pom.xml` como projeto e executar a classe `CrudprojectApplication`.

No Windows, usar `mvnw.cmd spring-boot:run`.

## Rodar a suíte de testes

Com a API no ar em `http://localhost:8080`, em outro terminal:

```bash
python3 -m venv .venv
source .venv/bin/activate        # Windows: .venv\Scripts\activate
pip install pytest requests
pytest hidden_tests.py public_tests.py
```

Reinicie a API antes de cada execução completa: alguns testes esperam a base vazia e ids começando em 1.
