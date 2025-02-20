package cz.bankid.bankid_coding_task.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api.security")
public record ApiSecurityOauthProperties(String issuerUri) { }
