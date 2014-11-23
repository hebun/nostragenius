package nostragenus;

import java.io.Serializable;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import freela.util.BaseBean;
import freela.util.Sql;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class BestTahmins extends BaseBean implements Serializable {
	public BestTahmins() {

		this.allData= Nostra.getBestTahmins();

		for (Map<String, String> tah : this.allData) {

			tah.put("pcount",
					new Sql.Count("tahminpartner").where("tahminId",
							tah.get("id")).get()
							+ "");
		}
		loadData();
	}
}
