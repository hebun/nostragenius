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
		pages.put("categories", "Kategoriler");
		pages.put("states", "Ürün	Durumları");
		pages.put("talepler", "Talepler");
		pages.put("users", "Satıcılar");
		pages.put("products", "Ürünler");
		pages.put("bulten", "Bülten Aboneleri");
		pages.put("userDetail", "Satıcı Detay");
		pages.put("proDetail", "Ürün Detay");
	}

	public String getCurrentPageName() {
		String viewId = FacesContext.getCurrentInstance().getViewRoot()
				.getViewId();
		String p1 = viewId.split("/")[2];
	
		String[] p = p1.split("\\.");
		
		return pages.get(p[0]);
	}
}
