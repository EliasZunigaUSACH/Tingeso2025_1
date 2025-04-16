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

    const handleEdit = (id) => {
        console.log("Printing id", id);
        navigate(`/kart/edit/${id}`);
    };

    return (
        <div>
            <h1>Listado de Karts</h1>
            <Button
                variant="contained"
                color="primary"
                onClick={() => navigate("/kart/add")}
            >
                <PersonAddIcon /> Agregar Kart
            </Button>
            <TableContainer component={Paper}>
                <TableHead>
                    <TableRow>
                        <TableCell>ID</TableCell>
                        <TableCell>Modelo</TableCell>
                        <TableCell>Marca</TableCell>
                        <TableCell>Acciones</TableCell>
                    </TableRow>
                </TableHead>
                {karts.map((kart) => (
                    <TableRow key={kart.id}>
                        <TableCell>{kart.id}</TableCell>
                        <TableCell>{kart.modelo}</TableCell>
                        <TableCell>{kart.marca}</TableCell>
                        <TableCell>
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={() => handleEdit(kart.id)}
                            >
                                <EditIcon /> Editar
                            </Button>
                            <Button
                                variant="contained"
                                color="secondary"
                                onClick={() => handleDelete(kart.id)}
                            >
                                <DeleteIcon /> Eliminar
                            </Button>
                        </TableCell>
                    </TableRow>
                ))}
            </TableContainer>
        </div>  
    );
};

export default KartList;