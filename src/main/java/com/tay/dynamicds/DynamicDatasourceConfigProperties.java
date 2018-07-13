package com.tay.dynamicds;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "dynamicds")
@Data
public class DynamicDatasourceConfigProperties {
	private String orgCodeHeader;
	private Map<String, String> general;
	private Map<String, Map<String, String>> tenants;
}
