import { useEffect, useState } from "react";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import receiptService from "../services/receipt.service";

const ReceiptList = () => {
  const [receipts, setReceipts] = useState([]);

  useEffect(() => {
    // Fetch receipts from the service
    receiptService.getAll().then((response) => {
      setReceipts(response.data);
    }).catch((error) => {
      console.error("Error fetching receipts:", error);
    });
  }, []);

  const handleViewReceipt = (receiptId) => {
    // Redirect or handle viewing the full receipt
    console.log(`View receipt with ID: ${receiptId}`);
    // Example: Navigate to a detailed receipt page
    // window.location.href = `/receipts/${receiptId}`;
  };

  return (
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Nombre del Cliente</TableCell>
            <TableCell>ID de la Reserva</TableCell>
            <TableCell>Acciones</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {receipts.map((receipt) => (
            <TableRow key={receipt.id}>
              <TableCell>{receipt.clientName}</TableCell>
              <TableCell>{receipt.reservationId}</TableCell>
              <TableCell>
                <Button
                  variant="contained"
                  color="primary"
                  onClick={() => handleViewReceipt(receipt.id)}
                >
                  Ver Boleta
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default ReceiptList;