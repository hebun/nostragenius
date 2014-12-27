package membership;

import static freela.util.FaceUtils.log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import model.User;
import freela.util.BaseBean;
import freela.util.Db;
import freela.util.FaceUtils;
import freela.util.Sql;
import freela.util.Sql.Select;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean(name = "act")
public class Activation extends BaseBean implements Serializable {
	String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Activation() {

		String col = FaceUtils.getGET("code");
		Map<String, String> rec = new HashMap<>();
		if (col != null) {

			try {
				Select prepare = new Sql.Select().from("activation")
						.where("code", col);
				List<Map<String, String>> result = prepare.getTable();
				if (result.size() > 0)
					rec = result.get(0);
			} catch (Exception e) {
				log.warning(e.getMessage());
			}

		}

		this.record = rec;

		if (this.record.size() > 0) {

			// User user = FaceUtils.getObjectById(User.class, "user",
			// record.get("id"));

			new Sql.Update("user").add("state", "ACTIVE")
					.where("id", this.record.get("userid"))
					.and("state", "PENDING").run();
			message = "Tebrikler Onlinetahmin.net   Üyeliğiniz Tamamlanmıştır. Şimdi Giriş Yapabilirsiniz.";

		} else {
			message = "Geçersiz Kod!";

		}

	}
}
