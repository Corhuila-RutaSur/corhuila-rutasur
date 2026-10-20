package co.edu.corhuila.rutasur.dto.response;
import java.util.List;
/** Par de tokens emitido tras login o renovación. */ public record TokenResponse(String accessToken, String refreshToken, String tipoToken, long expiraEnSegundos, List<String> roles) { }
