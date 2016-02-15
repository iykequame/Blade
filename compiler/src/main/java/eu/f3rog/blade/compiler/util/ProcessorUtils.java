package eu.f3rog.blade.compiler.util;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.sun.tools.javac.code.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Class {@link ProcessorUtils}.
 *
 * @author Frantisek Gazo
 * @version 2015-09-21
 */
public class ProcessorUtils {

    private static ProcessingEnvironment sProcessingEnvironment;

    public static void setProcessingEnvironment(ProcessingEnvironment processingEnvironment) {
        sProcessingEnvironment = processingEnvironment;
    }

    public static Elements getElementUtils() {
        return sProcessingEnvironment.getElementUtils();
    }

    public static Types getTypeUtils() {
        return sProcessingEnvironment.getTypeUtils();
    }

    public interface IGetter<A, T> {
        T get(A obj);
    }

    public static String fullName(ClassName className) {
        //return String.format("%s.%s", className.packageName(), className.simpleName());
        StringBuilder sb = new StringBuilder();

        sb.append(className.packageName());

        for (int i = 0; i < className.simpleNames().size(); i++) {
            String name = className.simpleNames().get(i);
            sb.append(".").append(name);
        }
        return sb.toString();
    }

    /**
     * Discovers if {@code element} is annotated with {@code needed} annotation.
     */
    public static boolean isAnnotated(final Element element, final TypeName needed) {
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        // if given element has no annotation => end now
        if (annotationMirrors == null || annotationMirrors.size() == 0) return false;
        // go through all annotation of given element
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            // check if found annotation is the same class as needed annotation
            if (needed.equals(ClassName.get(annotationMirror.getAnnotationType().asElement().asType())))
                return true;
        }
        return false;
    }

    /**
     * Retrieves {@link ClassName} from {@code annotation} with {@code getter} and if exception is thrown, retrieves it from the exception.
     */
    public static <A> ClassName getClass(final A annotation, final IGetter<A, Class<?>> getter) {
        ClassName className;
        try {
            className = ClassName.get(getter.get(annotation));
        } catch (MirroredTypeException mte) {
            try {
                className = (ClassName) ClassName.get(((DeclaredType) mte.getTypeMirror()).asElement().asType());
            } catch (Exception e) { // if there is 'primitive'.class
                className = null;
            }
        }
        return className;
    }

    /**
     * Retrieves {@link TypeName} from {@code annotation} with {@code getter} and if exception is thrown, retrieves it from the exception.
     */
    public static <A> TypeName getType(final A annotation, final IGetter<A, Class<?>> getter) {
        TypeName typeName;
        try {
            typeName = ClassName.get(getter.get(annotation));
        } catch (MirroredTypeException mte) {
            typeName = ClassName.get(mte.getTypeMirror());
        }
        return typeName;
    }

    /**
     * Retrieves {@link ClassName}s from {@code annotation} with {@code getter} and if exception is thrown, retrieves it from the exception.
     */
    public static <A> List<ClassName> getParamClasses(final A annotation, final IGetter<A, Class<?>[]> getter) {
        List<ClassName> className = new ArrayList<>();
        try {
            Class<?>[] classes = getter.get(annotation);
            for (Class<?> cls : classes) {
                className.add(ClassName.get(cls));
            }
        } catch (MirroredTypesException mte) {
            try {
                for (TypeMirror typeMirror : mte.getTypeMirrors()) {
                    className.add((ClassName) ClassName.get(((DeclaredType) typeMirror).asElement().asType()));
                }
            } catch (Exception e) { // if there is 'primitive'.class
            }
        }
        return className;
    }

    public static boolean inplements(final TypeName typeName, final Class interfaceClass) {
        return inplements(getTypeElement(typeName), ClassName.get(interfaceClass));
    }

    public static boolean inplements(final TypeElement element, final Class interfaceClass) {
        return inplements(element, ClassName.get(interfaceClass));
    }

    public static boolean inplements(final TypeElement element, final ClassName interfaceClassName) {
        TypeElement superClass = element;
        while (superClass != null) {
            for (int i = 0; i < superClass.getInterfaces().size(); i++) {
                TypeMirror interfaceType = superClass.getInterfaces().get(i);
                TypeName typeName = ClassName.get(interfaceType);
                if (typeName instanceof ParameterizedTypeName) {
                    ParameterizedTypeName ptn = (ParameterizedTypeName) typeName;
                    if (ptn.rawType.equals(interfaceClassName)) {
                        return true;
                    }
                } else {
                    if (typeName.equals(interfaceClassName)) {
                        return true;
                    }
                }
            }
            superClass = getSuperClass(superClass);
        }
        return false;
    }

    public static boolean isSubClassOf(final TypeElement element, final Class cls) {
        return isSubClassOf(element, ClassName.get(cls));
    }

    public static boolean isSubClassOf(final TypeElement element, final ClassName cls) {
        TypeElement superClass = element;
        do {
            superClass = getSuperClass(superClass);
            if (superClass != null && ClassName.get(superClass).equals(cls)) {
                return true;
            }
        } while (superClass != null);
        return false;
    }

    public static boolean isSubClassOf(final TypeElement element, final ClassName... classes) {
        TypeElement superClass = element;
        do {
            superClass = getSuperClass(superClass);
            for (ClassName cls : classes) {
                if (superClass != null && ClassName.get(superClass).equals(cls)) {
                    return true;
                }
            }
        } while (superClass != null);
        return false;
    }

    public static TypeElement getSuperClass(final TypeElement element) {
        if (element == null) return null;
        return (TypeElement) ((Symbol.ClassSymbol) element).getSuperclass().asElement();
    }

    public static String getParamName(final ClassName className) {
        return StringUtils.startLowerCase(className.simpleName()).replaceAll("_", "");
    }

    public static boolean hasSomeModifier(Element e, Modifier... modifiers) {
        if (e == null) {
            throw new IllegalStateException("Element cannot be null!");
        }
        Set<Modifier> m = e.getModifiers();
        for (int i = 0; i < modifiers.length; i++) {
            if (m.contains(modifiers[i])) return true;
        }
        return false;
    }

    public static boolean cannotHaveAnnotation(Element e) {
        return hasSomeModifier(e, Modifier.PRIVATE, Modifier.PROTECTED, Modifier.FINAL);
    }

    public static TypeElement getTypeElement(TypeName typeName) {
        String className;
        if (typeName instanceof ParameterizedTypeName) {
            className = ((ParameterizedTypeName) typeName).rawType.toString();
        } else {
            className = typeName.toString();
        }
        return sProcessingEnvironment.getElementUtils().getTypeElement(className);
    }

    /**
     * Finds requested supertype (can be also interface) of given type.
     */
    public static TypeName getSuperType(TypeMirror inspectedType, TypeName lookupType) {
        List<? extends TypeMirror> superTypes = ProcessorUtils.getTypeUtils().directSupertypes(inspectedType);

        for (TypeMirror typeMirror : superTypes) {
            TypeName tn = ClassName.get(typeMirror);
            if (tn instanceof ParameterizedTypeName) {
                ParameterizedTypeName paramTypeName = (ParameterizedTypeName) tn;
                if (paramTypeName.rawType.equals(lookupType)) {
                    return paramTypeName;
                }
            } else if (tn.equals(lookupType)) {
                return tn;
            }
        }

        for (TypeMirror typeMirror : superTypes) {
            TypeName tn = getSuperType(typeMirror, lookupType);
            if (tn != null) {
                return tn;
            }
        }

        return null;
    }

}
