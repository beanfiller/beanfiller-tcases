package io.github.beanfiller.annotation.builders;

import org.cornutum.tcases.VarValueDef;
import org.cornutum.tcases.conditions.ICondition;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VarValueDefBuilder {

    private final VarValueDef.Type type;
    private final String valueAsString;

    private final Map<String, String> annotations = new HashMap<>();
    private ICondition myCondition;
    private final List<String> properties = new ArrayList<>();
    private boolean isNull;

    public VarValueDefBuilder(String valueAsString, VarValueDef.Type type) {
        this.valueAsString = valueAsString;
        this.type = type;
    }

    @Nonnull
    public VarValueDefBuilder addAnnotations(Map<String, String> annotations) {
        this.annotations.putAll(annotations);
        return this;
    }

    @Nonnull
    public VarValueDefBuilder condition(ICondition condition) {
        this.myCondition = condition;
        return this;
    }

    @Nonnull
    public VarValueDefBuilder setNull() {
        this.isNull = true;
        return this;
    }

    @Nonnull
    public VarValueDefBuilder addProperties(String[] properties) {
        this.properties.addAll(Arrays.asList(properties));
        return this;
    }

    @Nonnull
    public VarValueDef build() {
        VarValueDef varDef;
        if (isNull) {
            varDef = new PatchedVarNaDef(valueAsString, type);
        } else {
            varDef = new VarValueDef(valueAsString, type);
        }
        varDef.addProperties(properties);
        for (Map.Entry<String, String> a : annotations.entrySet()) {
            varDef.setAnnotation(a.getKey(), a.getValue());
        }
        if (myCondition != null) {
            varDef.setCondition(myCondition);
        }
        return varDef;
    }


    /**
     * In TCases <= 2.0.0 VarValueDef does not allow null values, nor a type for VarNADef.
     * As a workaround using a custom VarValuDef subclass for null values.
     */
    private static class PatchedVarNaDef extends VarValueDef {

        PatchedVarNaDef(String valueAsString, Type type) {
            super(valueAsString, type);
        }

        @Override
        public boolean isNA()
        {
            return true;
        }

    }

}
