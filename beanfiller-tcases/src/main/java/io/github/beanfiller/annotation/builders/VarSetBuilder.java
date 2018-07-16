package io.github.beanfiller.annotation.builders;

import org.cornutum.tcases.IVarDef;
import org.cornutum.tcases.VarSet;
import org.cornutum.tcases.conditions.ICondition;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VarSetBuilder {


    private final String name;
    private final List<IVarDef> members = new ArrayList<>();
    private final Map<String, String> annotations = new HashMap<>();
    private ICondition myCondition;

    private VarSetBuilder(String name) {
        this.name = name;
    }

    @Nonnull
    public static VarSetBuilder varSet(String name) {
        return new VarSetBuilder(name);
    }

    @Nonnull
    public VarSetBuilder addMember(IVarDef member) {
        members.add(member);
        return this;
    }

    @Nonnull
    public VarSetBuilder addAnnotations(Map<String, String> newAnnotations) {
        this.annotations.putAll(newAnnotations);
        return this;
    }

    @Nonnull
    public VarSetBuilder condition(ICondition newCondition) {
        this.myCondition = newCondition;
        return this;
    }

    @Nonnull
    public VarSet build() {
        VarSet varDef = new VarSet(name);
        for (IVarDef member : members) {
            varDef.addMember(member);
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
