package com.school_management.overseas_language_centre.encryption;


public interface AesGcmService {
    String encrypt(String plainText);
    String decrypt(String cipherText);
}
