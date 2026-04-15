package hcltech.controller;
import hcltech.modelo.Solicitud;
import hcltech.modelo.Usuario;
import hcltech.repositorio.SolicitudRepositorio;
import hcltech.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {
    @Autowired
    private SolicitudRepositorio solicitudRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, String> body, Authentication auth) {
        Usuario usuario = usuarioRepositorio.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Solicitud solicitud = new Solicitud();
        solicitud.setUsuario(usuario);
        solicitud.setTipoHardware(body.get("tipoHardware"));
        solicitud.setJustificacion(body.get("justificacion"));
        solicitud.setUrgencia(Solicitud.Urgencia.valueOf(body.get("urgencia")));
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitudRepositorio.save(solicitud);
        return ResponseEntity.status(201).body("Solicitud creada correctamente");
    }
    @GetMapping
    public ResponseEntity<?> listar(Authentication auth) {
        Usuario usuario = usuarioRepositorio.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(solicitudRepositorio.findByUsuario(usuario));
    }
}
