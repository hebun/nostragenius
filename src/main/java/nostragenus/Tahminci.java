package nostragenus;

import static freela.util.FaceUtils.log;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.component.paginator.CurrentPageReportRenderer;

import freela.util.BaseBean;
import freela.util.FaceUtils;
import freela.util.Sql;
import freela.util.Sql.Select;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class Tahminci extends BaseBean implements Serializable {
	String userId;
	String userPoint;
	private Select select;

	public String getUserPoint() {
		return userPoint;
	}

	public void setUserPoint(String userPoint) {
		this.userPoint = userPoint;
	}

	public String getStateColor(String occurTime) {

		Date tahtime = FaceUtils.getTimeFromString(occurTime);
		Date now = Calendar.getInstance().getTime();

		if (tahtime.after(now)) {
			return "yesil";
		}
		if (tahtime.before(now)
				&& tahtime.after(new Date(now.getTime() - 48 * 3600 * 1000))) {
			return "sari";
		} else {
			return "kirmizi";
		}

	}

	public Tahminci() {
		this.userId = FaceUtils.getGET("taid");
		if (userId == null)
			return;
		this.table = "tahmin";

		this.record = new Sql.Select().from("user").where("id", userId)
				.getTable().get(0);
		select = new Sql.Select(
				"tahmin.id,difPoint,hitPoint,name,occurTime,count(comment.id) as ccount,(count(tahminpartner.id)-1)"
		+" as tpcount")
				.from("tahmin").leftJoin("comment")
				.on("comment.tahminId", "tahmin.id").leftJoin("tahminpartner")
				.on("tahminpartner.tahminId", "tahmin.id")
				.where("tahmin.userId", userId)
				.groupBy("tahmin.id,difPoint,hitPoint,name,occurTime")
				.order("occurTime").desc();
		allData = select.getTable();

		for (Map<String, String> map : allData) {
			if(map.get("difPoint").equals("NULL")){
				map.put("difPoint",Nostra.getDifPoint(map.get("id")));
			}
			if(map.get("hitPoint").equals("NULL")){
				map.put("hitPoint",Nostra.getHitPoint(map.get("id")));
			}
		}
		
		loadData();
		userPoint = Nostra.getUserPoint(userId);
	}


}
