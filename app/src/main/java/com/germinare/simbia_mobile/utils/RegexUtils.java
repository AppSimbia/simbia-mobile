package com.germinare.simbia_mobile.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static boolean validateCNPJ(String cnpj){
        Pattern pattern = Pattern.compile("([0-9]{2}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[\\/]?[0-9]{4}[-]?[0-9]{2})|([0-9]{3}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[-]?[0-9]{2})");
        Matcher matcher = pattern.matcher(cnpj);
        return matcher.matches();
    }

    public static boolean validatePassword(String password){
//        Pattern pattern = Pattern.compile("^.{8,}$\n");
//        Matcher matcher = pattern.matcher(password);
        return true;
    }

    public static boolean validateEmail(String email){
//        Pattern pattern = Pattern.compile("^[a-z]");
//        Matcher matcher = pattern.matcher(email);
        return true;
    }

    public static boolean validateCep(String cep){
        Pattern pattern = Pattern.compile("^\\d{5}-?\\d{3}$");
        Matcher matcher = pattern.matcher(cep);
        return matcher.matches();
    }

}
