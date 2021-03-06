package blade;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Target(FIELD)
@Retention(SOURCE)
public @interface State {

    Class<? extends Bundler> value() default Bundler.class;
}
