package io.github.beanfiller.annotation.sample.find;

import io.github.beanfiller.annotation.creator.OutputAnnotationContainer;

import java.util.Iterator;

/**
 * While the FindTestInput defines categories of tests,
 * this class defines actual inputs and an oracle of expected outputs.
 * Generally computing expected outputs without using the SUT is
 * possible when the additional abstract information is sufficient.
 * <p>
 * Note this seems to break the "Test should be dumb" principle here, though
 * how that principle could better be implemented with 1000s
 * of tested combinations is unclear.
 */
class TestInputs {
    final String filename;
    final String searchFilename;
    final String fileContent;
    final String resultContent;
    final String pattern;
    final FindTestInput findFunction;

    /**
     * create actual test values from abstract test case categories
     */
    TestInputs(FindTestInput findFunction) {
        // TODO: This method is so complex that it needs a unittest itself
        this.findFunction = findFunction;
        filename = "exist";
        searchFilename = findFunction.filenameDefined ? (findFunction.file.exists ? "exist" : "notexist") : null;

        String tempPattern = "";
        if (findFunction.pattern.size != null) {
            switch (findFunction.pattern.size) {
                case NONE:
                    tempPattern = "";
                    break;
                case ONE:
                    tempPattern = "x"; // TODO randomize with seed
                    if (findFunction.pattern.quoted == FindTestInput.Pattern.QuotedType.UNTERMINATED) {
                        tempPattern = "\"";
                    }
                    break;
                case MANY:
                    tempPattern = "foo"; // TODO randomize with seed
                    if (findFunction.pattern.blanks == FindTestInput.CardinalityZeroToN.ONE) {
                        tempPattern += " ";
                    }
                    if (findFunction.pattern.blanks == FindTestInput.CardinalityZeroToN.MANY) {
                        tempPattern += "x ";
                    }

                    if (findFunction.pattern.quoted == FindTestInput.Pattern.QuotedType.UNTERMINATED) {
                        tempPattern = '"' + tempPattern;
                    }
                    if (findFunction.pattern.quoted == FindTestInput.Pattern.QuotedType.YES) {
                        tempPattern = '"' + tempPattern + '"';
                    }
                    break;
            }
        }
        pattern = tempPattern;

        if (!findFunction.file.contents.linesLongerThanPattern) {
            fileContent = "\n\n\n";
            resultContent = "";
            return;
        }

        StringBuilder contentBuilder = new StringBuilder();
        StringBuilder expectResultBuilder = new StringBuilder();
        contentBuilder.append("aaaaaaaaaaaaaaaaaaaaaaaaaaa\n");

        String patternLine = "";

        switch (findFunction.file.contents.patternsInLine) {
            case ONE:
                patternLine = " yy " + pattern + " xx ";
                break;
            case MANY:
                patternLine = pattern + " xx " + pattern;
                break;
            default:
                throw new IllegalStateException("unmapped enum field " + findFunction.file.contents.patternsInLine);
        }

        switch (findFunction.file.contents.patterns) {
            case NONE:
                break;
            case MANY:
                for (int i = 0; i < 3; i++) {
                    contentBuilder.append(patternLine).append('\n');
                    expectResultBuilder.append(patternLine).append('\n');
                }
                break;
            case ONE:
                contentBuilder.append(patternLine).append('\n');
                expectResultBuilder.append(patternLine).append('\n');
                break;
        }

        fileContent = contentBuilder.toString();
        resultContent = expectResultBuilder.toString();
    }

    private static String getAnnotations(OutputAnnotationContainer testCaseAnnotation) {
        StringBuilder result = new StringBuilder();
        Iterator<String> keys = testCaseAnnotation.getVarBindingAnnotationKeys();
        while (keys.hasNext()) {
            result.append(testCaseAnnotation.getVarBindingAnnotation(keys.next()));
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return (findFunction.isFailure() ? "Fail - " + getAnnotations(findFunction.having()) + ':' : "")
                + '\'' + pattern + '\''
                + ' ' + findFunction;
    }

}
