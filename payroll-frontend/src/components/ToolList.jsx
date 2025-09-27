
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import toolService from "../services/tool.service";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

const ToolList = () => {
    const [tools, setTools] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        toolService.getAll()
            .then((response) => {
                setTools(response.data || []);
            })
            .catch((error) => {
                console.log("Error al obtener herramientas:", error);
            });
    }, []);

    const handleDelete = (id) => {
        if (window.confirm("¿Está seguro que desea eliminar esta herramienta?")) {
            toolService.remove(id)
                .then(() => {
                    toolService.getAll().then((response) => setTools(response.data || []));
                })
                .catch((error) => {
                    console.log("Error al eliminar herramienta:", error);
                });
        }
    };

    const handleEdit = (id) => {
        navigate(`/tool/edit/${id}`);
    };

    // Agrupa herramientas por categoría
    const agruparPorCategoria = (toolsArr) => {
        const categorias = {};
        toolsArr.forEach(tool => {
            const cat = tool.category || "Sin categoría";
            if (!categorias[cat]) categorias[cat] = [];
            categorias[cat].push(tool);
        });
        return categorias;
    };

    const renderStatus = (status) => {
        switch (status) {
            case 0:
                return <span style={{ color: "red" }}>Dado de baja</span>;
            case 1:
                return <span style={{ color: "orange" }}>En reparación</span>;
            case 2:
                return <span style={{ color: "goldenrod" }}>Prestado</span>;
            case 3:
                return <span style={{ color: "green" }}>Disponible</span>;
            default:
                return <span>{status}</span>;
        }
    };

    if (tools.length === 0) {
        return (
            <TableContainer component={Paper}>
                <br />
                <Button
                    variant="contained"
                    color="success"
                    onClick={() => navigate("/tool/add")}
                >
                    Agregar Herramienta
                </Button>
                <br /><br />
                <div style={{ color: "#888", textAlign: "center" }}>No hay herramientas registradas.</div>
                <br />
            </TableContainer>
        );
    }

    const categorias = agruparPorCategoria(tools);

    return (
        <TableContainer component={Paper}>
            <br />
            <Button
                variant="contained"
                color="success"
                onClick={() => navigate("/tool/add")}
            >
                Agregar Herramienta
            </Button>
            <br /><br />
            {Object.keys(categorias).map(cat => (
                <div key={cat} style={{ marginBottom: 32 }}>
                    <h3 style={{ color: "#333", marginTop: 24 }}>{cat}</h3>
                    <Table sx={{ minWidth: 650 }} size="small" aria-label="tabla herramientas">
                        <TableHead>
                            <TableRow>
                                <TableCell align="left">Id</TableCell>
                                <TableCell align="left">Nombre</TableCell>
                                <TableCell align="center">Estado</TableCell>
                                <TableCell align="right">Precio Reposición</TableCell>
                                <TableCell align="left">Historial (Préstamos IDs)</TableCell>
                                <TableCell align="center">Acciones</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {categorias[cat].map((tool) => (
                                <TableRow key={tool.id}>
                                    <TableCell align="left">{tool.id}</TableCell>
                                    <TableCell align="left">{tool.name}</TableCell>
                                    <TableCell align="center">{renderStatus(tool.status)}</TableCell>
                                    <TableCell align="right">${tool.price?.toLocaleString() ?? '-'}</TableCell>
                                    <TableCell align="left">
                                        {Array.isArray(tool.loansIds) && tool.loansIds.length > 0 ? (
                                            <ul style={{margin: 0, paddingLeft: 16}}>
                                                {tool.loansIds.map((loan, idx) => (
                                                    <li key={idx}>{loan}</li>
                                                ))}
                                            </ul>
                                        ) : (
                                            "Sin préstamos"
                                        )}
                                    </TableCell>
                                    <TableCell align="center">
                                        <Button
                                            variant="contained"
                                            color="primary"
                                            onClick={() => handleEdit(tool.id)}
                                            startIcon={<EditIcon />}
                                        >
                                            Editar
                                        </Button>
                                        <Button
                                            variant="contained"
                                            color="secondary"
                                            onClick={() => handleDelete(tool.id)}
                                            startIcon={<DeleteIcon />}
                                            style={{ marginLeft: 8 }}
                                        >
                                            Eliminar
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </div>
            ))}
            <br />
        </TableContainer>
    );
};

export default ToolList;