package org.devoware.homonculus.validators.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = FieldCompareValidator.class)
public @interface FieldCompare {
    String message() default "{first} must be {operator} {second}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * @return the first field
     */
    String first();

    
    /**
     * @return the comparison operator
     */
    ComparisonOperator operator();
    
    /**
     * @return the second field
     */
    String second();
    
    
    /**
     * @return the class of the two fields in question
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Comparable> fieldClass();
    
    
}
