package controllers;

import play.i18n.Lang;
import play.mvc.Controller;

public class Language extends Controller {

	public static void change(String locale, String from){
		Lang.change(locale);
		redirect(from);
	}
	
}
