import requests

BASE = "http://localhost:8080"

def test_criar_livro_retorna_201_com_campos():
    r = requests.post(f"{BASE}/books", json={
        "titulo": "Dom Casmurro", "autor": "Machado de Assis", "ano": 1899})
    assert r.status_code == 201
    body = r.json()
    assert body["titulo"] == "Dom Casmurro"
    assert body["disponivel"] is True
    assert isinstance(body["id"], int)

def test_emprestar_torna_indisponivel():
    r = requests.post(f"{BASE}/books", json={
        "titulo": "O Alienista", "autor": "Machado de Assis", "ano": 1882})
    id_ = r.json()["id"]
    r2 = requests.post(f"{BASE}/books/{id_}/emprestar")
    assert r2.status_code == 200
    assert r2.json()["disponivel"] is False
