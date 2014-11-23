package nostragenus;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import freela.util.BaseBean;
import freela.util.FaceUtils;
import freela.util.Sql;
import freela.util.Sql.Select;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class Search extends BaseBean implements Serializable {
	private Select select;
	private String key;

	public Search() {
		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.setRequestCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		this.key = FaceUtils.getGET("key");

		String searchText = this.key;
		if (searchText != null && !searchText.equals("")
				&& searchText.length() > 2) {
			String value = "%" + key + "%";
		
			select = new Sql.Select(
					"tahmin.id,difPoint,hitPoint,name,occurTime,uname,user.id as uid,count(comment.id) as ccount")
					.from("tahmin")
					.join("user")
					.on("tahmin.userId", "user.id")
					.leftJoin("comment")
					.on("comment.tahminId", "tahmin.id")
					.where("name like ", value)
					.or("tahmin.content like ", value)
					.or("tahmin.keywords like ", value)
					.groupBy(
							"tahmin.id,difPoint,hitPoint,name,occurTime,uname,uid")
					.order("occurTime").desc();
			allData = select.getTable();
			loadData();
		}
	}
}
