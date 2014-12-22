package nostragenus;

import static freela.util.FaceUtils.log;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RateEvent;

import freela.util.ASCIITable;
import freela.util.BaseBean;
import freela.util.Db;
import freela.util.FaceUtils;
import freela.util.Sql;
import freela.util.Sql.Insert;
import freela.util.Sql.Select;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class TahminDet extends BaseBean implements Serializable {

	int partnerCount, commentCount;
	int vote;
	boolean occured;

	public boolean isOccured() {
		return occured;
	}

	public void setOccured(boolean occured) {
		this.occured = occured;
	}

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	String ownerId;
	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	private boolean isVoted;
	private String partnerText;
	private boolean partnerDisabled;
	private String difPoint;

	public String getDifPoint() {
		return difPoint;
	}

	public void setDifPoint(String difPoint) {
		this.difPoint = difPoint;
	}

	public boolean isPartnerDisabled() {
		return partnerDisabled;
	}

	public void setPartnerDisabled(boolean partnerDisabled) {
		this.partnerDisabled = partnerDisabled;
	}

	public String getPartnerText() {
		return partnerText;
	}

	public void setPartnerText(String partnerText) {
		this.partnerText = partnerText;
	}

	public boolean isVoted() {
	
		return isVoted;
	}

	public void setVoted(boolean isVoted) {
		this.isVoted = isVoted;
	}

	public int getVote() {
		return vote;
	}

	public void setVote(int vote) {
		this.vote = vote;
	}

	public int getPartnerCount() {
		return partnerCount;
	}

	public void setPartnerCount(int partnerCount) {
		this.partnerCount = partnerCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	@SuppressWarnings("unchecked")
	public void checkVote() {
		Object obj = FaceUtils.getSession("user");
		String userId = "0";
		Map<String, String> user = null;
		if (obj != null) {

			user = (Map<String, String>) obj;
			userId = user.get("id");
		} else {

		}
		String hitTable = "difficulty";
		if(occured)
			hitTable="tahminhit";
		String checkSql = "select * from " + hitTable + " where tahminId=" + id + "";

		if (userId.equals("0")) {
			checkSql += " and ip='" + FaceUtils.getIp() + "'";

		} else {
			checkSql += " and (userId=" + userId + " or ip='"
					+ FaceUtils.getIp() + "')";

		}
		List<Map<String, String>> tableDif = Db.selectTable(checkSql);
		if (tableDif.size() != 0) {
			isVoted = true;
			vote = Integer.parseInt(tableDif.get(0).get("vote"));
		} else {
			isVoted = false;
		}

	}

	public void calcDif() {
		difPoint = Nostra.getDifPoint(id);
	}

	@SuppressWarnings({ "unchecked" })
	public void onPartner() {

		String userId = "0";
		Object obj = FaceUtils.getSession("user");
		Map<String, String> user = null;
		if (obj != null) {

			user = (Map<String, String>) obj;
			userId = user.get("id");
		} else {

		}

		Insert tahminDif = new Sql.Insert("tahminpartner").doNotUsePrepared()
				.add("userId", userId).add("tahminId", id + "").add("ownerId", record.get("user.id"));
		tahminDif.run();
		checkCounts();
		checkPartner();

		FaceUtils.addInfo("Bu tahmine ortak oldunuz.");
	}

	@SuppressWarnings("unchecked")
	public void onrate(RateEvent rateEvent) {

		String voteType = "";
		String userId = "0";
		Object obj = FaceUtils.getSession("user");
		Map<String, String> user = null;
		if (obj != null) {
			voteType = "3";
			user = (Map<String, String>) obj;
			userId = user.get("id");
		} else {
			voteType = "2";
		}

		String table2 = "difficulty";
		if(occured)
			table2="tahminhit";
		Insert tahminDif = new Sql.Insert(table2).doNotUsePrepared()
				.add("userId", userId).add("voteType", voteType)
				.add("vote", vote + "").add("tahminId", id + "")
				.add("ip", FaceUtils.getIp());
		tahminDif.run();
		checkVote();
		FaceUtils.addInfo(!occured?"Zorluk derecesi kaydedildi.":"Gerçekleşme Derecesi Kaydedildi.");

	}

	public TahminDet() {
		id = FaceUtils.getGET("tid");
		log.info("tid:" + id);
		if (id == null) {
			return;
		}
		this.table = "tahmin";
		this.record = new Sql.Select().from(table).join("user")
				.on("user.id", "tahmin.userId").where("tahmin.id", id)
				.getTable().get(0);

		checkOccured();
		checkCounts();
		checkVote();
		checkPartner();
		calcDif();
	}

	public void checkOccured() {

		List<Map<String, String>> list = new Sql.Select().from("tahmin")
				.where("id", id)
				.and("occurTime<", FaceUtils.getFormattedTime()).getTable();
		this.occured = list.size() > 0;

	}

	private void checkCounts() {
		String pcount = new Sql.Select("count(0) as say").from("tahminpartner")
				.where("tahminId", id).getTable().get(0).get("say");

		try {
			this.partnerCount = Integer.parseInt(pcount);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		String ccount = new Sql.Select("count(0) as say").from("comment")
				.where("tahminId", id).getTable().get(0).get("say");

		try {
			this.commentCount = Integer.parseInt(ccount);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void checkPartner() {

		Object obj = FaceUtils.getSession("user");
		String userId = "0";
		Map<String, String> user = null;
		if (obj != null) {

			user = (Map<String, String>) obj;
			userId = user.get("id");

		} else {
			partnerText = "Ortak Olmak İçin Giriş Yapın";
			partnerDisabled = true;
			return;
		}
		Select checkVote = new Sql.Select().doNotUsePrepared()
				.from("tahminpartner").where("tahminId", id);

		checkVote.and("userId", userId);

		List<Map<String, String>> tableDif = checkVote.getTable();
		if (tableDif.size() != 0) {
			partnerDisabled = true;
			partnerText = "Bu Tahminin Ortağısınız";

		} else {
			partnerDisabled = false;
			partnerText = "Sen de Ortak Ol";

		}
	}

}
