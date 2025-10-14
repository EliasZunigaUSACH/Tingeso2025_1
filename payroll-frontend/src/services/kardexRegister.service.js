import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/kardexRegisters/');
}

const get = id => {
    return httpClient.get(`/api/v1/kardexRegisters/${id}`);
}

const remove = id => {
    return httpClient.delete(`/api/v1/kardexRegisters/${id}`);
}

const getToolRegisters = toolName => {
    return httpClient.get(`/api/v1/kardexRegisters/tool/${toolName}`);
}

const getByDateRange = (startDate, endDate) => {
    return httpClient.get(`/api/v1/kardexRegisters/${startDate}_to_${endDate}`);
}

export default { getAll, get, remove, getToolRegisters, getByDateRange };