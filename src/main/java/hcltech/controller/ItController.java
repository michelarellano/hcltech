package hcltech.controller;
import hcltech.modelo.Solicitud;
import hcltech.repositorio.SolicitudRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
@RestController
@RequestMapping("/api/it")
public class ItController {
    @Autowired
    private SolicitudRepositorio solicitudRepositorio;
    @GetMapping("/solicitudes")
    public ResponseEntity<?> listarAprobadas() {
        return ResponseEntity.ok(solicitudRepositorio.findByEstado(Solicitud.Estado.aprobada));
    }
    @PatchMapping("/solicitudes/{id}/entrega")
    public ResponseEntity<?> registrarEntrega(@PathVariable Integer id) {
        Solicitud solicitud = solicitudRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        if (!solicitud.getEstado().equals(Solicitud.Estado.aprobada)) {
            return ResponseEntity.badRequest().body("La solicitud no está aprobada");
        }
        solicitud.setEstado(Solicitud.Estado.entregada);
        solicitud.setFechaEntrega(LocalDateTime.now());
        solicitudRepositorio.save(solicitud);
        return ResponseEntity.ok("Hardware entregado correctamente");
    }
}
