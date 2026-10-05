# Passo a passo — testar na máquina da faculdade

Os comandos estão para **Windows (PowerShell)**. Se a máquina for Linux, veja as
diferenças no fim de cada passo.

---

## 0. Ver o que a máquina tem

Abra o PowerShell e rode um por vez:

```powershell
git --version
java -version
mvn -version
python --version
docker --version
```

| Resultado | O que fazer |
|---|---|
| `java -version` mostra 17 ou mais | ✅ |
| `mvn` não reconhecido | Use o IntelliJ (passo 2, opção C) |
| `python` não reconhecido | Tente `py --version`; se funcionar, troque `python` por `py` nos comandos |
| `docker` funciona | Pode testar o caminho do professor (passo 4) |
| `docker` não existe / sem permissão | Tudo bem: teste local (passo 2) e confie no Dockerfile já validado |

---

## 1. Clonar o repositório

```powershell
cd ~\Desktop
git clone https://github.com/karentnk/CRUD-Juiz_Testes.git
cd CRUD-Juiz_Testes
```

---

## 2. Subir a API (terminal 1)

Escolha **uma** opção. Deixe esse terminal aberto: a API roda nele.

**A) Projeto de livros**
```powershell
cd crudproject
mvn spring-boot:run
```

**B) Simulado de tarefas (contrato de exemplo do professor)**
```powershell
cd simulado-tasks
mvn spring-boot:run
```

**C) Sem Maven no terminal: IntelliJ**
1. *File → Open* → escolha `crudproject\pom.xml` (ou `simulado-tasks\pom.xml`) → *Open as Project*.
2. Espere o IntelliJ baixar as dependências (barra no canto inferior).
3. Abra `src\main\java\com\example\crudproject\CrudprojectApplication.java` e clique no ▶ verde.

Pronto quando aparecer no log: `Tomcat started on port 8080`.

---

## 3. Testar (terminal 2)

Abra **outro** PowerShell, na pasta `CRUD-Juiz_Testes`:

```powershell
python -m venv .venv
.venv\Scripts\activate
pip install pytest requests
```

> Se o `activate` der erro de "execução de scripts desabilitada", rode
> `Set-ExecutionPolicy -Scope Process Bypass` e tente de novo.
> Ou pule o venv e use direto `python -m pip install --user pytest requests`.

Confira se a API responde:

```powershell
curl.exe http://localhost:8080/books     # opção A  → deve mostrar []
curl.exe http://localhost:8080/tasks     # opção B  → deve mostrar []
```

Rode os testes:

```powershell
cd crudproject;     python -m pytest hidden_tests.py -v                    # opção A
cd simulado-tasks;  python -m pytest hidden_tests.py public_tests.py -v    # opção B
```

Esperado: tudo `PASSED`.

**Antes de rodar de novo:** pare a API (`Ctrl+C` no terminal 1) e suba de novo.
O banco é em memória e alguns testes esperam lista vazia e id começando em 1.

> Linux: `source .venv/bin/activate`, `python3` no lugar de `python`, `curl` no lugar de `curl.exe`.

---

## 4. Testar como o professor (Docker)

Só se o Docker funcionar. Pare a API do passo 2 antes (a porta 8080 precisa estar livre).
Na pasta `CRUD-Juiz_Testes`:

```powershell
docker build -t api crudproject/          # ou: simulado-tasks/
docker run -d -p 8080:8080 --name api api
```

O primeiro build demora alguns minutos (baixa Maven e dependências).
Espere ~10 s, confira com `curl.exe`, rode os testes do passo 3 e no fim:

```powershell
docker rm -f api
```

---

## 5. Testar um caso à mão (útil para os testes escondidos)

Abra o **Prompt de Comando (cmd)**: o escape de aspas nele é o mesmo em qualquer Windows.

```bat
:: criar
curl -i -X POST http://localhost:8080/tasks -H "Content-Type: application/json" -d "{\"titulo\": \"Estudar\"}"
:: titulo vazio -> 400
curl -i -X POST http://localhost:8080/tasks -H "Content-Type: application/json" -d "{\"titulo\": \"\"}"
:: id inexistente -> 404
curl -i http://localhost:8080/tasks/9999
:: apagar -> 204
curl -i -X DELETE http://localhost:8080/tasks/1
```

> Linux: `curl -i -X POST http://localhost:8080/tasks -H "Content-Type: application/json" -d '{"titulo": "Estudar"}'`

O `-i` mostra o status code na primeira linha (`HTTP/1.1 201`).
Alternativa sem aspas complicadas: Postman / Insomnia, ou um teste novo no `hidden_tests.py`.

---

## 6. Dia da prova — roteiro

1. **Ler o contrato** e anotar: rota, campos (nome exato!), tipos, obrigatórios, valores
   padrão, status code de cada caso.
2. **Copiar o molde**: `crudproject/` (ou `simulado-tasks/`, se o contrato for parecido).
3. **Mudar só estes arquivos** (veja a tabela em `simulado-tasks/README.md`):
   model, model de entrada (`...In`), service (validações), controller (rota), repository.
4. **Rodar local** (passo 2) e **os `public_tests.py` do professor** (passo 3).
5. **Testar à mão os casos de borda** (passo 5): vazio, só espaços, ausente, id inexistente,
   id `abc`, PUT parcial, DELETE sem corpo, lista vazia `[]`, ids a partir de 1.
6. **Docker** (passo 4), se der.
7. **README**: atualizar título, tabela de endpoints e os caminhos do `docker build`.
8. **Commit e push**; abrir o repositório no GitHub e conferir se `Dockerfile` e código estão lá.

Se o professor fornecer um repositório com estrutura própria, siga a estrutura dele.
Se nada for dito, deixar o `Dockerfile` na raiz do repositório entregue é o mais seguro.

---

## Problemas comuns

| Erro | Solução |
|---|---|
| `Port 8080 was already in use` | Outra API ainda rodando: `Ctrl+C` no outro terminal ou `docker rm -f api` |
| `mvn` não reconhecido | Use o IntelliJ (passo 2C) |
| Testes falham com `ConnectionError` | A API não está no ar ainda: espere e confira com `curl.exe` |
| `test_lista_inicia_vazia` / `test_ids_sequenciais` falham | Reinicie a API antes de rodar |
| `docker` sem permissão / não instalado | Pule o passo 4 |
| `pip` não reconhecido | `python -m pip install ...` (ou `py -m pip ...`) |
