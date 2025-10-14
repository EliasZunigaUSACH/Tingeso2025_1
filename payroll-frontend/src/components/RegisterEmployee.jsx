import React, { useState } from 'react';
import { Link, useNavigate } from "react-router-dom";
import employeeService from '../services/employee.service';
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import SaveIcon from "@mui/icons-material/Save";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import InputLabel from "@mui/material/InputLabel";

const RegisterEmployee = () => {
  const [name, setName] = useState('');
  const [rut, setRut] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('');
  const [titleEmployeeForm] = useState('Nuevo Empleado');
  const navigate = useNavigate();

  // Función para formatear el RUT con puntos y guión
  const formatRut = (value) => {
    let clean = value.replace(/[^0-9kK]/g, "");
    let body = clean.slice(0, -1);
    let dv = clean.slice(-1);
    let formatted = "";
    while (body.length > 3) {
      formatted = "." + body.slice(-3) + formatted;
      body = body.slice(0, -3);
    }
    formatted = body + formatted;
    if (formatted) {
      formatted += "-" + dv;
    } else if (dv) {
      formatted = dv;
    }
    return formatted;
  };

	const saveEmployee = (e) => {
		e.preventDefault();
		const employee = { name, rut, email, password, role };
		employeeService
			.create(employee)
			.then((response) => {
				console.log('Empleado registrado:', response.data);
				navigate('/employee/list');
			})
			.catch((error) => {
				console.error('Error al registrar empleado:', error);
			});
	};

	return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      component="form"
      sx={{ color: 'white' }}
    >
      <h3 style={{ color: 'white' }}> {titleEmployeeForm} </h3>
      <hr style={{ borderColor: 'white', width: '100%' }} />
      <form>
        <FormControl fullWidth required>
          <TextField
            id="name"
            label="Nombre *"
            value={name}
            variant="standard"
            onChange={(e) => setName(e.target.value)}
            required
            InputLabelProps={{ style: { color: 'white' } }}
            InputProps={{ style: { color: 'white' } }}
          />
        </FormControl>

        <FormControl fullWidth required>
          <TextField
            id="rut"
            label="RUT *"
            value={rut}
            variant="standard"
            onChange={(e) => {
              const input = e.target.value;
              const formatted = formatRut(input);
              setRut(formatted);
            }}
            required
            inputProps={{ maxLength: 12 }}
            InputLabelProps={{ style: { color: 'white' } }}
            InputProps={{ style: { color: 'white' } }}
          />
        </FormControl>

        <FormControl fullWidth required>
          <TextField
            id="email"
            label="Correo electrónico *"
            value={email}
            variant="standard"
            onChange={(e) => setEmail(e.target.value)}
            required
            InputLabelProps={{ style: { color: 'white' } }}
            InputProps={{ style: { color: 'white' } }}
          />
        </FormControl>

        <FormControl fullWidth required>
          <TextField
            id="password"
            label="Contraseña *"
            value={password}
            variant="standard"
            onChange={(e) => setPassword(e.target.value)}
            required
            InputLabelProps={{ style: { color: 'white' } }}
            InputProps={{ style: { color: 'white' } }}
          />
        </FormControl>

        <FormControl fullWidth required>
          <InputLabel shrink htmlFor="role" style={{ color: 'white' }}>Rol *</InputLabel>
          <Select
            id="role"
            label="Rol *"
            value={role}
            onChange={(e) => setRole(e.target.value)}
            required
            sx={{ color: 'white', '.MuiSvgIcon-root': { color: 'white' } }}
            MenuProps={{ PaperProps: { sx: { color: 'white', backgroundColor: '#222' } } }}
          >
            <MenuItem value={'ADMIN'}>Administrador</MenuItem>
            <MenuItem value={'USER'}>Empleado</MenuItem>
          </Select>
        </FormControl>

        <FormControl>
          <br />
          <Button
            variant="contained"
            color="info"
            onClick={(e) => saveEmployee(e)}
            style={{ marginLeft: "0.5rem", color: 'white' }}
            startIcon={<SaveIcon />}
          >
            Guardar
          </Button>
        </FormControl>
        <br />
        <Button
          variant="contained"
          color="secondary"
          onClick={() => navigate("/employee/list")}
          style={{ color: 'white' }}
        >
          Back to List
        </Button>
      </form>
      <hr style={{ borderColor: 'white', width: '100%' }} />
    </Box>
  );
};

export default RegisterEmployee;
