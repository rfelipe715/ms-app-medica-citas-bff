package cl.duoc.ms_citas_bbf.service;

import cl.duoc.ms_citas_bbf.client.CitasBsRestClient;
import cl.duoc.ms_citas_bbf.model.dto.CitaDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CitasService {

    @Autowired
    private CitasBsRestClient citasBsRestClient;

    public List<CitaDTO> listarCitas() {
        return citasBsRestClient.listarCitas();
    }

    public CitaDTO registrarCita(CitaDTO citaDTO) {
        return citasBsRestClient.registrarCita(citaDTO);
    }

    public void eliminarCita (Long id) {
        citasBsRestClient.eliminarCita(id);
    }

    public CitaUpdateDTO actualizarCita (CitaUpdateDTO cita) {
        return citasBsRestClient.actualizarCita(cita);
    }
}
