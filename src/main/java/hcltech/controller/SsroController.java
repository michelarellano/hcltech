package hcltech.controller;
import hcltech.modelo.Solicitud;
import hcltech.repositorio.SolicitudRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/ssro")
public class SsroController {
    @Autowired
    private SolicitudRepositorio solicitudRepositorio;
    @GetMapping("/solicitudes")
    public ResponseEntity<?> listarPendientes() {
        return ResponseEntity.ok(solicitudRepositorio.findByEstado(Solicitud.Estado.pendiente));
    }
    @PatchMapping("/solicitudes/{id}/decision")
    public ResponseEntity<?> decision(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        Solicitud solicitud = solicitudRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        String decision = body.get("decision");
        if (!decision.equals("aprobada") && !decision.equals("rechazada")) {
            return ResponseEntity.badRequest().body("Decision invalida");
        }
        solicitud.setEstado(Solicitud.Estado.valueOf(decision));
        solicitud.setComentarioSsro(body.get("comentario"));
        solicitudRepositorio.save(solicitud);
        return ResponseEntity.ok("Solicitud " + decision + " correctamente");
    }
}
