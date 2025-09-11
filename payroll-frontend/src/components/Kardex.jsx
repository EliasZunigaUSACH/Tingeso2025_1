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
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import Box from "@mui/material/Box";



const Kardex = () => {
  const [records, setRecords] = useState([]);
  const [clients, setClients] = useState({});
  const [tools, setTools] = useState({});
  const [selectedLoan, setSelectedLoan] = useState(null);
  const [openModal, setOpenModal] = useState(false);
  const navigate = useNavigate();

  // Estado textual
  const getEstado = (estado) => {
    switch (estado) {
      case 0:
        return "Terminado";
      case 1:
        return "Vigente";
      case 2:
        return "Atrasado";
      default:
        return "Desconocido";
    }
  };

  // Obtener todos los registros (préstamos) y asociar clientes y herramientas
  const fetchRecords = () => {
    loanService.getAll()
      .then((response) => {
        // Ordenar por fecha descendente
        const sorted = response.data.slice().sort((a, b) => new Date(b.date) - new Date(a.date));
        setRecords(sorted);
        // Obtener clientes y herramientas relacionados
        const clientIds = [...new Set(sorted.map(r => r.clientId))];
        const toolIds = [...new Set(sorted.map(r => r.toolId))];

        Promise.all([
          Promise.all(clientIds.map(id => clientService.get(id).then(r => ({ id, name: r.data.name })))).
            then(arr => {
              const obj = {};
              arr.forEach(c => { obj[c.id] = c.name; });
              setClients(obj);
            }),
          Promise.all(toolIds.map(id => toolService.get(id).then(r => ({ id, name: r.data.name })))).
            then(arr => {
              const obj = {};
              arr.forEach(t => { obj[t.id] = t.name; });
              setTools(obj);
            })
        ]);
      })
      .catch((error) => {
        console.error("Error al obtener registros:", error);
      });
  };

  useEffect(() => {
    fetchRecords();
  }, []);

  const handleDelete = (id) => {
    if (window.confirm("¿Seguro que desea eliminar este registro?")) {
      loanService.delete(id)
        .then(() => {
          fetchRecords();
        })
        .catch((error) => {
          alert("Error al eliminar el registro");
          console.error(error);
        });
    }
  };

  return (
    <div>
      <Typography variant="h5" gutterBottom>Kardex de Registros</Typography>
      <Button
        variant="contained"
        color="primary"
        startIcon={<AddIcon />}
        style={{ marginBottom: "1rem" }}
        onClick={() => navigate("/kardex/add")}
      >
        Agregar Registro
      </Button>
      <TableContainer component={Paper}>
        <Table sx={{ minWidth: 650 }} size="small" aria-label="kardex registros">
          <TableHead>
            <TableRow>
              <TableCell align="center" sx={{ fontWeight: "bold" }}>ID</TableCell>
              <TableCell align="center" sx={{ fontWeight: "bold" }}>Movimiento</TableCell>
              <TableCell align="center" sx={{ fontWeight: "bold" }}>Cliente</TableCell>
              <TableCell align="center" sx={{ fontWeight: "bold" }}>Herramienta (ID)</TableCell>
              <TableCell align="center" sx={{ fontWeight: "bold" }}>Fecha</TableCell>
              <TableCell align="center" sx={{ fontWeight: "bold" }}>Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {records.map((rec) => (
              <TableRow key={rec.id}>
                <TableCell align="center">{rec.id}</TableCell>
                <TableCell align="center">{rec.movement}</TableCell>
                <TableCell align="center">{clients[rec.clientId] || rec.clientId}</TableCell>
                <TableCell align="center">{tools[rec.toolId] ? `${tools[rec.toolId]} (${rec.toolId})` : rec.toolId}</TableCell>
                <TableCell align="center">{rec.date ? format(new Date(rec.date), "yyyy-MM-dd") : '-'}</TableCell>
                <TableCell align="center">
                  {/* Solo mostrar detalles si es préstamo */}
                  {rec.movement && rec.movement.toLowerCase().includes("préstamo") && (
                    <Button
                      variant="outlined"
                      color="info"
                      size="small"
                      style={{ marginRight: 8 }}
                      onClick={() => {
                        setSelectedLoan(rec);
                        setOpenModal(true);
                      }}
                    >
                      Ver detalles
                    </Button>
                  )}
                  <Button
                    variant="contained"
                    color="error"
                    size="small"
                    startIcon={<DeleteIcon />}
                    onClick={() => handleDelete(rec.id)}
                  >
                    Eliminar
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Modal de detalles del préstamo */}
      <Dialog open={openModal} onClose={() => setOpenModal(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Detalles del Préstamo</DialogTitle>
        <DialogContent>
          {selectedLoan && (
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
              <Typography><b>Cliente:</b> {clients[selectedLoan.clientId] || selectedLoan.clientId}</Typography>
              <Typography><b>Herramienta:</b> {tools[selectedLoan.toolId] || selectedLoan.toolId}</Typography>
              <Typography><b>Fecha de préstamo:</b> {selectedLoan.dateStart ? format(new Date(selectedLoan.dateStart), "yyyy-MM-dd") : '-'}</Typography>
              <Typography><b>Fecha límite:</b> {selectedLoan.dateLimit ? format(new Date(selectedLoan.dateLimit), "yyyy-MM-dd") : '-'}</Typography>
              <Typography><b>Estado:</b> {getEstado(selectedLoan.status)}</Typography>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenModal(false)} color="primary">Cerrar</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default Kardex;