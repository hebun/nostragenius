package nostragenus;

import static freela.util.FaceUtils.log;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import freela.util.ASCIITable;
import freela.util.BaseBean;
import freela.util.Db;
import freela.util.FaceUtils;
import freela.util.Sql;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class Index extends BaseBean implements Serializable {



	List<Map<String, String>> activeTahmins;
	List<Map<String, String>> doneTahmins;
	List<Map<String, String>> bestUsers;
	List<Map<String, String>> bestPartners;
private int pageC;
	public int getPageC() {
		
	return super.getPageCount();
}

public void setPageC(int pageC) {
	this.pageC = pageC;
}

	public List<Map<String, String>> getBestPartners() {
		return bestPartners;
	}

	public void setBestPartners(List<Map<String, String>> bestPartners) {
		this.bestPartners = bestPartners;
	}

	public List<Map<String, String>> getBestUsers() {
		return bestUsers;
	}

	public void setBestUsers(List<Map<String, String>> bestUsers) {
		this.bestUsers = bestUsers;
	}

	public List<Map<String, String>> getDoneTahmins() {
		return doneTahmins;
	}

	public void setDoneTahmins(List<Map<String, String>> doneTahmins) {
		this.doneTahmins = doneTahmins;
	}

	public List<Map<String, String>> getActiveTahmins() {
		return activeTahmins;
	}

	public void setActiveTahmins(List<Map<String, String>> activeTahmins) {
		this.activeTahmins = activeTahmins;
	}

	public Index() {
	
			this.table = "tahmin";
			this.allData = new Sql.Select().from(table).join("user")
					.doNotUsePrepared().on("tahmin.userId", "user.id")
					.where("creationTime<", FaceUtils.getFormattedTime())
					.and("occurTime>", FaceUtils.getFormattedTime())
					.order("occurTime").desc().getTable();
			System.out.println(allData);
			this.activeTahmins=this.data;
			loadData();

			doneTahmins= Nostra.getBestTahmins();

			bestUsers = Db
					.selectTable(Nostra.BESTUSERS_SQL);

			for (Map<String, String> u : bestUsers) {
				u.put("userort", Nostra.calcUserPoint(u.get("say"), u.get("ort")));
			}

			Collections.sort(bestUsers, new Comparator<Map<String, String>>() {

				@Override
				public int compare(Map<String, String> o1, Map<String, String> o2) {
					double d1 = Double.parseDouble(o1.get("userort"));
					double d2 = Double.parseDouble(o2.get("userort"));

					return d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
				}
			});

			Collections.reverse(bestUsers);

			bestPartners = Db
					.selectTable(Nostra.BESTPARTNERS_SQL);
			for (Map<String, String> u : bestPartners) {
				u.put("userort", Nostra.calcUserPoint(u.get("say"), u.get("ort")));
			}
			Collections.sort(bestPartners, new Comparator<Map<String, String>>() {

				@Override
				public int compare(Map<String, String> o1, Map<String, String> o2) {
					double d1 = Double.parseDouble(o1.get("userort"));
					double d2 = Double.parseDouble(o2.get("userort"));

					return d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
				}
			});
			Collections.reverse(bestPartners);
		

		try {
			ASCIITable asciiTable = new ASCIITable();
			asciiTable.printTable(bestPartners, true);
		} catch (Exception e) {

		}
	}


}
