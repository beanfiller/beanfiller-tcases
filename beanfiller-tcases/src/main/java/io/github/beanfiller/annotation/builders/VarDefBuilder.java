package io.github.beanfiller.annotation.builders;

import org.cornutum.tcases.VarDef;
import org.cornutum.tcases.VarValueDef;
import org.cornutum.tcases.conditions.ICondition;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VarDefBuilder {


    private final String name;
    private final List<VarValueDef> varValueDefs = new ArrayList<>();
    private final Map<String, String> annotations = new HashMap<>();
    private ICondition myCondition;

    private VarDefBuilder(String name) {
        this.name = name;
    }

    public static VarDefBuilder varDef(String name) {
        return new VarDefBuilder(name);
    }

    @Nonnull
    public VarDefBuilder addValue(VarValueDef varValueDef) {
        varValueDefs.add(varValueDef);
        return this;
    }

    @Nonnull
    public VarDefBuilder addAnnotations(Map<String, String> newAnnotations) {
        this.annotations.putAll(newAnnotations);
        return this;
    }

    @Nonnull
    public VarDefBuilder condition(ICondition newCondition) {
        this.myCondition = newCondition;
        return this;
    }

    @Nonnull
    public VarDef build() {
        VarDef varDef = new VarDef(name);
        for (VarValueDef varValueDef : varValueDefs) {
            varDef.addValue(varValueDef);
        }
        for (Map.Entry<String, String> a : annotations.entrySet()) {
            varDef.setAnnotation(a.getKey(), a.getValue());
        }
        if (myCondition != null) {
            varDef.setCondition(myCondition);
        }
        return varDef;
    }
}
