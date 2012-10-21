package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
@Table(name="posts")
public class Post extends Model {
	
	public String title;
	public Date postedAt;	
	public String content;
	@ManyToOne
	public User author;
	
	public Post(User author, String title, String content) {
		this.author = author;
		this.title = title;
		this.content = content;
		this.postedAt = new Date();
	}
}