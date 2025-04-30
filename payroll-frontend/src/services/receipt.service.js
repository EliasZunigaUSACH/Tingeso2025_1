import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/receipts/');
}

const calculate = (reservation) => {
    return httpClient.get("/api/v1/receipts/calculate", { params: { reservation } });
}

// Add the missing method
const getByReservationId = (reservationId) => {
    return httpClient.get(`/api/v1/receipts/${reservationId}`);
}

export default { getAll, calculate, getByReservationId };