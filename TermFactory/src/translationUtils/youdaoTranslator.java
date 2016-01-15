package translationUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@SuppressWarnings("deprecation")
public class youdaoTranslator {
	static final String APIKey = "1618896162";
	static final String keyFrom = "TermFactory";
	
	public static void main(String[] args){
		String Chinese = getTranslation("Ear Normal");
		System.out.print(Chinese);
	}
	public static String getTranslation(String text){
		@SuppressWarnings("resource")
		HttpClient httpclient = new DefaultHttpClient();
        String urlStr = "http://fanyi.youdao.com/openapi.do?keyfrom=" + keyFrom + "&key=" + APIKey + "&type=data&doctype=json&version=1.1&q=" + URLEncoder.encode(text);
        HttpGet httpGet = new HttpGet(urlStr);
        
        HttpResponse response;
		try {
			response = httpclient.execute(httpGet);
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
                String resultString = writer.toString();
                Gson gson = new Gson();
                JsonElement element = gson.fromJson(resultString, JsonElement.class);
                JsonObject jsonObj = element.getAsJsonObject();
                String rawResult = null;
                //web翻译的效果更好,首先选择web中的 key与当前输入完全匹配的结果
                if(jsonObj.get("web") != null){
                	JsonElement webTrans = jsonObj.get("web");
                	//一般取第一个
                	if(webTrans.getAsJsonArray().get(0) != null){
                		JsonArray firstWeb = webTrans.getAsJsonArray();
                		//System.out.println(firstWeb);
                		JsonObject rawResultObj = firstWeb.get(0).getAsJsonObject();
                		rawResult = rawResultObj.get("value").getAsJsonArray().get(0).getAsString();
                		return rawResult.trim();
                	}
                }
                if(jsonObj.get("translation") == null)
                	return null;
                rawResult = jsonObj.get("translation").getAsString();
                return rawResult;
            }
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
        
		return null;
	}
}
