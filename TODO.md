# TODO

My list of ideas to implement... pull requests welcome.

* For 0.0.2 Release
  * Feature: properly support bean getters/setters (Consider Jackson-databind?)
  * Feature: support Lists of primitives
  * Design: Generator configuration inside FunctionInputDef (In particular for Combine)?
    * Design: Generator Combiners from input def

* High priority
  * Feature: Consider Var ranges for numbers, times, dates, ...?
  * Feature: Support special non-primitives (Locale, Currency?)
  * Feature: Consider Var ranges for numbers, times, dates, ...?
  * Documentation: full test example
  * Documentation: Custom type mapper
  * Do not pass around annotation instances, wrap into adapter class satisfying interface as domain model. This allows different annotations
  * Design: Same type but different / interrelated values (testtime?)
  * Test: Customizable AnnotationsReaders and Type Emitters
    * Design: Annotate Value with BoundaryValueEnum and generator?
    * Design: Support Primitive Collections
    * Design: Support any Collections and Maps
    * Design: static fields define values for non-statics
    * Design: ShortCut to define a Failure value with an annotation (failure = "fileNotFound")
    * Design: ShortCut annotation (@SimpleVar(value = "foo;bar;baz", fail = "bam;bim;bum")
  * Design: Better explicit support for testCase vs. test input (triangle)
    * Derived fields? (derivedFrom path)
    * simple getter Methods to call after instantiation (lazy, no ordering)
    * Single PostGeneration method to call after instantiation (nested?)
  * Design: Persist (expected) values for combinations of tests
  * Design: Create Testcase reports in Excel
  * Bug: check output annotation duplication for nested VarDef (bug?)
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
    * JSoup Spring Thymeleaf example
  * Design: More reusable BoundaryValueCategory enums
  * Feature: Use beanfillers-annotations without tcases on the compile classpath
  * Design: custom VarDef / VarSet subclasses for reflection access?

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
  * Design: Allows comma-separated String value for value properties?
  * Design: Auto-properties Shortcut. VarDef x with VarValue Y produces property 'x:y'?
  * Feature: Define Generators as JUnit TestRule (consider invocation order, or providing seed, for output matching inputs)
  * Feature: Support better testcase descriptions/ids than 0..n?
  * Design: Make Tcases produce a lazy iterator?
