package com.melly.myjpa.crawling;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BakeryCrwalerService {
    private final BakeryRepository bakeryRepository;

    @Value("${webdriver.chrome.path}")
    private String chromeDriverPath;

    public void crawlCafeData() {
        // 크롬 드라이버 설정
        System.setProperty("webdriver.chrome.driver", chromeDriverPath); // 윈도우

        // 크롬 옵션 설정
        ChromeOptions options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(options); // WebDriver 생성

        // 서울 특별시 구 리스트
        String[] guList = {"마포구", "서대문구", "은평구", "종로구", "중구", "용산구", "성동구", "광진구",
                "동대문구", "성북구", "강북구", "도봉구", "노원구", "중랑구", "강동구", "송파구",
                "강남구", "서초구", "관악구", "동작구", "영등포구", "금천구", "구로구", "양천구", "강서구"};

        try {
            for (String guName : guList) {
                // 1. 카카오맵 메인 페이지 열기
                driver.get("https://map.kakao.com/");

                // 2. 검색창에 구 이름 입력 후 엔터
                WebElement searchBox = driver.findElement(By.cssSelector("#search\\.keyword\\.query"));
                searchBox.clear(); // 검색창 초기화
                searchBox.sendKeys(guName + " 제과점"); // 구 이름 + 제과점 검색
                searchBox.sendKeys(Keys.RETURN);

                // 3. 검색 결과 로딩 대기
                Thread.sleep(2000); // 로딩 대기 시간 조정
                System.out.println(guName + " 검색 완료");

                // 3. "더보기" 버튼 반복 클릭
                try {
                    WebElement moreButton = driver.findElement(By.cssSelector("#info\\.search\\.place\\.more"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", moreButton);
                    Thread.sleep(2000); // 로딩 대기
                    System.out.println("더보기 버튼 클릭 완료");
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                } catch (NoSuchElementException e) {
                    System.out.println("더보기 버튼 없음. 모든 데이터 수집 완료.");
                }

                // 첫 번째 페이지 로딩 대기
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#info\\.search\\.page"))); // 페이지가 로딩될 때까지 대기

                int currentPage = 1; // 현재 페이지 번호
                int maxPage = 25; // 최대 페이지 수 (필요에 맞게 설정)

                while (currentPage < maxPage) {
                    pageSelect(driver,guName);
                    currentPage+=5;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            driver.quit(); // 드라이버 종료
        }
    }

    private void pageSelect(WebDriver driver,String guName) throws InterruptedException {
        // 1~5 페이지 크롤링
        for (int i = 1; i <= 5; i++) {
            // 페이지 번호 클릭하여 해당 페이지로 이동
            String pageSelector = "#info\\.search\\.page\\.no" + i; // 페이지 번호 셀렉터
            try {
                WebElement pageLink = driver.findElement(By.cssSelector(pageSelector));

                // 페이지 스크롤로 요소 보이게 함
                JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
                jsExecutor.executeScript("arguments[0].scrollIntoView(true);", pageLink);
                Thread.sleep(500); // 잠시 대기

                // JavaScript로 클릭 시도
                jsExecutor.executeScript("arguments[0].click();", pageLink);

                Thread.sleep(2000); // 페이지 로딩 대기
                extractBakeryData(driver,guName); // 해당 페이지에서 데이터 추출

                if(i == 5){
                    // 페이지 스크롤로 요소 보이게 함
                    jsExecutor.executeScript("arguments[0].scrollIntoView(true);", pageLink);
                    Thread.sleep(500); // 잠시 대기
                    // JavaScript로 클릭 시도
                    WebElement nextButton = driver.findElement(By.cssSelector("#info\\.search\\.page\\.next"));
                    jsExecutor.executeScript("arguments[0].click();", nextButton);
                }
            } catch (NoSuchElementException e) {
                System.out.println("페이지 번호를 찾을 수 없습니다: " + i);
                break; // 페이지가 더 이상 없다면 종료
            } catch (ElementClickInterceptedException e) {
                System.out.println("클릭이 가로막혔습니다. 2초 대기 후 재시도.");
                Thread.sleep(2000); // 대기 후 재시도
            }
        }
    }


    private void extractBakeryData(WebDriver driver, String guName) {
        List<WebElement> bakeryItems = driver.findElements(By.cssSelector(".PlaceItem"));
        System.out.println("현재 페이지 데이터 개수: " + bakeryItems.size());

        for (WebElement bakery : bakeryItems) {
            try {
                String bakeryName = bakery.findElement(By.cssSelector(".head_item .link_name")).getText();
                String bakeryAddress = bakery.findElement(By.cssSelector(".addr p")).getText();
                String bakeryTel = "";
                try {
                    bakeryTel = bakery.findElement(By.cssSelector(".contact .phone")).getText();
                } catch (NoSuchElementException e) {
                    bakeryTel = "전화번호 없음";
                }
                String bakeryHours = "";
                try {
                    bakeryHours = bakery.findElement(By.cssSelector(".openhour p a")).getText();
                } catch (NoSuchElementException e) {
                    bakeryHours = "영업시간 정보 없음";
                }

                System.out.printf("제과점 정보: %s / %s / %s / %s%n", bakeryName, bakeryAddress, bakeryTel, bakeryHours);
                saveBakeryData(bakeryName, bakeryAddress, bakeryTel, bakeryHours,guName);

            } catch (NoSuchElementException e) {
                System.out.println("데이터 추출 중 오류 발생.");
            }
        }
    }

    private void saveBakeryData(String name, String address, String tel, String openHour, String region) {
        // 중복 체크: 이름과 주소가 동일한 데이터가 있는지 확인
        if (bakeryRepository.existsByNameAndAddress(name, address)) {
            System.out.println("중복 데이터 스킵: " + name + ", " + address);
            return;
        }

        Bakery bakery = Bakery.builder()
                .name(name)
                .address(address)
                .tel(tel)
                .openHour(openHour)
                .region(region).build();
        try {
            bakeryRepository.save(bakery);
        } catch (Exception e) {
            System.err.println("데이터 저장 중 오류 발생: " + name + ", " + address);
            e.printStackTrace();
        }
    }
}
