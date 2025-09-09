import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/kardexRegisters/');
}

const create = data => {
    return httpClient.post("/api/v1/kardexRegisters/", data);
}

const get = id => {
    return httpClient.get(`/api/v1/kardexRegisters/${id}`);
}

const update = data => {
    return httpClient.put('/api/v1/kardexRegisters/', data);
}

const remove = id => {
    return httpClient.delete(`/api/v1/kardexRegisters/${id}`);
}

export default { getAll, create, get, update, remove };