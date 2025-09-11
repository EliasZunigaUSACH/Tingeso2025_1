import './App.css'
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom'
import Navbar from "./components/Navbar"
import Home from './components/Home';
import ClientList from './components/ClientList';
import AddClient from './components/AddClient';
import ToolList from './components/ToolList';
import AddTool from './components/AddTool';
//import EditTool from './components/EditTool';
import Kardex from './components/Kardex';
import AddEditLoan from './components/AddEditLoan';
//import EditLoan from './components/EditLoan';
import NotFound from './components/NotFound';
import Reports from './components/ReportList';
//import LoginEmployee from './components/LoginEmployee';
import EmployeesList from './components/EmployeesList';
import RegisterEmployee from './components/RegisterEmployee';

function App() {
  return (
      <Router>
        <div className='gradient-background'>
          <div className="container">
          <Navbar></Navbar>
            <Routes>
              <Route path="/" element={<Home/>} />
              <Route path="/home" element={<Home/>} />
              <Route path="/client/list" element={<ClientList/>} />
              <Route path="/client/add" element={<AddClient/>} />
              <Route path="/client/edit/:id" element={<AddClient/>} />
              <Route path="/employee/list" element={<EmployeesList/>} />
              <Route path="/employee/add" element={<RegisterEmployee/>} />
              <Route path="/tool/list" element={<ToolList/>} />
              <Route path="/tool/add" element={<AddTool/>} />
              <Route path="/report/list" element={<Reports/>} />
              <Route path="/kardex" element={<Kardex/>} />
              <Route path="/kardex/add" element={<AddEditLoan/>} />
              <Route path="*" element={<NotFound/>} />
            </Routes>
          </div>
        </div>
      </Router>
  );
}

export default App