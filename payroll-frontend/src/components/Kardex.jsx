import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
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


const KardexLoanList = () => {
  const [loans, setLoans] = useState([]);
  const [clients, setClients] = useState({});
  const [tools, setTools] = useState({});
  const navigate = useNavigate();

  const fetchLoans = () => {
    loanService.getAll()
      .then((response) => {
        // Ordenar los préstamos por fecha de forma decreciente
        const sortedLoans = response.data.slice().sort((a, b) => new Date(b.date) - new Date(a.date));
        setLoans(sortedLoans);
        // Obtener clientes y herramientas relacionados
        const clientIds = [...new Set(sortedLoans.map(l => l.clientId))];
        const toolIds = [...new Set(sortedLoans.map(l => l.toolId))];

        Promise.all([
          Promise.all(clientIds.map(id => clientService.get(id).then(r => ({ id, name: r.data.name })))).then(arr => {
            const obj = {};
            arr.forEach(c => { obj[c.id] = c.name; });
            setClients(obj);
          }),
          Promise.all(toolIds.map(id => toolService.get(id).then(r => ({ id, name: r.data.name }))).then(arr => {
            const obj = {};
            arr.forEach(t => { obj[t.id] = t.name; });
            setTools(obj);
          }))
        ]);
      })
      .catch((error) => {
        console.error("Error al obtener préstamos:", error);
      });
  };

  useEffect(() => {
    fetchLoans();
  }, []);

  const handleDelete = (id) => {
    if (window.confirm("¿Seguro que desea eliminar este registro de préstamo?")) {
      loanService.delete(id)
        .then(() => {
          fetchLoans();
        })
        .catch((error) => {
          alert("Error al eliminar el préstamo");
          console.error(error);
        });
    }
  };

  return (
    <div>
      <Typography variant="h5" gutterBottom>Kardex de Préstamos</Typography>
      <Button
        variant="contained"
        color="primary"
        startIcon={<AddIcon />}
        style={{ marginBottom: "1rem" }}
        onClick={() => navigate("/loan/add")}
      >
        Agregar Préstamo
      </Button>
      <TableContainer component={Paper}>
        <Table sx={{ minWidth: 650 }} size="small" aria-label="kardex prestamos">
          <TableHead>
            <TableRow>
              <TableCell align="center" sx={{ fontWeight: "bold" }}>Movimiento</TableCell>
              <TableCell align="center" sx={{ fontWeight: "bold" }}>Fecha</TableCell>
              <TableCell align="center" sx={{ fontWeight: "bold" }}>Cliente</TableCell>
              <TableCell align="center" sx={{ fontWeight: "bold" }}>Herramienta (ID)</TableCell>
              <TableCell align="center" sx={{ fontWeight: "bold" }}>Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loans.map((loan) => (
              <TableRow key={loan.id}>
                <TableCell align="center">{loan.movement}</TableCell>
                <TableCell align="center">{format(new Date(loan.date), "yyyy-MM-dd")}</TableCell>
                <TableCell align="center">{clients[loan.clientId] || loan.clientId}</TableCell>
                <TableCell align="center">{tools[loan.toolId] ? `${tools[loan.toolId]} (${loan.toolId})` : loan.toolId}</TableCell>
                <TableCell align="center">
                  <Button
                    variant="contained"
                    color="error"
                    size="small"
                    startIcon={<DeleteIcon />}
                    onClick={() => handleDelete(loan.id)}
                  >
                    Eliminar
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
};

export default KardexLoanList;