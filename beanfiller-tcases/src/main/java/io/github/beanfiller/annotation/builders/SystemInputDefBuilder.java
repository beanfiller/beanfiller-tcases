package io.github.beanfiller.annotation.builders;

import org.cornutum.tcases.FunctionInputDef;
import org.cornutum.tcases.SystemInputDef;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemInputDefBuilder {

    private final String name;
    private final Map<String, String> annotations = new HashMap<>();
    private final List<FunctionInputDef> functionInputDefs = new ArrayList<>();

    private SystemInputDefBuilder(String name) {
        this.name = name;
    }

    @Nonnull
    public static SystemInputDefBuilder system(String name, FunctionInputDef... functions) {
        SystemInputDefBuilder systemInputDefBuilder = new SystemInputDefBuilder(name);
        for (FunctionInputDef functionInputDef : functions) {
            systemInputDefBuilder.addInputDef(functionInputDef);
        }
        return systemInputDefBuilder;
    }

    @Nonnull
    public SystemInputDefBuilder addAnnotation(String key, String value) {
        this.annotations.put(key, value);
        return this;
    }

    @Nonnull
    public SystemInputDefBuilder addAnnotations(Map<String, String> annotations) {
        this.annotations.putAll(annotations);
        return this;
    }

    @Nonnull
    public SystemInputDefBuilder addInputDef(FunctionInputDef def) {
        this.functionInputDefs.add(def);
        return this;
    }

    @Nonnull
    public SystemInputDef build() {
        SystemInputDef systemInputDef = new SystemInputDef(name);
        for (Map.Entry<String, String> annotation : annotations.entrySet()) {
            systemInputDef.setAnnotation(annotation.getKey(), annotation.getValue());
        }
        for (FunctionInputDef functionInputDef : functionInputDefs) {
            systemInputDef.addFunctionInputDef(functionInputDef);
        }
        return systemInputDef;
    }
}
