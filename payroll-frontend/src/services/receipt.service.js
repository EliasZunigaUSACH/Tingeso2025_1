import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/receipts/');
}

const calculate = (reservation) => {
    return httpClient.get("/api/v1/receipts/calculate",{params:{reservation}});
}

export default { getAll, calculate };