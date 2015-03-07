package membership;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import freela.util.App;
import freela.util.Db;
import freela.util.FaceUtils;
import freela.util.MyLogger;
import freela.util.Sql;
import freela.util.Sql.Insert;

@ViewScoped
@ManagedBean
public class Register implements Serializable {
	Map<String, String> user;
	@ManagedProperty(value = "#{app}")
	App app;
	private Date birthday;

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	String rePassword;

	MyLogger log = FaceUtils.log;

	public Register() {
		user = new HashMap<String, String>();
		birthday=new GregorianCalendar(1900, 0, 2).getTime();
	}

	public String save() {

		if (!rePassword.equals(user.get("password"))) {
			FaceUtils.addError("Şifreler aynı değil");
			return null;
		}

		try {
			if (!checkExistingEmail()) {
				return null;
			}
			UUID uuid = UUID.randomUUID();
			user.put("uuid", uuid.toString());

			Insert insertUser = new Sql.Insert("user")
					.add("email", user.get("email"))
					.add("password", user.get("password"))
					.add("namesurname", user.get("namesurname"))
					.add("gender", user.get("gender"))

					.add("cepno", user.get("cepno"))
					.add("birthday", FaceUtils.getFormattedTime(birthday))
					.add("uname", user.get("uname"))
					.add("uuid", user.get("uuid"))
					.add("state", "PENDING");

			int insertedId = insertUser.run();

			new Sql.Insert("activation").add("code", uuid)
					.add("userid", insertedId)
					.add("tarih", FaceUtils.getFormattedTime()).run();

			if (!Db.USER.equals("root"))// local controle
				sendActivation(uuid.toString());

			app.setCurrentInfoMessage("Aktivasyon Kodunuz Mail Adresinize Gönderildi. "
					+ "Lütfen E-Mail Adresinizi Ziyaret Ediniz.");
			return "bilgi";
		} catch (Exception e) {
			e.printStackTrace();
			log.severe(e.getMessage());
			FaceUtils.addError("Hata olustu");
			return null;
		}
	}

	private void sendActivation(String uid)
			throws AuthenticationFailedException, MessagingException {

		List<Map<String, String>> table = new Sql.Select().from("mailcontent")
				.where("name", "activation").getTable();

		String mc = table.get(0).get("content");

		mc = mc.replaceAll("#link#",
				FaceUtils.getRootUrl() + "/activation.xhtml?code=" + uid).replaceAll(
				"#fullname#", user.get("uname"));

		log.info(mc);

		FaceUtils.postMail(
				new String[] { "ismettung@gmail.com", user.get("email") },
				"nostragenius.com aktivasyon", mc, "");

	}

	public boolean checkExistingEmail() {
		int size = new Sql.Select().from("user")
				.where("email=", user.get("email")).getTable().size();
		if (size > 0) {
			FaceUtils.addError("Bu E-mail adresi daha önce kullanıldı.");

			return false;
		}
		return true;
	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public Map<String, String> getUser() {
		return user;
	}

	public void setUser(Map<String, String> user) {
		this.user = user;
	}

	public String getRePassword() {
		return rePassword;
	}

	public void setRePassword(String rePassword) {
		this.rePassword = rePassword;
	}

	private static final long serialVersionUID = 5950439051586912211L;

}
