package adachi.site.model;
import adachi.site.dao.*;

/**
*Model object for a row in chapter table
*author: Joey Y. Ni
*/

public class Chapter{
	private ChapterDAO dao;
	private int mangaID;
	private String chapterTitle;
	private int chapterNum;
	
	public Chapter(ChapterDAO dao, int mangaid, String chapterTitle, int chapterNum){
		this.dao=dao;
		this.mangaID=mangaid;
		this.chapterTitle=chapterTitle;
		this.chapterNum=chapterNum;
	}

	//Collection of get methods
	public int getMangaID() { return mangaID;}
	public String getChapterTitle() { return chapterTitle; }
	public int getChapterNum() { return chapterNum; }
	
	//set methods
	public void setChapterNum(int ch) { this.chapterNum=ch; }
	public void setChapterTitle(String title) { 
		this.chapterTitle= title; 
		dao.changeTitle(mangaID, chapterNum, title);
	}
	//add chapter
	public void add(String title, int num) {
		dao.insert(mangaID, title, num);
	}
}
		
