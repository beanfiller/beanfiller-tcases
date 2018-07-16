# Beanfiller-Tcases

---

**This project is in early alpha stage, expect bugs, missing features, API changes**

---
[![Build Status](https://travis-ci.org/beanfiller/beanfiller-tcases.svg?branch=master)](https://travis-ci.org/beanfiller/beanfiller-tcases}) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![HitCount](http://hits.dwyl.com/beanfiller/beanfiller-tcases.svg)](http://hits.dwyl.com/beanfiller/beanfiller-tcases)

An annotation and reflections based java-library to fill Java beans with values according to combinatorial testing principles.
Combinatorial testing helps to run a useful small subset of tests where full coverage would require too many tests.
This is useful for acceptance-testing, integration-testing, and unit-testing of functions with high complexity.

## Short introduction

```java
public class EmojiTestInput {

    @Var(value = {@Value("^"), @Value("°"), @Value("ಠ"), @Value("≖"), @Value("•"),
           @Value("ˇ"), @Value("˘"), @Value("ᴗ"), @Value("\""), @Value("<"), @Value("╥")})
    char eye;

    @Var(value = {@Value("-"), @Value("_"), @Value("‿"), @Value("∇"), @Value("◡"),
           @Value("³"), @Value("ᴗ"), @Value("﹏"), @Value(".")})
    char snoot;

    @Var(value = {@Value("\\/"), @Value("ᕙᕗ"), @Value("ᕦᕤ"), @Value("┌ʃ")})
    String arms;

    private String getFace() {
        return (arms == null ? "" : arms.substring(0, 1))
                + '(' + eye + snoot + eye + ')' // chars cannot be null
                + (arms == null ? "" : arms.substring(1, 2));
    }

    public static void main(String[] args) {
        int tupleSize = 2;
        List<EmojiTestInput> testCases = new FunctionTestsCreator<>(EmojiTestInput.class)
                        .tupleGenerator(tupleSize)
                        .createDefs();

        testCases.forEach(test -> System.out.println(test.getFace()));

        System.out.println("\n" + testCases.size() + " faces generated with independent "
                           + tupleSize + "-tuples or properties");
    }
}
```

Running this class yields:

```
\(^-^)/  ᕙ(^-^)ᕗ  ᕦ(^-^)ᕤ  ┌(^-^)ʃ  (^-^)  (^_^)  ᕦ(^◡^)ᕤ  ┌(^◡^)ʃ  ┌(°﹏°)ʃ  (°﹏°)  \(°.°)/   \(ಠ-ಠ)/  ᕙ(ಠ-ಠ)ᕗ  ᕦ(ಠ-ಠ)ᕤ  ┌(ಠ-ಠ)ʃ  (ಠ-ಠ) ...
119 faces generated with independent 2-tuples or properties
```

Of the total possible combinations (11 x 9 x 5 = 495), this is a smaller subset of 119 faces where for each trait-pair (e.g. eyes and mouth) each combination appears at least once. (TODO: Show how number can be further reduced)
As test-inputs, using such subsets instead of all combinations reduces test-time while detecting most bugs that would be found by testing all combinations (some studies suggest 75% of bugs).

This shows only a small part of the possibilities to define combinations, for boundary-value testing conditions and failure values are very powerful.

## Motivation

When automating tests for system with complex inputs, a challenge is maintaining a large set of test input combinations, another challenge is to keep the duration of running all tests low.
Using Beanfiller-tcases, java developers can model any number of testcases in a simple java bean and have instances automatically generated to run tests, with the benefit of pairwise testing reducing the total number of tests.

This project uses [Tcases](https://github.com/Cornutum/tcases), a tool for combinatorial testing.
Tcases is intended as a standalone CLI tool to maintain a set of testcase xml-files over time, possibly maintaining also a set of generated+modified Test sources (in any language) containing expected values for each generated testcase (a.k.a Oracle).

In contract to Tcases, Beanfiller-Tcases aims to be used as a java library without necessarily handling XML or generated testing code.
A tester can easily generate a small subset of combinations of test variables, and thus have an easily readable and maintainable testing codebase.

## In-depth example

The initial Emoji example is just an introduction to how to use the library to manage combinatorial code problems.

TODO: Add an actual test exampe here with conditions and failures.

## How to use

### In Maven

```
    <dependency>
      <groupId>io.github.beanfiller</groupId>
      <artifactId>beanfiller-tcases</artifactId>
      <version>[latestVersion]</version>
    </dependency>
```

### In Gradle

```
dependencies {
    testCompile 'io.github.beanfiller:beanfiller-tcases:[latestVersion]'
}
```

The library can be used freely in any other Unit testing framework like JUnit or TestNg.
It comes with only a few common dependencies:

```
+--- org.cornutum.tcases:tcases-lib
     +--- org.apache.commons:commons-lang3
     +--- org.apache.commons:commons-collections4
     \--- org.slf4j:slf4j-api
```

There are example projects in the samples subfolder to look at.

## Test design

As a general approach for using this library, it is recommended to split the test code in 3 parts:

1. The testcase **input definition**
2. The actual **test input/output** values derived from the **input definition**
3. A parametrized tests running all tests cases with given **test input** and validate given **test output**

In simple cases the input definition can also serve as test input (Like in the example at the top), but when adding constraints this approach quickly becomes unmanageable.


## Q & A

### When should I use this?

The main benefit is for situations where you need to test many combinations of input values and tests are too slow to just run all possible combinations.
It might however also be beneficial when you need to run all combinations, and need a good way to define those combinations with nested structures.

### What datatypes can be used?

By default the library will work with Java primitives, Numbers, Booleans, Strings and Enums.
Custom Mappers can extend this to other types.

### Why not annotate methods instead of Beans?

Since one test seems to test one function, it might be useful to annotate a function, like

```java
    public void foo(@Values(@Value(""), @Value("x"), ...) String a,
                    @Values(@Value(""), @Value("x"), ...) String b,
                    @Values(@Value(""), @Value("x"), ...) String c, ...)
```

Then foo could be directly called by the framework.
However this is usually not practical, because a testcase definition needs to define and contain more metadata, and often the testcase values must be transformed into actual function inputs.


### How to handle expected test outputs?

In Tcases, the developer generally has to define the expected test output *after* generating combinations.
This can be done in a separate file, or in generated JUnit code.

Managing additional files mapping inputs to expected test outputs is tedious (to write and to maintain later on).

But in some cases, since the testcase knows moe than the input values, output values can be derived from the test definitions.
In particular, the test case has knowledge about:

1. The intended semantics of a value
2. The indented semantics of a testcase
3. Additional annotations added to the testcase model
4. The construction of actual test values

This knowledge can enable a test to correctly define an expected output without running the system under test.

### Doesn't this approach violate the KISS (Keep it simple stupid) guideline for testing?

Yes and no: the principle suggests to use the simplest out of all alternatives.
Maintaining 1000s of testcase methods that have been first generated and then manually extended can be much more complex than an approach without code generation.
However when your testcode becomes complex, it may be wise to write tests for your testcode itself.

## Resources

On pairwise testing:

* The Tcases guide: http://www.cornutum.org/tcases/docs/Tcases-Guide.htm
* Overview of pairwise-testing https://en.wikipedia.org/wiki/All-pairs_testing
* Overview of combinatorial testing tools: http://www.pairwise.org/tools.asp
* Overview of Boundary Value testing https://en.wikipedia.org/wiki/Boundary_testing

On very different approaches that also generate values for testing:

* Overview of Property-based testing https://en.wikipedia.org/wiki/QuickCheck
* EvoSuite http://www.evosuite.org/
* Mockaroo https://mockaroo.com/
