import * as React from "react";
import Box from "@mui/material/Box";
import Drawer from "@mui/material/Drawer";
import List from "@mui/material/List";
import Divider from "@mui/material/Divider";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import PeopleAltIcon from "@mui/icons-material/PeopleAlt";
import PaidIcon from "@mui/icons-material/Paid";
import CalculateIcon from "@mui/icons-material/Calculate";
import AnalyticsIcon from "@mui/icons-material/Analytics";
import DiscountIcon from "@mui/icons-material/Discount";
import MoreTimeIcon from "@mui/icons-material/MoreTime";
import HomeIcon from "@mui/icons-material/Home";
import { useNavigate } from "react-router-dom";
import { DirectionsCar } from "@mui/icons-material";

export default function Sidemenu({ open, toggleDrawer }) {
  const navigate = useNavigate();

  const listOptions = () => (
    <Box
      role="presentation"
      onClick={toggleDrawer(false)}
    >
      <List>
        <ListItemButton onClick={() => navigate("/home")}>
          <ListItemIcon>
            <HomeIcon />
          </ListItemIcon>
          <ListItemText primary="Home" />
        </ListItemButton>

        <Divider />

        <ListItemButton onClick={() => navigate("/client/list")}>
          <ListItemIcon>
            <PeopleAltIcon />
          </ListItemIcon>
          <ListItemText primary="Clientes" />
        </ListItemButton>

        <ListItemButton onClick={() => navigate("/kart/list")}>
          <ListItemIcon>
            <DirectionsCar />
          </ListItemIcon>
          <ListItemText primary="Karts" />
        </ListItemButton>

        <ListItemButton onClick={() => navigate("/reservation/list")}>
          <ListItemIcon>
            <MoreTimeIcon />
          </ListItemIcon>
          <ListItemText primary="Rack semanal" />
        </ListItemButton>

        <ListItemButton onClick={() => navigate("/receipt/list")}>
          <ListItemIcon>
            <PaidIcon />
          </ListItemIcon>
          <ListItemText primary="Boletas" />
        </ListItemButton>

        <ListItemButton onClick={() => navigate("/receipt/calculate")}>
          <ListItemIcon>
            <CalculateIcon />
          </ListItemIcon>
          <ListItemText primary="Calcular Boleta" />
        </ListItemButton>
        <ListItemButton onClick={() => navigate("/reports/MonthReport")}>
          <ListItemIcon>
            <AnalyticsIcon />
          </ListItemIcon>
          <ListItemText primary="Reporte mensual" />
        </ListItemButton>
      </List>

      <Divider />

      <List>
        <ListItemButton onClick={() => navigate("/client/discounts")}>
          <ListItemIcon>
            <DiscountIcon />
          </ListItemIcon>
          <ListItemText primary="Descuentos" />
        </ListItemButton>
      </List>
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
