
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
import TextField from "@mui/material/TextField";
import MenuItem from "@mui/material/MenuItem";

const ToolList = () => {
    const [tools, setTools] = useState([]);
    const [categorias, setCategorias] = useState([]);
    const [selectedCategoria, setSelectedCategoria] = useState("");
    // Eliminar estados para crear categorías
    const navigate = useNavigate();

    const init = () => {
        toolService
            .getAll()
            .then((response) => {
                setTools(response.data);
                // Extraer categorías únicas
                const cats = Array.from(new Set((response.data || []).map(t => t.categoria).filter(Boolean)));
                setCategorias(cats);
            })
            .catch((error) => {
                console.log("Error al obtener herramientas:", error);
            });
    };

    useEffect(() => {
        init();
    }, []);

    const handleDelete = (id) => {
        if (window.confirm("¿Está seguro que desea eliminar esta herramienta?")) {
            toolService
                .remove(id)
                .then(() => init())
                .catch((error) => {
                    console.log("Error al eliminar herramienta:", error);
                });
        }
    };

    const handleEdit = (id) => {
        navigate(`/tool/edit/${id}`);
    };

    const renderStatus = (status) => {
        switch (status) {
            case "dado de baja":
                return <span style={{ color: "red" }}>Dado de baja</span>;
            case "en reparación":
                return <span style={{ color: "orange" }}>En reparación</span>;
            case "prestado":
                return <span style={{ color: "goldenrod" }}>Prestado</span>;
            case "disponible":
                return <span style={{ color: "green" }}>Disponible</span>;
            default:
                return <span>{status}</span>;
        }
    };

    // Agrupa herramientas por estado
    const agruparPorEstado = (toolsArr) => {
        const estados = {};
        toolsArr.forEach(tool => {
            if (!estados[tool.estado]) estados[tool.estado] = [];
            estados[tool.estado].push(tool);
        });
        return estados;
    };



    // Si no hay herramientas, mostrar solo mensaje y botón de agregar herramienta
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

    // Si hay herramientas, mostrar agrupadas por categoría y botón de agregar herramienta
    return (
        <TableContainer component={Paper}>
            <br />
            <div style={{ display: "flex", gap: 16, alignItems: "center" }}>
                <Button
                    variant="contained"
                    color="success"
                    onClick={() => navigate("/tool/add")}
                >
                    Agregar Herramienta
                </Button>
            </div>
            <br />
            {categorias.length === 0 ? (
                <div style={{ color: "#888", textAlign: "center" }}>No hay categorías registradas.</div>
            ) : (
                categorias.map(cat => {
                    const toolsCategoria = tools.filter(t => t.categoria === cat);
                    if (toolsCategoria.length === 0) return null;
                    const agrupadas = agruparPorEstado(toolsCategoria);
                    return (
                        <div key={cat} style={{ marginBottom: 32 }}>
                            <h3 style={{ color: "#333", marginTop: 24 }}>{cat}</h3>
                            {Object.keys(agrupadas).map(estado => (
                                <div key={estado} style={{ marginBottom: 24 }}>
                                    <h4 style={{ color: "#555" }}>{renderStatus(estado)}</h4>
                                    <Table sx={{ minWidth: 650 }} size="small" aria-label="tabla herramientas">
                                        <TableHead>
                                            <TableRow>
                                                <TableCell align="left">Id</TableCell>
                                                <TableCell align="left">Nombre</TableCell>
                                                <TableCell align="center">Estado</TableCell>
                                                <TableCell align="right">Precio Reposición</TableCell>
                                                <TableCell align="left">Historial (Préstamos)</TableCell>
                                                <TableCell align="center">Acciones</TableCell>
                                            </TableRow>
                                        </TableHead>
                                        <TableBody>
                                            {agrupadas[estado].map((tool) => (
                                                <TableRow key={tool.id}>
                                                    <TableCell align="left">{tool.id}</TableCell>
                                                    <TableCell align="left">{tool.nombre}</TableCell>
                                                    <TableCell align="center">{renderStatus(tool.estado)}</TableCell>
                                                    <TableCell align="right">${tool.precioReposicion?.toLocaleString() ?? '-'}</TableCell>
                                                    <TableCell align="left">
                                                        {tool.historial && tool.historial.length > 0 ? (
                                                            <ul style={{ margin: 0, paddingLeft: 16 }}>
                                                                {tool.historial.map((prestamo, idx) => (
                                                                    <li key={idx}>
                                                                        {prestamo.fecha} - {prestamo.usuario}
                                                                    </li>
                                                                ))}
                                                            </ul>
                                                        ) : (
                                                            <span>Sin préstamos</span>
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
                        </div>
                    );
                })
            )}
            <br />
        </TableContainer>
    );
};

export default ToolList;