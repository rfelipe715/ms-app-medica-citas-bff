package cl.duoc.ms_citas_bbf.service;

import cl.duoc.ms_citas_bbf.client.CitasBsRestClient;
import cl.duoc.ms_citas_bbf.client.PacientesBffRestClient;
import cl.duoc.ms_citas_bbf.exception.CitaNotFoundException;
import cl.duoc.ms_citas_bbf.exception.ServicioNoDisponibleException;
import cl.duoc.ms_citas_bbf.model.dto.CitaDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaUpdateDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaConPacienteDTO;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitasService {

    private static final Logger log = LoggerFactory.getLogger(CitasService.class);

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
            log.warn("Cita id={} no encontrada en ms-citas-bs", id);
            throw new CitaNotFoundException(id);
        } catch (FeignException e) {
            log.error("ms-citas-bs no disponible al buscar cita id={}: {}", id, e.getMessage());
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
            CitaDTO registrada = citasBsRestClient.registrarCita(citaDTO);
            log.info("Cita registrada con id={}, pacienteId={}", registrada.getId(), registrada.getPacienteId());
            return registrada;
        } catch (FeignException e) {
            log.error("ms-citas-bs no disponible al registrar cita para pacienteId={}: {}", citaDTO.getPacienteId(), e.getMessage());
            throw new ServicioNoDisponibleException("ms-citas-bs", e);
        }
    }

    public void eliminarCita (Long id) {
        try {
            citasBsRestClient.eliminarCita(id);
            log.info("Cita id={} eliminada correctamente", id);
        } catch (FeignException.NotFound e) {
            log.warn("Intento de eliminar una cita inexistente, id={}", id);
            throw new CitaNotFoundException(id);
        } catch (FeignException e) {
            log.error("ms-citas-bs no disponible al eliminar cita id={}: {}", id, e.getMessage());
            throw new ServicioNoDisponibleException("ms-citas-bs", e);
        }
    }

    public CitaUpdateDTO actualizarCita (CitaUpdateDTO cita) {
        try {
            CitaUpdateDTO actualizada = citasBsRestClient.actualizarCita(cita);
            log.info("Cita id={} actualizada correctamente", cita.getId());
            return actualizada;
        } catch (FeignException.NotFound e) {
            log.warn("Intento de actualizar una cita inexistente, id={}", cita.getId());
            throw new CitaNotFoundException(cita.getId());
        } catch (FeignException e) {
            log.error("ms-citas-bs no disponible al actualizar cita id={}: {}", cita.getId(), e.getMessage());
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
            log.warn("No se pudo enriquecer la cita id={} con datos del paciente id={}: {}",
                    citaDTO.getId(), citaDTO.getPacienteId(), e.getMessage());
        }
        
        return conPaciente;
    }
}
