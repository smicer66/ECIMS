package com.ecims.commins;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Locale;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;

import com.ibm.icu.util.Currency;

public class Test {

	/**
	 * @param args
	 */
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		emailer.emailNewAccountRequest
		(emailAddress, 
			"", password1, portletState.getSystemUrl().getValue(), 
			createdUser.getFirstName(), createdUser.getSurname(), createdUser.getRoleType().getName().getValue(), 
			"New " + portletState.getApplicationName().getValue() + " ECIMS Signup Request", 
			portletState.getApplicationName().getValue());
//		String token = "471451";
//		String tokenConfirm = "471451";
//		String url="http://euc.nsa.gov.ng:8081/AuthServer/ActiveServlet?action=approveApplicationWithTokenHardware&token=" + token + "&tokenConfirm=" + tokenConfirm;
//		//PostMethod post = new PostMethod(url);
//		GetMethod post = new GetMethod(url);
//		
//		NameValuePair nvp1 = new NameValuePair("token", token);
//		NameValuePair nvp2 = new NameValuePair("tokenConfirm", tokenConfirm);
//		NameValuePair nvp3 = new NameValuePair("action", "approveApplicationWithTokenHardware");
//		//post.addParameters(new NameValuePair[]{nvp1, nvp2, nvp3});
//		post.setQueryString(new NameValuePair[]{nvp1, nvp2, nvp3});
//		HttpClient httpclient = new HttpClient();
//		try {
//			int result = httpclient.executeMethod(post);
//			System.out.println("result =" + result);
//			InputStream is = post.getResponseBodyAsStream();
//			StringWriter sw = new StringWriter();
//			IOUtils.copy(is, sw);
//			String theString = sw.toString();
//			System.out.println("String =" + theString);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		Locale[] l = Currency.getAvailableLocales();
//		for(int i=0; i<l.length; i++)
//		{
//			Locale.setDefault(l[i]);
//			//
//			System.out.println("Locale Name = " + l[i].getCountry());
//			if(l[i]!=null && l[i].getCountry().length()>0)
//			{
//				Currency c = Currency.getInstance(l[i]);
//				System.out.println("Locale Name = " + c.getCurrencyCode() + " && " + c.getSymbol());
//			}
//		}
//		
//		String ct[] = {"Afghanistan", "Akrotiri", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Ashmore and Cartier Islands", "Australia", "Austria", "Azerbaijan", "Bahamas, The", "Bahrain", "Bangladesh", "Barbados", "Bassas da India", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burma", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Clipperton Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo, Democratic Republic of the", "Congo, Republic of the", "Cook Islands", "Coral Sea Islands", "Costa Rica", "Cote d-Ivoire", "Croatia", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Dhekelia", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Europa Island", "Falkland Islands (Islas Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "French Guiana", "French Polynesia", "French Southern and Antarctic Lands", "Gabon", "Gambia, The", "Gaza Strip", "Georgia", "Germany", "Ghana", "Gibraltar", "Glorioso Islands", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guernsey", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard Island and McDonald Islands", "Holy See (Vatican City)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Isle of Man", "Israel", "Italy", "Jamaica", "Jan Mayen", "Japan", "Jersey", "Jordan", "Juan de Nova Island", "Kazakhstan", "Kenya", "Kiribati", "Korea, North", "Korea, South", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Namibia", "Nauru", "Navassa Island", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paracel Islands", "Paraguay", "Peru", "Philippines", "Pitcairn Islands", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russia", "Rwanda", "Saint Helena", "Saint Kitts and Nevis", "Saint Lucia", "Saint Pierre and Miquelon", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia and Montenegro", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "Spain", "Spratly Islands", "Sri Lanka", "Sudan", "Suriname", "Svalbard", "Swaziland", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tromelin Island", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Virgin Islands", "Wake Island", "Wallis and Futuna", "West Bank", "Western Sahara", "Yemen", "Zambia", "Zimbabwe"};
//		
//		for(int c=0; c<ct.length; c++)
//			System.out.println("INSERT INTO [ecims].[dbo].[COUNTRY] (NAME, CODE) VALUES ('"+ct[c]+"','"+ct[c]+"');");
	}

}
