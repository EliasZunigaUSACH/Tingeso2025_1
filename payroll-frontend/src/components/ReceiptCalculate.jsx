import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import receiptService from "../services/receipt.service";
import reservationService from "../services/reservation.service"; // Servicio para obtener reservas
import CalculateIcon from "@mui/icons-material/Calculate";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";

const ReceiptCalculate = () => {
  const [reservations, setReservations] = useState([]); // Lista de reservas
  const [selectedReservation, setSelectedReservation] = useState(""); // Reserva seleccionada
  const navigate = useNavigate();

  // Obtener reservas al cargar el componente
  useEffect(() => {
    reservationService
      .getAll()
      .then((response) => {
        setReservations(response.data);
      })
      .catch((error) => {
        console.error("Error al obtener reservas:", error);
      });
  }, []);

  const createReceipt = (e) => {
    e.preventDefault();
    if (!selectedReservation) {
      alert("Por favor, selecciona una reserva.");
      return;
    }

    receiptService
      .calculate(selectedReservation) // Enviar la reserva seleccionada al servicio
      .then((response) => {
        console.log("Boleta creada exitosamente:", response.data);
        navigate("/receipt/list"); // Redirigir a la lista de boletas
      })
      .catch((error) => {
        console.error("Error al crear la boleta:", error);
      });
  };

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      component="form"
    >
      <h3>Crear Boleta</h3>
      <FormControl fullWidth>
        <TextField
          id="reservation"
          label="Seleccionar Reserva"
          value={selectedReservation}
          select
          variant="standard"
          onChange={(e) => setSelectedReservation(e.target.value)}
          style={{ width: "50%" }}
        >
          {reservations.map((reservation) => (
            <MenuItem key={reservation.id} value={reservation.id}>
              {reservation.name} - {reservation.date}
            </MenuItem>
          ))}
        </TextField>
      </FormControl>
      <FormControl>
        <Button
          variant="contained"
          color="primary"
          onClick={createReceipt}
          style={{ marginTop: "1rem" }}
          startIcon={<CalculateIcon />}
        >
          Crear Boleta
        </Button>
      </FormControl>
    </Box>
  );
};

export default ReceiptCalculate;
