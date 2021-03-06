package eu.f3rog.blade.compiler.parcel;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import blade.Parcel;
import eu.f3rog.blade.compiler.BaseProcessorModule;
import eu.f3rog.blade.compiler.builder.ClassManager;
import eu.f3rog.blade.compiler.util.ProcessorError;

/**
 * Class {@link ParcelProcessorModule}
 *
 * @author FrantisekGazo
 * @version 2015-12-19
 */
public class ParcelProcessorModule
        extends BaseProcessorModule {

    @Override
    public void process(TypeElement bladeElement) throws ProcessorError {
        // do nothing
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws ProcessorError {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Parcel.class);
        for (Element e : elements) {
            if (e.getKind() != ElementKind.CLASS) continue;

            ClassManager.getInstance()
                    .getHelper((TypeElement) e)
                    .getModule(ParcelHelperModule.class)
                    .add((TypeElement) e);
        }
    }

}
