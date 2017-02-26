package com.github.mgramin.sqlboot.util.template_engine.impl;

import com.github.mgramin.sqlboot.util.template_engine.ITemplateEngine;
import groovy.text.markup.MarkupTemplateEngine;

/**
 * Created by mgramin on 06.01.2017.
 */
public class GroovyMarkupTemplateEngine extends AbstractGroovyTemplateEngine implements ITemplateEngine {

    public GroovyMarkupTemplateEngine() {
        engine = new MarkupTemplateEngine();
    }

    public GroovyMarkupTemplateEngine(String template) {
        this();
        setTemplate(template);
    }

    @Override
    public String toString() {
        return "GroovyMarkupTemplateEngine{" +
            "template=" + template +
            '}';
    }

}