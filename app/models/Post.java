package models;

import java.util.*;
import javax.persistence.*;

import org.hibernate.annotations.Type;

import play.db.jpa.*;

@Entity
@Table(name="posts")
public class Post extends Model {
	
	public String title;
	public Date postedAt;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	public String content;
	@ManyToOne
	public User author;
	@OneToMany(mappedBy="post", cascade=CascadeType.ALL)
	public List<Comment> comments;
	
	public Post(User author, String title, String content) {
		this.comments = new ArrayList<Comment>();
		this.author = author;
		this.title = title;
		this.content = content;
		this.postedAt = new Date();
	}
	
	public Post addComment(String author, String content) {
		Comment newComment = new Comment(this, author, content).save();
		this.comments.add(newComment);
		this.save();
		return this;
	}
}