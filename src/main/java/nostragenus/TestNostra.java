package nostragenus;

import static org.junit.Assert.*;

import org.junit.Test;

import admin.User;

public class TestNostra {
/**
 * CURRENT: non-logged in users making hitPoint
 * tahmin yap page.
 * ISSUES:
 * +datetime background
 * *register message for password not working
 * *datepicker too big
 * -local controle activation/register
 * *mailcontents are old
 * +message resource bundle
 * *yorum yapabilirsiniz vs in index page
 * -time format in index
 * -user or ip condition in sql
 * -go back to page after login.
 * -primefaces button in yorumlar
 * -paging in yorumlar
 * -order issue in index tahmins list
 * -6 hours pause for tahmin in index
 * -menu effect/jquery problems in yorum yap and index
 * -bestusers should include partner tahmins
 * -dont recalculate tahmin points before 10 mins
 * -in add tahmin check logged in users
 * -do not update done tahmins in nostra.getbesttahmins
 * 
 * admin/
 * CURRENT: tahmin list in admin(dynamic columns with primeface)
 * TODO's:
 * tahmin list in admin,
 * tahmin detay in admin,
 * -content pages implementation
 * -dynamic columns in admin(primefaces)
 * CHECK: admin login check disabled
 */
	
	
	@Test
	public void test() {
		
	}
	@Test
	public void testAdminUser(){
		User user=new User();
		user.getColumns().get(1).setHeader("from Test");
		user.updateColumns();
		
	}
/**
 * -puan ver yorum yap ve giris ayni sayfaya bakiyor. bu sayfada uye girs yapcm misfarim gibi yazilar var. 
 * -tahmin gir sayfasinda anahtar kelimeler nasil girilecek. virgul ile ayrilacak(belirtilmiyor) yada boslukla mi(iki kelimelik anahtar kelimeler olamayicak)
 * -en iyi tahminler sayfasindaki toplam puan neyin puani
 * -en iyi tahminler sayfasindaki liste asagi kayiyor.
 */
}
