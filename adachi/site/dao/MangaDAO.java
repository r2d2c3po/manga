package adachi.site.dao;
import java.sql.*;
import java.util.*;

/**
*Data Access Object for Manga table
*author: Joey Y. Ni
*/

public class MangaDAO{
	private Connection conn;
	private DatabaseManager dbm;
    private Map<Integer, Manga> cache; //one id per manga and no duplicate IDs
    
    public MangaDAO(Connection conn, DatabaseManager dbm) {
		this.conn = conn;
		this.dbm = dbm;
		this.cache = new HashMap<Integer, Manga>();
	}
	//Creating table, one byte char for mangaStatus(I for incomplete and c for complete)
	static void create(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "CREATE TABLE MANGA( "
				+ "mangaID int not null, "
				+ "title varchar(100) not null, "
				+ "genre varchar(25), "
				+ "mangaStatus boolean," 
				+ "releaseYear int, "
				+ "latestChapter int, "
				+ "authorName varchar(25) not null, " 
				+ "primary key(mangaID))";
		stmt.executeUpdate(s);
		System.out.print("table created");
	} 
        
	//add foreign key constraints
	static void addConstraints(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		String s= "alter table MANGA add constraint fk_mangaauth "
			+ "foreign key(authorName) references AUTHOR on delete cascade";
		stmt.executeUpdate(s);
	}
 	
	public Manga insert(int mangaid, String mangaTitle, String genre, boolean mangaStatus, int releaseYear, int latestChapter, String author)
	{
		try {
			// make sure that the mangaid is currently unused
			if (find(mangaid) != null)
				return null;

			String cmd = "insert into MANGA(mangaID, title, genre, mangaStatus, releaseYear, latestChapter, authorName) "
			           + "values(?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setInt(1, mangaid);
			pstmt.setString(2, mangaTitle);
			pstmt.setString(3, genre);
			pstmt.setBoolean(4, mangaStatus);
			pstmt.setInt(5, releaseYear);
			pstmt.setInt(6, latestChapter);
			pstmt.setString(7, author);
			pstmt.executeUpdate();
			
			Manga manga = new Manga(this, mangaid, mangaTitle, genre, mangaStatus, releaseYear, latestChapter, author);			
			cache.put(mangaid, manga);
			return manga;
		}
		catch(SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error inserting new manga", e);
		}
	}
	
	//clear data	
	void clear() throws SQLException {
		Statement stmt = conn.createStatement();
		String s = "delete from MANGA";
		stmt.executeUpdate(s);
		cache.clear();
	}

	public Manga find(int id){
		if (cache.containsKey(id)) return cache.get(id);
		
		try {
			String qry = "select title, genre, mangaStatus, releaseYear, latestChapter, authorName from MANGA where mangaID = ?";
			PreparedStatement pstmt = conn.prepareStatement(qry);
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();

			// return null if manga doesn't exist
			if (!rs.next())
				return null;
			
			String title = rs.getString("title");
			String genre = rs.getString("genre");
			boolean mangaStatus = rs.getBoolean("mangaStatus");
			int releaseYear =rs.getInt("releaseYear");
			int latestChap = rs.getInt("latestChapter");
			String author= rs.getString("authorName");

			rs.close();
			//create new
			Manga manga = new Manga(this, id, title, genre, mangaStatus, releaseYear, latestChap, author);
			cache.put(id, manga);
			return manga;
		} catch (SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error finding manga", e);
		}
	}

	//change manga status
	public void changeStatus(int mangaid, boolean c){ 
		try {
			String cmd = "update MANGA set mangaStatus = ? where mangaID = ?";
			PreparedStatement pstmt = conn.prepareStatement(cmd);
			pstmt.setBoolean(1, c);
			pstmt.setInt(2, mangaid);
			pstmt.executeUpdate();
		}
		catch(SQLException e) {
			dbm.cleanup();
			throw new RuntimeException("error changing title", e);
		}
	} 
	//find all manga by author name
    public ResultSet findByAuthor(String name){

          try {
                        String qry = "select title from MANGA where authorName = ?";
                        PreparedStatement pstmt = conn.prepareStatement(qry);
                        pstmt.setString(1, name);
                        ResultSet rs = pstmt.executeQuery();

                        // return null if author doesn't exist
                        if (!rs.next())
                                return null;
                        rs.close();
                        return rs;
            
                } catch (SQLException e) {
                        dbm.cleanup();
                        throw new RuntimeException("error finding author", e);
                }
        }		
    
    
}
