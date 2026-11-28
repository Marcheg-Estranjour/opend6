package fr.opend6;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchitectureTest {

    private static final JavaClasses CLASSES = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("fr.opend6");

    @Test
    void domain_depends_on_no_framework_and_no_other_layer() {
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.springframework..", "jakarta.persistence..",
                "..api..", "..application..", "..infrastructure..")
            .check(CLASSES);
    }

    @Test
    void api_never_reaches_infrastructure_directly() {
        noClasses().that().resideInAPackage("..api..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
            .check(CLASSES);
    }

    @Test
    void features_have_no_cycles() {
        slices().matching("fr.opend6.(*)..").should().beFreeOfCycles().check(CLASSES);
    }
}