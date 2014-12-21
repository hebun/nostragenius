package admin;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import freela.util.Sql;

@ViewScoped
@ManagedBean
public class AdminHome {

	Map<String,String> stats;

	public AdminHome() {
		stats=new HashMap<String, String>();
		stats.put("user", new Sql.Count("user").get()+"");
		stats.put("tahmin", new Sql.Count("tahmin").get()+"");
		stats.put("comment", new Sql.Count("comment").get()+"");
		
		 
	}
	
	public Map<String, String> getStats() {
		return stats;
	}

	public void setStats(Map<String, String> stats) {
		this.stats = stats;
	} 
	
}
