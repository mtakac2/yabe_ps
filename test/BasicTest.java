import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Test
    public void createAndRetrieveUser() {
    	User bob = new User("bob@gmail.com", "secret", "Bob").save();
    	
    	assertNotNull(bob);
    	assertEquals("Bob", bob.fullname);
    }
    
    @Test
    public void tryConnectAsUser() {
    	User bob = new User("bob@gmail.com", "secret", "Bob").save();
    	
    	assertNotNull(User.connect(bob.email, bob.password));
    	assertNull(User.connect(bob.email, "badpassword"));
    	assertNull(User.connect("tom@gmail.com", bob.password));
    }
}
