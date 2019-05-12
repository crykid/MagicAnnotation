package com.magic.magic_compiler;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.magic.magic_annotations.NotRelease;
import com.magic.magic_annotations.OnlyAvailable;
import com.magic.magic_annotations.OnlyDebug;
import com.magic.magic_compiler.magicelement.MagicElement;
import com.magic.magic_compiler.magicelement.NotReleaseMagic;
import com.magic.magic_compiler.magicelement.OnlyAvailableMagic;
import com.magic.magic_compiler.magicelement.OnlyDebugMagic;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by : mr.lu
 * Created at : 2019-05-11 at 19:17
 * Description:
 */
@SuppressWarnings("unused")
@AutoService(Processor.class)
public class MagicProcessor extends AbstractProcessor {

    private Messager messager;
    private static final String CONST_PARAM_TARGET_NAME = "target";
    private static final ClassName VIEW = ClassName.get("android.view", "View");

    private static final String MAGIC_SUFFIX = "_Magic";
    private static final char CHAR_DOT = '.';
    private static final String TARGET_STATEMENT_VISIBILITY_FORMAT = "target.%1$s.setVisibility(buildType.equals(%2$s))";

    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.mFiler = processingEnv.getFiler();
        messager = processingEnv.getMessager();

    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> types = new HashSet<>();
        final Set<Class<? extends Annotation>> annotations = getSupportedAnnotations();

        for (Class<? extends Annotation> annotation : annotations) {
            types.add(annotation.getCanonicalName());
        }

        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        final Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>(Arrays.asList(
                OnlyDebug.class,
                OnlyAvailable.class,
                NotRelease.class));

        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, List<MagicElement>> typeElementListMap = new HashMap<>();
        //找到注解并转化
        findAndParseMagic(roundEnv, typeElementListMap);
        //创建java类
        for (Map.Entry<String, List<MagicElement>> entry : typeElementListMap.entrySet()) {
            MethodSpec constructor = createConstructor(entry.getValue());

            TypeSpec binder = createClass(getClassName(entry.getKey()), constructor);

            JavaFile javaFile = JavaFile.builder(getPackajeName(entry.getKey()), binder)
                    .addFileComment("Generated code from Magic. Do not modify!")
                    .build();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private String getPackajeName(String qualifiedName) {
        return qualifiedName.substring(0, qualifiedName.lastIndexOf(CHAR_DOT));
    }

    private String getClassName(String qualifiedName) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(CHAR_DOT) + 1);
    }

    private TypeSpec createClass(String className, MethodSpec constructor) {
        return TypeSpec
                .classBuilder(className + MAGIC_SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .addMethod(constructor)
                .build();
    }


    private MethodSpec createConstructor(List<MagicElement> magicElements) {

        MagicElement firstElement = magicElements.get(0);


        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(firstElement.getElement().getEnclosingElement().asType()), CONST_PARAM_TARGET_NAME)
                .addParameter(String.class, "buildType");
//                .addStatement("$T release=buildType.equals($S)", TypeName.BOOLEAN, "release")
//                .addStatement("$T debug=buildType.equals($S)", TypeName.BOOLEAN, "debug");

        for (MagicElement element : magicElements) {
            addStatement(builder, element);
        }
        return builder.build();
    }

    /**
     * 声明变量
     * <ul>
     * <li>$L 值的替换</li>
     * <li>$S 字符串替换</li>
     * <li>$T 类型替换</li>
     * </ul>
     *
     * @param builder
     * @param magicElement
     */
    private void addStatement(MethodSpec.Builder builder, MagicElement magicElement) {
        if (magicElement instanceof OnlyDebugMagic) {
            builder.addStatement("target.$L.setVisibility(buildType.equals($S) ? View.VISIBLE : View.GONE)",
                    magicElement.getName(),
                    "debug");
        } else if (magicElement instanceof NotReleaseMagic) {

            builder.addStatement("target.$L.setVisibility(!buildType.equals($S) ? $T.VISIBLE : $T.GONE)",
                    magicElement.getName(),
                    "release",
                    VIEW);

        } else if (magicElement instanceof OnlyAvailableMagic) {
            OnlyAvailableMagic onlyAvailableMagic = (OnlyAvailableMagic) magicElement;

            builder.addStatement("target.$L.setVisibility(buildType.equals($S) ? $T.VISIBLE : $T.GONE)",
                    onlyAvailableMagic.getName(),
                    onlyAvailableMagic.getTypeName(),
                    VIEW,
                    VIEW);
        }
    }


    private void findAndParseMagic(RoundEnvironment env,
                                   Map<String, List<MagicElement>> typeElementListMap) {
        for (Element element : env.getElementsAnnotatedWith(OnlyDebug.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            MagicElement magicElement = new OnlyDebugMagic(element);
            final String qualifiedName = magicElement.getQualifiedName();
            List<MagicElement> magicElements = typeElementListMap.get(qualifiedName);
            if (magicElements == null) {
                magicElements = new ArrayList<>();
                typeElementListMap.put(qualifiedName, magicElements);
            }
            magicElements.add(magicElement);
        }
        for (Element element : env.getElementsAnnotatedWith(NotRelease.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            MagicElement magicElement = new NotReleaseMagic(element);
            final String qualifiedName = magicElement.getQualifiedName();
            List<MagicElement> magicElements = typeElementListMap.get(qualifiedName);
            if (magicElements == null) {
                magicElements = new ArrayList<>();
                typeElementListMap.put(qualifiedName, magicElements);
            }
            magicElements.add(magicElement);
        }
        for (Element element : env.getElementsAnnotatedWith(OnlyAvailable.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            MagicElement magicElement = new OnlyAvailableMagic(element);
            final String qualifiedName = magicElement.getQualifiedName();
            List<MagicElement> magicElements = typeElementListMap.get(qualifiedName);
            if (magicElements == null) {
                magicElements = new ArrayList<>();
                typeElementListMap.put(qualifiedName, magicElements);
            }
            magicElements.add(magicElement);
        }


    }
//
//    private void addAnnotationElement(Map<String, List<MagicElement>> typeElementListMap, Element element) {
//        MagicElement magicElement = new MagicElement(element);
//        final String qualifiedName = magicElement.getQualifiedName();
//        List<MagicElement> magicElements = typeElementListMap.get(qualifiedName);
//        if (magicElements == null) {
//            magicElements = new ArrayList<MagicElement>();
//            typeElementListMap.put(qualifiedName, magicElements);
//        }
//        magicElements.add(magicElement);
//    }
}
