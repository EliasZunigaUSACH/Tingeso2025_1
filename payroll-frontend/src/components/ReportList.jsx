
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

export default function ReportList() {
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [creationDate] = useState(null);
  const [activeLoans] = useState([]);
  const [delayedLoans] = useState([]);
  const [clientsWithDelayedLoans] = useState([]);
  const [topTools] = useState([]);
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

  const fetchReports = async () => {
    setLoading(true);
    try {
      const res = await reportService.getAll();
      setReports(res.data);
    } catch (err) {
      setReports([]);
    }
    setLoading(false);
  };

  useEffect(() => {
    fetchReports();
  }, []);

  const handleDelete = async (id) => {
    await reportService.remove(id);
    fetchReports();
  };

  const handleCreate = async () => {
    const creationDate = new Date().toISOString().split('T')[0]; // formato YYYY-MM-DD
    const report = { creationDate, activeLoans, delayedLoans, clientsWithDelayedLoans, topTools };
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
      <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth scroll="paper">
        <DialogTitle>Detalle del Reporte</DialogTitle>
        <DialogContent dividers>
          {selectedReport && (
            <Box>
              <Typography variant="subtitle1" color="text.secondary" gutterBottom>
                Fecha de creación: {selectedReport.creationDate || '-'}
              </Typography>
              <Box component={Paper} variant="outlined" sx={{ p: 2, mb: 3 }}>
                <Typography variant="h6" gutterBottom>Préstamos vigentes</Typography>
                {Array.isArray(selectedReport.activeLoans) && selectedReport.activeLoans.length > 0 ? (
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>ID</TableCell>
                        <TableCell>Herramienta</TableCell>
                        <TableCell>Cliente</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {selectedReport.activeLoans.map((loan, idx) => (
                        <TableRow key={idx}>
                          <TableCell>{loan.id}</TableCell>
                          <TableCell>{loan.toolName || loan.tool || '-'}</TableCell>
                          <TableCell>{loan.clientName || loan.client || '-'}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                ) : <Typography color="text.secondary">Sin préstamos vigentes.</Typography>}
              </Box>
              <Box component={Paper} variant="outlined" sx={{ p: 2, mb: 3 }}>
                <Typography variant="h6" gutterBottom>Préstamos atrasados</Typography>
                {Array.isArray(selectedReport.delayedLoans) && selectedReport.delayedLoans.length > 0 ? (
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>ID</TableCell>
                        <TableCell>Herramienta</TableCell>
                        <TableCell>Cliente</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {selectedReport.delayedLoans.map((loan, idx) => (
                        <TableRow key={idx}>
                          <TableCell>{loan.id}</TableCell>
                          <TableCell>{loan.toolName || loan.tool || '-'}</TableCell>
                          <TableCell>{loan.clientName || loan.client || '-'}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                ) : <Typography color="text.secondary">Sin préstamos atrasados.</Typography>}
              </Box>
              <Box component={Paper} variant="outlined" sx={{ p: 2, mb: 3 }}>
                <Typography variant="h6" gutterBottom>Clientes con préstamos atrasados</Typography>
                {Array.isArray(selectedReport.clientsWithDelayedLoans) && selectedReport.clientsWithDelayedLoans.length > 0 ? (
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>ID</TableCell>
                        <TableCell>Nombre</TableCell>
                        <TableCell>Préstamos</TableCell>
                        <TableCell>Multa</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {selectedReport.clientsWithDelayedLoans.map((client, idx) => (
                        <TableRow key={idx}>
                          <TableCell>{client.id}</TableCell>
                          <TableCell>{client.name || '-'}</TableCell>
                          <TableCell>
                            {Array.isArray(client.loans) && client.loans.length > 0 ? (
                              <ul style={{margin: 0, paddingLeft: 16}}>
                                {client.loans.map((loanId, i) => (
                                  <li key={i}>{loanId}</li>
                                ))}
                              </ul>
                            ) : 'Sin préstamos'}
                          </TableCell>
                          <TableCell>{client.fine != null ? `$${client.fine}` : '-'}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                ) : <Typography color="text.secondary">Sin clientes con préstamos atrasados.</Typography>}
              </Box>
              <Box component={Paper} variant="outlined" sx={{ p: 2 }}>
                <Typography variant="h6" gutterBottom>Herramientas más prestadas</Typography>
                {Array.isArray(selectedReport.topTools) && selectedReport.topTools.length > 0 ? (
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>ID</TableCell>
                        <TableCell>Nombre</TableCell>
                        <TableCell>Préstamos (IDs)</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {selectedReport.topTools.map((tool, idx) => (
                        <TableRow key={idx}>
                          <TableCell>{tool.id}</TableCell>
                          <TableCell>{tool.name || '-'}</TableCell>
                          <TableCell>
                            {Array.isArray(tool.loansIds) && tool.loansIds.length > 0 ? (
                              <ul style={{margin: 0, paddingLeft: 16}}>
                                {tool.loansIds.map((loanId, i) => (
                                  <li key={i}>{loanId}</li>
                                ))}
                              </ul>
                            ) : 'Sin préstamos'}
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                ) : <Typography color="text.secondary">Sin herramientas destacadas.</Typography>}
              </Box>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="primary">Cerrar</Button>
        </DialogActions>
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
