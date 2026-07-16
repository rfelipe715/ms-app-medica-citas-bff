package cl.duoc.ms_citas_bbf.exception;

public class CitaNotFoundException extends RuntimeException {

    public CitaNotFoundException(Long id) {
        super("Cita no encontrada con el id: " + id);
    }
}
