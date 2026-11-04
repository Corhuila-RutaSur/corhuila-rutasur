package co.edu.corhuila.rutasur.identidad;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Protege que el dominio de Identidad no dependa de adapters ni frameworks.
 */
@AnalyzeClasses(packages = "co.edu.corhuila.rutasur.identidad")
class ArquitecturaHexagonalTest {

    @ArchTest
    static final ArchRule dominioNoDependeDeInfraestructura = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..adapter..", "org.springframework..", "jakarta.persistence..");
}
