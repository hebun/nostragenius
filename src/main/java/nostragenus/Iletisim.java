package nostragenus;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import freela.util.Db;
import freela.util.FaceUtils;
import freela.util.MyCaptcha;
import freela.util.Sql;

@ViewScoped
@ManagedBean
public class Iletisim implements Serializable {

	String captca;
	String name, content, tel, email;

	public Iletisim() {

	}

	public String sendMessage() {

		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();

		javax.servlet.http.HttpSession session = request.getSession();

		String c = (String) session.getAttribute(MyCaptcha.CAPTCHA_KEY);
		if (!captca.equals(c)) {
			FaceUtils.addError("Doğrulama kodu yanlış.");
			return null;
		}

		List<Map<String, String>> table =new Sql.Select()
				.from("mailcontent").where("name", "iletisim").getTable();

		String mc = table.get(0).get("content");

		mc = mc.replaceAll("#Name#", name).replaceAll("#email#", email)
				.replaceAll("#Content#", content);

		try {
			FaceUtils.postMail(new String[] { "info@nethizmet.net",
					"info@nostragenius.com", "yesim@nethizmet.net" },
					"Nostragenius bilgi", mc, FaceUtils.emailFromAddress);
		} catch (MessagingException e) {
			FaceUtils.addError("Mesaj gönderilirken hata oluştu.");
			FaceUtils.log.warning(e.getMessage());
			return null;
		}
		FaceUtils.addError("Mesajınız İletildi.");
		return null;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCaptca() {
		return captca;
	}

	public void setCaptca(String captca) {
		this.captca = captca;
	}

	private static final long serialVersionUID = 1L;

}
