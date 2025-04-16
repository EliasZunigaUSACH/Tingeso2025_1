import HpptClient from '../http-common';

const getAll = () => {
    return HpptClient.get('/api/v1/karts/');
}

const create = data => {
    return HpptClient.post("/api/v1/karts/", data);
}

const get = id => {
    return HpptClient.get(`/api/v1/karts/${id}`);
}

const update = data => {
    return HpptClient.put('/api/v1/karts/', data);
}

const remove = id => {
    return HpptClient.delete(`/api/v1/karts/${id}`);
}

export default { getAll, create, get, update, remove };