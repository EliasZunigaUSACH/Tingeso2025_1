import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import LoanService from "../services/loan.service";
import clientService from "../services/client.service";
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

const LoanList = () => {
  const [loans, setLoans] = useState([]);
  const [clients, setClients] = useState([]);
  const [tools, setTools] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    LoanService.getAll()
      .then((response) => {
        setLoans(response.data || []);
      })
      .catch((error) => {
        console.error("Error al obtener préstamos:", error);
      });
    clientService.getAll()
      .then((response) => {
        setClients(response.data || []);
      })
      .catch((error) => {
        console.error("Error al obtener clientes:", error);
      });
    toolService.getAll()
      .then((response) => {
        setTools(response.data || []);
      })
      .catch((error) => {
        console.error("Error al obtener herramientas:", error);
      });
  }, []);

  // Helper para obtener nombre cliente/herramienta
  const getClientName = (id) => {
    const client = clients.find((c) => c.id === id);
    return client ? client.name : id;
  };
  const getToolName = (id) => {
    const tool = tools.find((t) => t.id === id);
    return tool ? tool.name : id;
  };
  // Helper para estado
  const getStatus = (status) => {
    switch (status) {
      case 0:
        return "Terminado";
      case 1:
        return "Vigente";
      case 2:
        return "Atrasado";
      default:
        return status;
    }
  };

  return (
    <TableContainer component={Paper}>
      <br />
      <h3 style={{ marginLeft: 16 }}>Listado de Préstamos</h3>
      <Table sx={{ minWidth: 650 }} size="small" aria-label="tabla préstamos">
        <TableHead>
          <TableRow>
            <TableCell align="left">ID</TableCell>
            <TableCell align="left">Cliente</TableCell>
            <TableCell align="left">Herramienta</TableCell>
            <TableCell align="left">Fecha de inicio</TableCell>
            <TableCell align="left">Fecha límite</TableCell>
            <TableCell align="left">Fecha de término</TableCell>
            <TableCell align="left">Estado</TableCell>
            <TableCell align="center">Acciones</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {loans.map((loan) => (
            <TableRow key={loan.id}>
              <TableCell align="left">{loan.id}</TableCell>
              <TableCell align="left">{getClientName(loan.clientId)}</TableCell>
              <TableCell align="left">{getToolName(loan.toolId)}</TableCell>
              <TableCell align="left">{loan.dateStart}</TableCell>
              <TableCell align="left">{loan.dateLimit}</TableCell>
              <TableCell align="left">
                {loan.status === 0 ? loan.dateEnd || "-" : "-"}
              </TableCell>
              <TableCell align="left">{getStatus(loan.status)}</TableCell>
              <TableCell align="center">
                <Button
                  variant="contained"
                  color="primary"
                  onClick={() => navigate(`/loan/edit/${loan.id}`)}
                  startIcon={<EditIcon />}
                  size="small"
                >
                  Modificar
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <br />
        <Button
          variant="contained"
          color="secondary"
          onClick={() => navigate("/kardex")}
          style={{ marginLeft: 16 }}
        >
          Volver a Kardex
        </Button>
    </TableContainer>
  );
};

export default LoanList;
