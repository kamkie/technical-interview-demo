package team.jit.technicalinterviewdemo.manualregression.harness;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Declares a suite's stable kebab-case name and the suites that must run successfully first. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SuiteName {

    /** Kebab-case suite name with the numeric prefix from the plan, e.g. {@code "07-book-lifecycle"}. */
    String value();

    /** Suite names whose successful completion is required before this suite may run. */
    String[] requires() default {};

    /** Marks the suite as requiring an admin identity. Skipped (BLOCKED) when not configured. */
    boolean requiresAdminIdentity() default false;

    /** Marks the suite as requiring a regular-user identity. Skipped (BLOCKED) when not configured. */
    boolean requiresRegularIdentity() default false;

    /** Marks the suite as requiring a regular-user id. Skipped (BLOCKED) when not configured. */
    boolean requiresRegularUserId() default false;
}
