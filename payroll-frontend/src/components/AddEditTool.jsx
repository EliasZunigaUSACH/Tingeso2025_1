import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import toolService from "../services/tool.service";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import FormControl from "@mui/material/FormControl";
import InputLabel from "@mui/material/InputLabel";

const AddEditTool = () => {
    // Detectar modo edición solo si la ruta es /tool/edit/:id
    const pathParts = window.location.pathname.split("/");
    const isEdit = pathParts[2] === "edit" && pathParts[3];
    const id = isEdit ? pathParts[3] : null;
    const [nombre, setNombre] = useState("");
    const [categoria, setCategoria] = useState("");
    const [precioReposicion, setPrecioReposicion] = useState("");
    const [estado, setEstado] = useState("");
    const [titleForm, setTitleForm] = useState("");
    const navigate = useNavigate();

    const saveTool = (e) => {
        e.preventDefault();
        if (!isEdit) {
            // Crear herramienta
            const tool = { nombre, categoria, precioReposicion };
            toolService
                .create(tool)
                .then((response) => {
                    console.log("Herramienta añadida.", response.data);
                    navigate("/tool/list");
                })
                .catch((error) => {
                    console.log("Error al crear herramienta.", error);
                });
        } else {
            // Editar herramienta
            const tool = { id, precioReposicion, estado };
            toolService
                .update(tool)
                .then((response) => {
                    console.log("Herramienta actualizada.", response.data);
                    navigate("/tool/list");
                })
                .catch((error) => {
                    console.log("Error al actualizar herramienta.", error);
                });
        }
    };

    useEffect(() => {
        if (isEdit) {
            setTitleForm("Editar Herramienta");
            toolService
                .get(id)
                .then((response) => {
                    setPrecioReposicion(response.data.precioReposicion || "");
                    setEstado(response.data.estado || "");
                })
                .catch((error) => {
                    console.log("Error al cargar herramienta.", error);
                });
        } else {
            setTitleForm("Agregar Herramienta");
            setNombre("");
            setCategoria("");
            setPrecioReposicion("");
            setEstado(3);
        }
    }, [isEdit, id]);

    const handleEstadoChange = (e) => {
        setEstado(e.target.value);
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
                {titleForm}
            </Typography>
            {!isEdit && (
                <>
                    <FormControl fullWidth margin="normal">
                        <InputLabel shrink htmlFor="nombre">Nombre</InputLabel>
                        <input
                            id="nombre"
                            type="text"
                            value={nombre}
                            onChange={e => setNombre(e.target.value)}
                            required
                            style={{ padding: 8, fontSize: 16 }}
                        />
                    </FormControl>
                    <FormControl fullWidth margin="normal">
                        <InputLabel shrink htmlFor="categoria">Categoría</InputLabel>
                        <Select
                            id="categoria"
                            value={categoria}
                            onChange={e => setCategoria(e.target.value)}
                            label="Categoría"
                        >
                            <MenuItem value="Manual">Manual</MenuItem>
                            <MenuItem value="Eléctrica">Eléctrica</MenuItem>
                            <MenuItem value="Medición">Medición</MenuItem>
                            <MenuItem value="Otra">Otra</MenuItem>
                        </Select>
                    </FormControl>
                </>
            )}
            <FormControl fullWidth margin="normal">
                <InputLabel shrink htmlFor="precioReposicion">Precio de Reposición</InputLabel>
                <input
                    id="precioReposicion"
                    type="number"
                    value={precioReposicion}
                    onChange={e => setPrecioReposicion(e.target.value)}
                    required
                    min={0}
                    style={{ padding: 8, fontSize: 16 }}
                />
            </FormControl>
            {isEdit && (
                <FormControl fullWidth margin="normal">
                    <InputLabel>Estado</InputLabel>
                    <Select
                        value={estado}
                        onChange={handleEstadoChange}
                        label="Estado"
                    >
                        <MenuItem value={3}>Disponible</MenuItem>
                        <MenuItem value={2}>Prestado</MenuItem>
                        <MenuItem value={1}>En reparación</MenuItem>
                        <MenuItem value={0}>Dado de baja</MenuItem>
                    </Select>
                </FormControl>
            )}
            <Button
                variant="contained"
                color="primary"
                onClick={saveTool}
            >
                Guardar
            </Button>
        </Box>
    );
};

export default AddEditTool;