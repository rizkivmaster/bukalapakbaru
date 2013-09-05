package bukalapak.view;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrencyFormatter {
	public static String format(int money)
	{
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(String.valueOf(money));
		NumberFormat nf = NumberFormat.getInstance();        
		StringBuffer sb = new StringBuffer();
		while(m.find()) {
		    String g = m.group();
		    m.appendReplacement(sb, nf.format(Double.parseDouble(g)));            
		}
		String result = sb.toString();
		if(result == null || result.trim().isEmpty()) {
		    result = String.valueOf(money);    
		}
		return result;
	}
}
