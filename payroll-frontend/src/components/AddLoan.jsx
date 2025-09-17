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

const AddLoan = () => {
  const { id } = useParams(); 
  const [clientId, setClientId] = useState("");
  const [clients, setClients] = useState([]);
  const [clientName, setClientName] = useState("");
  const [category, setCategory] = useState("");
  const [categories, setCategories] = useState([]);
  const [toolId, setToolId] = useState("");
  const [tools, setTools] = useState([]);
  const [filteredTools, setFilteredTools] = useState([]);
  const [toolName, setToolName] = useState("");
  const [status, setStatus] = useState(1); // 1: Vigente
  const [dateStart, setDateStart] = useState(null);
  const [dateLimit, setDateLimit] = useState(null);
  const [dateReturn, setDateReturn] = useState(null);
  const [price, setPrice] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();

  // Cargar clientes activos y herramientas disponibles
  useEffect(() => {
    // Obtener clientes activos
    clientService.getAll()
      .then((response) => {
        // Filtrar clientes activos si tienen una propiedad 'active' o similar
        const activos = response.data.filter(c => c.active !== false);
        setClients(activos);
      })
      .catch((error) => {
        console.error("Error al obtener clientes", error);
      });

    // Obtener herramientas
    toolService.getAll()
      .then((response) => {
        setTools(response.data);
        // Extraer categorías únicas
        const uniqueCategories = [...new Set(response.data.map(tool => tool.category))];
        setCategories(uniqueCategories);
      })
      .catch((error) => {
        console.error("Error al obtener herramientas", error);
      });
  }, []);

  // Filtrar herramientas por categoría seleccionada
  useEffect(() => {
    if (category) {
      setFilteredTools(tools.filter(tool => tool.category === category && tool.available !== false));
    } else {
      setFilteredTools([]);
    }
  }, [category, tools]);

  // Actualizar nombre de cliente y herramienta seleccionados
  useEffect(() => {
    const selectedClient = clients.find(c => c.id === clientId);
    setClientName(selectedClient ? selectedClient.name : "");
    const selectedTool = tools.find(t => t.id === toolId);
    setToolName(selectedTool ? selectedTool.name : "");
  }, [clientId, toolId, clients, tools]);

  const saveLoan = (e) => {
    e.preventDefault();
    // Validación previa
    if (!clientId || !toolId || !dateStart || !dateLimit || !price) {
      setErrorMessage("Por favor, complete todos los campos obligatorios.");
      return;
    }
    let formattedDateStart, formattedDateLimit, formattedDateReturn;
    try {
      formattedDateStart = format(dateStart, "yyyy-MM-dd");
      formattedDateLimit = format(dateLimit, "yyyy-MM-dd");
      formattedDateReturn = dateReturn ? format(dateReturn, "yyyy-MM-dd") : null;
    } catch (err) {
      setErrorMessage("Las fechas seleccionadas no son válidas.");
      return;
    }
    const loan = { clientId, clientName, toolId, toolName, dateStart: formattedDateStart, dateLimit: formattedDateLimit, dateReturn: formattedDateReturn, price, status };
    LoanService.create(loan)
      .then((response) => {
        console.log("Préstamo ha sido añadido.", response.data);
        const kardexRegister = {
          toolId: response.data.toolId,
          clientId: response.data.clientId,
          clientName: response.data.clientName,
          loanId: response.data.id || response.data._id, // Ajustar según backend
          toolName: response.data.toolName,
          movement: "Préstamo de herramienta",
          date: new Date().toISOString(),
          typeRelated: 2 // 2: Préstamo
        };
        import("../services/kardexRegister.service.js").then((kardexServiceModule) => {
          const kardexService = kardexServiceModule.default;
          kardexService.create(kardexRegister)
            .then(() => {
              console.log("Registro de kardex creado.");
              navigate("/kardex");
            })
            .catch((error) => {
              console.log("Error al crear registro en kardex.", error);
              navigate("/kardex");
            });
        });
      })
      .catch((error) => {
        console.log("Ha ocurrido un error al intentar crear nuevo préstamo.", error);
        setErrorMessage("Error al crear el préstamo. Verifique los datos e intente de nuevo.");
      });
};

return (
    <Box
      component="form"
      onSubmit={saveLoan}
      sx={{
        display: "flex",
        flexDirection: "column",
        gap: 2,
        p: 2,
        border: "1px solid #ccc",
        borderRadius: "4px",
        maxWidth: "600px",
        margin: "auto"
      }}
    >
      <h3>Nuevo Préstamo</h3>

      <FormControl fullWidth sx={{ mb: 2 }}>
        <TextField
          select
          label="Seleccionar Cliente Activo"
          value={clientId}
          onChange={(e) => setClientId(e.target.value)}
          variant="standard"
          required
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
          value={price}
          onChange={(e) => setPrice(e.target.value)}
          variant="standard"
          inputProps={{ min: 0 }}
          required
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
          required
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
          required
        >
          {filteredTools.map((tool) => (
            <MenuItem key={tool.id} value={tool.id}>
              {tool.name}
            </MenuItem>
          ))}
        </TextField>
      </FormControl>

      <FormControl>
        <Button
          variant="contained"
          color="info"
          type="submit"
          onClick={(e) => saveLoan(e)}
          startIcon={<SaveIcon />}
        >
          Guardar
        </Button>
      </FormControl>
      {errorMessage && (
        <div style={{ color: "red", marginTop: "10px" }}>{errorMessage}</div>
      )}
      <br />
      <Button
        variant="outlined"
        color= "secondary"
        onClick={() => navigate("/kardex")}
      >
        Cancelar
      </Button>
    </Box>
);

};

export default AddLoan;