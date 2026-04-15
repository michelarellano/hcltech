package hcltech;

import hcltech.modelo.Solicitud;
import hcltech.modelo.Rol;
import hcltech.modelo.Usuario;
import hcltech.repositorio.RolRepositorio;
import hcltech.repositorio.SolicitudRepositorio;
import hcltech.repositorio.UsuarioRepositorio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HcltechApplicationTests {

    @Autowired
    private SolicitudRepositorio solicitudRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RolRepositorio rolRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void crearSolicitudExitosa() {
        Rol rol = rolRepositorio.findByNombre("EMPLEADO");
        Usuario usuario = usuarioRepositorio.findByCorreo("test@hcltech.com")
                .orElseGet(() -> {
                    Usuario u = new Usuario();
                    u.setNombre("Test");
                    u.setCorreo("test@hcltech.com");
                    u.setContrasena(passwordEncoder.encode("123456"));
                    u.setRol(rol);
                    u.setCreatedAt(LocalDateTime.now());
                    return usuarioRepositorio.save(u);
                });

        Solicitud solicitud = new Solicitud();
        solicitud.setUsuario(usuario);
        solicitud.setTipoHardware("Headset");
        solicitud.setJustificacion("Necesito headset para calls");
        solicitud.setUrgencia(Solicitud.Urgencia.alta);
        solicitud.setFechaSolicitud(LocalDateTime.now());

        Solicitud guardada = solicitudRepositorio.save(solicitud);

        assertNotNull(guardada.getId());
        assertEquals(Solicitud.Estado.pendiente, guardada.getEstado());
        assertEquals("Headset", guardada.getTipoHardware());
    }

    @Test
    void aprobarSolicitud() {
        Solicitud solicitud = solicitudRepositorio.findByEstado(Solicitud.Estado.pendiente)
                .stream().findFirst().orElse(null);

        if (solicitud != null) {
            solicitud.setEstado(Solicitud.Estado.aprobada);
            solicitud.setComentarioSsro("Aprobado");
            Solicitud actualizada = solicitudRepositorio.save(solicitud);
            assertEquals(Solicitud.Estado.aprobada, actualizada.getEstado());
        }
    }

    @Test
    void rechazarSolicitud() {
        Solicitud solicitud = new Solicitud();
        Rol rol = rolRepositorio.findByNombre("EMPLEADO");
        Usuario usuario = usuarioRepositorio.findByCorreo("test@hcltech.com")
                .orElseGet(() -> {
                    Usuario u = new Usuario();
                    u.setNombre("Test");
                    u.setCorreo("test2@hcltech.com");
                    u.setContrasena(passwordEncoder.encode("123456"));
                    u.setRol(rol);
                    u.setCreatedAt(LocalDateTime.now());
                    return usuarioRepositorio.save(u);
                });

        solicitud.setUsuario(usuario);
        solicitud.setTipoHardware("Teclado");
        solicitud.setJustificacion("Teclado dañado");
        solicitud.setUrgencia(Solicitud.Urgencia.media);
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud = solicitudRepositorio.save(solicitud);

        solicitud.setEstado(Solicitud.Estado.rechazada);
        solicitud.setComentarioSsro("No hay stock");
        Solicitud actualizada = solicitudRepositorio.save(solicitud);

        assertEquals(Solicitud.Estado.rechazada, actualizada.getEstado());
    }
}
