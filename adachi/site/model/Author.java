package adachi.site.model;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import adachi.site.dao.*;
/**
	*Model object for a row in Author table
	*author: Joey Y. Ni
	*/
public class Author {
	
		private AuthorDAO dao;
		private List<Manga> manga;
		private String authorName;
		private String language;
		private String artist;
		
		public Author(AuthorDAO dao, String aName, String language, String artist){
			this.dao=dao;
			this.authorName=aName;
			this.language=language;
			this.artist=artist;
			this.manga=new ArrayList<Manga>();
		}

		//Collection of get methods
		public String getAuthor() { return authorName; }
		public Collection<Manga> getManga() { return dao.getManga(authorName); }
		public String getLanguage() { return language; }
		public String getArtist() { return artist; }
		//No set methods since items in this class is not supposed to change over time.
		//add manga to author
		public void addManga(Manga m){ manga.add(m); }
		

}
