package cl.duoc.ms_citas_bbf.service;

import cl.duoc.ms_citas_bbf.client.CitasBsRestClient;
import cl.duoc.ms_citas_bbf.client.PacientesBffRestClient;
import cl.duoc.ms_citas_bbf.model.dto.CitaDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaUpdateDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaConPacienteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitasService {

    @Autowired
    private CitasBsRestClient citasBsRestClient;

    @Autowired
    private PacientesBffRestClient pacientesBffRestClient;

    public List<CitaDTO> listarCitas() {
        return citasBsRestClient.listarCitas();
    }

    public List<CitaConPacienteDTO> listarCitasConPacientes() {
        return citasBsRestClient.listarCitas().stream()
            .map(this::enriquecerCita)
            .collect(Collectors.toList());
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

    public CitaConPacienteDTO obtenerCitaConPaciente(Long id) {
        CitaDTO cita = citasBsRestClient.listarCitas().stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .orElse(null);
        
        if (cita != null) {
            return enriquecerCita(cita);
        }
        return null;
    }

    private CitaConPacienteDTO enriquecerCita(CitaDTO citaDTO) {
        CitaConPacienteDTO conPaciente = new CitaConPacienteDTO();
        conPaciente.setId(citaDTO.getId());
        conPaciente.setPacienteId(citaDTO.getPacienteId());
        conPaciente.setFecha(citaDTO.getFecha());
        conPaciente.setHora(citaDTO.getHora());
        conPaciente.setEstado(citaDTO.getEstado());
        
        try {
            conPaciente.setPaciente(pacientesBffRestClient.obtenerPaciente(citaDTO.getPacienteId()));
        } catch (Exception e) {
            // Silenciar error si no encuentra paciente
        }
        
        return conPaciente;
    }
}
