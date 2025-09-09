import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import clientService from "../services/client.service";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

const ClientList = () => {
  const [clients, setClients] = useState([]);

  const navigate = useNavigate();

  const init = () => {
    clientService
      .getAll()
      .then((response) => {
        console.log("Mostrando listado de todos los clientes.", response.data);
        setClients(response.data);
      })
      .catch((error) => {
        console.log(
          "Se ha producido un error al intentar mostrar listado de todos los clientes.",
          error
        );
      });
  };

  useEffect(() => {
    init();
  }, []);

  const handleDelete = (id) => {
    console.log("Printing id", id);
    const confirmDelete = window.confirm(
      "¿Esta seguro que desea borrar este cliente?"
    );
    if (confirmDelete) {
      clientService
        .remove(id)
        .then((response) => {
          console.log("cliente ha sido eliminado.", response.data);
          init();
        })
        .catch((error) => {
          console.log(
            "Se ha producido un error al intentar eliminar al cliente",
            error
          );
        });
    }
  };

  const handleEdit = (id) => {
    console.log("Printing id", id);
    navigate(`/client/edit/${id}`);
  };

  // Si necesitas mapear el estado, puedes modificar esta función
  const getStatus = (status) => {
    switch (status) {
      case 0:
        return "Restringido";
      case 1:
        return "Activo";
    }
  };

  return (
    <TableContainer component={Paper}>
      <br />
      <Link
        to="/client/add"
        style={{ textDecoration: "none", marginBottom: "1rem" }}
      >
        <Button
          variant="contained"
          color="primary"
          startIcon={<PersonAddIcon />}
        >
          Añadir Cliente
        </Button>
      </Link>
      <br /> <br />
      <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
        <TableHead>
          <TableRow>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>
              Nombre
            </TableCell>
            <TableCell align="right" sx={{ fontWeight: "bold" }}>
              RUT
            </TableCell>
            <TableCell align="right" sx={{ fontWeight: "bold" }}>
              Email
            </TableCell>
            <TableCell align="right" sx={{ fontWeight: "bold" }}>
              Teléfono
            </TableCell>
            <TableCell align="right" sx={{ fontWeight: "bold" }}>
              Estado
            </TableCell>
            <TableCell align="right" sx={{ fontWeight: "bold" }}>
              Préstamos Vigentes
            </TableCell>
            <TableCell align="right" sx={{ fontWeight: "bold" }}>
              Multa
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Acciones
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {clients.map((client) => (
            <TableRow
              key={client.id}
              sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
            >
              <TableCell align="left">{client.name}</TableCell>
              <TableCell align="right">{client.rut}</TableCell>
              <TableCell align="right">{client.email}</TableCell>
              <TableCell align="right">{client.phone}</TableCell>
              <TableCell align="right">{getStatus(client.status)}</TableCell>
              <TableCell align="right">
                {Array.isArray(client.activeLoans) && client.activeLoans.length > 0 ? (
                  <ul style={{ margin: 0, paddingLeft: 16 }}>
                    {client.activeLoans.map((loan, idx) => (
                      <li key={idx}>{loan}</li>
                    ))}
                  </ul>
                ) : (
                  "Sin préstamos"
                )}
              </TableCell>
              <TableCell align="right">{client.fine ? `$${client.fine}` : "$0"}</TableCell>
              <TableCell align="center">
                <Button
                  variant="contained"
                  color="info"
                  size="small"
                  onClick={() => handleEdit(client.id)}
                  style={{ marginLeft: "0.5rem" }}
                  startIcon={<EditIcon />}
                >
                  Editar
                </Button>

                <Button
                  variant="contained"
                  color="secondary"
                  size="small"
                  onClick={() => handleDelete(client.id)}
                  style={{ marginLeft: "0.5rem" }}
                  startIcon={<DeleteIcon />}
                >
                  Eliminar
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default ClientList;
