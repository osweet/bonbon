package one.genchev.bonbon;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("one.genchev.bonbon");

        noClasses()
            .that()
            .resideInAnyPackage("one.genchev.bonbon.service..")
            .or()
            .resideInAnyPackage("one.genchev.bonbon.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..one.genchev.bonbon.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
