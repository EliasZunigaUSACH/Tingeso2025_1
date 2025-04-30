import { useState, useEffect } from "react";
import { Link, useParams, useNavigate, useSearchParams } from "react-router-dom";
import reservationService from "../services/reservation.service";
import clientService from "../services/client.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";
import DatePicker from "react-datepicker"; // Importar react-datepicker
import "react-datepicker/dist/react-datepicker.css"; // Importar estilos de react-datepicker
import { format, isWeekend } from "date-fns";

const AddEditReservation = () => {
  const [clientId, setClientId] = useState("");
  const [date, setDate] = useState(null);
  const [startTime, setStartTime] = useState("");
  const [peopleQuantity, setPeopleQuantity] = useState("");
  const [trackTime, setTrackTime] = useState("");
  const [clients, setClients] = useState([]);
  const [errorMessage, setErrorMessage] = useState("");
  const { id } = useParams();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const availableHours = {
    weekday: ["14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00"],
    weekend: ["10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00"],
  };

  useEffect(() => {
    const initialDate = searchParams.get("date");
    const initialTime = searchParams.get("time");
    if (initialDate) setDate(new Date(initialDate));
    if (initialTime) setStartTime(initialTime);

    clientService
      .getAll()
      .then((response) => setClients(response.data))
      .catch((error) => console.error("Error al obtener clientes", error));

    if (id) {
      reservationService
        .get(id)
        .then((reservation) => {
          setClientId(reservation.data.clientId);
          setDate(new Date(reservation.data.date));
          setStartTime(reservation.data.startTime);
          setPeopleQuantity(reservation.data.peopleQuantity);
          setTrackTime(reservation.data.trackTime);
        })
        .catch((error) => console.error("Error al cargar reserva", error));
    }
  }, [id, searchParams]);

  const validateFields = () => {
    if (!clientId || !date || !startTime || !trackTime || !peopleQuantity) {
      setErrorMessage("Todos los campos son obligatorios.");
      return false;
    }
    if (peopleQuantity < 1 || peopleQuantity > 15) {
      setErrorMessage("La cantidad de personas debe estar entre 1 y 15.");
      return false;
    }
    setErrorMessage("");
    return true;
  };

  const saveReservation = (e) => {
    e.preventDefault();
    if (!validateFields()) return;

    const reservation = {
      clientId,
      date: format(date, "yyyy-MM-dd"),
      startTime,
      peopleQuantity,
      trackTime,
      id,
    };

    const serviceCall = id
      ? reservationService.update(reservation)
      : reservationService.create(reservation);

    serviceCall
      .then(() => {
        console.log("Reserva guardada correctamente.");
        navigate("/reservation/list");
      })
      .catch((error) => console.error("Error al guardar reserva", error));
  };

  const deleteReservation = () => {
    if (!id) return; // Solo se puede eliminar si existe un ID
    if (window.confirm("¿Estás seguro de que deseas eliminar esta reserva?")) {
      reservationService
        .delete(id)
        .then(() => {
          console.log("Reserva eliminada correctamente.");
          navigate("/reservation/list");
        })
        .catch((error) => console.error("Error al eliminar reserva", error));
    }
  };

  const getAvailableHours = () => {
    if (!date) return [];
    return isWeekend(date) ? availableHours.weekend : availableHours.weekday;
  };

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      component="form"
    >
      <h3>{id ? "Editar Reserva" : "Nueva Reserva"}</h3>
      <hr />
      {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
      <FormControl fullWidth>
        <TextField
          select
          label="Seleccionar Cliente"
          value={clientId}
          onChange={(e) => setClientId(e.target.value)}
          variant="standard"
        >
          {clients.map((client) => (
            <MenuItem key={client.id} value={client.id}>
              {client.name} ({client.id})
            </MenuItem>
          ))}
        </TextField>
      </FormControl>
      <FormControl fullWidth>
        <label htmlFor="date">Seleccionar Fecha</label>
        <DatePicker
          id="date"
          selected={date}
          onChange={(newDate) => setDate(newDate)}
          dateFormat="yyyy-MM-dd"
          minDate={new Date()} // Deshabilitar fechas pasadas
          placeholderText="Selecciona una fecha"
        />
      </FormControl>
      <FormControl fullWidth>
        <TextField
          select
          label="Seleccionar Hora"
          value={startTime}
          onChange={(e) => setStartTime(e.target.value)}
          variant="standard"
          disabled={!date}
        >
          {getAvailableHours().map((hour) => (
            <MenuItem key={hour} value={hour}>
              {hour}
            </MenuItem>
          ))}
        </TextField>
      </FormControl>
      <FormControl fullWidth>
        <TextField
          label="Cantidad de Personas"
          type="number"
          value={peopleQuantity}
          onChange={(e) => setPeopleQuantity(Math.min(15, Math.max(1, e.target.value)))}
          variant="standard"
          helperText="Número de personas (mínimo 1, máximo 15)"
        />
      </FormControl>
      <FormControl fullWidth>
        <TextField
          select
          label="Tiempo en pista"
          value={trackTime}
          onChange={(e) => setTrackTime(e.target.value)}
          variant="standard"
        >
          <MenuItem value={10}>10 minutos</MenuItem>
          <MenuItem value={15}>15 minutos</MenuItem>
          <MenuItem value={20}>20 minutos</MenuItem>
        </TextField>
      </FormControl>
      <FormControl>
        <br />
        <Button
          variant="contained"
          color="info"
          onClick={saveReservation}
          startIcon={<SaveIcon />}
        >
          Guardar
        </Button>
      </FormControl>
      {id && (
        <FormControl>
          <br />
          <Button
            variant="contained"
            color="error"
            onClick={deleteReservation}
          >
            Eliminar
          </Button>
        </FormControl>
      )}
      <hr />
      <Link to="/reservation/list">Volver a la lista</Link>
    </Box>
  );
};

export default AddEditReservation;
