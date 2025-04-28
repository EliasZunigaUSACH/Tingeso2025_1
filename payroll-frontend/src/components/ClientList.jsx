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
              Fecha de cumpleaños
            </TableCell>
            <TableCell align="right" sx={{ fontWeight: "bold" }}>
              Categoria de fidelidad
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
              <TableCell align="right">{client.birthday}</TableCell>
              <TableCell align="right">{client.fidelityLevel}</TableCell>
              <TableCell>
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
                  color="error"
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
