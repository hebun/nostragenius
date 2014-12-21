package admin;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import freela.util.Sql;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class Tahmin extends CrudBase implements Serializable{

	private List<Map<String,String>> tahmins;
	private Map<String,String> selected;
	
	public Tahmin() {
		this.table="tahmin";
		tahmins=new  Sql.Select().from(this.table).join("user").on("tahmin.userId", "user.id").getTable();
		initColumns();
	}

	public List<Map<String,String>> getTahmins() {
		return tahmins;
	}

	public void setTahmins(List<Map<String,String>> tahmins) {
		this.tahmins = tahmins;
	}

	public Map<String,String> getSelected() {
		return selected;
	}

	public void setSelected(Map<String,String> selected) {
		this.selected = selected;
	}
	
}
