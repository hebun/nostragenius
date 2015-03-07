package membership;

import static freela.util.FaceUtils.log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import freela.util.App;
import freela.util.Db;
import freela.util.FaceUtils;
import freela.util.Sql;
import freela.util.Sql.Select;
import freela.util.Sql.Update;

@SessionScoped
@ManagedBean
public class Login implements Serializable {
	private static final String COOKIE_NAME = "remember";

	private String username;
	private String preUrl;

	String password;
	private boolean loggedIn;
	Map<String, String> user;
	private boolean remember;

	String newPassword;

	String reNewPassword;

	@ManagedProperty(value = "#{app}")
	App app;

	UserType userType;

	public String update() {

		updateUser();

		app.setCurrentInfoMessage("Profiliniz başarıyla güncellendi.");

		return "bilgi";

	}

	public void updateUser() {
		Update prepare = new Update("user").add("uname", user.get("name"))
				.add("firmaname", user.get("firmaname"))
				.add("sabitno", user.get("sabitno"))
				.add("cepno", user.get("cepno"))
				.add("address", user.get("address"))
				.add("city", user.get("city"))
				.add("vergidaire", user.get("vergidaire"))
				.add("vergino", user.get("vergino"))
				.add("website", user.get("website"))
				.where("id", user.get("id"));

		prepare.run();
	}

	@SuppressWarnings("unchecked")
	public Login() {
		Object object = FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("user");
		if (object != null) {
			this.user = (Map<String, String>) object;
			this.loggedIn = true;
		}
	}

	public String login() {
		Sql.Select select = new Select().from("user").where("email", username)
				.and("password", password);

		String sql = "select * from user where password=? and (email=? or uname=?)";
		List<String> params = new ArrayList<String>();
		params.add(password);
		params.add(username);
		params.add(username);

		List<Map<String, String>> table = Db.preparedSelect(sql, params);

		if (table.size() > 0) {
			if (table.get(0).get("state").toString().equals("PENDING")) {
				FaceUtils.addError("Hesabınız Aktif Değil!");
				loggedIn = false;
				return null;
			}

			user = table.get(0);

			loggedIn = true;

			if (remember) {
				FaceUtils.addCookie(COOKIE_NAME, user.get("uuid"), 100_000_000);

			}
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().put("user", user);

			this.userType = UserType.valueOf(user.get("state"));

			return "index";

		} else {
			FaceUtils.addError("Kullanıcı ve/veya şifre yanlış.");
			loggedIn = false;
			return null;
		}
	}

	public String forgotPassword() {

		List<Map<String, String>> userTable = new Sql.Select().from("user")
				.where("email", username).getTable();

		if (userTable.size() == 0) {
			FaceUtils
					.addError("Bu E-Mail adresi ile kayıtlı kullanıcı bulunamadı.");
			return null;
		}
		if (userTable.get(0).get("state").equals("PENDING")) {
			FaceUtils
					.addError("E-mail adresinize daha önce yollanmış olan aktivasyon işlemini yapınız.");
			return null;
		}
		List<Map<String, String>> table = new Sql.Select().from("mailcontent")
				.where("name", "resetpassword").getTable();

		String mc = table.get(0).get("content");

		String uid = UUID.randomUUID().toString();

		new Sql.Insert("resetpassword").add("code", uid)
				.add("userid", userTable.get(0).get("id"))
				.add("tarih", FaceUtils.getFormattedTime()).run();

		mc = mc.replaceAll("#link#", FaceUtils.getRootUrl()
				+ "/resetpassword.xhtml?code=" + uid);

		try {
			FaceUtils.postMail(
					new String[] { "ismettung@gmail.com", username },
					"nostragenius.com Şifre Yenile", mc, "");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		app.setCurrentInfoMessage("Ayrıntılı talimatlar E-Posta adresinize gönderildi");

		return "bilgi";
	}

	public String logout() {
		log.severe("here");
		username = "";
		password = "";
		user = null;
		loggedIn = false;
		FacesContext.getCurrentInstance().getExternalContext()
				.invalidateSession();
		FaceUtils.removeCookie((HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse(),
				COOKIE_NAME);
		return "index?faces-redirect=true";
	}

	public Map<String, String> getUser() {
		return user;
	}

	public void setUser(Map<String, String> user) {
		this.user = user;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public String getReNewPassword() {
		return reNewPassword;
	}

	public void setReNewPassword(String reNewPassword) {
		this.reNewPassword = reNewPassword;
	}

	public void setLoggedIn(boolean isLoggedIn) {
		this.loggedIn = isLoggedIn;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	private static final long serialVersionUID = -8938217548612577279L;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isRemember() {
		return remember;
	}

	public void setRemember(boolean remember) {
		this.remember = remember;
	}

	public String getPreUrl() {
		return preUrl;
	}

	public void setPreUrl(String preUrl) {
		this.preUrl = preUrl;
	}

	public String test() {

		return "";

	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}
}
