import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Before
    public void setup() {
        Fixtures.deleteDatabase();
    }

    @Test
    public void createAndRetrieveUser() {
        new User("bob@gmail.com", "secret", "Bob").save();
        User bob = User.find("byEmail", "bob@gmail.com").first();

        // Test
        assertNotNull(bob);
        assertEquals("Bob", bob.fullname);
    }

    @Test
    public void tryConnectAsUser() {
        new User("bob@gmail.com", "secret", "Bob").save();

        // Test
        assertNotNull(User.connect("bob@gmail.com", "secret"));
        assertNull(User.connect("bob@gmail.com", "badpassword"));
        assertNull(User.connect("tom@gmail.com", "secret"));
    }

    @Test
    public void createPost() {
        User bob = new User("bob@gmail.com", "secret", "Bob").save();
        new Post(bob, "My first post", "Hello world!").save();

        // Test that the post has been created
        assertEquals(1, Post.count());

        // Retrieve all posts created by Bob
        List<Post> bobPosts = Post.find("byAuthor", bob).fetch();

        // Tests
        assertEquals(1, bobPosts.size());
        Post firstPost = bobPosts.get(0);
        assertNotNull(firstPost);
        assertEquals(bob, firstPost.author);
        assertEquals("My first post", firstPost.title);
        assertEquals("Hello world!", firstPost.content);
        assertNotNull(firstPost.postedAt);
    }

    @Test
    public void postComments() {
        User bob = new User("bob@gmail.com", "secret", "Bob").save();
        Post bobPost = new Post(bob, "My first post", "Hello world").save();

        new Comment(bobPost, "Jeff", "Nice post").save();
        new Comment(bobPost, "Tom", "I knew that!").save();

        // Retrieve all comments
        List<Comment> bobPostComments = Comment.find("byPost", bobPost).fetch();

        // Test
        assertEquals(2, bobPostComments.size());

        Comment firstComment = bobPostComments.get(0);
        assertNotNull(firstComment);
        assertEquals("Jeff", firstComment.author);
        assertEquals("Nice post", firstComment.content);
        assertNotNull(firstComment.postedAt);

        Comment secondComment = bobPostComments.get(1);
        assertNotNull(secondComment);
        assertEquals("Tom", secondComment.author);
        assertEquals("I knew that!", secondComment.content);
        assertNotNull(secondComment.postedAt);
    }

    @Test
    public void useTheCommentsRelation() {
        User bob = new User("bob@gmail.com", "secret", "Bob").save();
        Post bobPost = new Post(bob, "My first post", "Hello world").save();

        bobPost.addComment("Jeff", "Nice post");
        bobPost.addComment("Tom", "I knew that !");

        assertEquals(1, User.count());
        assertEquals(1, Post.count());
        assertEquals(2, Comment.count());

        bobPost = Post.find("byAuthor", bob).first();
        assertNotNull(bobPost);

        assertEquals(2, bobPost.comments.size());
        assertEquals("Jeff", bobPost.comments.get(0).author);
        assertEquals("Tom", bobPost.comments.get(1).author);

        bobPost.delete();

        assertEquals(1, User.count());
        assertEquals(0, Post.count());
        assertEquals(0, Comment.count());
    }

    @Test
    public void fullTest() {
        Fixtures.loadModels("data.yml");

        // Count objects
        assertEquals(2, User.count());
        assertEquals(3, Post.count());
        assertEquals(3, Comment.count());

        // Try to connect as users
        assertNotNull(User.connect("bob@gmail.com", "secret"));
        assertNotNull(User.connect("jeff@gmail.com", "secret"));
        assertNull(User.connect("bob@gmail.com", "badpassword"));
        assertNull(User.connect("tom@gmail.com", "secret"));

        // Find all Bob's posts
        List<Post> bobPosts = Post.find("author.email", "bob@gmail.com").fetch();
        assertEquals(2, bobPosts.size());

        // Find all comments related to Bob's posts
        List<Comment> bobComments = Comment.find("post.author.email", "bob@gmail.com").fetch();
        assertEquals(3, bobComments.size());

        // Find the most recent post
        Post frontPost = Post.find("order by postedAt desc").first();
        assertNotNull(frontPost);
        assertEquals("About the model layer", frontPost.title);

        // Check that the most recent post has two comments
        assertEquals(2, frontPost.comments.size());

        // Post a new comment
        frontPost.addComment("Jim", "Hello guys");
        assertEquals(3, frontPost.comments.size());
        assertEquals(4, Comment.count());
    }

    @Test
    public void testTags() {
        User bob = new User("bob@gmail.com", "secret", "Bob").save();

        Post bobPost = new Post(bob, "My first post", "Hello world").save();
        Post anotherBobPost = new Post(bob, "Hop", "Hello world").save();

        assertEquals(0, Post.findTaggedWith("Red").size());

        bobPost.tagItWith("Red").tagItWith("Blue").save();
        anotherBobPost.tagItWith("Red").tagItWith("Green").save();

        assertEquals(2, Post.findTaggedWith("Red").size());
        assertEquals(1, Post.findTaggedWith("Blue").size());
        assertEquals(1, Post.findTaggedWith("Green").size());
        assertEquals(1, Post.findTaggedWith("Red", "Blue").size());
        assertEquals(1, Post.findTaggedWith("Red", "Green").size());
        assertEquals(0, Post.findTaggedWith("Red", "Green", "Blue").size());
        assertEquals(0, Post.findTaggedWith("Green", "Blue").size());

        List<Map> cloud = Tag.getCloud();
        assertEquals(
            "[{tag=Blue, pound=1}, {tag=Green, pound=1}, {tag=Red, pound=2}]",
            cloud.toString()
        );
    }
}