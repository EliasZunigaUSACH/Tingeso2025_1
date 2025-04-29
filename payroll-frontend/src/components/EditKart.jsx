import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import kartService from "../services/kart.service";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import FormControl from "@mui/material/FormControl";
import InputLabel from "@mui/material/InputLabel";

const EditKart = () => {
    const [id] = useState(window.location.pathname.split("/").pop()); // Obtener ID de la URL
    const [status, setStatus] = useState("");
    const [available, setAvailable] = useState(false); // Booleano para disponibilidad
    const [titleKartForm, setTitleKartForm] = useState("");
    const navigate = useNavigate();

    const saveKart = (e) => {
        e.preventDefault();

        const kart = { id, status, available };
        if (id) {
            kartService
                .update(kart)
                .then((response) => {
                    console.log("Kart ha sido actualizado.", response.data);
                    navigate("/kart/list");
                })
                .catch((error) => {
                    console.log(
                        "Ha ocurrido un error al intentar actualizar datos del kart.",
                        error
                    );
                });
        }
    };

    useEffect(() => {
        if (id) {
            setTitleKartForm("Editar Kart");
            kartService
                .get(id)
                .then((response) => {
                    setStatus(response.data.status);
                    setAvailable(response.data.available);
                })
                .catch((error) => {
                    console.log("Se ha producido un error.", error);
                });
        } else {

            const newKart = {
                status: "Buen estado", // Valores predeterminados
                available: true, // Corregido
            };
            setStatus(newKart.status);
            setAvailable(newKart.available);
        }
    }, [id]);

    const handleStatusChange = (e) => {
        const newStatus = e.target.value;
        setStatus(newStatus);

        // Bloquear disponibilidad si el estado es "En mantenimiento" o "En uso"
        if (newStatus === "En mantenimiento" || newStatus === "En uso") {
            setAvailable(false); // No disponible
        } else if (newStatus === "Buen estado") {
            setAvailable(true); // Disponible
        }
    };

    const handleAvailableChange = (e) => {
        setAvailable(e.target.value === "true"); // Convertir string a booleano
    };

    return (
        <Box
            display="flex"
            flexDirection="column"
            justifyContent="center"
            alignItems="center"
            component="form"
        >
            <Typography variant="h4" gutterBottom>
                {titleKartForm}
            </Typography>
            {id && ( // Mostrar la ID solo si está presente
                <Typography variant="subtitle1" gutterBottom>
                    Editando Kart con ID: {id}
                </Typography>
            )}
            <FormControl fullWidth margin="normal">
                <InputLabel>Estado</InputLabel>
                <Select
                    value={status}
                    onChange={handleStatusChange}
                    label="Estado"
                >
                    <MenuItem value="En mantenimiento">En mantenimiento</MenuItem>
                    <MenuItem value="En uso">En uso</MenuItem>
                    <MenuItem value="Buen estado">Buen estado</MenuItem>
                </Select>
            </FormControl>
            <FormControl
                fullWidth
                margin="normal"
                disabled={status !== "Buen estado"} // Deshabilitar si no está en "Buen estado"
            >
                <InputLabel>Disponibilidad</InputLabel>
                <Select
                    value={available.toString()} // Convertir booleano a string
                    onChange={handleAvailableChange}
                    label="Disponibilidad"
                >
                    <MenuItem value="true">Disponible</MenuItem>
                    <MenuItem value="false">No disponible</MenuItem>
                </Select>
            </FormControl>
            <Button
                variant="contained"
                color="primary"
                onClick={saveKart}
            >
                Guardar
            </Button>
        </Box>
    );
};

export default EditKart;