package com.khaled.creditcard;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Utils {

    static Boolean isValidCreditCardNumber(String creditCardNumber) {
        String regex = "^(?:(?<visa>4[0-9]{12}(?:[0-9]{3})?)|" +
                "(?<mastercard>5[1-5][0-9]{14})|" +
                "(?<discover>6(?:011|5[0-9]{2})[0-9]{12})|" +
                "(?<amex>3[47][0-9]{13})|" +
                "(?<diners>3(?:0[0-5]|[68][0-9])?[0-9]{11})|" +
                "(?<jcb>(?:2131|1800|35[0-9]{3})[0-9]{11}))$";

        Pattern pattern = Pattern.compile(regex);
        String card = creditCardNumber.replaceAll(" ", "");

        //Match the card
        Matcher matcher = pattern.matcher(card);
        return matcher.matches();
    }

    static Boolean isValidExpDate(String expDate) {
        if (expDate.isEmpty() || expDate.equalsIgnoreCase("MM/YY")) {
            return false;
        } else if (expDate.length() > 5) {
            return false;
        } else if (expDate.length() > 2 && expDate.contains("/")) {
            String[] dates = expDate.split("/");
            if (dates.length >= 1) {
                try {
                    return Integer.valueOf(dates[0]) <= 12;
                } catch (NumberFormatException e) {
                    Log.e(Utils.class.getName(), e.getMessage(), e);
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
