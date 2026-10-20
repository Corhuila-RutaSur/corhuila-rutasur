package co.edu.corhuila.rutasur.dto.request;
import jakarta.validation.constraints.*; import java.math.BigDecimal;
public record ProgramacionServicioRequest(@NotNull @Positive Long servicioExperienciaId,@NotNull @DecimalMin("0.00") BigDecimal precioUnitario,@Positive Integer cuposTotales){}
