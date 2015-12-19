package translationUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

@SuppressWarnings("deprecation")
public class microsoftTranslator {
	//6c5ef88f-635d-4e5e-91d6-bbe0d78484e5
	static final String CLIENT_ID = "TermFactory";
	//kyLRoOmeTtnhM24r/LFrfEKkr1dDonGWdNWOalrNwz4
	static final String CLIENT_SECRET = "flt2010013229flt2010013229";
	
	public static List<String> getTranslation(List<String> textList){
		List<String> result = new ArrayList<String>();
		Translate.setClientId(CLIENT_ID);
		Translate.setClientSecret(CLIENT_SECRET);
		for(String text:textList){
			try {
				result.add(Translate.execute(text, Language.ENGLISH, Language.CHINESE_SIMPLIFIED));
			} catch (Exception e) {
				result.add("&&出错了！");
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static String getTranslation(String text){
		String result = null;
		Translate.setClientId(CLIENT_ID);
		Translate.setClientSecret(CLIENT_SECRET);
		try {
			result = Translate.execute(text, Language.ENGLISH, Language.CHINESE_SIMPLIFIED);
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}
	
    public static void main(String[] args) throws Exception {  
        //List<String> translatedText = getTranslation(Arrays.asList("test"));  
        //System.out.println(translatedText.toString());  
        //translatedText = getTranslation(Arrays.asList("hello world"));  
        //System.out.println(translatedText.toString());  
        //translatedText = getTranslation(Arrays.asList("gene expression ontology"));  
        //System.out.println(translatedText.toString());  
    	String result = translate("current procedure terminology");
    	System.out.println(result);
    }
	public static String translate(String text) throws IOException {
        try {
            // Construct content
            String content = "grant_type=client_credentials";
            content += "&client_id=" + URLEncoder.encode(CLIENT_ID);
            content += "&client_secret=" + URLEncoder.encode(CLIENT_SECRET);
            content += "&scope=http://api.microsofttranslator.com";

            // Send data
            URL url = new URL("https://datamarket.accesscontrol.windows.net/v2/OAuth2-13/");
            URLConnection conn = url.openConnection();

            // Let the run-time system (RTS) know that we want input.
            conn.setDoInput(true);
            // Let the RTS know that we want to do output.
            conn.setDoOutput(true);
            // No caching, we want the real thing.
            conn.setUseCaches(false);
            // Specify the content type.
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Send POST output.
            DataOutputStream printout = new DataOutputStream(conn.getOutputStream());
            printout.writeBytes(content);
            printout.flush();
            printout.close();

            // Get response data.
            DataInputStream input = new DataInputStream(conn.getInputStream());
            String str = "";
            String accessToken = "";
            while (null != ((str = input.readLine()))) {
                System.out.println(str);
                str = str.split(",")[1].split(":")[1];
                accessToken = str.substring(1, str.length() - 1 );
                System.out.println("access token: ");
                System.out.println(accessToken);
            }
            input.close();

            //Call Microsoft Translate
            @SuppressWarnings("resource")
			HttpClient httpclient = new DefaultHttpClient();
            String urlStr = "http://api.microsofttranslator.com/v2/Http.svc/Translate?text=" + URLEncoder.encode(text) + "&from=en&to=zh";
            urlStr = urlStr + "&appId=" + URLEncoder.encode("Bearer "+accessToken);
            HttpGet httpGet = new HttpGet(urlStr);
            
            HttpResponse response = httpclient.execute(httpGet);
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                int l;
                Writer writer = new StringWriter();
                char[] buffer = new char[1024];
                Reader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
                while ((l = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, l);
                }

                //print result
                System.out.println(writer.toString());
                String result = writer.toString();
                result = result.substring(result.indexOf(">"), result.indexOf("</"));
                System.out.println(result);
                return result;
            }
        } catch (Exception e) {
        	return null;
        }
        return null;
    }
    
    //String urlStr = "http://api.microsofttranslator.com/v2/Http.svc/Translate?text="+URLEncoder.encode("hello world")+"&from=en&to=ja";
}

