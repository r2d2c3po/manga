package adachi.site.dao;
import java.sql.*;
import java.util.*;
import adachi.site.model.*;

public class AuthorDAO{
	private Connection conn;
	private DatabaseManager dbm;


	public AuthorDAO(Connection conn, DatabaseManager dbm) {
		this.conn = conn;
		this.dbm = dbm;
		//this.cache = new HashMap<Integer, Author>();
	}

	static void create(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "create table AUTHOR(" 
				+ "aName varchar(25) not null, "
				+ "language varchar(25), "
				+ "artist varchar(25),"
				+ "primary key(aName))";
		stmt.executeUpdate(s);
	}
    //Author contains no foreign contraints	
   
	//Add new author, one author per manga
	public Author insert(String aName, String language, String artist) {
		try {
			String cmd = "insert into AUTHOR(aName, language, artist) "
			           + "values(?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setString(1, aName);
			pstmt.setString(2, language);
			pstmt.setString(3, artist);
			pstmt.executeUpdate();
			Author author = new Author(this, aName, language, artist);
			
			return author;
		}
		catch(SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error adding new author", e);
		}
	}
	
	//retrieve all author name
	public ResultSet getName(){
		try{
			String qry ="select aName from AUTHOR";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			ResultSet rs = pstmt.executeQuery();
			rs.close();
			return rs;
		}catch(SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error getting authors", e);
		}
	}
	
	//Retrieve all title by author
	public Collection<Manga> getManga(String a) {
		try {
			List<Manga> manga = new ArrayList<Manga>();
			String qry = "select mangaID from MANGA where authorName = ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setString(1, a);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int mID = rs.getInt("mangaID");
				manga.add(dbm.findManga(mID));
			}
			rs.close();
			return manga;
		}
		catch(SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error getting manga by author", e);
		}
	}

	//clear data from author table
	void clear() throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "delete from AUTHOR";
		stmt.executeUpdate(s);
	}
}
