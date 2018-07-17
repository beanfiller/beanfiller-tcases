import org.gradle.api.Project


/**
 * Static linters for Java projects
 */
static void configure(Project project) {
    project.with() {
        // not a dependency, just ordering constraint
        check.mustRunAfter(clean)
    }

    configureCheckstyle(project)
    configurePMD(project)
    configureSpotbugs(project)
}


private static configureSpotbugs(Project project) {
    project.with() {

        apply plugin: 'com.github.spotbugs'

        // use with -Pxmlreports for reports in xml that Jenkins can read and check
        def xmlreports = project.hasProperty('xmlreports')
        // use with -PuseSpotbugs, very slow so disabled by default
        def useSpotbugs = project.hasProperty('useSpotbugs')

        // care about test results first
        spotbugsMain.mustRunAfter(test)
        spotbugsTest.mustRunAfter(test)
        spotbugs {
            toolVersion = ToolVersions.spotbugs
            effort = 'max'
            // low means all issues are reported
            reportLevel = 'low'
            ignoreFailures = false
            excludeFilter = new File("${project.rootDir}/buildSrc/linters/spotbugs/excludeFilter.xml")
            sourceSets = [sourceSets.main, sourceSets.test]
        }
        spotbugsMain {
            enabled = useSpotbugs
            reports {
                xml.enabled = xmlreports
                html.enabled = !xmlreports
            }
        }
        spotbugsTest {
            enabled = useSpotbugs
            reports {
                xml.enabled = xmlreports
                html.enabled = !xmlreports
            }
        }
    }
}


private static void configurePMD(Project project) {

    // project.with() allows most native gradle syntax
    project.with() {
        /**
         * Tasks collecting the reports from java subprojects.
         */

        apply plugin: 'pmd'

        task('displayPmdReport', {
            setGroup 'check'
            setDescription 'parse the result of pmd and display in command line'

            // show errors on command line to raise awareness
            doLast {
                def parser = new XmlSlurper(false, false)
                parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                parser.setFeature("http://xml.org/sax/features/namespaces", false)

                File pmdFile = file("${project.buildDir}/reports/pmd/main.xml")
                if (pmdFile.exists()) {
                    def pmdXml = parser.parse(pmdFile)
                    pmdXml.file.each({ fileNode ->
                        println("PMD of $project.name:" + fileNode.@name.text() + ':')
                        fileNode.violation.each({ violationNode ->
                            println("  ${violationNode.@beginline}-${violationNode.@endline}  " + violationNode.text().trim())
                        })
                    })
                }

            }

        })

        pmdMain.mustRunAfter(test)

        // exclude pmd using -x pmdMain
        pmd {
            toolVersion = ToolVersions.pmd
            sourceSets = [sourceSets.main]
            ruleSets = [] // else all base rulesets included
            ruleSetFiles = files("${project.rootDir}/buildSrc/linters/pmdRuleSet.xml")
            ignoreFailures = false
        }

        check.dependsOn(displayPmdReport)
        pmdMain.finalizedBy(displayPmdReport)
    }
}


private static void configureCheckstyle(Project project) {

    // project.with() allows most native gradle syntax
    project.with() {
        /**
         * Tasks collecting the reports from java subprojects.
         */

        apply plugin: 'checkstyle'

        checkstyleMain.mustRunAfter(test)
        checkstyleTest.mustRunAfter(test)

        String configFileDir = "${project.rootDir}/buildSrc/linters"

        // exclude checkstyle using -x checkstyleMain
        checkstyleMain {
            configProperties = ['basedir': "${configFileDir}/checkstyle"]
            ignoreFailures = false
            configFile = new File(configFileDir, 'checkstyle/checkstyle.xml')
        }
        checkstyleTest {
            configProperties = ['basedir': "${configFileDir}/checkstyle"]
            ignoreFailures = false
            configFile = new File(configFileDir, 'checkstyle/checkstyle-test.xml')
        }

        checkstyle {
            toolVersion = ToolVersions.checkstyle
        }
    }
}
