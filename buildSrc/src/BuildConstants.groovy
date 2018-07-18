import org.gradle.api.artifacts.DependencyResolveDetails


/**
 * Versions for build tool dependencies
 */
// probably has to be first class in this file for buildSrc tests to work
interface ToolVersions {
    static jdk = '1.8'
    static checkstyle = '8.10.1'
    static pmd = '6.4.0'
    static spotbugsPlugin = '1.6.0'
    static spotbugs = '3.1.6'
    static errorProne = '2.3.1'
    static jacocoPlugin = '0.4.0'
    static errorPronePlugin = '0.0.14'
    static pitestPlugin = '1.3.0'
}

interface Versions {

    static jsr305 = '3.0.2'

    static logback = '1.1.1'
    static slf4j = '1.7.25'

    static junit4 = '4.12'
    static junit5 = '5.2.0'
    static testng = '6.14.3'
    static mockito = '2.21.0'
    static powermock = '1.7.4'
    static hamcrest = '1.3'
}


interface Libraries {

    static commons_lang3 = "org.apache.commons:commons-lang3:3.7"
    static commons_coll4 = "org.apache.commons:commons-collections4:4.1"
    static commons_io = "commons-io:commons-io:2.4"

    static tcases = "org.cornutum.tcases:tcases-lib:2.0.0"

    static jsr305 = "com.google.code.findbugs:jsr305:${Versions.jsr305}"
    static spotbugsAnnotations = "com.github.spotbugs:spotbugs-annotations:${ToolVersions.spotbugs}"

    static slf4j_api = "org.slf4j:slf4j-api:${Versions.slf4j}"
    static jcl_over_slf4j = "org.slf4j:jcl-over-slf4j:${Versions.slf4j}"
    static log4j_over_slf4j = "org.slf4j:log4j-over-slf4j:${Versions.slf4j}"
    static logback = "ch.qos.logback:logback-classic:${Versions.logback}"

    // test dependencies
    static junit4 = "junit:junit:${Versions.junit4}"
    static junit5 = "org.junit.jupiter:junit-jupiter-api:${Versions.junit5}"
    static junit5params = "org.junit.jupiter:junit-jupiter-params:${Versions.junit5}"
    static assertj_core = "org.assertj:assertj-core:3.10.0"
    static mockito = "org.mockito:mockito-core:${Versions.mockito}"
    static hamcrest = "org.hamcrest:hamcrest-all:${Versions.hamcrest}"
    static powermock = "org.powermock:powermock-module-junit4:${Versions.powermock}"
    static powermock_mockito2 = "org.powermock:powermock-api-mockito2:${Versions.powermock}"

}

interface LibraryGroups {
    static unit_test = [
            Libraries.junit4,
            Libraries.hamcrest,
            Libraries.assertj_core,
            Libraries.mockito,
            Libraries.powermock,
            Libraries.powermock_mockito2
    ]
}


class Predefined {
    /**
     * Usage:
     * dependencies Predefined.commons
     * dependencies {*     // more dependencies ...
     *}*/
    static Closure commons = {
        compile Libraries.commons_lang3
        compile Libraries.commons_coll4

        // provides javax.annotationsNonnull and CheckForNull, no runtime dependency
        compileOnly Libraries.jsr305
        compileOnly Libraries.spotbugsAnnotations
        testCompileOnly Libraries.jsr305
        testCompileOnly Libraries.spotbugsAnnotations

        compile Libraries.slf4j_api

        runtime Libraries.jcl_over_slf4j
        runtime Libraries.log4j_over_slf4j
        runtime Libraries.logback

        // test dependencies
        testCompile LibraryGroups.unit_test
    }

}


class Resolver {
    static overrideVersions(DependencyResolveDetails details) {
        // for jar groups released with same version, this approach is easier and safer
        if (details.requested.group == 'org.mockito') {
            details.useVersion Versions.mockito
        } else if (details.requested.group == 'org.slf4j') {
            details.useVersion Versions.slf4j
        }

        // Manually resolve conflicts
        [
                // commons
                Libraries.commons_lang3,
                Libraries.commons_io,
                Libraries.junit4,
                "org.junit.platform:junit-platform-commons:1.2.0",
                "org.opentest4j:opentest4j:1.1.0",
                Libraries.jsr305,
                'org.objenesis:objenesis:2.6',
                'org.checkerframework:checker-qual:2.5.0',
                "com.google.errorprone:error_prone_annotations:errorProne:${ToolVersions.errorProne}"
        ].each { fixedDep ->
            if (fixedDep.startsWith("$details.requested.group:$details.requested.name")) {
                // in contrast to force, this allows using gradle dependencyUpdates task
                details.useVersion fixedDep.substring(fixedDep.lastIndexOf(':') + 1)
            }
        }
    }
}
