package co.edu.corhuila.rutasur.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Verifica que las dependencias entre capas respeten la arquitectura V1 de RutaSur.
 */
@AnalyzeClasses(packages = "co.edu.corhuila.rutasur")
class ArquitecturaCapasTest {

    /**
     * Impide que los controladores accedan directamente a la persistencia.
     */
    @ArchTest
    static final ArchRule controladores_no_deben_depender_de_repositorios_o_entidades = noClasses()
            .that().resideInAPackage("..controller..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..repository..", "..entity..")
            .allowEmptyShould(true);

    /**
     * Impide que los servicios dependan de adaptadores HTTP.
     */
    @ArchTest
    static final ArchRule servicios_no_deben_depender_de_controladores = noClasses()
            .that().resideInAPackage("..service..")
            .should().dependOnClassesThat()
            .resideInAPackage("..controller..")
            .allowEmptyShould(true);

    /**
     * Impide que la persistencia dependa de las capas superiores.
     */
    @ArchTest
    static final ArchRule repositorios_no_deben_depender_de_servicios_o_controladores = noClasses()
            .that().resideInAPackage("..repository..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..service..", "..controller..")
            .allowEmptyShould(true);

    /**
     * Mantiene las interfaces API independientes de implementación y persistencia.
     */
    @ArchTest
    static final ArchRule contratos_api_no_deben_depender_de_capas_internas = noClasses()
            .that().resideInAPackage("..api..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..service..", "..repository..", "..entity..")
            .allowEmptyShould(true);
}
