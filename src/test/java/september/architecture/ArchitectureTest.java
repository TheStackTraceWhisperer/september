package september.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * This class contains architectural tests for the project, ensuring that
 * the defined structural rules are not violated.
 */
@AnalyzeClasses(packages = "september")
public class ArchitectureTest {

    @ArchTest
    public static final ArchRule engine_should_not_depend_on_game =
        noClasses()
            .that().resideInAPackage("september.engine..")
            .should().dependOnClassesThat().resideInAPackage("september.game..")
            .as("The engine package must be self-contained and must not depend on the game package.");

}
