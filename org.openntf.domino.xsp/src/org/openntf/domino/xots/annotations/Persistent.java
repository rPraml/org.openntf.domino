package org.openntf.domino.xots.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Persistent {
	public enum Scope {
		SERVER, TEMPLATE, APPLICATION, USER, OBJECT
	}

	Scope scope() default Scope.SERVER;
}
