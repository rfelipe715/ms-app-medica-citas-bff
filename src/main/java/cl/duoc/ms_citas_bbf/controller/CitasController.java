package cl.duoc.ms_citas_bbf.controller;

import cl.duoc.ms_citas_bbf.client.CitasBsRestClient;
import cl.duoc.ms_citas_bbf.model.dto.CitaDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaUpdateDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaConPacienteDTO;
import cl.duoc.ms_citas_bbf.service.CitasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/citas")
public class CitasController {

    @Autowired
    private CitasService citasService;

    @PostMapping("/agendar")
    public ResponseEntity<CitaDTO> agendarCita(@RequestBody CitaDTO citaDTO) {
        try {
            CitaDTO cita = citasService.registrarCita(citaDTO);
            return new ResponseEntity<>(cita, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<CitaDTO>> listarCitas() {
        try {
            List<CitaDTO> citas = citasService.listarCitas();
            return ResponseEntity.ok(citas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/listar/con-pacientes")
    public ResponseEntity<List<CitaConPacienteDTO>> listarCitasConPacientes() {
        try {
            List<CitaConPacienteDTO> citas = citasService.listarCitasConPacientes();
            return ResponseEntity.ok(citas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/con-paciente")
    public ResponseEntity<CitaConPacienteDTO> obtenerCitaConPaciente(@PathVariable Long id) {
        try {
            CitaConPacienteDTO cita = citasService.obtenerCitaConPaciente(id);
            if (cita != null) {
                return ResponseEntity.ok(cita);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarCita(@PathVariable Long id) {
        try {
            citasService.eliminarCita(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<CitaUpdateDTO> actualizarCita(@RequestBody CitaUpdateDTO cita) {
        try {
            CitaUpdateDTO citaActualizada = citasService.actualizarCita(cita);
            return ResponseEntity.ok(citaActualizada);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
