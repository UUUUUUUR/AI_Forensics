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
            	 String endpointUrl = "https://api.openai.com/v1/chat/completions"; // Chat GPT API의 엔드포인트 URL
                 String apiKey = ""; // API 인증에 사용되는 키
                 String model = "gpt-3.5-turbo";
                 String[] data = new String[3];
                 //gpt-3.5-turbo
                 //gpt-4

                 try {
                     // HttpClient 인스턴스 생성
                     HttpClient httpClient = HttpClientBuilder.create().build();

                     // API 요청 생성
                     String apiUrl = endpointUrl;
                     HttpPost request = new HttpPost(apiUrl);
                     request.addHeader("Authorization", "Bearer " + apiKey);
                     request.addHeader("Content-Type", "application/json");

                     // API 요청 데이터 생성
                     String requestData = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" 
                     		+"레지스트리 중 muicache에 "+question+"은(는) 비인가 프로그램인가요,"
                     		+ "아니면 정상적인 파일인가요?"
                     		+ "이상이 없다고 판단된다면 0, 의심할 여지가 있다 판단된다면 1, 위험한 파일명이라 생각한다면 2라고 알려주세요."
                     		+ "답변은 파일의 위치 : <br> 판단된 숫자 : <br> 판단한 이유 : 이렇게 서술해주세요."
                     		+ "아래에는 규칙이 있습니다. "
                     		+ "제가 부탁드린 세개의 항목 이 외 아무런 추가적인 답변은 필요가 없습니다."
                     		+ "파일의 위치에는 파일이 저장된 위치와 파일명을 함께 적어주세요."
                     		+ "파일의 이름이 즉 exe이름이 정상적이라도 프로그램이 실행된 위치가 일반적으로 실행되는 위치와 다르다면 위험하다고 판단해주세요."
                     		+ "파일의 위치와 파일명을 고려했을 때 정상적인 위치에 정상적인 파일이라면 위험하지 않다고 판단해도됩니다."
                     		+ "정상적인 파일이라면 파일의 용도를 판단된 이유에 서술해주세요." + "\"}]}";
                     StringEntity entity = new StringEntity(requestData, "utf-8");
                     request.setEntity(entity);

                     // API 요청 전송
                     HttpResponse response = httpClient.execute(request);

                     // API 응답 처리
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
        

        return future; // Future 객체 반환. 이 객체를 통해 쓰레드의 결과를 얻을 수 있음
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
