
import React, { useEffect, useState } from "react";
import reportService from "../services/report.service";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";

export default function ReportList() {
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [creationDate] = useState(null);
  const [activeLoans] = useState([]);
  const [delayedLoans] = useState([]);
  const [clientsWithDelayedLoans] = useState([]);
  const [topTools] = useState([]);

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
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr>
                <th style={{ textAlign: "left", padding: "8px" }}>Nombre</th>
                <th style={{ textAlign: "left", padding: "8px" }}>Fecha</th>
                <th style={{ textAlign: "center", padding: "8px" }}>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {reports.map((report) => (
                <tr key={report.id}>
                  <td style={{ padding: "8px" }}>{report.name || report.title}</td>
                  <td style={{ padding: "8px" }}>{report.date ? new Date(report.date).toLocaleDateString() : "-"}</td>
                  <td style={{ textAlign: "center", padding: "8px" }}>
                    <IconButton color="error" onClick={() => handleDelete(report.id)}>
                      <DeleteIcon />
                    </IconButton>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Box>
    </Box>
  );
}
