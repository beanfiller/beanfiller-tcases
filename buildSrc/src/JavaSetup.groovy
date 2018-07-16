import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.tasks.compile.JavaCompile


/**
 * gradle tricks for simple java projects
 */
static void configure(Project project) {

    configureJava(project)

    configureJacoco(project)

    JavaCheckersSetup.configure(project)

}

/**
 * defaults for any java project
 */
private static void configureJava(Project project) {

    // project.with() allows most native gradle syntax
    project.with() {
        apply plugin: 'java'

        // defines settings in IntelliJ
        sourceCompatibility = ToolVersions.jdk
        targetCompatibility = ToolVersions.jdk

        tasks.withType(JavaCompile) {
            options.encoding = 'UTF-8'
            options.compilerArgs << '-Xlint:all'
            // options.compilerArgs << '-Xlint:-processing'
            // options.compilerArgs << '-Xlint:-serial'
            // options.compilerArgs << '-Xlint:-cast'
            options.compilerArgs << '-Werror'
        }

        test {

            minHeapSize = "1024m"

            testLogging {
                showStandardStreams = true
                exceptionFormat "full" // default is "short"
            }
        }

        // restrict transitive dependencies for normal java projects.
        configurations { conf ->
            // do not allow using transitive dependencies (declare what you use)
            compile.transitive = false
            compileClasspath.transitive = false
            // allow transitive dependencies in tests (less risk for product)
            testCompile.transitive = true
            testCompileClasspath.transitive = true
        }

        // exclude ancient libraries
        configurations { conf ->
            // jackson2 has new group com.fasterxml.jackson. Old one sometimes needed by test libraries
            compileOnly.exclude group: 'org.codehaus.jackson'

            // hibernate-validator moved to group org.hibernate.validator
            compile.exclude group: 'org.hibernate', module: 'hibernate-validator'

            conf.all*.exclude module: 'mockito-all'
            // no log4j, we use logback
            conf.all*.exclude module: 'log4j'
            conf.all*.exclude module: 'slf4j-log4j12'

            // using jcl-over-slf4j
            conf.all*.exclude module: 'commons-logging'
            // using hamcrest-all instead
            conf.all*.exclude module: 'hamcrest-core'
            conf.all*.exclude module: 'hamcrest-library'
        }

        // define resolution strategy for dependencies
        configurations.all { conf ->
            conf.resolutionStrategy {
                // dependencyUpdates tasks cannot deal with forced versions
                if (gradle.startParameter.taskNames.contains('dependencyUpdates')) {
                    return
                }

                // ignore version conflicts in test dependencies, let gradle autohandle
                // fail eagerly on version conflict (includes transitive dependencies)
                // e.g. multiple different versions of the same dependency (group and name are equal)
                failOnVersionConflict()

                // cache dynamic versions for 10 minutes (these are versions like '2.4.+')
                cacheDynamicVersionsFor 10 * 60, 'seconds'
                // don't cache changing modules at all
                cacheChangingModulesFor 0, 'seconds'
                eachDependency { DependencyResolveDetails details ->
                    Resolver.overrideVersions(details)
                }

            }
        }

    }
}

/**
 * code coverage checks
 */
private static void configureJacoco(Project project) {

    // project.with() allows most native gradle syntax
    project.with() {

        apply plugin: 'com.palantir.jacoco-full-report'
        apply plugin: 'com.palantir.jacoco-coverage'

        // See https://github.com/palantir/gradle-jacoco-coverage
        jacocoCoverage {
            // Enforce minimum code coverage for every Java file.
            classThreshold 0.8, INSTRUCTION
            classThreshold 0.8, BRANCH
            classThreshold 0.0, INSTRUCTION, ~".*Exception"
            classThreshold 0.0, BRANCH, ~".*Exception"
        }

        test {
            finalizedBy(jacocoFullReport)
            finalizedBy(jacocoTestReport)

            // ensure jacoco data is deleted before tests
            doFirst {
                delete "${buildDir}/jacoco/test.exec"
            }

        }
    }

}