package io.github.zodh.college.manager.utils;


import java.security.SecureRandom;
import java.util.Base64;

public class RandomGenerator {

  public static String generateRequestId(String user) {
    var length = Math.max(user.length(), 13);
    var secureRandom = new SecureRandom();
    var bytes = new byte[length];
    secureRandom.nextBytes(bytes);
    var encoder = Base64.getUrlEncoder().withoutPadding();
    return encoder.encodeToString(bytes);
  }

}
