import { useEffect, useState } from "react";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import receiptService from "../services/receipt.service";

const ReceiptViewer = ({ reservationId }) => {
  const [receipt, setReceipt] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    receiptService
      .getByReservationId(reservationId)
      .then((response) => {
        setReceipt(response.data);
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
      <Typography variant="h6" component="h2">
        Detalles de la Boleta
      </Typography>
      <Typography sx={{ mt: 2 }}>
        <strong>Nombre del Cliente:</strong> {receipt.clientName}
      </Typography>
      <Typography sx={{ mt: 2 }}>
        <strong>ID de la Reserva:</strong> {receipt.reservationId}
      </Typography>
      <Typography sx={{ mt: 2 }}>
        <strong>Monto Total:</strong> {receipt.totalAmount}
      </Typography>
      <Typography sx={{ mt: 2 }}>
        <strong>Fecha:</strong> {receipt.date}
      </Typography>
      {/* Agrega más campos según los datos disponibles en la entidad Receipt */}
    </Box>
  );
};

export default ReceiptViewer;
