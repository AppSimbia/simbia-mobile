package com.germinare.simbia_mobile.utils;

public class CnpjCepUtils {

    public static String formartterCnpj(String cnpj){
        if (cnpj == null || cnpj.isEmpty()) {
            return "";
        }

        String cleanedCnpj = cnpj.replaceAll("[^0-9]", "");

        StringBuilder formattedCnpj = new StringBuilder(cleanedCnpj);

        int length = formattedCnpj.length();

        if (length > 12) {
            formattedCnpj.insert(12, '-');
        }
        if (length > 8) {
            formattedCnpj.insert(8, '/');
        }
        if (length > 5) {
            formattedCnpj.insert(5, '.');
        }
        if (length > 2) {
            formattedCnpj.insert(2, '.');
        }

        return formattedCnpj.toString();
    }

    public static String sanitize(String value){
        return value
                .trim()
                .replaceAll("[^0-9]", "");
    }

}
