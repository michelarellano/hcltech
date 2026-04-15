package hcltech.repositorio;
import hcltech.modelo.Solicitud;
import hcltech.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SolicitudRepositorio extends JpaRepository<Solicitud, Integer> {
    List<Solicitud> findByUsuario(Usuario usuario);
    List<Solicitud> findByEstado(Solicitud.Estado estado);
}
