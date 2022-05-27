package com.pooch.api.entity.notification.email;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public interface EmailUtils {
    
    static String subValues(Map<String, Object> params, String templateContent) {

        MustacheFactory mustacheFactory = new DefaultMustacheFactory();
        StringReader inlineTemplateReader = new StringReader(templateContent);
        
        // Normally mustache reads templates from a resource directory. Here we want to pass inline template as a
        // string. Hence use StringReader.
        Mustache mustache = mustacheFactory.compile(inlineTemplateReader, "populateEmailTemplateUsingMustache");

        StringWriter writer = new StringWriter();
        
        try {
            mustache.execute(writer, params).flush();
        } catch (IOException e) {
            System.err.println("IOException, msg="+e.getLocalizedMessage());
            e.printStackTrace();
        }

        // return transformed template. All placeholders will be replaced with data.
        String content = writer.toString();

        return content;
    }
}
