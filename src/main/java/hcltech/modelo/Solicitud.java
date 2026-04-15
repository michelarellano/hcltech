package hcltech.modelo;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "solicitudes")
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @Column(name = "tipo_hardware", nullable = false)
    private String tipoHardware;
    @Column(nullable = false)
    private String justificacion;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Urgencia urgencia;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.pendiente;
    @Column(name = "comentario_ssro")
    private String comentarioSsro;
    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;
    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;
    public enum Urgencia { baja, media, alta }
    public enum Estado { pendiente, aprobada, rechazada, entregada }
}
