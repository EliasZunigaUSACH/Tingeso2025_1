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
import RequestQuoteIcon from "@mui/icons-material/RequestQuote";
import GoEyeIcon from "@mui/icons-material/Visibility";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import Typography from "@mui/material/Typography";

const daysOfWeek = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"];

const getTimeBlocks = () => {
  return [
    "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
    "18:00", "19:00", "20:00", "21:00", "22:00",
  ];
};

const ReservationList = () => {
  const [reservations, setReservations] = useState([]);
  const [currentWeek, setCurrentWeek] = useState(startOfWeek(new Date(), { weekStartsOn: 1 }));
  const [selectedReservation, setSelectedReservation] = useState(null); // Estado para la reserva seleccionada
  const [isModalOpen, setIsModalOpen] = useState(false); // Estado para controlar el modal
  const navigate = useNavigate();

  const handleOpenModal = (reservation) => {
    setSelectedReservation(reservation);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setSelectedReservation(null);
    setIsModalOpen(false);
  };

  const init = () => {
    reservationService
      .getAll()
      .then((response) => {
        console.log("Mostrando listado de todas las Reservas.", response.data);
        const formattedReservations = response.data.map((reservation) => ({
          ...reservation,
          date: format(new Date(reservation.date), "yyyy-MM-dd"),
          startTime: reservation.startTime.slice(0, 5),
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

  const isUnavailable = (day, time) => {
    const weekdays = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes"];
    return weekdays.includes(day) && ["10:00", "11:00", "12:00", "13:00"].includes(time);
  };

  const getDatesForWeek = () => {
    const start = startOfWeek(currentWeek, { weekStartsOn: 1 });
    return daysOfWeek.map((_, index) => {
      const date = addDays(start, index);
      return format(new Date(date.setHours(0, 0, 0, 0)), "yyyy-MM-dd", { locale: es });
    });
  };

  const getReservationForBlock = (day, time) => {
    try {
      const datesForWeek = getDatesForWeek();
      const dayIndex = daysOfWeek.indexOf(day);
      if (dayIndex === -1) {
        console.error(`Día no válido: ${day}`);
        return null;
      }

      const reservationDate = datesForWeek[dayIndex];
      return reservations.find(
        (reservation) => reservation.date === reservationDate && reservation.startTime === time
      );
    } catch (error) {
      console.error("Error al procesar la reserva:", error);
      return null;
    }
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
        <Button
          variant="contained"
          style={{ backgroundColor: "black", color: "white" }}
          onClick={handlePreviousWeek}
        >
          Semana Anterior
        </Button>
        <h2>Semana del {format(currentWeek, "dd 'de' MMMM 'de' yyyy", { locale: es })}</h2>
        <Button
          variant="contained"
          style={{ backgroundColor: "black", color: "white" }}
          onClick={handleNextWeek}
        >
          Semana Siguiente
        </Button>
      </div>
      <Button
        variant="contained"
        color="primary"
        style={{ marginBottom: "1rem" }}
        onClick={() => navigate(`/reservation/add`)}
      >
        Crear Nueva Reserva
      </Button>
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
                        backgroundColor: reservation ? "#ffecb3" : "#e0f7fa",
                        border: reservation ? "1px solid #ffa000" : "none",
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
                            <RequestQuoteIcon />
                          </Button>
                          <Button
                            variant="contained"
                            color="secondary"
                            size="small"
                            onClick={() => handleOpenModal(reservation)}
                            style={{ minWidth: "40px", padding: "0.2rem" }}
                          >
                            <GoEyeIcon />
                          </Button>
                        </div>
                      ) : (
                        <span>Disponible</span>
                      )}
                    </TableCell>
                  );
                })}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <Dialog open={isModalOpen} onClose={handleCloseModal}>
        <DialogTitle>Detalles de la Reserva</DialogTitle>
        <DialogContent>
          {selectedReservation ? (
            <>
              <Typography><strong>ID:</strong> {selectedReservation.id}</Typography>
              <Typography><strong>Cliente:</strong> {selectedReservation.clientName}</Typography>
              <Typography><strong>ID de Cliente:</strong> {selectedReservation.clientId}</Typography>
              <Typography><strong>Fecha:</strong> {selectedReservation.date}</Typography>
              <Typography><strong>Hora de Inicio:</strong> {selectedReservation.startTime}</Typography>
              <Typography><strong>Hora de Fin:</strong> {selectedReservation.endTime}</Typography>
              <Typography><strong>Cantidad de Personas:</strong> {selectedReservation.peopleQuantity}</Typography>
              <Typography><strong>Tiempo en pista:</strong> {selectedReservation.trackTime + " minutos"}</Typography>
              <Typography><strong>Precio base:</strong> {"$" + selectedReservation.price}</Typography>
              <Typography><strong>Descuento de fidelidad:</strong> {selectedReservation.fidelityDiscount + "%"}</Typography>
              <Typography><strong>Descuento por cumpleaños:</strong> {selectedReservation.birthdayDiscount + "%"}</Typography>
              <Typography><strong>Descuento por cantidad de personas:</strong> {selectedReservation.peopleQuantityDiscount + "%"}</Typography>
              <Typography><strong>Descuento por fin de semana:</strong> {selectedReservation.weekendDiscount + "%"}</Typography>
              <Typography><strong>Descuento por día feriado:</strong> {selectedReservation.specialDayDiscount + "%"}</Typography>
              <Typography><strong>IVA:</strong> {"$" + selectedReservation.iva}</Typography>
              <Typography><strong>Precio total:</strong> {"$" + selectedReservation.total}</Typography>
            </>
          ) : (
            <Typography>No hay detalles disponibles.</Typography>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseModal} color="primary">
            Cerrar
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default ReservationList;