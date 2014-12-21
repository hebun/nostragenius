package admin;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ViewScoped
@ManagedBean
public class AdminMaster {

	public Map<String, String> pages;

	public AdminMaster() {
		pages = new HashMap<String, String>();

		pages.put("index", "Anasayfa");
		pages.put("tahmin", "Tahminler");
		pages.put("users", "Kullanıcılar");
	}

	public String getCurrentPageName() {
		String viewId = FacesContext.getCurrentInstance().getViewRoot()
				.getViewId();
		String p1 = viewId.split("/")[2];
	
		String[] p = p1.split("\\.");
		
		return pages.get(p[0]);
	}
}
