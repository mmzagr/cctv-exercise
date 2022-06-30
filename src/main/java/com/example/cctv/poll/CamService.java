package com.example.cctv.poll;

import com.example.cctv.data.CameraInfo;
import com.example.cctv.data.SourceData;
import com.example.cctv.data.TokenData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientRequest;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CamService {
    private List<CameraInfo> cameraInfoList;
    private final WebClient webClient;
    private final TaskExecutor taskExecutor;

    public List<CameraInfo> getCamList() {
        return cameraInfoList;
    }

    public void setCamList() {
        Mono<CameraInfo[ ]> response = webClient.get().uri("5c51b9dd3400003252129fb5").accept(MediaType.APPLICATION_JSON).retrieve()
                .bodyToMono(CameraInfo[].class);
        CameraInfo[] objects = response.block();                                                                                            //wait until all initial data is received

        cameraInfoList = Arrays.stream(objects).toList();
        cameraInfoList.forEach(cam -> taskExecutor.execute(() -> setSourceData(cam)));
        cameraInfoList.forEach(cam -> taskExecutor.execute(() -> setTokenData(cam)));
        return;
    }

    public void setSourceData(CameraInfo cameraInfo) {
        log.debug("getting sourceDataUrl for cam id {}", cameraInfo.getId());
        String uri = cameraInfo.getSourceDataUrl().substring(cameraInfo.getSourceDataUrl().lastIndexOf("/") + 1);

        Mono<SourceData> response = webClient.get().uri(uri)
                .httpRequest(request -> {
                    HttpClientRequest nativeRequest = request.getNativeRequest();
                    nativeRequest.responseTimeout(Duration.ofMillis(getDelay(uri)));
                })
                .accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(SourceData.class);

        SourceData sourceData = response.block();
        cameraInfo.setSourceData(sourceData);

//        log.info("{}", cameraInfo);

        return;
    }

    public void setTokenData(CameraInfo cameraInfo) {
        log.debug("getting tokenDataUrl for cam id {}", cameraInfo.getId());
        String uri = cameraInfo.getTokenDataUrl().substring(cameraInfo.getTokenDataUrl().lastIndexOf("/") + 1);

        Mono<TokenData> response = webClient.get().uri(uri)
                .httpRequest(request -> {
                    HttpClientRequest nativeRequest = request.getNativeRequest();
                    nativeRequest.responseTimeout(Duration.ofMillis(getDelay(uri)));
                })
                .accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(TokenData.class);
        TokenData tokenData = response.block();
        cameraInfo.setTokenData(tokenData);
//        log.info("{}", cameraInfo);

        return;
    }

    public int getDelay(String uri) {
        int delay = 1000;

        int idx = uri.indexOf("mocky-delay=") + 12;

        if (idx == 11)
            return delay;

        var num = new StringBuilder();

        while (idx < uri.length()) {
            var ch = uri.charAt(idx);
            if (ch >= '0' && ch <= '9')
                num.append(ch);
            else
                break;
            idx++;
        }
        delay = NumberUtils.parseNumber(num.toString(), Integer.class);
        delay =  (uri.charAt(idx) == 's') ? delay * 1000 : delay;

        log.debug("url = {} timeout = {} resolution = {}", uri, num, uri.substring(idx));
        return delay * 2;
    }

}
