package hcltech.repositorio;

import hcltech.modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepositorio extends JpaRepository<Rol, Integer> {
    Rol findByNombre(String nombre);
}