package util;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Created by David on 2015-12-16.
 */
public class Registration {

    public static boolean verifyFirstname(String firstName)
    {
        return firstName.matches("^[a-zA-ZåäöÅÄÖ-]{2,30}$"); // Matchar a till z & A - Z med minst 2 bokstäver och max 30 bokstäver
    }

    public static boolean verifyLastname(String lastName)
    {
        return lastName.matches("^[a-zA-ZåäöÅÄÖ-]{2,30}$"); // Matchar a till z & A - Z med minst 2 bokstäver och max 30 bokstäver
    }

    public static boolean verifyBirthday(String birthday) {
        return birthday.matches("^\\d{6}-\\d{4}$");
    }

    public static boolean verifyPhoneNr(String phoneNr) {
        return phoneNr.matches("^\\d{10,12}$"); // Matchar om telefonnumret innehåller endast siffror och är 10 till 12 siffror långt
    }

    public static boolean verifyEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}
