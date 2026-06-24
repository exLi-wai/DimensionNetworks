package com.lw.DimensionNetworks.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public final class EnergyFormat {

    private static final BigInteger THOUSAND = BigInteger.valueOf(1000L);
    private static final BigInteger MILLION = BigInteger.valueOf(1000000L);
    private static final BigInteger BILLION = BigInteger.valueOf(1000000000L);
    private static final BigInteger TRILLION = BigInteger.valueOf(1000000000000L);
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    private static final DecimalFormat WHOLE = new DecimalFormat("#,##0");
    private static final DecimalFormat TWO_DECIMAL = new DecimalFormat("0.##");

    private EnergyFormat() {
    }

    public static String formatFe(BigInteger value) {
        return format(value, "FE");
    }

    public static String formatCapacityFe(BigInteger value) {
        return value != null && value.compareTo(LONG_MAX) >= 0 ? "\u221E FE" : formatFe(value);
    }

    public static String format(BigInteger value, String unit) {
        BigInteger safeValue = value == null ? BigInteger.ZERO : value.max(BigInteger.ZERO);
        String suffix = unit == null || unit.isEmpty() ? "" : " " + unit;

        if (safeValue.compareTo(THOUSAND) < 0) {
            return WHOLE.format(safeValue) + suffix;
        }
        if (safeValue.compareTo(MILLION) < 0) {
            return WHOLE.format(safeValue) + suffix;
        }
        if (safeValue.compareTo(BILLION) < 0) {
            return scaled(safeValue, MILLION) + " M" + unit;
        }
        if (safeValue.compareTo(TRILLION) < 0) {
            return scaled(safeValue, BILLION) + " G" + unit;
        }

        return scientific(safeValue) + suffix;
    }

    private static String scaled(BigInteger value, BigInteger divisor) {
        BigDecimal decimal = new BigDecimal(value).divide(new BigDecimal(divisor), 2, RoundingMode.DOWN);
        return TWO_DECIMAL.format(decimal);
    }

    private static String scientific(BigInteger value) {
        String digits = value.toString();
        int exponent = digits.length() - 1;
        String mantissa;
        if (digits.length() == 1) {
            mantissa = digits;
        } else {
            String fraction = digits.substring(1, Math.min(3, digits.length()));
            mantissa = digits.charAt(0) + "." + fraction;
        }
        return mantissa + "E" + exponent;
    }
}
