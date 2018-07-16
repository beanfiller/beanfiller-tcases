# TODO

My list of ideas to implement... pull requests welcome.

* Minimal for 0.0.1 Release
  * new repository / dependency vs. fork, ..., integration test, travis
  * Todo: Cleanup code:
    * Builders
  * Todo: Full unit tests

* High priority
  * Feature: Prevent when/whenNot and notNull=true
  * Feature: test null values for VarSets variables
  * Design: Same type but different / interrelated values (testtime?)
  * Test: Customizable AnnotationsReaders and Type Emitters
    * Design: Annotate Value with BoundaryValueEnum and generator?
    * Design: Support Collections and Maps
    * Design: static fields define values for non-statics
    * Design: ShortCut to define a Failure value with an annotation (failure = "fileNotFound")
    * Design: ShortCut annotation (@SimpleVar(value = "foo;bar;baz", fail = "bam;bim;bum")
  * Design: Better explicit support for testCase vs. test input (triangle)
    * Derived fields? (derivedFrom path)
    * simple getter Methods to call after instantiation (lazy, no ordering)
    * Single PostGeneration method to call after instantiation (nested?)
  * Design: Persist (expected) values for combinations of tests
  * Design: Handle quotes and special chars in String values
  * Bug: check output annotation duplication for nested VarDef (bug?)
  * Feature: Allow annotating enum values for varvalue defaults
  * Feature: properly support bean getters/setters (Consider Jackson-databind?)
  * Design: Require each non-static field to have one Tcases annotation (Var, VarSet, TestCaseId, IsFailure)?
  * Feature: Create more test examples:
    * Triangle
    * Shop cart checkout
    * Parser
    * Order discount
    * Doodle wizard
    * Authorization example from doc
    * ATM withdraw
    * Restassured/spring? example
  * Design: Combiners from input def
  * Design: More reusable BoundaryValueCategory eums
  * Feature: Use beanfillers-annotations without tcases on the compile classpath
  * Design, custom VArDef / VarSet subclasses for reflection access?

* Nice to have
  * Design: Annotate Java methods semantically about content/ validation, define filling strategy independently.
    * Combinatorial (tcases, http://ecfeed.com)
    * Property-based
      * pholser.github.io/junit-quickcheck/site/0.8.1 (Junit4 Runner, proper shrinking)
      * https://github.com/ncredinburgh/QuickTheories ()
      * jkwiq (junit5, simple shrinking))
      * In this combination, one combinatorial Testcase may map to N random testInputs
    * Faker-based (https://mockaroo.com/, java-faker)

  * Design: Repeatable Var annotation for valid, / invalid cases, varValueDef property in @Var
    * Test split Var/env/abstract, env values generated given abstract values
  * Design: Generator configuration inside FunctionInputDef (In particular for Combine)?
  * Design: Value generator method reference for vars with many values.
  * Design: Allows comma-separated String value for value properties?
  * Design: Auto-properties Shortcut. VarDef x with VarValue Y produces property 'x:y'?
  * Feature: Define Generators as JUnit TestRule (consider invocation order, or providing seed, for output matching inputs)
  * Feature: @Value annotation attribute for Enum values not to be used for a given @Var
  * Design: Refactor TCases to allow getTests() for FunctionInputDef without SystemInputDef?
  * Feature: Junit4 / Junit5 Test Rules?
  * Feature: Support special non-primitives (Time, date, ..?)
  * Feature: Consider Var ranges for numbers, times, dates, ...?
  * Feature: Support better testcase descriptions/ids than 0..n?
  * Feature: Support defining Generators of varvalues
  * Design: Make Tcases produce a lazy iterator?
  * Modernize: Apply common Java codestyle to tCases / Apply static code checkers
  * Modernize: Migrate Tcases to Gradle
  * Modernize: Use Java8 Stream, Optional, FindBugs
  * Modernize: Move Ant Support  / TCases class to separate modules out of tcases-lib
