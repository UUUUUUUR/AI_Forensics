package ChatGPT;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;





public class ModulesTest {

	public static Future<String[]> main(String question, ExecutorService executor) {
		
        Callable<String[]> callable = new Callable<String[]>() {
            @Override
            public String[] call() throws Exception {
            	 String endpointUrl = "https://api.openai.com/v1/chat/completions"; // Chat GPT API�� ��������Ʈ URL
                 String apiKey = ""; // API ������ ���Ǵ� Ű
                 String model = "gpt-3.5-turbo";
                 String[] data = new String[3];
                 //gpt-3.5-turbo
                 //gpt-4

                 try {
                     // HttpClient �ν��Ͻ� ����
                     HttpClient httpClient = HttpClientBuilder.create().build();

                     // API ��û ����
                     String apiUrl = endpointUrl;
                     HttpPost request = new HttpPost(apiUrl);
                     request.addHeader("Authorization", "Bearer " + apiKey);
                     request.addHeader("Content-Type", "application/json");

                     // API ��û ������ ����
                     String requestData = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" 
                     		+"������Ʈ�� �� muicache�� "+question+"��(��) ���ΰ� ���α׷��ΰ���,"
                     		+ "�ƴϸ� �������� �����ΰ���?"
                     		+ "�̻��� ���ٰ� �Ǵܵȴٸ� 0, �ǽ��� ������ �ִ� �Ǵܵȴٸ� 1, ������ ���ϸ��̶� �����Ѵٸ� 2��� �˷��ּ���."
                     		+ "�亯�� ������ ��ġ : <br> �Ǵܵ� ���� : <br> �Ǵ��� ���� : �̷��� �������ּ���."
                     		+ "�Ʒ����� ��Ģ�� �ֽ��ϴ�. "
                     		+ "���� ��Ź�帰 ������ �׸� �� �� �ƹ��� �߰����� �亯�� �ʿ䰡 �����ϴ�."
                     		+ "������ ��ġ���� ������ ����� ��ġ�� ���ϸ��� �Բ� �����ּ���."
                     		+ "������ �̸��� �� exe�̸��� �������̶� ���α׷��� ����� ��ġ�� �Ϲ������� ����Ǵ� ��ġ�� �ٸ��ٸ� �����ϴٰ� �Ǵ����ּ���."
                     		+ "������ ��ġ�� ���ϸ��� ������� �� �������� ��ġ�� �������� �����̶�� �������� �ʴٰ� �Ǵ��ص��˴ϴ�."
                     		+ "�������� �����̶�� ������ �뵵�� �Ǵܵ� ������ �������ּ���." + "\"}]}";
                     StringEntity entity = new StringEntity(requestData, "utf-8");
                     request.setEntity(entity);

                     // API ��û ����
                     HttpResponse response = httpClient.execute(request);

                     // API ���� ó��
                     HttpEntity responseEntity = response.getEntity();
                     if (responseEntity != null) {
                     	
                     	String responseBody = EntityUtils.toString(responseEntity);
                     	
                     	JSONObject jsonObject = new JSONObject(responseBody);
                         JSONArray choicesArray = jsonObject.getJSONArray("choices");
                         JSONObject firstChoiceObject = choicesArray.getJSONObject(0);
                         JSONObject messageObject = firstChoiceObject.getJSONObject("message");
                         String content = messageObject.getString("content");
                         //System.out.println(content+ "\n\n");
                         String[] contentsplit = content.split("\n");
                         for(int i = 0; i < 3; i++) {
                         	String result = removeInfo(contentsplit[i]);
                         	data[i] = result.trim();
                         }
                         
                     }
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
            	
            	return data;
            }
        };

        Future<String[]> future = executor.submit(callable);
        

        return future; // Future ��ü ��ȯ. �� ��ü�� ���� �������� ����� ���� �� ����
    }
	
	 private static String removeInfo(String input) {
	        String[] lines = input.split("\n");
	        StringBuilder sb = new StringBuilder();

	        for (String line : lines) {
	            int colonIndex = line.indexOf(":");
	            if (colonIndex != -1) {
	                String value = line.substring(colonIndex + 1).trim();
	                sb.append(value).append("\n");
	            }
	        }

	        return sb.toString().trim();
	    }
	 

}
