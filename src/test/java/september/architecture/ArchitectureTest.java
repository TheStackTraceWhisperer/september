package september.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.*;

/**
 * This class contains architectural tests for the project, ensuring that
 * the defined structural rules are not violated.
 */
@AnalyzeClasses(packages = "september", importOptions = { ImportOption.DoNotIncludeTests.class })
public class ArchitectureTest {

    // ============== Layer Dependencies ============== //

    @ArchTest
    public static final ArchRule engine_should_not_depend_on_game =
        noClasses()
            .that().resideInAPackage("september.engine..")
            .should().dependOnClassesThat().resideInAPackage("september.game..")
            .as("The engine package must be self-contained and must not depend on the game package.");

    @ArchTest
    public static final ArchRule game_should_not_depend_on_lwjgl =
        noClasses()
            .that().resideInAPackage("september.game..")
            .should().dependOnClassesThat().resideInAPackage("org.lwjgl..")
            .as("The game package must not depend directly on LWJGL. Use engine abstractions instead.");

    // ============== General Coding Rules ============== //

    @ArchTest
    public static final ArchRule no_field_injection = NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    @ArchTest
    public static final ArchRule no_java_util_logging = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    @ArchTest
    public static final ArchRule no_standard_streams = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    public static final ArchRule no_deprecated_apis = DEPRECATED_API_SHOULD_NOT_BE_USED;

    // ============== Forbidden API Rules ============== //

    @ArchTest
    public static final ArchRule no_internal_jdk_apis =
        noClasses()
            .should().dependOnClassesThat().resideInAnyPackage("sun..", "com.sun..")
            .as("Should not use internal JDK APIs");

}
