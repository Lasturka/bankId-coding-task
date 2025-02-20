package cz.bankid.bankid_coding_task.config;

import cz.bankid.bankid_coding_task.repository.CurrencyProvider;
import io.netty.handler.logging.LogLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    public WebClient webClient(CurrencyProvider provider) {
        return WebClient.builder()
                .baseUrl(provider.getBaseURL())
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .build();
    }

    private HttpClient httpClient() {
        return HttpClient.create()
                .wiretap("reactor.netty.http.client.HttpClient",
                        LogLevel.DEBUG,
                        AdvancedByteBufFormat.TEXTUAL);
    }

}