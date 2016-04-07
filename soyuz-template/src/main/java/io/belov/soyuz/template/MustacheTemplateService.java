package io.belov.soyuz.template;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.resolver.ClasspathResolver;

import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbelov on 07.04.16.
 */
public class MustacheTemplateService {

    private ConcurrentHashMap<String, Mustache> templates = new ConcurrentHashMap<>();
    private MustacheFactory mf;

    public MustacheTemplateService() {
        mf = new DefaultMustacheFactory(new ClasspathResolver("templates"));
    }

    public Mustache getTemplate(String key) {
        if (!templates.contains(key)) {
            templates.putIfAbsent(key, mf.compile(key));
        }

        return templates.get(key);
    }

    public String execute(String key, Object data) {
        Writer writer = new StringWriter();

        getTemplate(key).execute(writer, data);

        return writer.toString();
    }

}
