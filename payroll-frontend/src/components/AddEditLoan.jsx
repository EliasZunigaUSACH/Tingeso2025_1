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
  const [dateStart, setDateStart] = useState(null);
  const [dateLimit, setDateLimit] = useState(null);
  const [commission, setCommission] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const { id } = useParams();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
  // Set initial dates if provided
  const initialDateStart = searchParams.get("dateStart");
  const initialDateLimit = searchParams.get("dateLimit");
  if (initialDateStart) setDateStart(new Date(initialDateStart));
  if (initialDateLimit) setDateLimit(new Date(initialDateLimit));

    // Get active clients
    clientService
      .getAll()
      .then((response) => {
        const activeClients = response.data.filter((c) => c.status === 1);
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
          setDateStart(new Date(Loan.data.dateStart));
          setDateLimit(new Date(Loan.data.dateLimit));
          setCommission(Loan.data.commission || "");
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
    if (!clientId || !toolId || !category || !dateStart || !dateLimit || !commission) {
      setErrorMessage("Todos los campos son obligatorios.");
      return false;
    }
    setErrorMessage("");
    return true;
  };

  const saveLoan = async (e) => {
    e.preventDefault();
    if (!validateFields()) return;

    const loan = {
      clientId,
      toolId,
      category,
      dateStart: format(dateStart, "yyyy-MM-dd"),
      dateLimit: format(dateLimit, "yyyy-MM-dd"),
      commission,
      status: 1, // Vigente
      id,
    };

    if (!id) {
      // Nuevo préstamo: preguntar si actualizar o crear
      const updateExisting = window.confirm("¿Deseas actualizar un préstamo existente con estos datos? (Aceptar = Actualizar, Cancelar = Crear nuevo)");
      if (updateExisting) {
        // Buscar si existe un préstamo igual para este cliente, herramienta y fecha
        try {
          const response = await LoanService.getAll();
          const existingLoan = response.data.find(l => l.clientId === clientId && l.toolId === toolId && l.dateStart === format(dateStart, "yyyy-MM-dd"));
          if (existingLoan) {
            // Actualizar préstamo existente
            const updatedLoan = { ...existingLoan, category, dateStart: format(dateStart, "yyyy-MM-dd"), dateLimit: format(dateLimit, "yyyy-MM-dd"), commission };
            await LoanService.update(updatedLoan);
            // Registrar movimiento en kardex
            const kardexRegister = {
              clientId,
              toolId,
              category,
              date: format(dateStart, "yyyy-MM-dd"),
              movement: "Préstamo (Actualizado)"
            };
            await kardexRegisterService.create(kardexRegister);
            navigate("/kardex");
            return;
          } else {
            // No existe, crear nuevo
            await LoanService.create(loan);
            const kardexRegister = {
              clientId,
              toolId,
              category,
              date: format(dateStart, "yyyy-MM-dd"),
              movement: "Préstamo"
            };
            await kardexRegisterService.create(kardexRegister);
            navigate("/kardex");
            return;
          }
        } catch (error) {
          console.error("Error al buscar o actualizar préstamo", error);
        }
      } else {
        // Crear nuevo préstamo
        LoanService.create(loan)
          .then(() => {
            const kardexRegister = {
              clientId,
              toolId,
              category,
              date: format(dateStart, "yyyy-MM-dd"),
              movement: "Préstamo"
            };
            kardexRegisterService.create(kardexRegister)
              .then(() => {
                navigate("/kardex");
              })
              .catch((error) => {
                console.error("Error al crear registro en kardex", error);
                navigate("/kardex");
              });
          })
          .catch((error) => console.error("Error al guardar préstamo", error));
        return;
      }
    } else {
      // EDICIÓN: movimiento Devolución o Atraso
      LoanService.update(loan)
        .then(() => {
          const today = new Date();
          const editedDate = new Date(dateStart);
          let movement = "Devolución";
          if (editedDate < new Date(today.getFullYear(), today.getMonth(), today.getDate())) {
            movement = "Atraso";
          }
          const kardexRegister = {
            clientId,
            toolId,
            category,
            date: format(dateStart, "yyyy-MM-dd"),
            movement
          };
          kardexRegisterService.create(kardexRegister)
            .then(() => {
              navigate("/kardex");
            })
            .catch((error) => {
              console.error("Error al crear registro en kardex (edición)", error);
              navigate("/kardex");
            });
        })
        .catch((error) => console.error("Error al guardar préstamo", error));
    }
  };

  const deleteLoan = () => {
    if (!id) return; // Solo se puede eliminar si existe un ID
    if (window.confirm("¿Estás seguro de que deseas eliminar esta reserva?")) {
      LoanService
        .delete(id)
        .then(() => {
          console.log("Reserva eliminada correctamente.");
          navigate("/kardex");
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
      {/* Selección de fecha de inicio */}
      <FormControl fullWidth sx={{ mb: 2 }}>
        <label htmlFor="dateStart">Fecha de Préstamo / Inicio</label>
        <DatePicker
          id="dateStart"
          selected={dateStart}
          onChange={(newDate) => setDateStart(newDate)}
          dateFormat="yyyy-MM-dd"
          minDate={new Date()}
          placeholderText="Selecciona la fecha de inicio"
        />
      </FormControl>
      {/* Selección de fecha límite */}
      <FormControl fullWidth sx={{ mb: 2 }}>
        <label htmlFor="dateLimit">Fecha Límite</label>
        <DatePicker
          id="dateLimit"
          selected={dateLimit}
          onChange={(newDate) => setDateLimit(newDate)}
          dateFormat="yyyy-MM-dd"
          minDate={dateStart || new Date()}
          placeholderText="Selecciona la fecha límite"
        />
      </FormControl>
      {/* Precio de comisión */}
      <FormControl fullWidth sx={{ mb: 2 }}>
        <TextField
          label="Precio de Comisión"
          type="number"
          value={commission}
          onChange={(e) => setCommission(e.target.value)}
          variant="standard"
          inputProps={{ min: 0 }}
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