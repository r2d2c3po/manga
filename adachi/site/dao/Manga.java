package adachi.site.dao;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import adachi.site.model.*;
/**
	*Model object for a row in Author table
	*author: Joey Y. Ni
	*/
public class Manga {
		private int mangaID;
		private String title;
		private String genre;
		private boolean mangaStatus; //false for incomplete and true for complete
		private int releaseYear;
		private int latestChapter; 
	//latestChapter here means the last chapter updated where in Chapter it means the date latestChapter updated
		private String author;
		private MangaDAO dao;
		private List <Chapter> chapter;
		private Map<Integer, Manga> cache;
		
		public Manga(MangaDAO dao, int a, String b, String c, boolean d, int e, int f, String author){
			this.mangaID=a;
			this.title=b;
			this.genre=c;
			this.mangaStatus=d;
			this.releaseYear=e;
			this.latestChapter=f; 
			this.author=author;
			this.dao=dao;
			this.chapter=new ArrayList<Chapter>();
			this.cache=new HashMap<Integer, Manga>();
		}
		
		public String toString() { return mangaID + " " + title + "by:" + author; }
		//Collection of get methods of fields
		public Map<Integer, Manga> getCache() { return cache;}
		public int getMangaID(){ return mangaID; }
		public String getTitle() { return title; }
		public String getGenre() { return genre; }
		public boolean getStatus() { return mangaStatus; }
		public int getReleaseYear() { return releaseYear; }
		public int getLatestChapter() {return latestChapter; }
		public String getAuthor() { return author; }
		public Chapter getChapter(int c) { return chapter.get(c);}
		//Collection of two set methods for variables that can be changed. Other fields cannot be changed once set.
		public void setStatus(boolean status) { 
			this.mangaStatus= status; 
			dao.changeStatus(mangaID, status);
		}

		public void setLatestChapter(int ch) { this.latestChapter = ch; }
		
		//adding a new chapter
		/*public void addChapter(String chapterTitle, int num) {
			this.latestChapter++; 	
			Chapter.add(chapterTitle, num);
			chapter.add(chapterTitle, num);
			  
		}*/
}
		

