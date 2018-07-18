package io.github.beanfiller.annotation.builders;

import org.cornutum.tcases.FunctionInputDef;
import org.cornutum.tcases.SystemInputDef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemInputDefBuilder {

    private final String name;
    private final Map<String, String> annotations = new HashMap<>();
    private final List<FunctionInputDef> functionInputDefs = new ArrayList<>();

    private SystemInputDefBuilder(@Nullable String name) {
        this.name = name;
    }

    @Nonnull
    public static SystemInputDefBuilder system(@Nullable String name, FunctionInputDef... functions) {
        final SystemInputDefBuilder systemInputDefBuilder = new SystemInputDefBuilder(name);
        for (final FunctionInputDef functionInputDef : functions) {
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
        final SystemInputDef systemInputDef = new SystemInputDef(name);
        for (final Map.Entry<String, String> annotation : annotations.entrySet()) {
            systemInputDef.setAnnotation(annotation.getKey(), annotation.getValue());
        }
        for (final FunctionInputDef functionInputDef : functionInputDefs) {
            systemInputDef.addFunctionInputDef(functionInputDef);
        }
        return systemInputDef;
    }
}
