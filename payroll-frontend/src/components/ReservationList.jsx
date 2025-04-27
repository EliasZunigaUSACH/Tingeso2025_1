import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import reservationService from "../services/reservation.service";
import { format, addDays, startOfWeek } from "date-fns";
import { es } from "date-fns/locale";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import MoreTimeIcon from "@mui/icons-material/MoreTime";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

const daysOfWeek = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"];

const getTimeBlocks = () => {
  return [
    "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
    "18:00", "19:00", "20:00", "21:00", "22:00",
  ];
};

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
    const confirmDelete = window.confirm(
      "¿Está seguro que desea borrar esta Reserva?"
    );
    if (confirmDelete) {
      reservationService
        .remove(id)
        .then(() => {
          console.log("Reserva ha sido eliminada.");
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
    navigate(`/reservation/edit/${id}`);
  };

  const getReservationForBlock = (day, time) => {
    return reservations.find((reservation) => {
      const reservationDate = new Date(reservation.date);
      const reservationDay = format(reservationDate, "EEEE", { locale: es });
      const reservationTime = format(reservationDate, "HH:mm");
      return reservationDay === day && reservationTime === time;
    });
  };

  const isUnavailable = (day, time) => {
    const weekdays = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes"];
    return weekdays.includes(day) && ["10:00", "11:00", "12:00", "13:00"].includes(time);
  };

  const getDatesForWeek = () => {
    const start = startOfWeek(new Date(), { weekStartsOn: 1 }); // Lunes como inicio de la semana
    return daysOfWeek.map((_, index) => format(addDays(start, index), "dd 'de' MMMM 'de' yyyy", { locale: es }));
  };

  const datesForWeek = getDatesForWeek();

  return (
    <TableContainer component={Paper}>
      <br />
      <Table sx={{ minWidth: 650 }} size="small" aria-label="rack semanal">
        <TableHead>
          <TableRow>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Hora
            </TableCell>
            {daysOfWeek.map((day, index) => (
              <TableCell key={day} align="center" sx={{ fontWeight: "bold" }}>
                {day} <br /> {datesForWeek[index]}
              </TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          {getTimeBlocks().map((time) => (
            <TableRow key={time}>
              <TableCell align="center" sx={{ fontWeight: "bold" }}>
                {time}
              </TableCell>
              {daysOfWeek.map((day, index) => {
                if (isUnavailable(day, time)) {
                  return (
                    <TableCell key={index} align="center" sx={{ color: "gray" }}>
                      No disponible
                    </TableCell>
                  );
                }
                const reservation = getReservationForBlock(day, time);
                return (
                  <TableCell
                    key={index}
                    align="center"
                    sx={{
                      cursor: reservation ? "default" : "pointer",
                      backgroundColor: reservation ? "inherit" : "#e0f7fa",
                    }}
                    onClick={() => {
                      if (!reservation) {
                        const selectedDate = datesForWeek[index]; // Obtén la fecha correspondiente al día
                        navigate(`/reservation/add?date=${selectedDate}&time=${time}`);
                      }
                    }}
                  >
                    {reservation ? (
                      <>
                        <div>{reservation.rut}</div>
                        <div>{reservation.numReservations}</div>
                        <Button
                          variant="contained"
                          color="info"
                          size="small"
                          onClick={() => handleEdit(reservation.id)}
                          style={{ margin: "0.2rem" }}
                          startIcon={<EditIcon />}
                        >
                          Editar
                        </Button>
                        <Button
                          variant="contained"
                          color="error"
                          size="small"
                          onClick={() => handleDelete(reservation.id)}
                          style={{ margin: "0.2rem" }}
                          startIcon={<DeleteIcon />}
                        >
                          Eliminar
                        </Button>
                      </>
                    ) : (
                      "Disponible"
                    )}
                  </TableCell>
                );
              })}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default ReservationList;