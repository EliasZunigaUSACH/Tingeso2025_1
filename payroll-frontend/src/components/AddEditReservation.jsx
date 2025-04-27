import { useState, useEffect } from "react";
import { Link, useParams, useNavigate, useSearchParams } from "react-router-dom";
import reservationService from "../services/reservation.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";

const AddEditReservation = () => {
  const [clientId, setClientId] = useState("");
  const [date, setDate] = useState("");
  const [numGuests, setNumGuests] = useState("");
  const [startTime, setStartTime] = useState("");
  const [duration, setDuration] = useState("");
  const [clients, setClients] = useState([]);
  const { id } = useParams();
  const [titleReservationForm, setTitleReservationForm] = useState("");
  const navigate = useNavigate();
  const [searchParams] = useSearchParams(); // Para leer los parámetros de la URL

  useEffect(() => {
    // Leer parámetros de la URL
    const initialDate = searchParams.get("date");
    const initialTime = searchParams.get("time");
    if (initialDate) setDate(initialDate);
    if (initialTime) setStartTime(initialTime);

    // Obtener lista de clientes
    reservationService
      .getClients()
      .then((response) => {
        setClients(response.data);
      })
      .catch((error) => {
        console.log("Error al obtener la lista de clientes.", error);
      });

    if (id) {
      setTitleReservationForm("Editar reserva");
      reservationService
        .get(id)
        .then((reservation) => {
          setClientId(reservation.data.clientId);
          const formattedDate = new Date(reservation.data.date).toISOString().split("T")[0];
          setDate(formattedDate);
          setNumGuests(reservation.data.numGuests);
          setStartTime(reservation.data.startTime);
          setDuration(reservation.data.duration);
        })
        .catch((error) => {
          console.log("Error al cargar la reserva.", error);
        });
    } else {
      setTitleReservationForm("Nueva reserva");
    }
  }, [id, searchParams]);

  const getAvailableTimes = () => {
    const selectedDate = new Date(date);
    const day = selectedDate.getDay(); // 0 = Domingo, 6 = Sábado
    const times = [];

    if (day === 0 || day === 6) {
      // Sábado y Domingo: 10:00 a 22:00
      for (let hour = 10; hour <= 22; hour++) {
        times.push(`${hour}:00`);
        times.push(`${hour}:30`);
      }
    } else {
      // Lunes a Viernes: 14:00 a 22:00
      for (let hour = 14; hour <= 22; hour++) {
        times.push(`${hour}:00`);
        times.push(`${hour}:30`);
      }
    }

    return times;
  };

  const saveReservation = (e) => {
    e.preventDefault();

    const reservation = { clientId, date, numGuests, startTime, duration, id };
    if (id) {
      reservationService
        .update(reservation)
        .then((response) => {
          console.log("Reserva actualizada.", response.data);
          navigate("/reservations/list");
        })
        .catch((error) => {
          console.log("Error al actualizar la reserva.", error);
        });
    } else {
      reservationService
        .create(reservation)
        .then((response) => {
          console.log("Reserva creada.", response.data);
          navigate("/reservations/list");
        })
        .catch((error) => {
          console.log("Error al crear la reserva.", error);
        });
    }
  };

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      component="form"
    >
      <h3> {titleReservationForm} </h3>
      <hr />
      <form>
        <FormControl fullWidth>
          <TextField
            select
            id="id"
            label="Seleccionar Cliente"
            value={id}
            variant="standard"
            onChange={(e) => setClientId(e.target.value)}
          >
            {clients.map((client) => (
              <MenuItem key={client.id} value={client.id}>
                {client.name} ({client.id})
              </MenuItem>
            ))}
          </TextField>
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="date"
            label="Fecha"
            type="date"
            value={date}
            variant="standard"
            onChange={(e) => setDate(e.target.value)}
            InputLabelProps={{
              shrink: true,
            }}
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="startTime"
            label="Hora"
            value={startTime}
            variant="standard"
            InputProps={{
              readOnly: true, // Hace que el campo sea de solo lectura
            }}
            InputLabelProps={{
              shrink: true, // Mantiene la etiqueta visible
            }}
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="numGuests"
            label="Cantidad de Personas"
            type="number"
            value={numGuests}
            variant="standard"
            onChange={(e) => setNumGuests(e.target.value)}
            helperText="Número de personas"
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            select
            id="duration"
            label="Duración (minutos)"
            value={duration}
            variant="standard"
            onChange={(e) => setDuration(e.target.value)}
          >
            <MenuItem value={10}>10</MenuItem>
            <MenuItem value={15}>15</MenuItem>
            <MenuItem value={20}>20</MenuItem>
          </TextField>
        </FormControl>

        <FormControl>
          <br />
          <Button
            variant="contained"
            color="info"
            onClick={(e) => saveReservation(e)}
            style={{ marginLeft: "0.5rem" }}
            startIcon={<SaveIcon />}
          >
            Guardar
          </Button>
        </FormControl>
      </form>
      <hr />
      <Link to="/reservations/list">Volver a la lista</Link>
    </Box>
  );
};

export default AddEditReservation;