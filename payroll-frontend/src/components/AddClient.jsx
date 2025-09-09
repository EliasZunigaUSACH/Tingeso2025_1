import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import clientService from "../services/client.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import SaveIcon from "@mui/icons-material/Save";
import "react-datepicker/dist/react-datepicker.css";

const AddClient = () => {
  const [name, setName] = useState("");
  const [rut, setRut] = useState("");
  const [phone, setPhone] = useState("");
  const [email, setEmail] = useState("");
  const [status, setStatus] = useState(0);
  const [activeLoans, setActiveLoans] = useState([]);
  const [fine, setFine] = useState(0);
  const [titleClientForm] = useState("Nuevo Cliente");
  const navigate = useNavigate();

  const saveClient = (e) => {
    e.preventDefault();
    const client = { name, rut, phone, email, status, activeLoans, fine };
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
  };

  // No useEffect necesario para edición

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
            id="name"
            label="Nombre"
            value={name}
            variant="standard"
            onChange={(e) => setName(e.target.value)}
            required
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="rut"
            label="RUT"
            value={rut}
            variant="standard"
            onChange={(e) => setRut(e.target.value)}
            required
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="phone"
            label="Teléfono"
            value={phone}
            variant="standard"
            onChange={(e) => setPhone(e.target.value)}
            required
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="email"
            label="Correo electrónico"
            value={email}
            variant="standard"
            onChange={(e) => setEmail(e.target.value)}
            required
          />
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

export default AddClient;