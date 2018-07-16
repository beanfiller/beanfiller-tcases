//////////////////////////////////////////////////////////////////////////////
//
//                    Copyright 2012, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package io.github.beanfiller.annotation.sample;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import io.github.beanfiller.annotation.annotations.Value;
import io.github.beanfiller.annotation.annotations.Var;
import io.github.beanfiller.annotation.creator.AbstractTestInput;

import static org.cornutum.tcases.TestCase.Type.FAILURE;

public class TriangleProblemTestInput extends AbstractTestInput {

  public static final String SAME_LENGTH = "SAME_LENGTH";
  private static final String FIRST_ZERO = "FIRST_ZERO";
  private static final String SECOND_ZERO = "SECOND_ZERO";
  private static final String SECOND_NEGATIVE = "SECOND_NEGATIVE";

  enum FirstSegmentLength {
    NEGATIVE,
    ZERO,
    POSITIVE
  }

  @Var(value = {
          @Value(value = "NEGATIVE", type = FAILURE),
          @Value(value = "ZERO", properties = FIRST_ZERO, once = true)
  })
  FirstSegmentLength firstSegmentLength;

  enum SecondSegmentLength {
    NEGATIVE,
    ZERO,
    LARGER_THAN_FIRST,
    SAME_AS_FIRST
  }

  @Var(value = {
          @Value(value = "NEGATIVE", type = FAILURE, properties = SECOND_NEGATIVE),
          @Value(value = "ZERO", type = FAILURE, when = FIRST_ZERO),
          @Value(value = "SAME_AS_FIRST", properties = SAME_LENGTH)
  })
  SecondSegmentLength secondSegmentLength;

  enum ThirdSegmentLength {
    NEGATIVE,
    TOO_SHORT,
    DIFFERENCE_BETWEEN_FIRST_TWO,
    SAME_AS_SECOND,
    SHORTER_THAN_SECOND
  }

  @Var(value = {
          @Value(value = "NEGATIVE", type = FAILURE),
          @Value(value = "TOO_SHORT", type = FAILURE),
          @Value(value = "DIFFERENCE_BETWEEN_FIRST_TWO", whenNot = {SAME_LENGTH, FIRST_ZERO, SECOND_ZERO}),
          @Value(value = "SAME_AS_SECOND", whenNot = {SECOND_ZERO, SECOND_NEGATIVE}),
          @Value(value = "SHORTER_THAN_SECOND", whenNot = {SECOND_ZERO, SECOND_NEGATIVE})
  })
  ThirdSegmentLength thirdSegmentLength;


  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

}
