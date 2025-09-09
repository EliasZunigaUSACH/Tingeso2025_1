
import React, { useState } from 'react';
import employeeService from '../services/employee.service';

const LoginEmployee = () => {
	const [email, setEmail] = useState('');
	const [password, setPassword] = useState('');
	const [error, setError] = useState('');

		const handleSubmit = async (e) => {
			e.preventDefault();
			setError('');
			if (!email || !password) {
				setError('Por favor, ingrese correo y contraseña.');
				return;
			}
			try {
				const response = await employeeService.login({ email, password });
				// Puedes guardar el token o datos aquí si es necesario
				alert('Inicio de sesión exitoso');
				// Redirigir o actualizar estado según sea necesario
			} catch (err) {
				setError('Credenciales incorrectas o error de servidor.');
			}
		};

	return (
		<div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
			<form onSubmit={handleSubmit} style={{ background: '#fff', padding: '2rem', borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)', minWidth: '320px' }}>
				<h2 style={{ textAlign: 'center', marginBottom: '1.5rem' }}>Iniciar Sesión</h2>
				<div style={{ marginBottom: '1rem' }}>
					<label htmlFor="email" style={{ display: 'block', marginBottom: '0.5rem' }}>Correo electrónico</label>
					<input
						type="email"
						id="email"
						value={email}
						onChange={e => setEmail(e.target.value)}
						style={{ width: '100%', padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc' }}
						required
					/>
				</div>
				<div style={{ marginBottom: '1rem' }}>
					<label htmlFor="password" style={{ display: 'block', marginBottom: '0.5rem' }}>Contraseña</label>
					<input
						type="password"
						id="password"
						value={password}
						onChange={e => setPassword(e.target.value)}
						style={{ width: '100%', padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc' }}
						required
					/>
				</div>
				{error && <div style={{ color: 'red', marginBottom: '1rem', textAlign: 'center' }}>{error}</div>}
				<button type="submit" style={{ width: '100%', padding: '0.75rem', background: '#007bff', color: '#fff', border: 'none', borderRadius: '4px', fontWeight: 'bold', cursor: 'pointer' }}>
					Ingresar
				</button>
			</form>
		</div>
	);
};

export default LoginEmployee;
