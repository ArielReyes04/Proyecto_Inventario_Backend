package ec.edu.espe.master.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EcuadorianIdValidator implements ConstraintValidator<EcuadorianId, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // Dejar la validación de no nulo a @NotBlank
        }

        String id = value.trim();

        if (id.length() != 10 && id.length() != 13) {
            return false;
        }

        if (!id.matches("^[0-9]+$")) {
            return false;
        }

        int provinceCode = Integer.parseInt(id.substring(0, 2));
        if (provinceCode < 1 || provinceCode > 24) {
            if (provinceCode != 30) {
                return false;
            }
        }

        int thirdDigit = Integer.parseInt(id.substring(2, 3));

        if (thirdDigit < 6) {
            if (id.length() == 13 && !id.substring(10, 13).equals("001")) {
                return false;
            }

            int checkDigit = Integer.parseInt(id.substring(9, 10));
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                int val = Integer.parseInt(id.substring(i, i + 1));
                if (i % 2 == 0) {
                    val = val * 2;
                    if (val >= 10) val = val - 9;
                }
                sum += val;
            }
            int nextMultiple10 = (int) Math.ceil(sum / 10.0) * 10;
            int calculatedDigit = nextMultiple10 - sum;
            int finalDigit = calculatedDigit == 10 ? 0 : calculatedDigit;

            return finalDigit == checkDigit;
        } else if (thirdDigit == 9) {
            if (id.length() != 13) return false;

            int checkDigit = Integer.parseInt(id.substring(9, 10));
            int[] coefficients = {4, 3, 2, 7, 6, 5, 4, 3, 2};
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += Integer.parseInt(id.substring(i, i + 1)) * coefficients[i];
            }
            int remainder = sum % 11;
            int calculatedDigit = remainder == 0 ? 0 : 11 - remainder;

            return calculatedDigit == checkDigit;
        } else if (thirdDigit == 6) {
            if (id.length() != 13) return false;

            int checkDigit = Integer.parseInt(id.substring(8, 9));
            int[] coefficients = {3, 2, 7, 6, 5, 4, 3, 2};
            int sum = 0;
            for (int i = 0; i < 8; i++) {
                sum += Integer.parseInt(id.substring(i, i + 1)) * coefficients[i];
            }
            int remainder = sum % 11;
            int calculatedDigit = remainder == 0 ? 0 : 11 - remainder;

            return calculatedDigit == checkDigit;
        }

        return false;
    }
}
