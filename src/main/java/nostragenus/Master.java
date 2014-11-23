package nostragenus;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.xml.ws.soap.Addressing;

import freela.util.BaseBean;
import freela.util.FaceUtils;

@ManagedBean
@ViewScoped
@SuppressWarnings("serial")
public class Master extends BaseBean implements Serializable{
	private String searchText;
	public String getSearchText() {
		return searchText;
	}
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	public Master() {
		
		
	}public String search() {

	
		if (searchText != null && !searchText.equals("")
				&& searchText.length() > 2) {
			FacesContext.getCurrentInstance().getExternalContext()
					.setResponseCharacterEncoding("UTF-8");
			return "arama-sonuclari.xhtml?faces-redirect=true&key=" + searchText;
		} else {
			return null;
		}

	}

}
