package com.deligo.RestApi.Utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class SqlInjectionValidator implements ConstraintValidator<NoSqlInjection, String> {

    // SQL injection patterns to detect
    private static final Pattern[] SQL_INJECTION_PATTERNS = {
        // Basic SQL injection patterns
        Pattern.compile("(?i)\\b(SELECT|INSERT|UPDATE|DELETE|DROP|ALTER|CREATE|EXEC|UNION|WHERE)\\b.*?\\b(FROM|INTO|TABLE|DATABASE|EXECUTE)\\b"),
        Pattern.compile("(?i)\\b(UNION|SELECT)\\b.*?\\b(FROM)\\b"),
        Pattern.compile("(?i)(\\bOR\\b|\\bAND\\b)\\s+\\d+\\s*=\\s*\\d+"),
        Pattern.compile("(?i)(\\bOR\\b|\\bAND\\b)\\s+'[^']*'\\s*=\\s*'[^']*'"),
        Pattern.compile("(?i)--\\s*$"),
        Pattern.compile("(?i);\\s*$"),
        Pattern.compile("(?i)/\\*.*?\\*/"),
        Pattern.compile("(?i)\\bEXEC\\b\\s+(\\w+)"),
        Pattern.compile("(?i)';\\s*--"),
        Pattern.compile("(?i)';\\s*#"),
        Pattern.compile("(?i)'\\s+OR\\s+'1'\\s*=\\s*'1"),
        Pattern.compile("(?i)'\\s+OR\\s+1\\s*=\\s*1"),
        Pattern.compile("(?i)\\bSYSTEM_USER\\b"),
        Pattern.compile("(?i)\\bTABLE_NAME\\b"),
        Pattern.compile("(?i)\\bCOLUMN_NAME\\b"),
        Pattern.compile("(?i)\\bINFORMATION_SCHEMA\\b"),
        Pattern.compile("(?i)\\bSYS\\b\\.\\b\\w+\\b"),
        Pattern.compile("(?i)\\bSYSDATE\\b"),
        Pattern.compile("(?i)\\bCURRENT_USER\\b"),
        Pattern.compile("(?i)\\bCONCAT\\b\\s*\\("),
        Pattern.compile("(?i)\\bCHAR\\b\\s*\\("),
        Pattern.compile("(?i)\\bDECLARE\\b"),
        Pattern.compile("(?i)\\bWAITFOR\\b\\s+\\bDELAY\\b"),
        Pattern.compile("(?i)\\bWAITFOR\\b\\s+\\bTIME\\b"),
        Pattern.compile("(?i)\\bBENCHMARK\\b\\s*\\("),
        Pattern.compile("(?i)\\bSLEEP\\b\\s*\\(")
    };

    @Override
    public void initialize(NoSqlInjection constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values are handled by @NotNull if needed
        }

        // Check for SQL injection patterns
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(value).find()) {
                return false; // SQL injection pattern detected
            }
        }

        return true; // No SQL injection patterns found
    }
}