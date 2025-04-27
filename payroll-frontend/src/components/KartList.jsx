import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import kartService from "../services/kart.service";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";

import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";


const KartList = () => {
    const [karts, setKarts] = useState([]);

    const navigate = useNavigate();

    const init = () => {
        kartService
            .getAll()
            .then((response) => {
                console.log("Mostrando listado de todos los karts.", response.data);
                setKarts(response.data);
            })
            .catch((error) => {
                console.log(
                    "Se ha producido un error al intentar mostrar listado de todos los karts.",
                    error
                );
            });
    };

    useEffect(() => {
        init();
    }, []);

    const handleDelete = (id) => {
        console.log("Printing id", id);
        const confirmDelete = window.confirm(
            "¿Esta seguro que desea borrar este kart?"
        );
        if (confirmDelete) {
            kartService
                .remove(id)
                .then((response) => {
                    console.log("kart ha sido eliminado.", response.data);
                    init();
                })
                .catch((error) => {
                    console.log(
                        "Se ha producido un error al intentar eliminar el kart",
                        error
                    );
                });
        }
    };

    const handleAddKart = () => {
        if (karts.length >= 15) {
            alert("No se pueden agregar más de 15 karts.");
            return;
        }
    
        const newKart = {
            status: "Disponible", // Valores predeterminados
            avaliable: true,
        };
    
        kartService
            .create(newKart)
            .then((response) => {
                console.log("Nuevo kart creado:", response.data);
                init(); // Actualiza la lista de karts
            })
            .catch((error) => {
                console.log("Error al crear un nuevo kart:", error);
            });
    };

    const handleEdit = (id) => {
        console.log("Printing id", id);
        navigate(`/kart/edit/${id}`);
    };

    return (
        <TableContainer component={Paper}>
            <br />
                <Button 
                 variant="contained"
                 color="primary"
                 onClick={handleAddKart}>
                    Agregar Kart
                </Button>
            <br /><br />
            <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
                <TableHead>
                    <TableRow>
                        <TableCell>Id</TableCell>
                        <TableCell align="right">Estado</TableCell>
                        <TableCell align="right">¿Disponible?</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {karts.map((kart) => (
                        <TableRow
                         key={kart.id}>
                            <TableCell align='left'>
                                {`Kart${kart.id.toString().padStart(3, '0')}`}
                            </TableCell>
                            <TableCell align='right'>
                                {kart.status}
                            </TableCell>
                            <TableCell align='right'>
                                {kart.avaliable ? "Sí" : "No"}
                            </TableCell>
                            <TableCell align='right'>
                                <Button
                                 variant="contained"
                                 color="primary"
                                 onClick={() => handleEdit(kart.id)}
                                 startIcon={<EditIcon />}>
                                    Editar
                                </Button>
                                <Button
                                 variant="contained"
                                 color="secondary"
                                 onClick={() => handleDelete(kart.id)}
                                 startIcon={<DeleteIcon />}>
                                    Eliminar
                                </Button>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
            <br />
        </TableContainer>
    );
};

export default KartList;