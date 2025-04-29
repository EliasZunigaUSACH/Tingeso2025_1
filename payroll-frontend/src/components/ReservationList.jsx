import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import reservationService from "../services/reservation.service";
import { format, addDays, startOfWeek, subWeeks, addWeeks } from "date-fns";
import { es } from "date-fns/locale";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import EditIcon from "@mui/icons-material/Edit";

const daysOfWeek = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"];

const getTimeBlocks = () => {
  return [
    "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
    "18:00", "19:00", "20:00", "21:00", "22:00",
  ];
};

const ReservationList = () => {
  const [reservations, setReservations] = useState([]);
  const [currentWeek, setCurrentWeek] = useState(startOfWeek(new Date(), { weekStartsOn: 1 })); // Semana actual
  const navigate = useNavigate();

  const init = () => {
    reservationService
      .getAll()
      .then((response) => {
        console.log("Mostrando listado de todas las Reservas.", response.data);

        // Formatear las fechas de las reservas al formato yyyy-MM-dd
        const formattedReservations = response.data.map((reservation) => ({
          ...reservation,
          date: format(new Date(reservation.date), "yyyy-MM-dd"), // Asegurar formato de fecha
          startTime: reservation.startTime.slice(0, 5), // Asegurar formato de hora
        }));

        setReservations(formattedReservations);
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

  const handleEdit = (id) => {
    navigate(`/reservation/edit/${id}`);
  };

  const getReservationForBlock = (day, time) => {
    try {
      // Obtener el índice del día en el array daysOfWeek
      const dayIndex = daysOfWeek.indexOf(day);

      // Validar que el índice sea válido
      if (dayIndex === -1) {
        console.error(`Día no válido: ${day}`);
        return false;
      }

      // Obtener la fecha correspondiente al día actual
      const reservationDate = datesForWeek[dayIndex];

      // Comparar la fecha y la hora de la reserva con el bloque actual
      return reservations.find((reservation) => {
        return (
          reservation.date === reservationDate && // Comparar fechas
          reservation.startTime === time // Comparar horas
        );
      });
    } catch (error) {
      console.error("Error al procesar la reserva:", error);
      return false;
    }
  };

  const isUnavailable = (day, time) => {
    const weekdays = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes"];
    return weekdays.includes(day) && ["10:00", "11:00", "12:00", "13:00"].includes(time);
  };

  const getDatesForWeek = () => {
    const start = startOfWeek(currentWeek, { weekStartsOn: 1 }); // Asegurar que la semana comience el lunes
    const dates = daysOfWeek.map((_, index) =>
      format(addDays(start, index), "yyyy-MM-dd", { locale: es })
    );
    return dates;
  };

  const datesForWeek = getDatesForWeek();

  const handlePreviousWeek = () => {
    setCurrentWeek((prevWeek) => subWeeks(prevWeek, 1));
  };

  const handleNextWeek = () => {
    setCurrentWeek((prevWeek) => addWeeks(prevWeek, 1));
  };

  return (
    <div>
      <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "1rem" }}>
        <Button variant="contained" color="primary" onClick={handlePreviousWeek}>
          Semana Anterior
        </Button>
        <h2>Semana del {format(currentWeek, "dd 'de' MMMM 'de' yyyy", { locale: es })}</h2>
        <Button variant="contained" color="primary" onClick={handleNextWeek}>
          Semana Siguiente
        </Button>
      </div>
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
                        No aplica
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
                        backgroundColor: reservation ? "#ffecb3" : "#e0f7fa", // Cambia el color de fondo para bloques reservados
                        border: reservation ? "1px solid #ffa000" : "none", // Añade un borde para bloques reservados
                      }}
                    >
                      {reservation ? (
                        <div style={{ display: "flex", justifyContent: "center", gap: "0.5rem" }}>
                          <Button
                            variant="contained"
                            color="info"
                            size="small"
                            onClick={() => navigate(`/reservation/edit/${reservation.id}`)}
                            style={{ minWidth: "40px", padding: "0.2rem" }}
                          >
                            <EditIcon />
                          </Button>
                          <Button
                            variant="contained"
                            color="primary"
                            size="small"
                            onClick={() => navigate(`/reservation/view/${reservation.id}`)}
                            style={{ minWidth: "40px", padding: "0.2rem" }}
                          >
                            <i className="fas fa-file-alt" /> {/* Icono de boleta */}
                          </Button>
                        </div>
                      ) : (
                        <Button
                          variant="contained"
                          color="success"
                          size="small"
                          onClick={() => {
                            const selectedDate = datesForWeek[index];
                            navigate(`/reservation/add?date=${selectedDate}&time=${time}`);
                          }}
                        >
                          Disponible
                        </Button>
                      )}
                    </TableCell>
                  );
                })}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
};

export default ReservationList;