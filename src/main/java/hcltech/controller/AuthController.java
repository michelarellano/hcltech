package hcltech.controller;

import hcltech.modelo.Rol;
import hcltech.modelo.Usuario;
import hcltech.repositorio.RolRepositorio;
import hcltech.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RolRepositorio rolRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody Map<String, String> body) {
        if (usuarioRepositorio.findByCorreo(body.get("correo")).isPresent()) {
            return ResponseEntity.badRequest().body("El correo ya está registrado");
        }

        Rol rol = rolRepositorio.findByNombre(body.get("rol"));
        if (rol == null) {
            return ResponseEntity.badRequest().body("Rol no válido");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(body.get("nombre"));
        usuario.setCorreo(body.get("correo"));
        usuario.setContrasena(passwordEncoder.encode(body.get("contrasena")));
        usuario.setRol(rol);
        usuario.setCreatedAt(LocalDateTime.now());

        usuarioRepositorio.save(usuario);
        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(org.springframework.security.core.Authentication auth) {
        return ResponseEntity.ok(Map.of("usuario", auth.getName(), "rol", auth.getAuthorities()));
    }
}
