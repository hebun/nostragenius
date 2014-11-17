package nostragenus;

import static freela.util.FaceUtils.log;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import freela.util.App;
import freela.util.BaseBean;
import freela.util.FaceUtils;
import freela.util.Sql;
import freela.util.Sql.Insert;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean
public class NewTahmin extends BaseBean implements Serializable {

	@ManagedProperty(value = "#{app}")
	App app;

	public App getApp() {
		return app;
	}
public void setApp(App app) {
		this.app = app;
	}
	private Date occurTime;

	public Date getOccurTime() {
		return occurTime;
	}

	public void setOccurTime(Date occurTime) {
		this.occurTime = occurTime;
	}

	

	private int vote = 0;

	public int getVote() {
		return vote;
	}

	public void setVote(int vote) {
		this.vote = vote;
	}

	public NewTahmin() {
		this.table = "tahmin";
		this.record = new HashMap<String, String>();

	}

	@SuppressWarnings("unchecked")
	public String save() {

		if (vote == 0) {
			FaceUtils.addError("Lütfen zorluk derecesini seçin.");
			return null;
		}

		Insert addTahmin = new Sql.Insert(table)
				.add("name", record.get("name"))
				.add("content", record.get("content"))
				.add("keywords", record.get("keywords"))
				.add("occurTime", FaceUtils.getFormattedTime(occurTime))
				.add("creationTime", FaceUtils.getFormattedTime());

		int tahminId = addTahmin.run();

		Object obj = FaceUtils.getSession("user");
		Map<String, String> user = null;
		if (obj != null) {
			user = (Map<String, String>) obj;

		} else {
			return "yorum-yap";
		}

		Insert tahminHit = new Sql.Insert("difficulty")
				.add("userId", user.get("id")).add("voteType", "1")
				.add("vote", vote + "").add("tahminId", tahminId + "")
				.add("ip", FaceUtils.getIp());
		tahminHit.run();
		app.setCurrentInfoMessage("Tahmin başarıyla oluşturuldu.");
		return "bilgi";

	}
}
