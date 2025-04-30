import { useEffect, useState } from "react";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import receiptService from "../services/receipt.service";

const ReceiptViewer = ({ reservationId }) => {
  const [reservation, setReservation] = useState(null);
  const [receipt, setReceipt] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    receiptService
      .getByReservationId(reservationId)
      .then((response) => {
        setReceipt(response.data);
        setReservation(response.data.reservation); // Assuming the reservation data is nested in the receipt response
        setIsLoading(false);
      })
      .catch((error) => {
        console.error("Error fetching receipt:", error);
        setIsLoading(false);
      });
  }, [reservationId]);

  if (isLoading) {
    return <Typography>Cargando...</Typography>;
  }

  if (!receipt) {
    return <Typography>No se encontró la boleta para esta reserva.</Typography>;
  }

  return (
    <Box
      sx={{
        width: 400,
        bgcolor: "background.paper",
        border: "2px solid #000",
        boxShadow: 24,
        p: 4,
        margin: "auto",
        mt: 4,
      }}
    >
      <Typography variant="h6" component="h2" sx={{ mb: 2 }}>
        Detalles de la Boleta
      </Typography>
      <TableContainer component={Paper}>
        <Table>
          <TableBody>
            <TableRow>
              <TableCell><strong>Nombre del Cliente</strong></TableCell>
              <TableCell>{receipt.clientName}</TableCell>
            </TableRow>
            <TableRow>
              <TableCell><strong>ID de la Reserva</strong></TableCell>
              <TableCell>{receipt.reservationId}</TableCell>
            </TableRow>
            <TableRow>
              <TableCell><strong>Fecha</strong></TableCell>
              <TableCell>{receipt.date}</TableCell>
            </TableRow>
            <TableRow>
              <TableCell><strong>Hora de Inicio</strong></TableCell>
              <TableCell>{receipt.startTime}</TableCell>
            </TableRow>
            <TableRow>
              <TableCell><strong>Hora de Fin</strong></TableCell>
              <TableCell>{receipt.endTime}</TableCell>
            </TableRow>
            <TableRow>
              <TableCell><strong>Cantidad de Personas</strong></TableCell>
              <TableCell>{receipt.peopleQuantity}</TableCell>
            </TableRow>
            <TableRow>
              <TableCell><strong>Tiempo de Pista</strong></TableCell>
              <TableCell>{receipt.trackTime}</TableCell>
            </TableRow>
            <TableRow>
              <TableCell><strong>Precio por Hora</strong></TableCell>
              <TableCell>{reservation.price}</TableCell>
            </TableRow>
            <TableRow>
              <TableCell><strong>Descuento</strong></TableCell>
              <TableCell>{receipt.discount}</TableCell>
            </TableRow>
            <TableRow>
              <TableCell><strong>Subtotal</strong></TableCell>
              <TableCell>{receipt.subtotal}</TableCell>
            </TableRow>
            <TableRow>
              <TableCell><strong>IVA</strong></TableCell>
              <TableCell>{receipt.tax}</TableCell>
            </TableRow>
            <TableRow>
              <TableCell><strong>Monto Total</strong></TableCell>
              <TableCell>{receipt.total}</TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};

export default ReceiptViewer;
