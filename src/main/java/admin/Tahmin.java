package admin;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import freela.util.FaceUtils;
import freela.util.Sql;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class Tahmin extends CrudBase implements Serializable{

	private List<Map<String,String>> tahmins;
	
	
	public Tahmin() {
		this.table="tahmin";
		tahmins=new  Sql.Select().from(this.table).doNotUsePrepared().join("user").on("tahmin.userId", "user.id").getTable();
		this.data=tahmins;
		initColumns();
	}

	public List<Map<String,String>> getTahmins() {
		return tahmins;
	}

	public void setTahmins(List<Map<String,String>> tahmins) {
		this.tahmins = tahmins;
	}


	
}
