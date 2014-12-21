package admin;

import java.io.Serializable;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import membership.Login;
import membership.UserType;
import freela.util.FaceUtils;
import freela.util.Sql;
import freela.util.Sql.Select;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class User extends CrudBase implements Serializable {

	@ManagedProperty(value = "#{login}")
	private Login login;

	public User() {
System.out.println("blablab");
		this.table = "user";
		Select select = new Sql.Select().from(this.table);

		login.setUserType(UserType.ACTIVE);
		if (login.getUserType() != UserType.SA) {
			select.where("state<>", UserType.ADMIN.getCode()).and("state<>",
					UserType.SA.getCode());
		}
		this.data = select.getTable();
		FaceUtils.log.info(select.get());
		for (Map<String, String> row : data) {
			for (Map.Entry<String, String> col : row.entrySet()) {
				if (col.getValue().equals("NULL"))
					col.setValue("");
			}
		}
		initColumns();

	}

	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}
}
