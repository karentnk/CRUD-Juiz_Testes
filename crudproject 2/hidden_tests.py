# Gabarito dos testes escondidos (uso: roda a suíte INTEIRA com o container fresco)
import requests

BASE = "http://localhost:8080"

def test_lista_inicia_vazia():
    r = requests.get(f"{BASE}/books")
    assert r.status_code == 200 and r.json() == []

def test_ids_sequenciais():
    ids = [requests.post(f"{BASE}/books", json={
        "titulo": f"L{i}", "autor": "A", "ano": 2000}).json()["id"] for i in range(3)]
    assert ids == [1, 2, 3]

def test_titulo_invalido_da_400():
    for payload in [{"autor": "A", "ano": 2000},
                    {"titulo": "", "autor": "A", "ano": 2000},
                    {"titulo": "   ", "autor": "A", "ano": 2000},
                    {"titulo": None, "autor": "A", "ano": 2000}]:
        assert requests.post(f"{BASE}/books", json=payload).status_code == 400

def test_ano_invalido_da_400():
    for ano in [None, 1449, 0, 3000]:
        assert requests.post(f"{BASE}/books", json={
            "titulo": "T", "autor": "A", "ano": ano}).status_code == 400

def test_paginas_default_e_validacao():
    r = requests.post(f"{BASE}/books", json={"titulo": "T", "autor": "A", "ano": 2000})
    assert r.status_code == 201 and r.json()["paginas"] == 0
    r2 = requests.post(f"{BASE}/books", json={
        "titulo": "T", "autor": "A", "ano": 2000, "paginas": 0})
    assert r2.status_code == 400

def test_campo_extra_ignorado():
    r = requests.post(f"{BASE}/books", json={
        "titulo": "T", "autor": "A", "ano": 2000, "isbn": "X", "outro": 1})
    assert r.status_code == 201 and "isbn" not in r.json()

def test_obter_404_e_400():
    assert requests.get(f"{BASE}/books/9999").status_code == 404
    assert requests.get(f"{BASE}/books/abc").status_code == 400

def test_put_parcial_nao_apaga_campos():
    id_ = requests.post(f"{BASE}/books", json={
        "titulo": "Original", "autor": "Autor", "ano": 2000, "paginas": 100}).json()["id"]
    r = requests.put(f"{BASE}/books/{id_}", json={"concluida": True})
    assert r.status_code == 200
    b = r.json()
    assert b["titulo"] == "Original" and b["paginas"] == 100
    assert requests.put(f"{BASE}/books/{id_}", json={"titulo": ""}).status_code == 400
    assert requests.put(f"{BASE}/books/9999", json={"titulo": "X"}).status_code == 404

def test_delete_204_e_404():
    id_ = requests.post(f"{BASE}/books", json={
        "titulo": "Morrerei", "autor": "A", "ano": 2000}).json()["id"]
    r = requests.delete(f"{BASE}/books/{id_}")
    assert r.status_code == 204 and not r.content
    assert requests.get(f"{BASE}/books/{id_}").status_code == 404
    assert requests.delete(f"{BASE}/books/{id_}").status_code == 404

def test_emprestar_devolver_409():
    id_ = requests.post(f"{BASE}/books", json={
        "titulo": "T", "autor": "A", "ano": 2000}).json()["id"]
    assert requests.post(f"{BASE}/books/{id_}/emprestar").status_code == 200
    assert requests.post(f"{BASE}/books/{id_}/emprestar").status_code == 409
    assert requests.post(f"{BASE}/books/{id_}/devolver").json()["disponivel"] is True
    assert requests.post(f"{BASE}/books/{id_}/devolver").status_code == 409
    assert requests.post(f"{BASE}/books/9999/emprestar").status_code == 404

def test_filtro_disponivel():
    d1 = requests.post(f"{BASE}/books", json={
        "titulo": "F1", "autor": "A", "ano": 2000}).json()["id"]
    d2 = requests.post(f"{BASE}/books", json={
        "titulo": "F2", "autor": "A", "ano": 2000}).json()["id"]
    requests.post(f"{BASE}/books/{d1}/emprestar")
    r_true = requests.get(f"{BASE}/books", params={"disponivel": "true"})
    r_false = requests.get(f"{BASE}/books", params={"disponivel": "false"})
    assert r_true.status_code == 200 and d2 in [l["id"] for l in r_true.json()]
    assert d1 in [l["id"] for l in r_false.json()]
    assert requests.get(f"{BASE}/books?disponivel=abc").status_code == 400
