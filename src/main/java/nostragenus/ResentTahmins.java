package nostragenus;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import freela.util.BaseBean;
import freela.util.FaceUtils;
import freela.util.Sql;
import freela.util.Sql.Select;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class ResentTahmins extends BaseBean implements Serializable {
	private Select select;

	public ResentTahmins() {
		Date time = new Date(
				Calendar.getInstance().getTime().getTime() - 48 * 3600 * 1000);
		select = new Sql.Select(
				"tahmin.id,difPoint,hitPoint,name,occurTime,uname,user.id as uid,count(comment.id) as ccount")
				.from("tahmin").join("user").on("tahmin.userId", "user.id")
				.leftJoin("comment").on("comment.tahminId", "tahmin.id")				
				.where("occurTime>", FaceUtils.getFormattedTime(time))
				.groupBy("tahmin.id,difPoint,hitPoint,name,occurTime,uname,uid")
				.order("occurTime").desc();
		allData = select.getTable();
		loadData();
	}
}
