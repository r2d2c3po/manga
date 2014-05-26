package adachi.site.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import adachi.site.model.*;

public class ChapterDAO{
	private Connection conn;
	private DatabaseManager dbm;
	private Map<Chapter, Integer> cache; //one chapter maps to one mangaID, no duplicate chapters
	
	public ChapterDAO(Connection conn, DatabaseManager db) {
		this.conn=conn; 
		this.dbm=db; 
		this.cache=new HashMap<Chapter, Integer>();
	}
	
	//Create table
	static void create(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "create table CHAPTER("
				+ "mangaID int not null, "
				+ "chapterTitle varchar(25), "
				+ "chapterNum int not null, "
				+ "latestUpdate TIMESTAMP)";
		stmt.executeUpdate(s);
	}
	
	//foreign key constraints
	static void addConstraints(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "alter table CHAPTER add constraint fk_chapmanga "
				+ "foreign key(mangaID) references MANGA on delete cascade";
		stmt.executeUpdate(s);
	}
	
	//find one chapter by mangaID and chapter number
	public Chapter find(int mangaID, int num)
	{
		if(!cache.containsValue(mangaID)) return null; //check if manga exist
		
		try {
			String qry = "select chapterTitle from CHAPTER where mangaID = ? and chapterNum= ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setInt(1, mangaID);
			pstmt.setInt(2, num);
			ResultSet rs = pstmt.executeQuery();

			// return null if chapter doesn't exist
			if (!rs.next())
				return null;

			String chs = rs.getString("chapterTitle");
			rs.close();

			Chapter chap = new Chapter(this, mangaID, chs, num);
			
			cache.put(chap, mangaID); //store in cache
			return chap;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error finding this chapter of manga", e);
		}
	
	}
	
	//find all chapters by mangaID
	public ResultSet findAll(int mangaID)
	{
		if(!cache.containsValue(mangaID)) return null; //check if manga exist
		
		try {
			String qry = "select chapterTitle from CHAPTER where mangaID = ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setInt(1, mangaID);
			ResultSet rs = pstmt.executeQuery();

			// return null if chapter doesn't exist
			if (!rs.next())
				return null;
			return rs;	
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error finding chapters of manga", e);
		}
	}
	
	public Chapter insert(int mangaID, String title, int num){
		try{
			String cmd = "insert into CHAPTER(mangaID, chapterTitle, chapterNum) "
			           + "values(?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setInt(1, mangaID);
			pstmt.setString(2, title);
			pstmt.setInt(3, num);
			pstmt.executeUpdate();
			Chapter ch = new Chapter(this, mangaID, title, num);
			cache.put(ch, mangaID);
			return ch;
		}
		catch(SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error adding chapter", e);
		}
	} 
	
	public void changeTitle(int mangaid, int num, String title) {
		try {
			String cmd = "update CHAPTER set chapterTitle = ? where mangaID = ? and chapterNum = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setString(1, title);
			pstmt.setInt(2, mangaid);
			pstmt.setInt(3, num);
			pstmt.executeUpdate();
		}
		catch(SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing title", e);
		}
	}	
	//clear data	
	public void clear() throws SQLException {
			Statement stmt = conn.createStatement();
			String s = "delete from CHAPTER";
			stmt.executeUpdate(s);
			//cache.clear();
	}
	//generic method for getting keys by value
	//if (cache.containsValue(mangaID)) return getKeysByValue(cache, mangaID);
	public static Set<Chapter> getKeysByValue(Map<Chapter, Integer> map, int value) {
	    Set<Chapter> keys = new HashSet<Chapter>();
	    for (Entry<Chapter, Integer> entry : map.entrySet()) {
	        if (value==entry.getValue()) {
	            keys.add(entry.getKey());
	        }
	    }
	    return keys;
	}
}
