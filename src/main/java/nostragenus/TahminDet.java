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

	private String id;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		log.info(isVoted + "");
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
		Select checkVote = new Sql.Select().doNotUsePrepared()
				.from("difficulty").where("tahminId", id);
		if (userId.equals("0"))
			checkVote.and("ip", FaceUtils.getIp());
		else {
			checkVote.and("userId", userId).or("ip", FaceUtils.getIp() + "");

		}
		List<Map<String, String>> tableDif = checkVote.getTable();
		if (tableDif.size() != 0) {
			isVoted = true;
			vote = Integer.parseInt(tableDif.get(0).get("vote"));
		} else {
			isVoted = false;
		}

	}

	public void calcDif() {
		log.info("tidc:" + id);
		List<Map<String, String>> dif = new Select(
				"tahminId,voteType, avg(vote) as ort").from("difficulty")
				.where("tahminId", id).groupBy("voteType,tahminId").getTable();
		double point = 0.0;
		int deno = 0;
		for (Map<String, String> map : dif) {
			if (map.get("voteType").equals("1")
					|| map.get("voteType").equals("2")) {
				point += Double.parseDouble(map.get("ort"));
				deno++;
			} else if (map.get("voteType").equals("3")) {
				point += Double.parseDouble(map.get("ort")) * 3;
				deno+=3;
			} else if (map.get("voteType").equals("4")) {
				point += Double.parseDouble(map.get("ort")) * 5;
				deno+=5;
			}
		}
		point = point / deno;
		DecimalFormat df = new DecimalFormat("####0.0");
		difPoint = String.valueOf(point);
		log.info(difPoint);
		
		difPoint= df.format(point);
		try {
			new ASCIITable().printTable(dif, false);
		} catch (Exception e) {
			
		}
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
				.add("userId", userId).add("tahminId", id + "");
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

		Insert tahminDif = new Sql.Insert("difficulty").doNotUsePrepared()
				.add("userId", userId).add("voteType", voteType)
				.add("vote", vote + "").add("tahminId", id + "")
				.add("ip", FaceUtils.getIp());
		tahminDif.run();
		checkVote();
		FaceUtils.addInfo("Zorluk derecesi kaydedildi.");

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

		checkCounts();
		checkVote();
		checkPartner();
		calcDif();
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
