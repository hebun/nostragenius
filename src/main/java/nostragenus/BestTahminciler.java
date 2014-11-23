package nostragenus;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import freela.util.BaseBean;
import freela.util.Db;

@SuppressWarnings("serial")
@ManagedBean(name="bt")
@ViewScoped
public class BestTahminciler extends BaseBean implements Serializable{



	public BestTahminciler() {
		this.table="user";
		this.allData = Db
				.selectTable(Nostra.BESTUSERS_SQL);

		for (Map<String, String> u : allData) {
			u.put("userort", Nostra.calcUserPoint(u.get("say"), u.get("ort")));
		}

		Collections.sort(allData, new Comparator<Map<String, String>>() {

			@Override
			public int compare(Map<String, String> o1, Map<String, String> o2) {
				double d1 = Double.parseDouble(o1.get("userort"));
				double d2 = Double.parseDouble(o2.get("userort"));

				return d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
			}
		});

		Collections.reverse(allData);
		loadData();
		
	
	}


}
