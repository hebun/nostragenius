package nostragenus;

import static freela.util.FaceUtils.log;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import freela.util.ASCIITable;
import freela.util.Db;
import freela.util.FaceUtils;
import freela.util.Sql;
import freela.util.Sql.Select;

public class Nostra {
	public static final int RECORD_COUNT=2;
	public static final String BESTUSERS_SQL = " SELECT u.id as taid,u.uname,avg(t.point) as ort,count(tp.id) as say FROM `tahminpartner` "
			+ "as tp join user as u on u.id=tp.userId join tahmin as t "
			+ "		on tp.tahminId=t.id group by u.uname,taid";
	public static final String BESTPARTNERS_SQL = " SELECT u.id as taid,u.uname,avg(t.point) as ort,count(tp.id) as say FROM `tahminpartner` "
			+ "as tp join user as u on u.id=tp.userId join tahmin as t "
			+ "		on tp.tahminId=t.id where tp.userId<>tp.ownerId group by u.uname,taid";

	public static String getDifPoint(String tahId) {
		List<Map<String, String>> dif = new Select(
				"tahminId,voteType, avg(vote) as ort").from("difficulty")
				.where("tahminId", tahId).groupBy("voteType,tahminId")
				.getTable();
		double point = 0.0;
		int deno = 0;
		for (Map<String, String> map : dif) {
			if (map.get("voteType").equals("1")
					|| map.get("voteType").equals("2")) {
				point += Double.parseDouble(map.get("ort"));
				deno++;
			} else if (map.get("voteType").equals("3")) {
				point += Double.parseDouble(map.get("ort")) * 3;
				deno += 3;
			} else if (map.get("voteType").equals("4")) {
				point += Double.parseDouble(map.get("ort")) * 5;
				deno += 5;
			}
		}
		if (deno == 0)
			return "0.0";
		point = point / deno;
		DecimalFormat df = new DecimalFormat("####0.0");
		String string = df.format(point);

		return string;

	}

	public static String getHitPoint(String tahId) {
		List<Map<String, String>> dif = new Select(
				"tahminId,voteType, avg(vote) as ort").from("tahminhit")
				.where("tahminId", tahId).groupBy("voteType,tahminId")
				.getTable();
		double point = 0.0;
		int deno = 0;
		for (Map<String, String> map : dif) {
			if (map.get("voteType").equals("3")) {
				point += Double.parseDouble(map.get("ort")) * 3;
				deno += 3;
			} else if (map.get("voteType").equals("4")) {
				point += Double.parseDouble(map.get("ort")) * 7;
				deno += 7;
			}
		}
		if (deno == 0)
			return "0.0";
		point = point / deno;
		DecimalFormat df = new DecimalFormat("####0.0");
		String string = df.format(point);

		return string;

	}

	public static int getMaxTahSay() {
		Object maxtah = FaceUtils.getSession("maxtah");

		if (maxtah == null) {
			String say = new Sql.Select("userId,count(*) as say ")
					.from("tahmin").groupBy("userId").order("say").desc()
					.limit(1).getTable().get(0).get("say");
			log.info("maxtah is null calced:" + say);
			FaceUtils.setSession("maxtah", say);
			return Integer.parseInt(say);

		} else {
			log.info("maxtah is NOT null . got:" + maxtah);
			return Integer.parseInt(maxtah.toString());
		}

	}

	public static String getUserPoint(String id) {
		List<Map<String, String>> table = Db
				.selectTable(" SELECT avg(t.point) as ort,count(tp.id) as say FROM `tahminpartner` "
						+ "as tp join user as u on u.id=tp.userId join tahmin as t "
						+ "		on tp.tahminId=t.id where tp.userId=" + id);
		;
		if (table.size() == 0)
			throw new RuntimeException("the user with id:" + id
					+ " has no tahmin");

		String ortalama = table.get(0).get("ort");

		return calcUserPoint(table.get(0).get("say"), ortalama);

	}

	public static String calcUserPoint(String say1, String ortalama) {
		double say = Double.parseDouble(say1);
		say = say / getMaxTahSay();

		if (say < 0.5)
			say = 0.5;

		double ort = Double.parseDouble(ortalama);
		ort = ort * say;
		DecimalFormat df = new DecimalFormat("####0.0");
		String string = df.format(ort);
		return string;
	}

	public static List<Map<String, String>> getBestTahmins() {
		Date time = new Date(
				Calendar.getInstance().getTime().getTime() - 48 * 3600 * 1000);
		List<Map<String, String>> tahs = new Sql.Select().from("tahmin")
				.join("user").doNotUsePrepared().on("tahmin.userId", "user.id")
				
				.where("occurTime<", FaceUtils.getFormattedTime()).getTable();

		long tenMin = Calendar.getInstance().getTime().getTime() - 600 * 1000;
		List<Map<String, String>> updateTable = new Sql.Select()
				.from("updatetahmin")
				.where("tarih>", FaceUtils.getFormattedTime(new Date(tenMin)))
				.getTable();
		boolean doUpdate = false;
		if (updateTable.size() == 0) {// no update in last 10 mins
			doUpdate = true;
		}

		for (Map<String, String> tah : tahs) {

			double dif1 = Double.parseDouble(Nostra.getDifPoint(tah.get("id")));
			double hit1 = Double.parseDouble(Nostra.getHitPoint(tah.get("id")));
			log.info("dif:" + dif1 + " hit:" + hit1);
			double d = (dif1 * hit1) / 10.0;
			tah.put("point", String.valueOf(d));
			if (doUpdate) {
				
				new Sql.Update("tahmin").add("point", d).add("difPoint", dif1)
						.add("hitPoint", hit1).where("id", tah.get("id")).run();
			}
		}
		if (doUpdate) {
			new Sql.Insert("updatetahmin").add("type", "1")
					.add("tarih", FaceUtils.getFormattedTime()).run();
		}
		Collections.sort(tahs, new Comparator<Map<String, String>>() {

			@Override
			public int compare(Map<String, String> o1, Map<String, String> o2) {
				double d1 = Double.parseDouble(o1.get("point"));
				double d2 = Double.parseDouble(o2.get("point"));

				return d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
			}
		});
		Collections.reverse(tahs);
		return tahs;
	}
}
