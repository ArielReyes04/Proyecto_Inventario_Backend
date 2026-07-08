package ec.edu.espe.master.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EcuadorianIdValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EcuadorianId {
    String message() default "El documento de identidad (Cédula o RUC) es inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
