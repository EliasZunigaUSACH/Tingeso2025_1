import * as React from "react";
import Box from "@mui/material/Box";
import Drawer from "@mui/material/Drawer";
import List from "@mui/material/List";
import Divider from "@mui/material/Divider";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import PeopleAltIcon from "@mui/icons-material/PeopleAlt";
import AnalyticsIcon from "@mui/icons-material/Analytics";
import HomeIcon from "@mui/icons-material/Home";
import { useNavigate } from "react-router-dom";
import { DirectionsCar } from "@mui/icons-material";
import CalendarTodayIcon from "@mui/icons-material/CalendarToday"; // Importar el nuevo icono

export default function Sidemenu({ open, toggleDrawer }) {
  const navigate = useNavigate();

  const listOptions = () => (
    <Box
      role="presentation"
      onClick={toggleDrawer(false)}
      sx={{
        backgroundColor: "black", // Fondo negro
        color: "white", // Texto blanco
        height: "100%", // Asegura que ocupe todo el espacio del Drawer
        fontFamily: "'DIN Pro Cond Black', sans-serif", // Aplicar la fuente
      }}
    >
      <List>
        <ListItemButton onClick={() => navigate("/home")}>
          <ListItemIcon sx={{ color: "white" }}>
            <HomeIcon />
          </ListItemIcon>
          <ListItemText primary="Home" />
        </ListItemButton>

        <Divider sx={{ backgroundColor: "white" }} />

        <ListItemButton onClick={() => navigate("/client/list")}>
          <ListItemIcon sx={{ color: "white" }}>
            <PeopleAltIcon />
          </ListItemIcon>
          <ListItemText primary="Clientes" />
        </ListItemButton>

        <ListItemButton onClick={() => navigate("/kart/list")}>
          <ListItemIcon sx={{ color: "white" }}>
            <DirectionsCar />
          </ListItemIcon>
          <ListItemText primary="Karts" />
        </ListItemButton>

        <ListItemButton onClick={() => navigate("/reservation/list")}>
          <ListItemIcon sx={{ color: "white" }}>
            <CalendarTodayIcon />
          </ListItemIcon>
          <ListItemText primary="Rack semanal" />
        </ListItemButton>

        <ListItemButton onClick={() => navigate("/reports/MonthReport")}>
          <ListItemIcon sx={{ color: "white" }}>
            <AnalyticsIcon />
          </ListItemIcon>
          <ListItemText primary="Reporte mensual" />
        </ListItemButton>
      </List>
      <Divider sx={{ backgroundColor: "white" }} />
    </Box>
  );

  return (
    <div>
      <Drawer anchor={"left"} open={open} onClose={toggleDrawer(false)}>
        {listOptions()}
      </Drawer>
    </div>
  );
}
