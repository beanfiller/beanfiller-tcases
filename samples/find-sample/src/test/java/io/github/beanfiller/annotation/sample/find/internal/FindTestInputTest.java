package io.github.beanfiller.annotation.sample.find.internal;

import org.apache.commons.collections4.IteratorUtils;
import io.github.beanfiller.annotation.creator.AbstractTestInput;
import io.github.beanfiller.annotation.creator.FunctionTestsCreator;
import io.github.beanfiller.annotation.reader.AnnotatedFunctionDefReader;
import io.github.beanfiller.annotation.sample.find.FindTestInput;
import org.cornutum.tcases.FunctionInputDef;
import org.cornutum.tcases.FunctionTestDef;
import org.cornutum.tcases.IVarDef;
import org.cornutum.tcases.SystemTestDef;
import org.cornutum.tcases.Tcases;
import org.cornutum.tcases.TestCase;
import org.cornutum.tcases.generator.GeneratorOptions;
import org.cornutum.tcases.generator.GeneratorSet;
import org.cornutum.tcases.generator.TupleGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Comprehensive Test using the sample.FindTestInput class as SystemTestDefinition/FunctionDefinition
 */
public class FindTestInputTest {


    public static final String SYSTEM = "findSystem";
    private GeneratorSet genDef;
    private SystemTestDef baseDef;
    private GeneratorOptions options;

    @Before
    public void setUp() {
        genDef = GeneratorSet.basicGenerator();

        baseDef = null;
        options = new GeneratorOptions();
    }



    @Test
    public void testSystemDefFromAnnotations() {
        FunctionInputDef fun1Def = AnnotatedFunctionDefReader.withDefaultAnnotations().readFunctionInputDef(FindTestInput.class);
        assertThat(fun1Def.getName(), equalTo(FindTestInput.FUNCTION_NAME));

        List<IVarDef> varDefs = IteratorUtils.toList(fun1Def.getVarDefs());
        assertThat(varDefs.size(), equalTo(3));

        assertNotNull(fun1Def.findVarPath("filenameDefined"));
        assertNotNull(fun1Def.findVarPath("pattern.size"));
        assertNotNull(fun1Def.findVarPath("file.exists"));
    }


    @Test
    public void testTestDefFromAnnotations() {
        FunctionInputDef fun1Def = AnnotatedFunctionDefReader.withDefaultAnnotations().readFunctionInputDef(FindTestInput.class);
        /* generate testcases */

        FunctionTestDef testDef = Tcases.getTests(fun1Def, genDef, baseDef, options);
        assertThat(testDef.getName(), equalTo(FindTestInput.FUNCTION_NAME));
        List<TestCase> testCaseList = IteratorUtils.toList(testDef.getTestCases());
        // check total number
        assertThat(testCaseList.size(), equalTo(10));
        // check failure number
        assertThat(testCaseList.stream().filter(testCase -> testCase.getType() == TestCase.Type.FAILURE).count(), equalTo(5L));
        // Check id
        for (int i = 0; i < testCaseList.size(); i++) {
            assertThat(testCaseList.get(i).getId(), equalTo(i));
        }
    }


    @Test
    public void testInstanceCreation() {
        FunctionInputDef fun1Def = AnnotatedFunctionDefReader.withDefaultAnnotations().readFunctionInputDef(FindTestInput.class);
        /* generate testcases */

        FunctionTestDef testDef = Tcases.getTests(fun1Def, genDef, baseDef, options);
        assertThat(testDef.getName(), equalTo(FindTestInput.FUNCTION_NAME));
        List<TestCase> testCaseList = IteratorUtils.toList(testDef.getTestCases());

        /* generate test instances */
        List<FindTestInput> findList = new FunctionTestsCreator<>(FindTestInput.class)
                .createDefs();

        assertThat(findList.size(), equalTo(testCaseList.size()));
        // check failure number
        List<FindTestInput> failures = findList.stream().filter(AbstractTestInput::isFailure).collect(Collectors.toList());
        assertEquals(failures.size(), 5);
        failures.forEach(FindTestInput -> {
            assertTrue(FindTestInput.having().getVarBindingAnnotationKeys().hasNext());
        });

        // Check id
        for (int i = 0; i < findList.size(); i++) {
            assertThat(findList.get(i).getTestCaseId(), equalTo(i));
            System.out.println(findList.get(i));
        }
    }


    @Test
    public void testTestDefFromAnnotations2Tupel() {
        FunctionInputDef fun1Def = AnnotatedFunctionDefReader.withDefaultAnnotations().readFunctionInputDef(FindTestInput.class);
        /* generate testcases */
        genDef.addGenerator(FindTestInput.FUNCTION_NAME, new TupleGenerator(2));
        FunctionTestDef testDef = Tcases.getTests(fun1Def, genDef, baseDef, options);
        assertThat(testDef.getName(), equalTo(FindTestInput.FUNCTION_NAME));
        List<TestCase> testCaseList = IteratorUtils.toList(testDef.getTestCases());

        // check total number
        assertThat(testCaseList.size(), equalTo(22));
        // check failure number
        assertThat(testCaseList.stream().filter(testCase -> testCase.getType() == TestCase.Type.FAILURE).count(), equalTo(5L));
        // Check id
        for (int i = 0; i < testCaseList.size(); i++) {
            assertThat(testCaseList.get(i).getId(), equalTo(i));
        }
    }

}