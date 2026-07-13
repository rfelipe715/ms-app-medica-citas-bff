package cl.duoc.ms_citas_bbf.controller;

import cl.duoc.ms_citas_bbf.client.CitasBsRestClient;
import cl.duoc.ms_citas_bbf.model.dto.CitaDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaUpdateDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaConPacienteDTO;
import cl.duoc.ms_citas_bbf.service.CitasService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/citas")
@Tag(name = "Citas", description = "Gestión de citas médicas del sistema hospitalario")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@SecurityRequirement(name = "bearerAuth")
@OpenAPIDefinition(servers = @Server(url = "${api-gateway.url:http://localhost:8080}"))
public class CitasController {

    @Autowired
    private CitasService citasService;

    @Operation(summary = "Agendar una nueva cita", description = "Crea una nueva cita médica, validando que el paciente exista.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cita agendada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud")
    })
    @PostMapping("/agendar")
    public ResponseEntity<CitaDTO> agendarCita(@RequestBody CitaDTO citaDTO) {
        try {
            CitaDTO cita = citasService.registrarCita(citaDTO);
            return new ResponseEntity<>(cita, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Listar todas las citas", description = "Retorna la lista completa de citas registradas en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de citas obtenida exitosamente")
    @GetMapping("/listar")
    public ResponseEntity<List<CitaDTO>> listarCitas() {
        try {
            List<CitaDTO> citas = citasService.listarCitas();
            return ResponseEntity.ok(citas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Listar citas con datos de paciente", description = "Retorna las citas enriquecidas con los datos del paciente asociado a cada una.")
    @ApiResponse(responseCode = "200", description = "Lista de citas con paciente obtenida exitosamente")
    @GetMapping("/listar/con-pacientes")
    public ResponseEntity<List<CitaConPacienteDTO>> listarCitasConPacientes() {
        try {
            List<CitaConPacienteDTO> citas = citasService.listarCitasConPacientes();
            return ResponseEntity.ok(citas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Buscar cita con datos de paciente", description = "Retorna una cita específica enriquecida con los datos del paciente asociado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cita encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una cita con el ID indicado")
    })
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

    @Operation(summary = "Eliminar una cita", description = "Elimina de forma permanente una cita del sistema, identificada por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cita eliminada exitosamente, sin contenido de respuesta"),
            @ApiResponse(responseCode = "404", description = "No existe una cita con el ID indicado")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarCita(@PathVariable Long id) {
        try {
            citasService.eliminarCita(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar una cita existente", description = "Actualiza los datos de una cita ya registrada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cita actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No existe una cita con el ID indicado")
    })
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
