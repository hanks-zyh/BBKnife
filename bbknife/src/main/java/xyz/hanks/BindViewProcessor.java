package xyz.hanks;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * 编译期间处理注解
 * Created by hanks on 2016/7/31.
 */
@SupportedAnnotationTypes("xyz.hanks.BindView")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class BindViewProcessor extends AbstractProcessor {
    private Messager messager;
    public static final String SUFFIX = "$ViewBinder";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, List<VariableElement>> map = new HashMap<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
            if (element == null || !(element instanceof VariableElement)) {
                continue;
            }
            VariableElement variableElement = (VariableElement) element;
            String className = element.getEnclosingElement().getSimpleName().toString();
            List<VariableElement> variableElementList = map.get(className);
            if (variableElementList == null) {
                variableElementList = new ArrayList<>();
                map.put(className, variableElementList);
            }
            variableElementList.add(variableElement);
        }

        generate(map);
        return true;
    }

    private void generate(Map<String, List<VariableElement>> map) {
        if (null == map || map.size() == 0) {
            return;
        }
        for (String className : map.keySet()) {
            List<VariableElement> variableElementList = map.get(className);
            if (variableElementList == null || variableElementList.size() <= 0) {
                continue;
            }
            String packageName = variableElementList.get(0).getEnclosingElement().getEnclosingElement().toString();
            StringBuilder builder = new StringBuilder()
                    .append("package ").append(packageName).append(";\n\n")
                    .append("public class ").append(className).append(SUFFIX).append("{\n") // open class
                    .append("    public void bind(Object target) {\n")
                    .append("        ").append(className).append(" activity = (").append(className).append(")target;\n");

            for (VariableElement variableElement : variableElementList) {
                BindView bindView = variableElement.getAnnotation(BindView.class);
                log(bindView.toString());
                builder.append("        activity.").append(variableElement.getSimpleName().toString()).append("=(").append(variableElement.asType()).append(")activity.findViewById(").append(bindView.value()).append(");\n");
            }
            builder.append("    }\n}\n");
            // write the file
            try {
                String bindViewClassName = packageName + "." + className + SUFFIX;
                JavaFileObject source = processingEnv.getFiler().createSourceFile(bindViewClassName);
                Writer writer = source.openWriter();
                writer.write(builder.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                log(e.getMessage());
            }
        }
    }

    private void log(String msg) {
        messager.printMessage(Diagnostic.Kind.WARNING, msg);
    }

}
