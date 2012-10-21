package models;
 
import java.util.*;
import javax.persistence.*;

import org.hibernate.annotations.Type;
 
import play.db.jpa.*;
import play.data.validation.*;
 
@Entity
@Table(name="comments")
public class Comment extends Model {
    
    @Required
    public String author;
    @Required
    public Date postedAt;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Required
    @MaxSize(10000)
    public String content;
    @ManyToOne
    @Required
    public Post post;

    public Comment(Post post, String author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
    }
}