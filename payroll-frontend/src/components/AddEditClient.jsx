import { useState, useEffect } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import clientService from "../services/client.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

const AddEditClient = () => {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [birthday, setBirthday] = useState("");
  const [fidelityLevel, setFidelityLevel] = useState(0);
  const { id } = useParams();
  const [titleClientForm, setTitleClientForm] = useState("");
  const navigate = useNavigate();

  const saveClient = (e) => {
    e.preventDefault();

    const client = { id, name, birthday, fidelityLevel };
    if (id) {
      //Actualizar Datos Cliente
      clientService
        .update(client)
        .then((response) => {
          console.log("Cliente ha sido actualizado.", response.data);
          navigate("/client/list");
        })
        .catch((error) => {
          console.log(
            "Ha ocurrido un error al intentar actualizar datos del cliente.",
            error
          );
        });
    } else {
      //Crear nuevo cliente
      clientService
        .create(client)
        .then((response) => {
          console.log("Cliente ha sido añadido.", response.data);
          navigate("/client/list");
        })
        .catch((error) => {
          console.log(
            "Ha ocurrido un error al intentar crear nuevo cliente.",
            error
          );
        });
    }
  };

  useEffect(() => {
    if (id) {
      setTitleClientForm("Editar Cliente");
      clientService
        .get(id)
        .then((client) => {
          setName(client.data.name);
          setEmail(client.data.email);
          setBirthday(client.data.birthday);
          setFidelityLevel(client.data.fidelityLevel);
        })
        .catch((error) => {
          console.log("Se ha producido un error.", error);
        });
    } else {
      setTitleClientForm("Nuevo Cliente");
      setFidelityLevel(0); // Asignar nivel de fidelidad por defecto
    }
  }, []);

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      component="form"
    >
      <h3> {titleClientForm} </h3>
      <hr />
      <form>
        <FormControl fullWidth>
          <TextField
            id="Name" 
            label="Nombre"
            value={name}
            variant="standard"
            onChange={(e) => setName(e.target.value)}
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="email" 
            label="Correo electrónico"
            value={email}
            variant="standard"
            onChange={(e) => setEmail(e.target.value)}
          />
        </FormControl>

        <FormControl fullWidth>
          <label htmlFor="birthday">Cumpleaños</label>
          <DatePicker
            id="birthday"
            selected={birthday ? new Date(birthday) : null}
            onChange={(date) => {
              if (date) {
                const formattedDate = `${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
                setBirthday(formattedDate);
              }
            }}
            dateFormat="MM-dd"
            showMonthDropdown
            showDayDropdown
            showYearDropdown={false}
            dropdownMode="select"
            placeholderText="Selecciona una fecha"
            minDate={new Date(new Date().getFullYear(), 0, 1)}
            maxDate={new Date(new Date().getFullYear(), 11, 31)}
          />
        </FormControl>

        <FormControl fullWidth>
          <label htmlFor="fidelityLevel">Nivel de Fidelidad</label>
          <TextField
            id="fidelityLevel"
            select
            value={fidelityLevel}
            onChange={(e) => setFidelityLevel(Number(e.target.value))}
            variant="standard"
          >
            <MenuItem value={0}>No frecuente</MenuItem>
            <MenuItem value={1}>Regular</MenuItem>
            <MenuItem value={2}>Frecuente</MenuItem>
            <MenuItem value={3}>Muy frecuente</MenuItem>
          </TextField>
        </FormControl>

        <FormControl>
          <br />
          <Button
            variant="contained"
            color="info"
            onClick={(e) => saveClient(e)}
            style={{ marginLeft: "0.5rem" }}
            startIcon={<SaveIcon />}
          >
            Guardar
          </Button>
        </FormControl>
          <br />
          <Button
            variant="contained"
            color="secondary"
            onClick={() => navigate("/client/list")}
          >
            Back to List
          </Button>
        </form>
      <hr />
    </Box>
  );
};

export default AddEditClient;