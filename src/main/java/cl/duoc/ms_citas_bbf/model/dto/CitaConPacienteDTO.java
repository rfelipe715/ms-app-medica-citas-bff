package cl.duoc.ms_citas_bbf.model.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CitaConPacienteDTO {

    private Long id;
    private Long pacienteId;
    private String fecha;
    private String hora;
    private String estado;
    
    // Datos relacionados
    private PacienteBffDto paciente;

}
