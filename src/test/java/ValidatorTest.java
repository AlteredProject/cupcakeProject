import app.services.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest {
    @Test
    void shouldRejectNullOrBlankMail() {
        assertEquals("Email skal udfyldes",
            Validator.validateUser(null, "Password1!"), Validator.validateUser("", "Password1!"));
    }

    @Test
    void shouldRejectInvalidMail() {
        assertEquals("Email er ikke gyldig",
            Validator.validateUser("invalidEmail", "Password1!"));
    }

    @Test
    void shouldRejectNullOrBlankPassword() {
        assertEquals("Password skal udfyldes",
            Validator.validateUser("test@test", null), Validator.validateUser("test@test", ""));
    }

    @Test
    void shouldRejectShortPassword() {
        assertEquals("Password skal være mindst 8 tegn",
            Validator.validateUser("test@test.com", "Pass1!"));
    }

    @Test
    void shouldRejectNumberlessPassword() {
        assertEquals("Password skal indeholde mindst ét tal",
                Validator.validateUser("test@test.com", "Password!"));
    }

    @Test
    void shouldRejectPasswordWithoutSpecialCharacter() {
        assertEquals("Password skal indeholde mindst ét specialtegn",
                Validator.validateUser("test@test.com", "Password1"));
    }

    @Test
    void shouldRejectInvalidUser() {
        assertNull(Validator.validateUser("test@test.com", "Password1@"));
    }
}
