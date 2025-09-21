package travel.travel.plan.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

@Service
@Slf4j
public class AreaCodeService {

    @Value("${area.api.service-key}")
    private String serviceKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Long getAreaCodeByName(String areaName) {
        try {
            String url = "https://apis.data.go.kr/B551011/KorService2/areaCode2?serviceKey=" +serviceKey+
                    "&numOfRows=100&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=xml";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Charset", "UTF-8");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            String xml = response.getBody();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("ISO-8859-1")));

            NodeList items = doc.getElementsByTagName("item");

            for (int i = 0; i < items.getLength(); i++) {
                Element elem = (Element) items.item(i);
                String name = elem.getElementsByTagName("name").item(0).getTextContent().trim();
                String code = elem.getElementsByTagName("code").item(0).getTextContent().trim();
                String firstTwo = areaName.length() >= 2 ? areaName.substring(0, 2) : areaName;
                if (name.equals(firstTwo) || name.contains(firstTwo) || firstTwo.contains(name)) {
                    log.info("매칭: " + name + " -> " + code);
                    return Long.parseLong(code);
                }
            }

            return null;
        } catch (Exception e) {
            log.info("오류: " + e.getMessage());
            return null;
        }
    }
}