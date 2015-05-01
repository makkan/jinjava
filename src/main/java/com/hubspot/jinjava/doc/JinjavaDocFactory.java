package com.hubspot.jinjava.doc;


import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.doc.annotations.JinjavaParam;
import com.hubspot.jinjava.doc.annotations.JinjavaSnippet;
import com.hubspot.jinjava.lib.exptest.ExpTest;
import com.hubspot.jinjava.lib.filter.Filter;
import com.hubspot.jinjava.lib.fn.ELFunctionDefinition;
import com.hubspot.jinjava.lib.fn.InjectedContextFunctionProxy;
import com.hubspot.jinjava.lib.tag.EndTag;
import com.hubspot.jinjava.lib.tag.Tag;

public class JinjavaDocFactory {
  private static final Logger LOG = LoggerFactory.getLogger(JinjavaDocFactory.class);
  
  private final Jinjava jinjava;
  
  public JinjavaDocFactory(Jinjava jinjava) {
    this.jinjava = jinjava;
  }
  
  public JinjavaDoc get() {
    JinjavaDoc doc = new JinjavaDoc();
    
    addExpTests(doc);
    addFilterDocs(doc);
    addFnDocs(doc);
    addTagDocs(doc);
    
    return doc;
  }

  private void addExpTests(JinjavaDoc doc) {
    for(ExpTest t : jinjava.getGlobalContext().getAllExpTests()) {
      com.hubspot.jinjava.doc.annotations.JinjavaDoc docAnnotation = t.getClass().getAnnotation(com.hubspot.jinjava.doc.annotations.JinjavaDoc.class);
      
      if(docAnnotation == null) {
        LOG.warn("Expression Test {} doesn't have a @{} annotation", t.getName(), com.hubspot.jinjava.doc.annotations.JinjavaDoc.class.getName());
        doc.addExpTest(new JinjavaDocExpTest(t.getName(), "", "", new JinjavaDocParam[]{}, new JinjavaDocSnippet[]{}));
      }
      else if(!docAnnotation.hidden()) {
        doc.addExpTest(new JinjavaDocExpTest(t.getName(), docAnnotation.value(), docAnnotation.aliasOf(), 
            extractParams(docAnnotation.params()), extractSnippets(docAnnotation.snippets())));
      }
    }
  }

  private void addFilterDocs(JinjavaDoc doc) {
    for(Filter f : jinjava.getGlobalContext().getAllFilters()) {
      com.hubspot.jinjava.doc.annotations.JinjavaDoc docAnnotation = f.getClass().getAnnotation(com.hubspot.jinjava.doc.annotations.JinjavaDoc.class);
      
      if(docAnnotation == null) {
        LOG.warn("Filter {} doesn't have a @{} annotation", f.getClass(), com.hubspot.jinjava.doc.annotations.JinjavaDoc.class.getName());
        doc.addFilter(new JinjavaDocFilter(f.getName(), "", "", new JinjavaDocParam[]{}, new JinjavaDocSnippet[]{}));
      }
      else if(!docAnnotation.hidden()) {
        doc.addFilter(new JinjavaDocFilter(f.getName(), docAnnotation.value(), docAnnotation.aliasOf(), 
            extractParams(docAnnotation.params()), extractSnippets(docAnnotation.snippets())));
      }
    }
  }

  private void addFnDocs(JinjavaDoc doc) {
    for(ELFunctionDefinition fn : jinjava.getGlobalContext().getAllFunctions()) {
      if(StringUtils.isBlank(fn.getNamespace())) {
        Method realMethod = fn.getMethod();
        if(realMethod.getDeclaringClass().getName().contains(InjectedContextFunctionProxy.class.getSimpleName())) {
          try {
            realMethod = (Method) realMethod.getDeclaringClass().getField("delegate").get(null);
          } catch (Exception e) {
            throw Throwables.propagate(e);
          }
        }
        
        com.hubspot.jinjava.doc.annotations.JinjavaDoc docAnnotation = realMethod.getAnnotation(com.hubspot.jinjava.doc.annotations.JinjavaDoc.class);
        
        if(docAnnotation == null) {
          LOG.warn("Function {} doesn't have a @{} annotation", fn.getName(), com.hubspot.jinjava.doc.annotations.JinjavaDoc.class.getName());
          doc.addFunction(new JinjavaDocFunction(fn.getLocalName(), "", "", new JinjavaDocParam[]{}, new JinjavaDocSnippet[]{}));
        }
        else if(!docAnnotation.hidden()) {
          doc.addFunction(new JinjavaDocFunction(fn.getLocalName(), docAnnotation.value(), docAnnotation.aliasOf(), 
              extractParams(docAnnotation.params()), extractSnippets(docAnnotation.snippets())));
        }
      }
    }
  }
  
  private void addTagDocs(JinjavaDoc doc) {
    for(Tag t : jinjava.getGlobalContext().getAllTags()) {
      if(t instanceof EndTag) {
        continue;
      }
      com.hubspot.jinjava.doc.annotations.JinjavaDoc docAnnotation = t.getClass().getAnnotation(com.hubspot.jinjava.doc.annotations.JinjavaDoc.class);
      
      if(docAnnotation == null) {
        LOG.warn("Tag {} doesn't have a @{} annotation", t.getName(), com.hubspot.jinjava.doc.annotations.JinjavaDoc.class.getName());
        doc.addTag(new JinjavaDocTag(t.getName(), StringUtils.isBlank(t.getEndTagName()), "", "", new JinjavaDocParam[]{}, new JinjavaDocSnippet[]{}));
      }
      else if(!docAnnotation.hidden()) {
        doc.addTag(new JinjavaDocTag(t.getName(), StringUtils.isBlank(t.getEndTagName()), docAnnotation.value(), docAnnotation.aliasOf(), 
            extractParams(docAnnotation.params()), extractSnippets(docAnnotation.snippets())));
      }
    }
  }
  
  private JinjavaDocParam[] extractParams(JinjavaParam[] params) {
    JinjavaDocParam[] result = new JinjavaDocParam[params.length];

    for(int i = 0; i < params.length; i++) {
      JinjavaParam p = params[i];
      result[i] = new JinjavaDocParam(p.value(), p.type(), p.desc(), p.defaultValue());
    }

    return result;
  }
  
  private JinjavaDocSnippet[] extractSnippets(JinjavaSnippet[] snippets) {
    JinjavaDocSnippet[] result = new JinjavaDocSnippet[snippets.length];
    
    for(int i = 0; i < snippets.length; i++) {
      JinjavaSnippet s = snippets[i];
      result[i] = new JinjavaDocSnippet(s.desc(), s.code(), s.output());
    }
    
    return result;
  }
  
}