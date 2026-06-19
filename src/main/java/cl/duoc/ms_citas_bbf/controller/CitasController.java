package cl.duoc.ms_citas_bbf.controller;

import cl.duoc.ms_citas_bbf.client.CitasBsRestClient;
import cl.duoc.ms_citas_bbf.model.dto.CitaDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaUpdateDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaConPacienteDTO;
import cl.duoc.ms_citas_bbf.service.CitasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/citas")
public class CitasController {

    @Autowired
    private CitasService citasService;

    @PostMapping("/agendar")
    public CitaDTO agendarCita(@RequestBody CitaDTO citaDTO) {
        return citasService.registrarCita(citaDTO);
    }

    @GetMapping("/listar")
    public List<CitaDTO> listarCitas() {
        return citasService.listarCitas();
    }

    @GetMapping("/listar/con-pacientes")
    public List<CitaConPacienteDTO> listarCitasConPacientes() {
        return citasService.listarCitasConPacientes();
    }

    @GetMapping("/{id}/con-paciente")
    public ResponseEntity<CitaConPacienteDTO> obtenerCitaConPaciente(@PathVariable Long id) {
        CitaConPacienteDTO cita = citasService.obtenerCitaConPaciente(id);
        if (cita != null) {
            return ResponseEntity.ok(cita);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarCita (@RequestParam Long id) {

        citasService.eliminarCita(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/actualizar")
    public ResponseEntity<CitaUpdateDTO> actualizarCita (@RequestBody CitaUpdateDTO cita) {
        return ResponseEntity.ok(citasService.actualizarCita(cita));
    }
}
