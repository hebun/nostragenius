package nostragenus;

import static freela.util.FaceUtils.log;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import membership.Login;
import freela.util.BaseBean;
import freela.util.FaceUtils;
import freela.util.Sql;
import freela.util.Sql.Insert;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class Comment extends BaseBean implements Serializable {
	private String id;
	private String content;

	@ManagedProperty("#{login}")
	Login login;

	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Comment() {

		this.table = "comment";
		id = FaceUtils.getGET("tid");
		log.info("tid:" + id);
		if (id == null) {
			return;
		}
		this.table = "tahmin";
		this.record = new Sql.Select().from("tahmin").join("user")
				.on("user.id", "tahmin.userId").where("tahmin.id", id)
				.getTable().get(0);

		loadComments();

	}

	public String login() {
		login.setPreUrl("yapilmis-yorumlar.xhtml?tid="+id+"&faces-redirect=true");
		return "yorum-yap";
	}

	private void loadComments() {
		this.data = new Sql.Select().from("comment").join("user")
				.on("comment.userId", "user.id").where("tahminId", id)
				.getTable();
	}

	public void save() {
		Insert com = new Insert("comment").add("tahminId", id)
				.add("userId", FaceUtils.getUserId()).add("content", content)
				.add("tarih", FaceUtils.getFormattedTime())
				.add("state", "PENDING");
		com.run();
		loadComments();
		content = "";
		FaceUtils.addInfo("Yorumunuz Kaydedildi.");

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
