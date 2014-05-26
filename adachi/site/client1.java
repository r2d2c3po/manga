package adachi.site;
import java.util.Collection;
import adachi.site.dao.*;
import adachi.site.model.*;
/**
*Simple client that retrieves data from an already populated database
*author: Joey Y. Ni
*Last update: April 25, 2014
*/

public class client1
{
	
	public static void main(String[] args) {
		DatabaseManager dbm = new DatabaseManager(); 
		dbm.clearTables();
		Author AdachiMitsuru = dbm.insertAuthor("Adachi Mitsuru", "Japanese", "Himself");
		Author unknown = dbm.insertAuthor("Unknown", "Default-Japanese", "Unknown");
		Manga soredemo = dbm.insertManga(1, "soredemo", "romance", false, 2010, 15, "Unknown");
		Manga katsu = dbm.insertManga(4, "Katsu!", "sports", true, 1997, 186, "Adachi Mitsuru");
		Manga crossgames = dbm.insertManga(5, "Cross Games", "sports", true, 2004, 256, "Adachi Mitsuru"); 
		Manga h2 = dbm.insertManga(6, "H2", "sports", true, 1999, 190, "Adachi Mitsuru");
		Manga nogamenolife= dbm.insertManga(2, "nogamenolife", "fantasy", false, 2014, 2, "Unknown");
		Manga nglife = dbm.insertManga(3, "nglife", "romance", true, 2009, 50, "Unknown");		
		dbm.insertChap(2, "hello world", 3); //mangaID, chapTitle, chapNumber
		dbm.insertChap(1, "Development", 16); 
		dbm.commit();
		//get all manga by Adachi Mitsuru
		Collection <Manga> manga = AdachiMitsuru.getManga(); 	
		for (Manga m: manga) {
			System.out.println("Manga ID:"+m.getMangaID()+"  Manga Title:"+m.getTitle());
		}
		dbm.commit();
		System.out.println("NGLife's author is:" + nglife.getAuthor());
		System.out.println("No Game No Life's release year is:" + nogamenolife.getReleaseYear());
		System.out.println("Latest chapter of H2:"+h2.getLatestChapter());
		System.out.println("Katsu's genrr:"+katsu.getGenre());
		System.out.println("Cross Games' status(false for incomplete, true for complete):"+crossgames.getStatus());
		System.out.println("Soredemo's ID:"+soredemo.getMangaID());
		System.out.println("Manga's with unknown authors:"+unknown.getManga());
		crossgames.setStatus(false);
		System.out.println("changing manga status of Cross Games:"+ crossgames.getStatus());
		dbm.commit();
		
		dbm.close();
		
		System.out.println("Done");
	}
		
}

