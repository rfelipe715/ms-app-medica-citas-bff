package cl.duoc.ms_citas_bbf.service;

import cl.duoc.ms_citas_bbf.client.CitasBsRestClient;
import cl.duoc.ms_citas_bbf.client.PacientesBffRestClient;
import cl.duoc.ms_citas_bbf.exception.CitaNotFoundException;
import cl.duoc.ms_citas_bbf.exception.ServicioNoDisponibleException;
import cl.duoc.ms_citas_bbf.model.dto.CitaDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaUpdateDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaConPacienteDTO;
import feign.FeignException;
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

    public CitaDTO obtenerCitaPorId(Long id) {
        try {
            return citasBsRestClient.obtenerCitaPorId(id);
        } catch (FeignException.NotFound e) {
            throw new CitaNotFoundException(id);
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("ms-citas-bs", e);
        }
    }

    public List<CitaConPacienteDTO> listarCitasConPacientes() {
        return citasBsRestClient.listarCitas().stream()
            .map(this::enriquecerCita)
            .collect(Collectors.toList());
    }

    public CitaDTO registrarCita(CitaDTO citaDTO) {
        try {
            return citasBsRestClient.registrarCita(citaDTO);
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("ms-citas-bs", e);
        }
    }

    public void eliminarCita (Long id) {
        try {
            citasBsRestClient.eliminarCita(id);
        } catch (FeignException.NotFound e) {
            throw new CitaNotFoundException(id);
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("ms-citas-bs", e);
        }
    }

    public CitaUpdateDTO actualizarCita (CitaUpdateDTO cita) {
        try {
            return citasBsRestClient.actualizarCita(cita);
        } catch (FeignException.NotFound e) {
            throw new CitaNotFoundException(cita.getId());
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("ms-citas-bs", e);
        }
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
