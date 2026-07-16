package cl.duoc.ms_citas_bbf.service;

import cl.duoc.ms_citas_bbf.client.CitasBsRestClient;
import cl.duoc.ms_citas_bbf.client.PacientesBffRestClient;
import cl.duoc.ms_citas_bbf.exception.CitaNotFoundException;
import cl.duoc.ms_citas_bbf.exception.ServicioNoDisponibleException;
import cl.duoc.ms_citas_bbf.model.dto.CitaConPacienteDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaDTO;
import cl.duoc.ms_citas_bbf.model.dto.CitaUpdateDTO;
import cl.duoc.ms_citas_bbf.model.dto.PacienteBffDto;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitasServiceTest {

    @Mock
    private CitasBsRestClient citasBsRestClient;

    @Mock
    private PacientesBffRestClient pacientesBffRestClient;

    @InjectMocks
    private CitasService citasService;

    private CitaDTO citaDTO;

    @BeforeEach
    void setUp() {
        citaDTO = new CitaDTO(1L, 10L, "2026-08-01", "10:00", "PENDIENTE");
    }

    @Test
    void listarCitas_delegaEnCitasBsRestClient() {
        when(citasBsRestClient.listarCitas()).thenReturn(List.of(citaDTO));

        List<CitaDTO> resultado = citasService.listarCitas();

        assertThat(resultado).containsExactly(citaDTO);
    }

    @Test
    void obtenerCitaPorId_retornaLaCita_cuandoExiste() {
        when(citasBsRestClient.obtenerCitaPorId(1L)).thenReturn(citaDTO);

        CitaDTO resultado = citasService.obtenerCitaPorId(1L);

        assertThat(resultado).isEqualTo(citaDTO);
    }

    @Test
    void obtenerCitaPorId_lanzaCitaNotFoundException_cuandoBsRetorna404() {
        when(citasBsRestClient.obtenerCitaPorId(99L)).thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> citasService.obtenerCitaPorId(99L))
                .isInstanceOf(CitaNotFoundException.class);
    }

    @Test
    void obtenerCitaPorId_lanzaServicioNoDisponible_cuandoFallaElServicioRemoto() {
        when(citasBsRestClient.obtenerCitaPorId(1L)).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> citasService.obtenerCitaPorId(1L))
                .isInstanceOf(ServicioNoDisponibleException.class);
    }

    @Test
    void registrarCita_retornaLaCitaCreada() {
        when(citasBsRestClient.registrarCita(citaDTO)).thenReturn(citaDTO);

        CitaDTO resultado = citasService.registrarCita(citaDTO);

        assertThat(resultado).isEqualTo(citaDTO);
    }

    @Test
    void registrarCita_lanzaServicioNoDisponible_cuandoFallaElServicioRemoto() {
        when(citasBsRestClient.registrarCita(citaDTO)).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> citasService.registrarCita(citaDTO))
                .isInstanceOf(ServicioNoDisponibleException.class);
    }

    @Test
    void eliminarCita_delegaEnCitasBsRestClient() {
        citasService.eliminarCita(1L);

        verify(citasBsRestClient).eliminarCita(1L);
    }

    @Test
    void eliminarCita_lanzaCitaNotFoundException_cuandoBsRetorna404() {
        org.mockito.Mockito.doThrow(mock(FeignException.NotFound.class)).when(citasBsRestClient).eliminarCita(99L);

        assertThatThrownBy(() -> citasService.eliminarCita(99L))
                .isInstanceOf(CitaNotFoundException.class);
    }

    @Test
    void actualizarCita_retornaLaCitaActualizada() {
        CitaUpdateDTO update = new CitaUpdateDTO(1L, 10L, "2026-08-02", "11:00", "CONFIRMADA");
        when(citasBsRestClient.actualizarCita(update)).thenReturn(update);

        CitaUpdateDTO resultado = citasService.actualizarCita(update);

        assertThat(resultado).isEqualTo(update);
    }

    @Test
    void actualizarCita_lanzaCitaNotFoundException_cuandoBsRetorna404() {
        CitaUpdateDTO update = new CitaUpdateDTO(99L, 10L, "2026-08-02", "11:00", "CONFIRMADA");
        when(citasBsRestClient.actualizarCita(update)).thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> citasService.actualizarCita(update))
                .isInstanceOf(CitaNotFoundException.class);
    }

    @Test
    void listarCitasConPacientes_enriqueceCadaCitaConSuPaciente() {
        PacienteBffDto paciente = new PacienteBffDto(10L, "11111111-1", "R1", "F1", "Juan", "Perez", "M", "1990-01-01", "Calle 1", "123456");
        when(citasBsRestClient.listarCitas()).thenReturn(List.of(citaDTO));
        when(pacientesBffRestClient.obtenerPaciente(10L)).thenReturn(paciente);

        List<CitaConPacienteDTO> resultado = citasService.listarCitasConPacientes();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(citaDTO.getId());
        assertThat(resultado.get(0).getPaciente()).isEqualTo(paciente);
    }

    @Test
    void listarCitasConPacientes_dejaElPacienteEnNull_siFallaElEnriquecimiento() {
        when(citasBsRestClient.listarCitas()).thenReturn(List.of(citaDTO));
        when(pacientesBffRestClient.obtenerPaciente(10L)).thenThrow(new RuntimeException("paciente no encontrado"));

        List<CitaConPacienteDTO> resultado = citasService.listarCitasConPacientes();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPaciente()).isNull();
    }

    @Test
    void obtenerCitaConPaciente_retornaNull_siLaCitaNoExiste() {
        when(citasBsRestClient.listarCitas()).thenReturn(List.of(citaDTO));

        CitaConPacienteDTO resultado = citasService.obtenerCitaConPaciente(999L);

        assertThat(resultado).isNull();
    }

    @Test
    void obtenerCitaConPaciente_retornaLaCitaEnriquecida_siExiste() {
        PacienteBffDto paciente = new PacienteBffDto(10L, "11111111-1", "R1", "F1", "Juan", "Perez", "M", "1990-01-01", "Calle 1", "123456");
        when(citasBsRestClient.listarCitas()).thenReturn(List.of(citaDTO));
        when(pacientesBffRestClient.obtenerPaciente(10L)).thenReturn(paciente);

        CitaConPacienteDTO resultado = citasService.obtenerCitaConPaciente(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getPaciente()).isEqualTo(paciente);
    }
}
