package io.github.beanfiller.annotation.sample;


import io.github.beanfiller.annotation.creator.FunctionTestsCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.github.beanfiller.annotation.sample.TriangleProblemTest.TriangleCategory.DEGENERATE;
import static io.github.beanfiller.annotation.sample.TriangleProblemTest.TriangleCategory.EQUILATERAL;
import static io.github.beanfiller.annotation.sample.TriangleProblemTest.TriangleCategory.INVALID;
import static io.github.beanfiller.annotation.sample.TriangleProblemTest.TriangleCategory.ISOSCELES;
import static io.github.beanfiller.annotation.sample.TriangleProblemTest.TriangleCategory.SCALENE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class TriangleProblemTest {

    private static final Logger logger = LoggerFactory.getLogger(TriangleProblemTest.class);

    enum TriangleCategory {
        ISOSCELES,
        EQUILATERAL,
        SCALENE,
        DEGENERATE,
        INVALID
    }

    /**
     * The method under test, categorizes a Triangle.
     * @param a
     * @param b
     * @param c
     * @return
     */
    public static TriangleCategory classifyTriangle(double a, double b, double c) {
        if (a <= 0 || b <= 0 || c <= 0) return INVALID; // added test
        if (a == b && b == c) return EQUILATERAL;
        if (a == b + c || c == b + a || b == a + c) return DEGENERATE;
        if (a >= b + c || c >= b + a || b >= a + c) return INVALID;
        if (b == c || a == b || c == a) return ISOSCELES;
        return SCALENE;
    }


    @DataProvider(name = "triangles")
    public static Object[][] testCases() {
        return new FunctionTestsCreator<>(TriangleProblemTestInput.class)
                // to create this file from scratch, use SystemTestDefWriter
                .baseResource(TriangleProblemTestInput.class, "Find-Test.xml")
                .tupleGenerator(2)
                .createDefs() // the magic happens here
                .stream()
                // map abstract input to concrete values (here to improve test label)
                .map(triangleTestInput -> new Object[]{triangleTestInput})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "triangles")
    public void testClassifyTriangle(TriangleProblemTestInput input) {
        double[] values = getDoubleValuesForTestCase(input);

        logger.debug(input.toString());
        double a, b, c;

        // Permutate a, b, and c positions. TODO: Do not test duplicate values.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                a = values[i];
                int bPos = (i + j + 1) % 3;
                b = values[bPos];
                System.out.println(i + " " + bPos + " " + (3 - (bPos + i)));
                c = values[3 - (bPos + i)];
                TriangleCategory result = classifyTriangle(a, b, c);
                logger.debug(a + " " + b + " " + c + " " + result.name());
                TriangleCategory expected = getTriangleExpectedCategory(input);
                assertNotNull(result);
                assertEquals(result, expected);
            }
        }
    }

    private double[] getDoubleValuesForTestCase(TriangleProblemTestInput input) {
        // TODO: randomize values based on seed?
        double[] values = new double[3];
        switch (input.firstSegmentLength) {
            case NEGATIVE:
                values[0] = -1;
                break;
            case ZERO:
                values[0] = 0;
                break;
            case POSITIVE:
                values[0] = 1;
                break;
        }

        switch (input.secondSegmentLength) {
            case NEGATIVE:
                values[1] = -1;
                break;
            case ZERO:
                values[1] = 0;
                break;
            case SAME_AS_FIRST:
                values[1] = values[0];
                break;
            case LARGER_THAN_FIRST:
                values[1] = values[0] + 1;
                break;
        }

        switch (input.thirdSegmentLength) {
            case NEGATIVE:
                values[2] = -1;
                break;
            case TOO_SHORT:
                values[2] = Math.abs((values[0] - values[1]) - 0.5);
                break;
            case SHORTER_THAN_SECOND:
                values[2] = values[1] - 0.5;
                break;
            case SAME_AS_SECOND:
                values[2] = values[1];
                break;
            case DIFFERENCE_BETWEEN_FIRST_TWO:
                values[2] = Math.abs(values[0] - values[1]);
                break;
        }
        return values;
    }

    /**
     * Oracle calculating the expected test output without
     * knowledge of the actual double values used.
     * @param input
     * @return
     */
    private TriangleCategory getTriangleExpectedCategory(TriangleProblemTestInput input) {
        TriangleCategory expected = null;
        switch (input.firstSegmentLength) {
            case NEGATIVE:
                expected = INVALID;
                break;
            case ZERO:
                expected = INVALID;
                break;
            case POSITIVE:
                switch (input.secondSegmentLength) {
                    case NEGATIVE:
                        expected = INVALID;
                        break;
                    case ZERO:
                        expected = INVALID;
                        break;
                    case SAME_AS_FIRST:
                        switch (input.thirdSegmentLength) {
                            case NEGATIVE:
                                expected = INVALID;
                                break;
                            case TOO_SHORT:
                                expected = INVALID;
                                break;
                            case SHORTER_THAN_SECOND:
                                expected = SCALENE;
                                break;
                            case SAME_AS_SECOND:
                                expected = EQUILATERAL;
                                break;
                            case DIFFERENCE_BETWEEN_FIRST_TWO:
                                expected = DEGENERATE;
                                break;
                        }
                        break;
                    case LARGER_THAN_FIRST:
                        switch (input.thirdSegmentLength) {
                            case NEGATIVE:
                                expected = INVALID;
                                break;
                            case TOO_SHORT:
                                expected = INVALID;
                                break;
                            case SHORTER_THAN_SECOND:
                                expected = SCALENE;
                                break;
                            case SAME_AS_SECOND:
                                expected = ISOSCELES;
                                break;
                            case DIFFERENCE_BETWEEN_FIRST_TWO:
                                expected = DEGENERATE;
                                break;
                        }
                        break;
                }
                break;
        }
        return expected;
    }

    // fallback for IDE not running TestNG
    public static void main(String[] args) {
        TriangleProblemTest instance = new TriangleProblemTest();
        for (Object[] input : testCases()) {
            instance.testClassifyTriangle((TriangleProblemTestInput) input[0]);
        }
    }
}
