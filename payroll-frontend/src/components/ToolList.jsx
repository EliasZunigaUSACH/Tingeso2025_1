
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import toolService from "../services/tool.service";
import loanService from "../services/loan.service";
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
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import CloseIcon from "@mui/icons-material/Close";
import IconButton from "@mui/material/IconButton";

const ToolList = () => {
    const [tools, setTools] = useState([]);
    const navigate = useNavigate();
    const [openHistory, setOpenHistory] = useState(false);
    const [historyLoans, setHistoryLoans] = useState([]);
    const [historyLoading, setHistoryLoading] = useState(false);
    const [historyError, setHistoryError] = useState("");

    const handleViewHistory = async (tool) => {
        if (!tool.history || tool.history.length === 0) return;
        setHistoryLoading(true);
        setHistoryError("");
        try {
            // Obtener detalles de cada préstamo
            const loanIds = tool.history.map(h => h.id).filter(Boolean);
            const loanPromises = loanIds.map(id => loanService.get(id));
            const results = await Promise.all(loanPromises);
            setHistoryLoans(results.map(r => r.data));
            setOpenHistory(true);
        } catch (err) {
            setHistoryError("Error al cargar historial de préstamos");
        }
        setHistoryLoading(false);
    };

    const handleCloseHistory = () => {
        setOpenHistory(false);
        setHistoryLoans([]);
    };
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
                                        {tool.history && tool.history.length > 0 ? (
                                            <Button
                                                variant="outlined"
                                                color="info"
                                                size="small"
                                                onClick={() => handleViewHistory(tool)}
                                            >
                                                Visualizar historial
                                            </Button>
                                        ) : (
                                            <span>Sin préstamos</span>
                                        )}
                                    </TableCell>
            {/* Dialogo para mostrar historial de préstamos */}
            <Dialog open={openHistory} onClose={handleCloseHistory} maxWidth="md" fullWidth>
                <DialogTitle>
                    Historial de Préstamos
                    <IconButton
                        aria-label="close"
                        onClick={handleCloseHistory}
                        sx={{ position: 'absolute', right: 8, top: 8 }}
                    >
                        <CloseIcon />
                    </IconButton>
                </DialogTitle>
                <DialogContent dividers>
                    {historyLoading ? (
                        <div>Cargando...</div>
                    ) : historyError ? (
                        <div style={{ color: 'red' }}>{historyError}</div>
                    ) : historyLoans.length === 0 ? (
                        <div>No hay préstamos para mostrar.</div>
                    ) : (
                        <Table size="small">
                            <TableHead>
                                <TableRow>
                                    <TableCell>ID Préstamo</TableCell>
                                    <TableCell>Cliente</TableCell>
                                    <TableCell>Fecha Inicio</TableCell>
                                    <TableCell>Fecha Devolución</TableCell>
                                    <TableCell>Estado herramienta post-préstamo</TableCell>
                                    <TableCell>¿Devuelto con atraso?</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {historyLoans.map((loan) => (
                                    <TableRow key={loan.id}>
                                        <TableCell>{loan.id}</TableCell>
                                        <TableCell>{loan.clientName ?? loan.clientId}</TableCell>
                                        <TableCell>{loan.dateStart ? new Date(loan.dateStart).toLocaleDateString() : '-'}</TableCell>
                                        <TableCell>{loan.dateReturn ? new Date(loan.dateReturn).toLocaleDateString() : '-'}</TableCell>
                                        <TableCell>{
                                            loan.toolStatus === 0 ? 'Irreparable' :
                                            loan.toolStatus === 1 ? 'Dañado' :
                                            loan.toolStatus === 3 ? 'Buen estado' :
                                            loan.toolStatus === 2 ? 'Prestado' : 'Desconocido'
                                        }</TableCell>
                                        <TableCell>{loan.dateReturn && loan.dateLimit && new Date(loan.dateReturn) > new Date(loan.dateLimit) ? 'Sí' : 'No'}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseHistory} color="primary">Cerrar</Button>
                </DialogActions>
            </Dialog>

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