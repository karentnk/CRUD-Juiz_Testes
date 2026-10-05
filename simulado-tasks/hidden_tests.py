import requests

BASE = "http://localhost:8080"

def test_lista_inicia_vazia():
    r = requests.get(f"{BASE}/tasks")
    assert r.status_code == 200 and r.json() == []

def test_ids_sequenciais():
    ids = [requests.post(f"{BASE}/tasks", json={"titulo": f"T{i}"}).json()["id"]
           for i in range(3)]
    assert ids == [1, 2, 3]

def test_titulo_invalido_da_400():
    for payload in [{}, {"titulo": ""}, {"titulo": "   "}, {"titulo": None},
                    {"descricao": "sem titulo"}]:
        assert requests.post(f"{BASE}/tasks", json=payload).status_code == 400

def test_descricao_opcional_e_concluida_false():
    r = requests.post(f"{BASE}/tasks", json={"titulo": "So titulo"})
    assert r.status_code == 201
    b = r.json()
    assert b["descricao"] == "" and b["concluida"] is False

def test_obter_200_404_400():
    id_ = requests.post(f"{BASE}/tasks", json={"titulo": "X"}).json()["id"]
    r = requests.get(f"{BASE}/tasks/{id_}")
    assert r.status_code == 200 and r.json()["titulo"] == "X"
    assert requests.get(f"{BASE}/tasks/9999").status_code == 404
    assert requests.get(f"{BASE}/tasks/abc").status_code == 400

def test_put_parcial():
    id_ = requests.post(f"{BASE}/tasks", json={
        "titulo": "Original", "descricao": "D"}).json()["id"]
    r = requests.put(f"{BASE}/tasks/{id_}", json={"concluida": True})
    assert r.status_code == 200
    b = r.json()
    assert b["concluida"] is True and b["titulo"] == "Original" and b["descricao"] == "D"
    assert requests.put(f"{BASE}/tasks/9999", json={"titulo": "Y"}).status_code == 404

def test_delete_204_e_404():
    id_ = requests.post(f"{BASE}/tasks", json={"titulo": "Apagar"}).json()["id"]
    r = requests.delete(f"{BASE}/tasks/{id_}")
    assert r.status_code == 204 and not r.content
    assert requests.get(f"{BASE}/tasks/{id_}").status_code == 404
    assert requests.delete(f"{BASE}/tasks/{id_}").status_code == 404
