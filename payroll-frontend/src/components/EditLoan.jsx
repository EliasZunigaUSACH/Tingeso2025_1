
import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import LoanService from "../services/loan.service";
import kardexRegisterService from "../services/kardexRegister.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";

const EditLoan = () => {
	const { id } = useParams();
	const [loan, setLoan] = useState(null);
	const [status, setStatus] = useState(1); // 0: Terminado, 2: Atrasado
	const [price, setPrice] = useState(0);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState("");
	const navigate = useNavigate();

	useEffect(() => {
		LoanService.get(id)
			.then((response) => {
				setLoan(response.data);
				setStatus(response.data.status);
				setPrice(response.data.commission || 0);
				setLoading(false);
			})
			.catch(() => {
				setError("No se pudo cargar el préstamo");
				setLoading(false);
			});
	}, [id]);

	const handleSave = (e) => {
		e.preventDefault();
		if (!loan) return;
		// Solo permitir cambios en status (0,2) y precio
		const hasChanges = (status !== loan.status) || (Number(price) !== Number(loan.commission));
		if (!hasChanges) {
			navigate("/kardex");
			return;
		}
		const updatedLoan = { ...loan, status, commission: Number(price) };
		LoanService.update(updatedLoan)
			.then(() => {
				// Registrar en kardex según estado
				let movimiento = "";
				if (status === 0) movimiento = "Devolución de herramienta";
				else if (status === 2) movimiento = "Actualización de préstamo";
				const kardexRegister = {
					loanId: loan.id,
					toolId: loan.toolId,
					clientId: loan.clientId,
					movement: movimiento,
					date: new Date().toISOString(),
					typeRelated: 2 // 2: Préstamo
				};
				kardexRegisterService.create(kardexRegister)
					.then(() => navigate("/kardex"))
					.catch(() => navigate("/kardex"));
			})
			.catch(() => setError("Error al actualizar préstamo"));
	};

	if (loading) return <div>Cargando...</div>;
	if (error) return <div style={{ color: "red" }}>{error}</div>;
	if (!loan) return <div>No se encontró el préstamo.</div>;

	return (
		<Box
			display="flex"
			flexDirection="column"
			alignItems="center"
			justifyContent="center"
			component="form"
			sx={{ mt: 4 }}
		>
			<h3>Editar Préstamo</h3>
			<hr />
			{/* Datos solo visualización */}
			<FormControl fullWidth sx={{ mb: 2 }}>
				<TextField
					label="ID Préstamo"
					value={loan.id}
					variant="standard"
					InputProps={{ readOnly: true }}
				/>
			</FormControl>
			<FormControl fullWidth sx={{ mb: 2 }}>
				<TextField
					label="ID Cliente"
					value={loan.clientId}
					variant="standard"
					InputProps={{ readOnly: true }}
				/>
			</FormControl>
			<FormControl fullWidth sx={{ mb: 2 }}>
				<TextField
					label="ID Herramienta"
					value={loan.toolId}
					variant="standard"
					InputProps={{ readOnly: true }}
				/>
			</FormControl>
			<FormControl fullWidth sx={{ mb: 2 }}>
				<TextField
					label="Categoría"
					value={loan.category}
					variant="standard"
					InputProps={{ readOnly: true }}
				/>
			</FormControl>
			<FormControl fullWidth sx={{ mb: 2 }}>
				<TextField
					label="Fecha Inicio"
					value={loan.dateStart}
					variant="standard"
					InputProps={{ readOnly: true }}
				/>
			</FormControl>
			<FormControl fullWidth sx={{ mb: 2 }}>
				<TextField
					label="Fecha Límite"
					value={loan.dateLimit}
					variant="standard"
					InputProps={{ readOnly: true }}
				/>
			</FormControl>
			{/* Solo editar status y precio */}
			<FormControl fullWidth sx={{ mb: 2 }}>
				<TextField
					select
					label="Estado"
					value={status}
					onChange={e => setStatus(Number(e.target.value))}
					variant="standard"
				>
					<MenuItem value={0}>Terminado</MenuItem>
					<MenuItem value={2}>Atrasado</MenuItem>
				</TextField>
			</FormControl>
			<FormControl fullWidth sx={{ mb: 2 }}>
				<TextField
					label="Precio"
					type="number"
					value={price}
					onChange={e => setPrice(e.target.value)}
					variant="standard"
					inputProps={{ min: 0 }}
				/>
			</FormControl>
			<FormControl>
				<Button
					variant="contained"
					color="info"
					type="submit"
					onClick={handleSave}
					startIcon={<SaveIcon />}
				>
					Guardar
				</Button>
			</FormControl>
			<br />
			<Button
				variant="contained"
				color="secondary"
				onClick={() => navigate("/kardex")}
			>
				Volver a la lista
			</Button>
			<hr />
		</Box>
	);
};

export default EditLoan;
