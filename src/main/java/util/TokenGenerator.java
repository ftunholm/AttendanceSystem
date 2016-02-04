package util;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Created by LanfeaR on 2015-12-28.
 */
public class TokenGenerator {

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
