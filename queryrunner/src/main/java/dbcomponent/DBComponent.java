package dbcomponent;
import de.ralleytn.simple.json.JSONArray;
import de.ralleytn.simple.json.JSONObject;
import java.io.*;
import java.util.Properties;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.sql.Statement;

/**
 * Clase manejadora de base de datos
 * @author William Suárez
 *
 */
public class DBComponent {
	/**
	 *  Nombre del host de la base de datos
	 */
	private String host;
	/**
	 * Puerto de la base de datos
	 */
	private String port;
	/**
	 * Nombre de la base de datos
	 */
	private String dataBase;
	/**
	 * Usuario de la base de datos
	 */
	private String user;
	/**
	 * Contraseña de la base de datos
	 */
	private String password;
	/**
	 * Instancia de la clase Properties que posee las sentencias definidas en sentences.properties
	 */
	private Properties sentences;
	/**
	 * Instancia de la calse properties que posee las configuraciones necesarias para acceder a la base datos. El archivo se ubica en connection.properties
	 */
	private Properties DBSettings;
	/**
	 * Objeto de conexión a la base de datos
	 */
	private Connection conn;
	
	public DBComponent(){
		try {
			System.out.println("Realizando conexión a la base de datos...");
			this.updateConnection();
			System.out.println("Cargando archivos de sentencias...");
			this.updateSentences();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Carga las sentencias sql definididas en el archivo de sentencias sentences.properties
	 * @return void
	 */
	public void updateSentences() {
		try {
			sentences = new Properties();
			sentences.load(new FileInputStream(new File("C:\\Users\\jesus\\OneDrive\\Documents\\william\\programacion\\java\\practica-java\\queryrunner\\src/sentences.properties")));					
		} catch (Exception e) {
			System.out.println("Ocurrio un error al cargar las sentencias");
		}
	}
	
	/**
	 * Actualiza las conexión a la base de datos usando el archivo conection.properties
	 * @return void
	 */
	public void updateConnection() {
		try {
			DBSettings = new Properties();
			DBSettings.load(new FileInputStream(new File("C:\\Users\\jesus\\OneDrive\\Documents\\william\\programacion\\java\\curso\\producto\\src/connection.properties")));
			host = (String) DBSettings.get("host");
			dataBase = (String) DBSettings.get("database");
			user = (String) DBSettings.get("user");
			port = (String) DBSettings.getProperty("port");
			password = (String) DBSettings.get("password");
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(host+":"+port+"/"+dataBase,user,password);
			System.out.println("Conexion a la base de datos realizada con exito");
		} catch (Exception e) {
			System.out.println("Ocurrio un error al conectar a la base de datos");
			e.printStackTrace();
		}
	}
	
	public String getQuery(String queryId) {
		return (String) sentences.getProperty(queryId);
	}
	
	public JSONArray executeQuery(String queryId) {
		try {
			Statement statement = conn.createStatement(); 
			ResultSet rs = statement.executeQuery(getQuery(queryId));
			return getJSON(rs);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public JSONArray executeQuery(String queryId, ArrayList params){
		try {
			PreparedStatement statement = conn.prepareStatement(getQuery(queryId));
			prepareStatement(statement, params);
			System.out.println(statement.toString());
			boolean result = statement.execute();
			if(result == true) {
				ResultSet rs = statement.getResultSet();
				return getJSON(rs);
			} 
			else {
				int count = statement.getUpdateCount();
				return getJSON(count);
			}
//			ResultSet rs = statement.executeQuery();
//			return getJSON(rs);
		} catch (SQLException e) {
			System.out.println("ocurrio un error");
			e.printStackTrace();
			return null;
		}
	}
	
	public void prepareStatement(PreparedStatement statement, ArrayList params) throws SQLException {
		int size = params.size();
		for(int  i = 0; i < size; i++) {
			Object param = params.get(i);
			if(param instanceof Integer) {
				statement.setInt(i+1, (Integer) param);
			} else if(param instanceof String) {
				statement.setString(i+1, (String) param);
			}
			else if(param instanceof Double) {
				statement.setDouble(i+1, (double) param);
			}
		}
	}
	
	/**
	 * Devuelve un Json con los resultados a partir del objeto ResultSet obtenido de la consulta
	 * @param ResultSet rs - Objeto ResultSet
	 * @return JSONArray Json con las filas devueltas por la sentencia sql
	 */
	public JSONArray getJSON(ResultSet rs){
		try {
			ResultSetMetaData metaData = rs.getMetaData();
			JSONArray result = new JSONArray();
			while(rs.next()) {
				JSONObject row = new JSONObject();
				for(int i = 1; i <= metaData.getColumnCount(); i++) {
					row.put(metaData.getColumnName(i), rs.getObject(metaData.getColumnName(i)));
				}
				result.add(row);
			}
			return result;
		} catch (SQLException e) {
			System.out.println("Ocurrio un error generando el JSON");
			return null;
		}
	}
	
	public JSONArray getJSON(int count) {
		try {
			JSONArray result = new JSONArray();
			JSONObject row = new JSONObject();
			row.put("id", count);
			result.add(row);
			return result;
		} catch (Exception e) {
			System.out.println("Ocurrio un error generando el JSON");
			return null;
		}
	}
	
}
