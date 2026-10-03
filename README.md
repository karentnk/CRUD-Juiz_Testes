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

A API leva uns 5–10 s para subir (Spring Boot). Para conferir que está no ar:

```bash
curl http://localhost:8080/books   # deve responder []
```

Para parar e remover o container:

```bash
docker rm -f bib
```

## Subir localmente (desenvolvimento)

Requisitos: Java 17 ou superior. O Maven já vem no wrapper (`./mvnw`), não precisa instalar.

```bash
./mvnw spring-boot:run
```

Se o Maven estiver instalado, também funciona `mvn spring-boot:run`.
No IntelliJ: abrir o `pom.xml` como projeto e executar a classe `CrudprojectApplication`.

No Windows, usar `mvnw.cmd spring-boot:run`.

Ou gerar o `.jar` e rodar direto:

```bash
./mvnw -DskipTests package
java -jar target/crudproject-0.0.1-SNAPSHOT.jar
```

## Validações

- `titulo` e `autor`: obrigatórios, string, não vazios (só espaços conta como vazio).
- `ano`: obrigatório, inteiro entre 1450 e o ano atual.
- `paginas`: opcional, inteiro >= 1; se omitido, vale `0`.
- Tipo errado (ex.: `"titulo": 123`, `"ano": "2000"`, `"ano": 2000.5`) → 400.
- JSON inválido ou corpo vazio no POST → 400. Campos extras são ignorados.
- `id` e `disponivel` não são aceitos no corpo; `disponivel` só muda via emprestar/devolver.

## Estrutura

```
Dockerfile             # build multi-stage (Maven -> JRE 17)
pom.xml, mvnw, .mvn/   # build Maven + wrapper
src/main/java/...      # controller / service / repository / model
public_tests.py        # testes públicos (modelo)
hidden_tests.py        # simulação de testes escondidos
```

## Rodar a suíte de testes

Com a API no ar em `http://localhost:8080`, em outro terminal:

```bash
python3 -m venv .venv
source .venv/bin/activate        # Windows: .venv\Scripts\activate
pip install pytest requests
pytest hidden_tests.py public_tests.py
```

Reinicie a API antes de cada execução completa: alguns testes esperam a base vazia e ids começando em 1.
