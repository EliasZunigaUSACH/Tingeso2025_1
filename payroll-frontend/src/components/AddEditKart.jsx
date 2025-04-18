import { useEffect } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import kartService from "../services/kart.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";


const AddEditKart = () => {
    const [id, setId] = useState("");
    const [status, setStatus] = useState("");
    const [avaliable, setAvaliable] = useState("");
    const navigate = useNavigate();

    const saveKart = (e) => {
        e.preventDefault();

        const kart = { id, status, avaliable };
        if (id){

            kartService
                .update(kart)
                .then((response) => {
                    console.log("Kart ha sido actualizado.", response.data);
                    navigate("/kart/list");
                })
                .catch((error) => {
                    console.log(
                        "Ha ocurrido un error al intentar actualizar datos del kart.",
                        error
                    );
                });
        } else {
            kartService
                .create(kart)
                .then((response) => {
                    console.log("Kart ha sido añadido.", response.data);
                    navigate("/kart/list");
                })
                .catch((error) => {
                    console.log(
                        "Ha ocurrido un error al intentar crear nuevo kart.",
                        error
                    );
                });
        }
    };

    useEffect(() => {
        if (id) {
            setTitleKartForm("Editar Kart");
            kartService
                .get(id)
                .then((response) => {
                    setStatus(response.data.status);
                    setAvaliable(response.data.avaliable);
                })
                .catch((error) => {
                    console.log("Se ha producido un error.", error);
                });
        } else {
            setTitleKartForm("Agregar Kart");
        }
    }, []);

    return (
        <Box
            display="flex"
            flexDirection="column"
            justifyContent="center"
            alignItems="center"
            component="form"
        >
            <Typography variant="h4" gutterBottom>
                {titleKartForm}
            </Typography>
            <TextField
                label="ID"
                variant="outlined"
                value={id}
                onChange={(e) => setId(e.target.value)}
                fullWidth
                margin="normal"
            />
            <TextField
                label="Estado"
                variant="outlined"
                value={status}
                onChange={(e) => setStatus(e.target.value)}
                fullWidth
                margin="normal"
            />
            <TextField
                label="Disponibilidad"
                variant="outlined"
                value={avaliable}
                onChange={(e) => setAvaliable(e.target.value)}
                fullWidth
                margin="normal"
            />
            <Button
                variant="contained"
                color="primary"
                onClick={saveKart}
            >
                Guardar
            </Button>
        </Box>
    );
};

export default AddEditKart;