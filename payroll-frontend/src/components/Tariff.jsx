import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import TariffService from "../services/tariff.service";

import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import EditIcon from "@mui/icons-material/Edit";

const Tariff = () => {
    const [tariff, setTariff] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const fetchTariff = async () => {
            try {
                const response = await TariffService.get();
                setTariff(response.data);
            } catch (error) {
                handleCreate();
            } finally {
                setLoading(false);
            }
        };
        fetchTariff();
    }, []);

    const handleCreate = (e) => {
        e.preventDefault();
        const newTariff = { dailyTariff: 0, delayTariff: 0 };
        TariffService.create(newTariff)
            .then((response) => {
                setTariff(response.data);
            })
            .catch((error) => {
                setError("Error al crear la tarifa");
            });
    };

    const handleEdit = () => {
        navigate("/tariff/edit");
    };

    if (loading) return <div>Cargando...</div>;
    if (error) return <div>{error}</div>;

    return (
        <Paper>
            <h2>Detalles de la Tarifa</h2>
            {tariff && (
                <div>
                    <p>Tarifa diaria: {tariff.dailyTariff}</p>
                    <p>Tarifa diaria por atraso: {tariff.delayTariff}</p>
                    <Button onClick={handleEdit} startIcon={<EditIcon />}>
                        Editar
                    </Button>
                </div>
            )}
        </Paper>
    );
};

export default Tariff;