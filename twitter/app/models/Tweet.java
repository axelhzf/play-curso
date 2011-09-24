package models;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import net.sf.oval.constraint.MaxLength;

import play.data.validation.Max;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.modules.elasticsearch.annotations.ElasticSearchEmbedded;
import play.modules.elasticsearch.annotations.ElasticSearchIgnore;
import play.modules.elasticsearch.annotations.ElasticSearchable;

@ElasticSearchable
@Entity
public class Tweet extends Model {

	@Required
	@MaxLength(140)
	public String msg;

	@ElasticSearchIgnore
	@ManyToOne
	public User author;

	@ElasticSearchIgnore
	public Date date;

	public static Tweet create(String msg, User author){
		Tweet t = new Tweet();
		t.msg = msg;
		t.author = author;
		t.date = new Date();
		return t;
	}
	
}