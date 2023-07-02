package products;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import de.ralleytn.simple.json.JSONArray;
import de.ralleytn.simple.json.JSONObject;
import dbcomponent.DBComponent;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Servlet implementation class Producto
 */
public class Producto extends HttpServlet {
	private DBComponent dbc = new DBComponent();
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Producto() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.getProducts(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String methodName = request.getParameter("methodname");
		if(methodName.equals("UPDATE"))this.updateProduct(request, response);
		else if (methodName.equals("INSERT")) {
			this.insertProduct(request, response);
		}
		else if (methodName.equals("DELETE")) this.deleteProduct(request, response);
	}
	
	public void getProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String products = request.getParameter("products");
		JSONArray j;
		String jsonSerialized = "";
		if(products != null) {
			j = dbc.executeQuery("getProducts");
			jsonSerialized = j.toString();
		}
		response.getWriter().append(jsonSerialized).append(request.getContextPath());
	}
	
	public void insertProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String descripcion, producto;
		descripcion = request.getParameter("descripcion");
		producto = request.getParameter("producto");
		ArrayList parameters = new ArrayList();
		double precio = Double.parseDouble(request.getParameter("precio"));
		parameters.add(producto);
		parameters.add(descripcion);
		parameters.add(precio);
		dbc.executeQuery("setProduct", parameters);
		JSONObject res = new JSONObject();
		res.put("sts","ok");
		response.getWriter().append(res.toString());
	}
	
   public void updateProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String descripcion, producto;
		int productId;
		descripcion = request.getParameter("descripcion");
		producto = request.getParameter("producto");
		productId = Integer.parseInt(request.getParameter("productid"));
		double precio = Double.parseDouble(request.getParameter("precio"));
		ArrayList parameters = new ArrayList();
		parameters.add(producto);
		parameters.add(descripcion);
		parameters.add(precio);
		parameters.add(productId);
		dbc.executeQuery("updateProduct", parameters);
		JSONObject res = new JSONObject();
		res.put("sts","ok");
		response.getWriter().append(res.toString());
    }
   
   public void deleteProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
	   int productId = Integer.parseInt(request.getParameter("id"));
		Enumeration<String> params = request.getParameterNames();
		while(params.hasMoreElements()) {
			System.out.println(params.nextElement());
		}
		ArrayList parameters = new ArrayList();
		parameters.add(productId);
		dbc.executeQuery("deleteProduct", parameters);
		JSONObject res = new JSONObject();
		res.put("sts", "ok");
		res.put("msg", "Producto eliminado exitosamente");
			response.getWriter().append(res.toString());
	   }
}
