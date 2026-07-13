package cl.duoc.ms_citas_bbf.client;

import cl.duoc.ms_citas_bbf.model.dto.PacienteBffDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "pacientes-bff", url = "${pacientes-bff.url:http://localhost:8081/api/v1/pacientes}")
public interface PacientesBffRestClient {

    @GetMapping("/{id}")
    PacienteBffDto obtenerPaciente(@PathVariable Long id);

}
