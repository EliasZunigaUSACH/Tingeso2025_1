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
    >
      <h3> {titleEmployeeForm} </h3>
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
            id="email"
            label="Correo electrónico"
            value={email}
            variant="standard"
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="password"
            label="Contraseña"
            value={password}
            variant="standard"
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </FormControl>

		<FormControl fullWidth>
			<InputLabel shrink htmlFor="role">Rol</InputLabel>
			<Select
				id="role"
				label="Rol"
				value={role}
				onChange={(e) => setRole(e.target.value)}
				required
			>
				<MenuItem value={2}>Administrador</MenuItem>
				<MenuItem value={1}>Empleado</MenuItem>
			</Select>
		</FormControl>

        <FormControl>
          <br />
          <Button
            variant="contained"
            color="info"
            onClick={(e) => saveEmployee(e)}
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
          onClick={() => navigate("/employee/list")}
        >
          Back to List
        </Button>
      </form>
      <hr />
    </Box>
  );
};

export default RegisterEmployee;
