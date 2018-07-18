package io.github.beanfiller.annotation.builders;

import org.cornutum.tcases.FunctionInputDef;
import org.cornutum.tcases.IVarDef;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionInputDefBuilder {

    private final String name;
    private final Map<String, String> annotations = new HashMap<>();
    private final List<IVarDef> varDefs = new ArrayList<>();

    private FunctionInputDefBuilder(String name) {
        this.name = name;
    }

    public static FunctionInputDefBuilder function(String name) {
        return new FunctionInputDefBuilder(name);
    }

    @Nonnull
    public FunctionInputDefBuilder addAnnotation(String key, String value) {
        this.annotations.put(key, value);
        return this;
    }

    @Nonnull
    public FunctionInputDefBuilder addAnnotations(Map<String, String> annotations) {
        this.annotations.putAll(annotations);
        return this;
    }

    @Nonnull
    public FunctionInputDefBuilder addVarDef(IVarDef varDef) {
        this.varDefs.add(varDef);
        return this;
    }

    @Nonnull
    public FunctionInputDef build() {
        final FunctionInputDef functionInputDef = new FunctionInputDef(name);
        for (final Map.Entry<String, String> annotation : annotations.entrySet()) {
            functionInputDef.setAnnotation(annotation.getKey(), annotation.getValue());
        }
        for (final IVarDef varDef : varDefs) {
            functionInputDef.addVarDef(varDef);
        }
        return functionInputDef;
    }
}
