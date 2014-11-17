package nostragenus;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import freela.util.ASCIITable;
import freela.util.BaseBean;
import freela.util.FaceUtils;
import freela.util.Sql;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class Index extends BaseBean implements Serializable {

	List<Map<String, String>> activeTahmins;

	public List<Map<String, String>> getActiveTahmins() {
		return activeTahmins;
	}

	public void setActiveTahmins(List<Map<String, String>> activeTahmins) {
		this.activeTahmins = activeTahmins;
	}

	public Index() {
		this.table = "tahmin";
		activeTahmins = new Sql.Select().from(table).join("user")
				.on("tahmin.userId", "user.id")
				.where("creationTime<", FaceUtils.getFormattedTime())
				.and("occurTime<", FaceUtils.getFormattedTime())
				.order("occurTime").desc().getTable();
		ASCIITable asciiTable = new ASCIITable();
		asciiTable.printTable(activeTahmins, true);
	}

}
