import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import reservationService from "../services/reservation.service";
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
import MoreTimeIcon from '@mui/icons-material/MoreTime';

const ReservationList = () => {
  const [reservations, setReservations] = useState([]);

  const navigate = useNavigate();

  const init = () => {
    reservationService
      .getAll()
      .then((response) => {
        console.log("Mostrando listado de todas las Reservas.", response.data);
        setReservations(response.data);
      })
      .catch((error) => {
        console.log(
          "Se ha producido un error al intentar mostrar listado de todas las Reservas.",
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
      "¿Esta seguro que desea borrar esta Reserva?"
    );
    if (confirmDelete) {
      reservationService
        .remove(id)
        .then((response) => {
          console.log("Reserva ha sido eliminada.", response.data);
          init();
        })
        .catch((error) => {
          console.log(
            "Se ha producido un error al intentar eliminar la Reserva",
            error
          );
        });
    }
  };

  const handleEdit = (id) => {
    console.log("Printing id", id);
    navigate(`/reservations/edit/${id}`);
  };

  return (
    <TableContainer component={Paper}>
      <br />
      <Link
        to="/reservations/add"
        style={{ textDecoration: "none", marginBottom: "1rem" }}
      >
        <Button
          variant="contained"
          color="primary"
          startIcon={<MoreTimeIcon />}
        >
          Ingresar Reserva
        </Button>
      </Link>
      <br /> <br />
      <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
        <TableHead>
          <TableRow>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>
              Rut
            </TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>
              Fecha
            </TableCell>
            <TableCell align="right" sx={{ fontWeight: "bold" }}>
              Nro. Reservas
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {reservations.map((reservation) => (
            <TableRow
              key={reservation.id}
              sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
            >
              <TableCell align="left">{reservation.rut}</TableCell>
              <TableCell align="left">{new Date(reservation.date).toISOString().split('T')[0]}</TableCell>
              <TableCell align="right">{reservation.numReservations}</TableCell>
              <TableCell>
                <Button
                  variant="contained"
                  color="info"
                  size="small"
                  onClick={() => handleEdit(reservation.id)}
                  style={{ marginLeft: "0.5rem" }}
                  startIcon={<EditIcon />}
                >
                  Editar
                </Button>

                <Button
                  variant="contained"
                  color="error"
                  size="small"
                  onClick={() => handleDelete(reservation.id)}
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

export default ReservationList;
