package cz.bankid.bankid_coding_task.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

public class CurrencyUtils {

    public static BigDecimal parseDecimalFromCzechFormat(String rate) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault()); // Uses system default locale
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator(' ');

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalFormatSymbols(symbols);
        decimalFormat.setParseBigDecimal(true);

        try {
            return (BigDecimal) decimalFormat.parse(rate.trim());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid number format: " + rate, e);
        }
    }
}
