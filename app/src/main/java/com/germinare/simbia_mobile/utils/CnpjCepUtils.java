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
    public static String formartterCep(String cep){
        if (cep == null || cep.isEmpty()) {
            return "";
        }

        String cleanedCep = cep.replaceAll("[^0-9]", "");

        StringBuilder formattedCep = new StringBuilder(cleanedCep);

        int length = formattedCep.length();

        if (length > 5) {
            formattedCep.insert(5, '-');
        }
        return formattedCep.toString();
    }
    public static String sanitizeCep(String cep){
        String cleanedCep = cep
                .trim()
                .replaceAll(".", "")
                .replaceAll("-", "");

        return cleanedCep;
    }
    public static String sanitizeCnpj(String cpnj){
        String cleanedCnpj = cpnj
                .trim()
                .replaceAll(".", "")
                .replaceAll("/", "")
                .replaceAll("-", "");

        return cleanedCnpj;
    }

}
