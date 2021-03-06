package eu.f3rog.blade.compiler.mvp;

import blade.mvp.IPresenter;
import blade.mvp.IView;
import eu.f3rog.blade.compiler.ErrorMsg;

/**
 * Enum {@link MvpErrorMsg}
 *
 * @author FrantisekGazo
 * @version 2015-10-17
 */
public interface MvpErrorMsg extends ErrorMsg {

    String Invalid_class_with_injected_Presenter = "Only Activity or Fragment subclass that implements " + IView.class.getCanonicalName() + " can inject class that implements " + IPresenter.class.getSimpleName() + ".";
    String Invalid_view_class = "Class must implement %s interface in order to work with %s.";
    String Missing_dagger_dependency = "Blade module 'mvp' requires dagger2 dependency! See https://github.com/FrantisekGazo/Blade/wiki/mvp for more information.";

}
