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
import freela.util.FaceUtils;
import freela.util.Sql;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class TahminPartners extends BaseBean implements Serializable {

	public TahminPartners() {
		this.table = "user";
		// List<Map<String, String>> table2 = new Sql.Select(
		// "user.id as taid,uname,avg(tahmin.point) as ort,count(tahmin.id) as say")
		// .from(table).leftJoin("tahmin").on("tahmin.userId", "user.id")
		// .leftJoin("tahminpartner")
		// .on("tahminpartner.tahminId", "tahmin.id")
		// .where("tahmin.id", FaceUtils.getGET("tid"))
		// .and("tahminpartner.ownerId<>", "user.id")
		// .groupBy("user.id,uname").getTable();

		String sql = "select user.id as taid,uname,avg(tahmin.point) as ort,count(tahmin.id) as say from user "
				+ " inner join tahminpartner on tahminpartner.userId=user.id left join tahmin on "
				+ "tahminpartner.tahminId=tahmin.id where tahmin.id="
				+ FaceUtils.getGET("tid")
				+ "  and tahminpartner.ownerId<>tahminpartner.userId "
				+ " group by user.id,uname";
		this.allData = Db.selectTable(sql);
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
