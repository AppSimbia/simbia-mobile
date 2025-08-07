package com.germinare.simbia_mobile.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static boolean validateCNPJ(String cnpj){
        Pattern pattern = Pattern.compile("([0-9]{2}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[\\/]?[0-9]{4}[-]?[0-9]{2})|([0-9]{3}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[-]?[0-9]{2})");
        Matcher matcher = pattern.matcher(cnpj);
        return matcher.matches();
    }

    public static boolean validatePassword(String password){
        Pattern pattern = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[$*&@#])(?:([0-9a-zA-Z$*&@#])(?!\\1)){8,}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean validateEmail(String email){
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validateCep(String cep){
        Pattern pattern = Pattern.compile("^\\d{5}-?\\d{3}$");
        Matcher matcher = pattern.matcher(cep);
        return matcher.matches();
    }
}