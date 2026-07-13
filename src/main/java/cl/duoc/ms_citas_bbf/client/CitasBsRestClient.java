package cl.duoc.ms_citas_bbf.client;

import cl.duoc.ms_citas_bbf.model.dto.CitaDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaUpdateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "citas-bs", url = "${citas-bs.url:http://localhost:8091/api/v1/citas}")
public interface CitasBsRestClient {

    @PostMapping
    CitaDTO registrarCita(@RequestBody CitaDTO citaDTO);

    @GetMapping
    List<CitaDTO> listarCitas();

    @DeleteMapping
    public void eliminarCita(Long id);

    @PutMapping
    public CitaUpdateDTO actualizarCita(@RequestBody CitaUpdateDTO cita);
}
