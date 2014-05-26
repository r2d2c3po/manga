package adachi.site.dao;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.derby.jdbc.EmbeddedDriver;
import adachi.site.model.*;

/**
*This class is basically the client code for connecting to database
*author: Joey Y. Ni
*Reference: DatabaseManager.java by bhoward
*Last updated: April 8, 2014
*/

public class DatabaseManager {
	private Driver driver;
	private Connection conn;
	private MangaDAO mangaDAO;
	private AuthorDAO authorDAO;
	private ChapterDAO chapterDAO;
	
	private final String url = "jdbc:derby:dbdb";

	public DatabaseManager() {
		driver = new EmbeddedDriver();
		
		Properties prop = new Properties();
		prop.put("create", "false");
		
		// try to connect to an existing database
		try {
			conn = driver.connect(url, prop);
			conn.setAutoCommit(false);
		}
		catch(SQLException e) {
			// database doesn't exist, so try creating it
			try {
				prop.put("create", "true");
				conn = driver.connect(url, prop);
				conn.setAutoCommit(false);
				create(conn);
			}
			catch (SQLException e2) {
				throw new RuntimeException("cannot connect to database", e2);
			}
		}
		
		chapterDAO = new ChapterDAO(conn, this);
		authorDAO = new AuthorDAO(conn, this);
		mangaDAO = new MangaDAO(conn, this);
	}

	/**
	 * Initialize the tables and their constraints in a newly created database
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	private void create(Connection conn) throws SQLException {
		System.out.println("creating tables");
		ChapterDAO.create(conn);
		MangaDAO.create(conn);
		AuthorDAO.create(conn);
		MangaDAO.addConstraints(conn);
		ChapterDAO.addConstraints(conn);
		conn.commit();
	}

	public Manga findManga(int mangaID){
		return mangaDAO.find(mangaID);
	}

	public Chapter findChapter(int mangaID, int chapterNum){
		return chapterDAO.find(mangaID, chapterNum);
	}

	//data insert functions
	public Manga insertManga(int mangaID, String mangaTitle, String genre, boolean mangaStatus, int releaseYear, int latestChap, String author)
	{
		return mangaDAO.insert(mangaID, mangaTitle, genre, mangaStatus, releaseYear, latestChap, author);
	}
	
	public Chapter insertChap(int mangaID, String chapTitle, int chapNum)
	{
		return chapterDAO.insert(mangaID, chapTitle, chapNum);
	}
	
	public Author insertAuthor(String aName, String language, String artist)
	{
		return authorDAO.insert(aName, language, artist);
	}

	//***************************************************************
	// Utility functions
	
	/**
	 * Commit changes since last call to commit
	 */
	public void commit() {
		try {
			conn.commit();
		}
		catch(SQLException e) {
			throw new RuntimeException("cannot commit database", e);
		}
	}

	/**
	 * Abort changes since last call to commit, then close connection
	 */
	public void cleanup() {
		try {
			conn.rollback();
			conn.close();
		}
		catch(SQLException e) {
			System.out.println("fatal error: cannot cleanup connection");
		}
	}

	/**
	 * Close connection and shutdown database
	 */
	public void close() {
		try {
			conn.close();
		}
		catch(SQLException e) {
			throw new RuntimeException("cannot close database connection", e);
		}
		
		// Now shutdown the embedded database system -- this is Derby-specific
		try {
			Properties prop = new Properties();
			prop.put("shutdown", "true");
			conn = driver.connect(url, prop);
		} catch (SQLException e) {
			// This is supposed to throw an exception...
			System.out.println("Derby has shut down successfully");
		}
	}

	/**
	 * Clear out all data from database (but leave empty tables)
	 */
	public void clearTables() {
		try {
			// This is not as straightforward as it may seem, because
			// of the cyclic foreign keys -- I had to play with
			// "on delete set null" and "on delete cascade" for a bit
			chapterDAO.clear();
			mangaDAO.clear();
			authorDAO.clear();
		} catch (SQLException e) {
			throw new RuntimeException("cannot clear tables", e);
		}
	}
}

