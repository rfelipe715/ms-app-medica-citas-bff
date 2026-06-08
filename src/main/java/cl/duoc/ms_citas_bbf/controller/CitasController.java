package cl.duoc.ms_citas_bbf.controller;

import cl.duoc.ms_citas_bbf.client.CitasBsRestClient;
import cl.duoc.ms_citas_bbf.model.dto.CitaDTO;
import cl.duoc.ms_citas_bbf.service.CitasService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
