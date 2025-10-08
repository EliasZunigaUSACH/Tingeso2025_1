
import React, { useEffect, useState } from "react";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import Typography from "@mui/material/Typography";
import reportService from "../services/report.service";
import toolService from "../services/tool.service";
import loanService from "../services/loan.service";
import clientService from "../services/client.service";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import VisibilityIcon from "@mui/icons-material/Visibility";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";

const ReportList = () => {
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);
  const [selectedReport, setSelectedReport] = useState(null);

  const handleView = (report) => {
    setSelectedReport(report);
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedReport(null);
  };

  const init = () => {
    reportService
    .getAll()
    .then((response) => {
      console.log("Reportes obtenidos:", response.data);
      setReports(response.data);
    })
    .catch((error) => {
      console.log("Error al obtener reportes:", error);
    })
  };

  useEffect(() => {
    init();
  }, []);

  const handleDelete = async (id) => {
    await reportService.remove(id);
    fetchReports();
  };

  const handleCreate = async () => {
    const creationDate = new Date().toISOString().split('T')[0]; // formato YYYY-MM-DD
    // Los arrays se envían vacíos, el backend debe llenarlos
    const report = {
      creationDate,
      activeLoans: [],
      delayedLoans: [],
      clientsWithDelayedLoans: [],
      topTools: []
    };
    reportService
      .create(report)
      .then((response) => {
        console.log("Reporte ha sido creado.", response.data);
        fetchReports();
      })
      .catch((error) => {
        console.log("Ha ocurrido un error al intentar crear nuevo reporte.", error);
      });
  };

  return (
    <TableContainer component={Paper}>
      <Box display="flex" flexDirection="column" alignItems="center" mt={4}>
        <Box mb={2}>
          <Button variant="contained" color="primary" onClick={handleCreate}>
            Crear Reporte
          </Button>
        </Box>
        <Box width={"100%"} maxWidth={600}>
          {loading ? (
            <div>Cargando...</div>
          ) : reports.length === 0 ? (
            <div>No hay reportes disponibles.</div>
          ) : (
            <Table sx={{ minWidth: 400 }} size="small" aria-label="tabla reportes">
              <TableHead>
                <TableRow>
                  <TableCell align="left">ID</TableCell>
                  <TableCell align="center">Fecha</TableCell>
                  <TableCell align="center">Acciones</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {reports.map((report) => (
                  <TableRow key={report.id}>
                    <TableCell align="left">{report.id}</TableCell>
                    <TableCell align="center">{report.creationDate}</TableCell>
                    <TableCell align="center">
                      <IconButton color="primary" onClick={() => handleView(report)}>
                        <VisibilityIcon />
                      </IconButton>
                      <IconButton color="error" onClick={() => handleDelete(report.id)}>
                        <DeleteIcon />
                      </IconButton>
                    </TableCell>
      {/* Modal de detalles del reporte */}
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Reporte del {report.creationDate}</DialogTitle>
        <DialogContent>
          <h3>Préstamos Activos</h3>
          <ul>
            {report.activeLoans.map((loan) => (
              <li key={loan.id}>{loan.client} - {loan.tool}</li>
            ))}
          </ul>

          <h3>Préstamos Atrasados</h3>
          <ul>
            {report.delayedLoans.map((loan) => (
              <li key={loan.id}>{loan.client} - {loan.tool}</li>
            ))}
          </ul>

          <h3>Clientes con Préstamos Atrasados</h3>
          <ul>
            {report.clientsWithDelayedLoans.map((client) => (
              <li key={client.id}>{client.name}</li>
            ))}
          </ul>

          <h3>Herramientas más Usadas</h3>
          <ul>
            {report.topTools.map((tool) => (
              <li key={tool.id}>{tool.name}</li>
            ))}
          </ul>
        </DialogContent>
      </Dialog>

                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </Box>
      </Box>
    </TableContainer>
  );
}

export default ReportList;