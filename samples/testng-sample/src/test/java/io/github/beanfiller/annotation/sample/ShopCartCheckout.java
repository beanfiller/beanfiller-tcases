package io.github.beanfiller.annotation.sample;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import io.github.beanfiller.annotation.annotations.Value;
import io.github.beanfiller.annotation.annotations.Var;
import io.github.beanfiller.annotation.creator.AbstractTestInput;
import org.cornutum.tcases.TestCase;


public class ShopCartCheckout extends AbstractTestInput {

    public static final String FUNCTION_NAME = "ShopCartCheckout";
    @Var({
            @Value("Visa"),
            @Value("MasterCard"),
            @Value("Amex"),
            @Value("Discover")
    })
    String creditCard;

    @Var({
            @Value("Correct"),
            @Value(value = "BadLength", type = TestCase.Type.FAILURE),
            @Value(value = "Invalid", type = TestCase.Type.FAILURE)
    })
    String creditCardNumberCheck;

//    @Var({
//            @Value("50"),
//            @Value(value = "today"),
//            @Value(value = "invalidYear", type = TestCase.Type.FAILURE),
//            @Value(value = "invalidChar", type = TestCase.Type.FAILURE),
//            @Value(value = "yesterday", type = TestCase.Type.FAILURE)
//    })
//    Date expirationInYears;

    //@Var
    //List<String> purchases;

    @Var({
            @Value(value = "-1", type = TestCase.Type.FAILURE),
            @Value(value = "0", type = TestCase.Type.FAILURE),
            @Value("1"),
            @Value(value = "10")
    })
    int amountPurchased;


    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
