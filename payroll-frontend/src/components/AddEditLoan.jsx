import { useState, useEffect } from "react";
import { Link, useParams, useNavigate, useSearchParams } from "react-router-dom";
import LoanService from "../services/loan.service";
import clientService from "../services/client.service";
import toolService from "../services/tool.service";
import kardexRegisterService from "../services/kardexRegister.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { format } from "date-fns";

const AddEditLoan = () => {
  const [clientId, setClientId] = useState("");
  const [clients, setClients] = useState([]);
  const [category, setCategory] = useState("");
  const [categories, setCategories] = useState([]);
  const [toolId, setToolId] = useState("");
  const [tools, setTools] = useState([]);
  const [filteredTools, setFilteredTools] = useState([]);
  const [date, setDate] = useState(null);
  const [errorMessage, setErrorMessage] = useState("");
  const { id } = useParams();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    // Set initial date if provided
    const initialDate = searchParams.get("date");
    if (initialDate) setDate(new Date(initialDate));

    // Get active clients
    clientService
      .getAll()
      .then((response) => {
        const activeClients = response.data.filter((c) => c.status === "activo" || c.status === "active");
        setClients(activeClients);
      })
      .catch((error) => console.error("Error al obtener clientes", error));

    // Get all tools and categories
    toolService
      .getAll()
      .then((response) => {
        setTools(response.data);
        // Get unique categories from tools
        const uniqueCategories = [...new Set(response.data.map((tool) => tool.category))];
        setCategories(uniqueCategories);
      })
      .catch((error) => console.error("Error al obtener herramientas", error));

    if (id) {
      LoanService
        .get(id)
        .then((Loan) => {
          setClientId(Loan.data.clientId);
          setToolId(Loan.data.toolId);
          setDate(new Date(Loan.data.date));
          setCategory(Loan.data.category || "");
        })
        .catch((error) => console.error("Error al cargar prestamo", error));
    }
  }, [id, searchParams]);

  // Update filtered tools when category changes
  useEffect(() => {
    if (category) {
      setFilteredTools(tools.filter((tool) => tool.category === category));
    } else {
      setFilteredTools([]);
    }
  }, [category, tools]);

  const validateFields = () => {
    if (!clientId || !toolId || !category || !date) {
      setErrorMessage("Todos los campos son obligatorios.");
      return false;
    }
    setErrorMessage("");
    return true;
  };

  const saveLoan = (e) => {
    e.preventDefault();
    if (!validateFields()) return;

    const loan = {
      clientId,
      toolId,
      category,
      date: format(date, "yyyy-MM-dd"),
      id,
    };

    const serviceCall = id
      ? LoanService.update(loan)
      : LoanService.create(loan);

    serviceCall
      .then((response) => {
        if (!id) {
          // CREACIÓN: movimiento Préstamo
          const kardexRegister = {
            clientId,
            toolId,
            category,
            date: format(date, "yyyy-MM-dd"),
            movement: "Préstamo"
          };
          kardexRegisterService.create(kardexRegister)
            .then(() => {
              console.log("Registro de movimiento en kardex creado correctamente.");
              navigate("/Loan/list");
            })
            .catch((error) => {
              console.error("Error al crear registro en kardex", error);
              navigate("/Loan/list");
            });
        } else {
          // EDICIÓN: movimiento Devolución o Atraso
          // Suponiendo que la devolución es si la fecha es hoy o anterior, atraso si es anterior a hoy
          const today = new Date();
          const editedDate = new Date(date);
          let movement = "Devolución";
          // Si la fecha editada es menor a hoy, es atraso
          if (editedDate < new Date(today.getFullYear(), today.getMonth(), today.getDate())) {
            movement = "Atraso";
          }
          const kardexRegister = {
            clientId,
            toolId,
            category,
            date: format(date, "yyyy-MM-dd"),
            movement
          };
          kardexRegisterService.create(kardexRegister)
            .then(() => {
              console.log("Registro de movimiento en kardex creado correctamente (edición).");
              navigate("/Loan/list");
            })
            .catch((error) => {
              console.error("Error al crear registro en kardex (edición)", error);
              navigate("/Loan/list");
            });
        }
      })
      .catch((error) => console.error("Error al guardar préstamo", error));
  };

  const deleteLoan = () => {
    if (!id) return; // Solo se puede eliminar si existe un ID
    if (window.confirm("¿Estás seguro de que deseas eliminar esta reserva?")) {
      LoanService
        .delete(id)
        .then(() => {
          console.log("Reserva eliminada correctamente.");
          navigate("/Loan/list");
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
      <h3>{id ? "Editar Préstamo" : "Nuevo Préstamo"}</h3>
      <hr />
      {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
      {/* Selección de cliente activo */}
      <FormControl fullWidth sx={{ mb: 2 }}>
        <TextField
          select
          label="Seleccionar Cliente Activo"
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
      {/* Selección de fecha */}
      <FormControl fullWidth sx={{ mb: 2 }}>
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
      {/* Selección de categoría de herramienta */}
      <FormControl fullWidth sx={{ mb: 2 }}>
        <TextField
          select
          label="Seleccionar Categoría"
          value={category}
          onChange={(e) => {
            setCategory(e.target.value);
            setToolId("");
          }}
          variant="standard"
        >
          {categories.map((cat) => (
            <MenuItem key={cat} value={cat}>
              {cat}
            </MenuItem>
          ))}
        </TextField>
      </FormControl>
      {/* Selección de herramienta por nombre */}
      <FormControl fullWidth sx={{ mb: 2 }}>
        <TextField
          select
          label="Seleccionar Herramienta"
          value={toolId}
          onChange={(e) => setToolId(e.target.value)}
          variant="standard"
          disabled={!category}
        >
          {filteredTools.map((tool) => (
            <MenuItem key={tool.id} value={tool.id}>
              {tool.name}
            </MenuItem>
          ))}
        </TextField>
      </FormControl>
      <FormControl>
        <br />
        <Button
          variant="contained"
          color="info"
          onClick={saveLoan}
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
            onClick={deleteLoan}
          >
            Eliminar
          </Button>
        </FormControl>
      )}
      <hr />
      <Link to="/kardex">Volver a la lista</Link>
    </Box>
  );
};

export default AddEditLoan;