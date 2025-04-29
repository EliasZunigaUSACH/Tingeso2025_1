import './App.css'
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom'
import Navbar from "./components/Navbar"
import Home from './components/Home';
import ClientList from './components/ClientList';
import AddEditClient from './components/AddEditClient';
import KartList from './components/KartList';
import EditKart from './components/EditKart';
import ReservationList from './components/ReservationList';
import AddEditReservation from './components/AddEditReservation';
import NotFound from './components/NotFound';
import ReceiptViewer from './components/ReceiptViewer';
import AnualReport from './components/Report';

function App() {
  return (
      <Router>
        <div className='gradient-background'>
          <div className="container">
          <Navbar></Navbar>
            <Routes>
              <Route path="/home" element={<Home/>} />
              <Route path="/client/list" element={<ClientList/>} />
              <Route path="/client/add" element={<AddEditClient/>} />
              <Route path="/client/edit/:id" element={<AddEditClient/>} />
              <Route path="/kart/list" element={<KartList/>} />
              <Route path="/kart/edit/:id" element={<EditKart/>} />
              <Route path="/report/MonthReport" element={<AnualReport/>} />
              <Route path="/reservation/list" element={<ReservationList/>} />
              <Route path="/reservation/add" element={<AddEditReservation/>} />
              <Route path="/reservation/edit/:id" element={<AddEditReservation/>} />
              <Route path="/reservation/view/:id" element={<ReceiptViewer/>} />
              <Route path="*" element={<NotFound/>} />
            </Routes>
          </div>
        </div>
      </Router>
  );
}

export default App