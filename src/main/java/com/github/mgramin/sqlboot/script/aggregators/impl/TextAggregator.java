package com.github.mgramin.sqlboot.script.aggregators.impl;

import com.github.mgramin.sqlboot.exceptions.SqlBootException;
import com.github.mgramin.sqlboot.model.DBSchemaObject;
import com.github.mgramin.sqlboot.script.aggregators.AbstractAggregator;
import com.github.mgramin.sqlboot.script.aggregators.IAggregator;
import com.github.mgramin.sqlboot.util.template_engine.ITemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mgramin on 17.12.2016.
 */
public class TextAggregator extends AbstractAggregator implements IAggregator {

    public TextAggregator(String name, Boolean isDefault, Map<String, String> httpHeaders) {
        this.name = name;
        this.isDefault = isDefault;
        this.httpHeaders = httpHeaders;
    }

    public TextAggregator(String name, Map<String, String> httpHeaders, ITemplateEngine templateEngine, String template) {
        this.name = name;
        this.httpHeaders = httpHeaders;
        this.templateEngine = templateEngine;
        this.template = template;
    }

    private String template;
    private ITemplateEngine templateEngine;

    @Override
    public byte[] aggregate(List<DBSchemaObject> objects) throws SqlBootException {
        if (objects == null) return null;
        if (template == null || template.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (DBSchemaObject o : objects) builder.append(o.ddl).append("\n");
            return builder.toString().getBytes();
        }
        else {
            Map<String, Object> variables = new HashMap<>();
            variables.put("objects", objects);
            templateEngine.setTemplate(template); // TODO move to constructor
            return templateEngine.process(variables).getBytes();
        }
    }

}
