import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from "react-router-dom";
import employeeService from '../services/employee.service';
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

const EmployeeList = () => {
  const [employees, setEmployees] = useState([]);

  const init = () => {
	employeeService
	  .getAll()
	  .then((response) => {
		console.log("Mostrando listado de todos los empleados.", response.data);
		setEmployees(response.data);
	  })
	  .catch((error) => {
		console.log(
		  "Se ha producido un error al intentar mostrar listado de todos los empleados.",
		  error
		);
	  });
  };

  useEffect(() => {
	init();
  }, []);

  const getRoleName = (roleId) => {
	switch (roleId) {
	  case 1:
		return "Administrador";
	  case 2:
		return "Empleado";
		}
	};

  const handleDelete = (id) => {
	console.log("Printing id", id);
	const confirmDelete = window.confirm(
	  "¿Esta seguro que desea borrar este empleado?"
	);
	if (confirmDelete) {
	  employeeService
		.remove(id)
		.then((response) => {
		  console.log("empleado ha sido eliminado.", response.data);
		  init();
		})
		.catch((error) => {
		  console.log(
			"Se ha producido un error al intentar eliminar al empleado",
			error
		  );
		});
	}
  };

  return (
	<TableContainer component={Paper}>
	  <br />
	  <Link
		to="/employee/add"
		style={{ textDecoration: "none", marginBottom: "1rem" }}
	  >
		<Button
		  variant="contained"
		  color="primary"
		  startIcon={<PersonAddIcon />}
		>
		  Registrar nuevo empleado
		</Button>
	  </Link>
	  <br /> <br />
	  <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
		<TableHead>
		  <TableRow>
			<TableCell align="left" sx={{ fontWeight: "bold" }}>
			  Nombre
			</TableCell>
			<TableCell align="right" sx={{ fontWeight: "bold" }}>
			  RUT
			</TableCell>
			<TableCell align="right" sx={{ fontWeight: "bold" }}>
			  Email
			</TableCell>
			<TableCell align="right" sx={{ fontWeight: "bold" }}>
			  Rol
			</TableCell>
			<TableCell align="center" sx={{ fontWeight: "bold" }}>
			  Acciones
			</TableCell>
		  </TableRow>
		</TableHead>
		<TableBody>
		  {employees.map((employee) => (
			<TableRow
			  key={employee.id}
			  sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
			>
			  <TableCell align="left">{employee.name}</TableCell>
			  <TableCell align="right">{employee.rut}</TableCell>
			  <TableCell align="right">{employee.email}</TableCell>
			  <TableCell align="right">{getRoleName(employee.role)}</TableCell>
			  <TableCell align="center">
				<Button
				  variant="contained"
				  color="info"
				  size="small"
				  onClick={() => handleEdit(employee.id)}
				  style={{ marginLeft: "0.5rem" }}
				  startIcon={<EditIcon />}
				>
				  Editar
				</Button>

				<Button
				  variant="contained"
				  color="secondary"
				  size="small"
				  onClick={() => handleDelete(employee.id)}
				  style={{ marginLeft: "0.5rem" }}
				  startIcon={<DeleteIcon />}
				>
				  Eliminar
				</Button>
			  </TableCell>
			</TableRow>
		  ))}
		</TableBody>
	  </Table>
	</TableContainer>
  );
};

export default EmployeeList;