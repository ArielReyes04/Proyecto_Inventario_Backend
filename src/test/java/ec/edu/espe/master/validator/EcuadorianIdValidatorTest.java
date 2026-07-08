package ec.edu.espe.master.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class EcuadorianIdValidatorTest {

    private EcuadorianIdValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new EcuadorianIdValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void isValid_NullOrEmpty_ReturnsTrue() {
        assertTrue(validator.isValid(null, context));
        assertTrue(validator.isValid("", context));
        assertTrue(validator.isValid("   ", context));
    }

    @Test
    void isValid_InvalidLength_ReturnsFalse() {
        assertFalse(validator.isValid("123456789", context)); // 9
        assertFalse(validator.isValid("12345678901", context)); // 11
        assertFalse(validator.isValid("12345678901234", context)); // 14
    }

    @Test
    void isValid_NonNumeric_ReturnsFalse() {
        assertFalse(validator.isValid("172a394851", context));
        assertFalse(validator.isValid("1723394851abc", context));
    }

    @Test
    void isValid_InvalidProvinceCode_ReturnsFalse() {
        assertFalse(validator.isValid("0023394851", context)); // 00
        assertFalse(validator.isValid("2523394851", context)); // 25
    }

    @Test
    void isValid_ValidCedula_ReturnsTrue() {
        assertTrue(validator.isValid("1710034065", context)); 
    }

    @Test
    void isValid_InvalidCedulaCheckDigit_ReturnsFalse() {
        assertFalse(validator.isValid("1710034066", context));
    }

    @Test
    void isValid_ValidRucNatural_ReturnsTrue() {
        assertTrue(validator.isValid("1710034065001", context));
    }

    @Test
    void isValid_InvalidRucNaturalSuffix_ReturnsFalse() {
        assertFalse(validator.isValid("1710034065002", context));
    }

    @Test
    void isValid_ValidRucPrivada_ReturnsTrue() {
        assertTrue(validator.isValid("1790011674001", context)); // Valid private RUC
    }

    @Test
    void isValid_InvalidRucPrivadaCheckDigit_ReturnsFalse() {
        assertFalse(validator.isValid("1790011675001", context));
    }
    
    @Test
    void isValid_InvalidLengthRucPrivada_ReturnsFalse() {
        assertFalse(validator.isValid("1790011674", context)); // Length 10
    }

    @Test
    void isValid_ValidRucPublica_ReturnsTrue() {
        assertTrue(validator.isValid("1760001550001", context)); // Valid public RUC
    }

    @Test
    void isValid_InvalidRucPublicaCheckDigit_ReturnsFalse() {
        assertFalse(validator.isValid("1760001560001", context));
    }
    
    @Test
    void isValid_InvalidLengthRucPublica_ReturnsFalse() {
        assertFalse(validator.isValid("1760001550", context)); // Length 10
    }

    @Test
    void isValid_InvalidThirdDigit_ReturnsFalse() {
        assertFalse(validator.isValid("1770034065", context)); // 3rd digit is 7
    }
}
