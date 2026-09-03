package com.school_management.overseas_language_centre.component;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;

@Component
public class StringNormalizer {
    public String normalizeUpper(String value){
        return value == null ? null :
                Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    public String normalizeTrim(String value){
        return value == null ? null : value.trim();
    }

    public String normalizeLower(String value){
        return value == null ? null : value.toLowerCase(Locale.ROOT).trim();
    }
}
