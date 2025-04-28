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

const AddEditReservation = () => {
  const [clientId, setClientId] = useState("");
  const [clientName, setClientName] = useState("");
  const [date, setDate] = useState("");
  const [peopleQuantity, setPeopleQuantity] = useState("");
  const [startTime, setStartTime] = useState("");
  const [trackTime, setTrackTime] = useState("");
  const [clients, setClients] = useState([]);
  const [isDateTimeLocked, setIsDateTimeLocked] = useState(false);
  const { id } = useParams();
  const [titleReservationForm, setTitleReservationForm] = useState("");
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    const initialDate = searchParams.get("date");
    const initialTime = searchParams.get("time");
    if (initialDate) setDate(initialDate);
    if (initialTime) setStartTime(initialTime);

    clientService
      .getAll()
      .then((response) => {
        setClients(response.data);
      })
      .catch((error) => {
        console.log("Error al obtener la lista de clientes", error);
      });

    if (id) {
      setTitleReservationForm("Editar reserva");
      reservationService
        .get(id)
        .then((reservation) => {
          setClientId(reservation.data.clientId);
          setClientName(reservation.data.clientName);
          const formattedDate = new Date(reservation.data.date).toISOString().split("T")[0];
          setDate(formattedDate);
          setPeopleQuantity(reservation.data.peopleQuantity);
          setStartTime(reservation.data.startTime);
          setTrackTime(reservation.data.trackTime);
          setIsDateTimeLocked(true);
        })
        .catch((error) => {
          console.log("Error al cargar la reserva", error);
        });
    } else {
      setTitleReservationForm("Nueva reserva");
    }
  }, [id, searchParams]);

  const validateFields = () => {
    if (!clientId || !clientName || !date || !startTime || !trackTime || !peopleQuantity) {
      setErrorMessage("Todos los campos son obligatorios.");
      return false;
    }
    if (peopleQuantity < 1) {
      setErrorMessage("La cantidad de personas debe ser al menos 1.");
      return false;
    }
    setErrorMessage("");
    return true;
  };

  const saveReservation = (e) => {
    e.preventDefault();

    if (!validateFields()) {
      return;
    }

    const formattedDate = new Date(date);

    const reservation = {
      clientId,
      clientName,
      date: formattedDate,
      peopleQuantity,
      startTime,
      trackTime,
      id,
    };

    if (id) {
      reservationService
        .update(reservation)
        .then((response) => {
          console.log("Reserva actualizada", response.data);
          navigate("/reservation/list");
        })
        .catch((error) => {
          console.log("Error al actualizar la reserva", error);
        });
    } else {
      reservationService
        .create(reservation)
        .then((response) => {
          console.log("Reserva creada", response.data);
          navigate("/reservation/list");
        })
        .catch((error) => {
          console.log("Error al crear la reserva", error);
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
      {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
      <form>
        {/* Selector de clientes */}
        <FormControl fullWidth>
          <TextField
            select
            id="id"
            label="Seleccionar Cliente"
            value={clientId}
            variant="standard"
            onChange={(e) => {
              const selectedClient = clients.find((client) => client.id === e.target.value);
              setClientId(e.target.value);
              setClientName(selectedClient?.name || "");
            }}
          >
            {clients.map((client) => (
              <MenuItem key={client.id} value={client.id}>
                {client.name} ({client.id})
              </MenuItem>
            ))}
          </TextField>
        </FormControl>

        {/* Mostrar fecha seleccionada */}
        <FormControl fullWidth>
          <TextField
            id="date"
            label="Fecha seleccionada"
            value={date}
            variant="standard"
            InputProps={{
              readOnly: true,
            }}
          />
        </FormControl>

        {/* Mostrar hora seleccionada */}
        <FormControl fullWidth>
          <TextField
            id="startTime"
            label="Hora seleccionada"
            value={startTime}
            variant="standard"
            InputProps={{
              readOnly: true,
            }}
          />
        </FormControl>

        {/* Cantidad de personas (mínimo 1, máximo 15) */}
        <FormControl fullWidth>
          <TextField
            id="peopleQuantity"
            label="Cantidad de Personas"
            type="number"
            value={peopleQuantity}
            variant="standard"
            onChange={(e) => {
              const value = Math.min(15, Math.max(1, e.target.value));
              setPeopleQuantity(value);
            }}
            helperText="Número de personas (mínimo 1, máximo 15)"
          />
        </FormControl>

        {/* Seleccionar duración */}
        <FormControl fullWidth>
          <TextField
            select
            id="trackTime"
            label="Tiempo en pista"
            value={trackTime}
            variant="standard"
            onChange={(e) => setTrackTime(e.target.value)}
          >
            <MenuItem value={10}>10 minutos</MenuItem>
            <MenuItem value={15}>15 minutos</MenuItem>
            <MenuItem value={20}>20 minutos</MenuItem>
          </TextField>
        </FormControl>

        {/* Botón para guardar */}
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
      <Link to="/reservation/list">Volver a la lista</Link>
    </Box>
  );
};

export default AddEditReservation;