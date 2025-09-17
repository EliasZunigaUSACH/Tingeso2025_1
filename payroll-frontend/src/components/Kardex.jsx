import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import kardexService from "../services/kardexRegister.service";
import loanService from "../services/loan.service";
import clientService from "../services/client.service";
import toolService from "../services/tool.service";
import { format } from "date-fns";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import DeleteIcon from "@mui/icons-material/Delete";
import AddIcon from "@mui/icons-material/Add";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import Box from "@mui/material/Box";


const Kardex = () => {
	const [kardexList, setKardexList] = useState([]);
	const [tools, setTools] = useState({});
	const [clients, setClients] = useState({});
	const [loading, setLoading] = useState(true);
	const [loanDialogOpen, setLoanDialogOpen] = useState(false);
	const [selectedLoan, setSelectedLoan] = useState(null);
	const [deleting, setDeleting] = useState(false);
	const navigate = useNavigate();

	useEffect(() => {
		const fetchData = async () => {
			setLoading(true);
			try {
				const { data: kardexData } = await kardexService.getAll();
				const [{ data: toolsData }, { data: clientsData }] = await Promise.all([
					toolService.getAll(),
					clientService.getAll(),
				]);
				const toolsMap = {};
				toolsData.forEach(t => { toolsMap[t.id] = t; });
				const clientsMap = {};
				clientsData.forEach(c => { clientsMap[c.id] = c; });
				setTools(toolsMap);
				setClients(clientsMap);
				setKardexList(kardexData);
			} catch (err) {
				setKardexList([]);
			}
			setLoading(false);
		};
		fetchData();
	}, []);

	const getRelacionLabel = (relacion) => {
		if (relacion === 1) return "Herramienta";
		if (relacion === 2) return "Préstamo";
		return "-";
	};

	const getToolName = (toolId) => {
		return tools[toolId]?.name || "-";
	};

	const getClientName = (clientId) => {
		return clients[clientId]?.name || "No aplica";
	};

	const handleViewLoan = async (loanId) => {
		try {
			const { data } = await loanService.get(loanId);
			setSelectedLoan(data);
			setLoanDialogOpen(true);
		} catch (e) {
			setSelectedLoan(null);
			setLoanDialogOpen(false);
		}
	};

	const handleDeleteLoan = async (loanId) => {
		if (!window.confirm("¿Eliminar préstamo y registros asociados?")) return;
		setDeleting(true);
		try {
			await loanService.remove(loanId);
			const { data: kardexData } = await kardexService.getAll();
			setKardexList(kardexData);
		} catch (e) {}
		setDeleting(false);
	};

	return (
		<Box sx={{ p: 2 }}>
					<Box display="flex" alignItems="center" justifyContent="space-between" mb={2}>
						<Typography variant="h5" gutterBottom>Kardex - Registros</Typography>
						<Box>
							<Button
								variant="contained"
								color="primary"
								startIcon={<AddIcon />}
								sx={{ mr: 1 }}
								onClick={() => navigate('/loan/add')}
							>
								Agregar Préstamo
							</Button>
							<Button
								variant="contained"
								color="secondary"
								onClick={() => navigate('/loan/list')}
							>
								Modificar Préstamo
							</Button>
						</Box>
					</Box>
			<TableContainer component={Paper}>
				<Table>
					<TableHead>
						<TableRow>
							<TableCell>ID</TableCell>
							<TableCell>Movimiento</TableCell>
							<TableCell>Relación</TableCell>
							<TableCell>Herramienta</TableCell>
							<TableCell>ID Herramienta</TableCell>
							<TableCell>ID Préstamo</TableCell>
							<TableCell>Cliente</TableCell>
							<TableCell>Acciones</TableCell>
						</TableRow>
					</TableHead>
					<TableBody>
						{loading ? (
							<TableRow><TableCell colSpan={8}>Cargando...</TableCell></TableRow>
						) : kardexList.length === 0 ? (
							<TableRow><TableCell colSpan={8}>Sin registros</TableCell></TableRow>
						) : kardexList.map((row) => (
							<TableRow key={row.id}>
								<TableCell>{row.id}</TableCell>
								<TableCell>{row.movement || '-'}</TableCell>
								<TableCell>{getRelacionLabel(row.typeRelated)}</TableCell>
								<TableCell>{getToolName(row.toolId)}</TableCell>
								<TableCell>{row.toolId || 'No aplica'}</TableCell>
								<TableCell>{row.loanId || 'No aplica'}</TableCell>
								<TableCell>{getClientName(row.clientId)}</TableCell>
								<TableCell>
									{row.typeRelated === 2 ? (
										<>
											<Button
												size="small"
												variant="outlined"
												onClick={() => handleViewLoan(row.loanId)}
												sx={{ mr: 1 }}
											>
												Ver detalles
											</Button>
											<Button
												size="small"
												color="error"
												variant="outlined"
												onClick={() => handleDeleteLoan(row.loanId)}
												disabled={deleting}
												startIcon={<DeleteIcon />}
											>
												Eliminar préstamo
											</Button>
										</>
									) : row.typeRelated === 1 ? (
										<Button
											size="small"
											variant="outlined"
											onClick={() => navigate('/tool/list')}
										>
											Ir a herramientas
										</Button>
									) : null}
								</TableCell>
							</TableRow>
						))}
					</TableBody>
				</Table>
			</TableContainer>

			{/* Dialogo de detalles del préstamo */}
			<Dialog open={loanDialogOpen} onClose={() => setLoanDialogOpen(false)} maxWidth="sm" fullWidth>
				<DialogTitle>Detalles del Préstamo</DialogTitle>
				<DialogContent>
					{selectedLoan ? (
						<Box>
							{Object.entries(selectedLoan).map(([key, value]) => (
								<Typography key={key}><b>{key}:</b> {String(value)}</Typography>
							))}
						</Box>
					) : (
						<Typography>No se pudo cargar el préstamo.</Typography>
					)}
				</DialogContent>
				<DialogActions>
					<Button onClick={() => setLoanDialogOpen(false)}>Cerrar</Button>
				</DialogActions>
			</Dialog>
		</Box>
	);
};

export default Kardex;