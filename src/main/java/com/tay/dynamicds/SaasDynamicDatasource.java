package com.tay.dynamicds;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.AbstractDataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SaasDynamicDatasource extends AbstractDataSource{

	private Map<String, DataSource> dataSourceMap = new WeakHashMap<String, DataSource>();
	
	private GeneralAttributes generalAttributes;
	private Map<String, TenantDatasourceAttributes> tenantDatasourceAttributesMap;
	
	
	public void setDsProperties(DynamicDatasourceConfigProperties dsProperties) {
		parse(dsProperties);
	}
	
	private void parse(DynamicDatasourceConfigProperties dsProperties2) {
		Map<String, String> generalMap = dsProperties2.getGeneral();
		generalAttributes = new GeneralAttributes();
		generalAttributes.setMaxPoolSize(Integer.parseInt(generalMap.get("maxPoolSize")));
		generalAttributes.setMinIdle(Integer.parseInt(generalMap.get("minIdle")));
		generalAttributes.setDefaultTenant(generalMap.get("defaultTenant"));

		Map<String, Map<String, String>> tenants = dsProperties2.getTenants();
		tenantDatasourceAttributesMap = new HashMap<String, TenantDatasourceAttributes>();
		
		for (String orgCode : tenants.keySet()) {
			Map<String, String> tenantDSAttr = tenants.get(orgCode);
			TenantDatasourceAttributes tenantDatasourceAttributes = new TenantDatasourceAttributes();
			tenantDatasourceAttributes.setUrl(tenantDSAttr.get("url"));
			tenantDatasourceAttributes.setUserName(tenantDSAttr.get("userName"));
			tenantDatasourceAttributes.setPassword(tenantDSAttr.get("password"));
			if(tenantDSAttr.containsKey("maxPoolSize")) {
				tenantDatasourceAttributes.setMaxPoolSize(Integer.parseInt(tenantDSAttr.get("maxPoolSize")));
			}
			else {
				tenantDatasourceAttributes.setMaxPoolSize(generalAttributes.getMaxPoolSize());
			}
			if(tenantDSAttr.containsKey("minIdle")) {
				tenantDatasourceAttributes.setMinIdle(Integer.parseInt(tenantDSAttr.get("minIdle")));
			}
			else {
				tenantDatasourceAttributes.setMinIdle(generalAttributes.getMinIdle());
			}
			tenantDatasourceAttributesMap.put(orgCode, tenantDatasourceAttributes);
		}
		
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	private static class GeneralAttributes {
		private int maxPoolSize;
		private int minIdle;
		private String defaultTenant;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	private static class TenantDatasourceAttributes {
		private String url;
		private String userName;
		private String password;
		private int maxPoolSize;
		private int minIdle;
	}

	@Override
	public Connection getConnection() throws SQLException {
		String currentOrgCode = OrgCodeHolder.getOrgCode();
		if(currentOrgCode == null) {
			currentOrgCode = generalAttributes.getDefaultTenant();
		}
		if(!tenantDatasourceAttributesMap.containsKey(currentOrgCode)) {
			throw new SQLException("there is no datasource configuration for the organization with code " + currentOrgCode);
		}
		TenantDatasourceAttributes tenantDatasourceAttributes = tenantDatasourceAttributesMap.get(currentOrgCode);
		DataSource ds = dataSourceMap.get(currentOrgCode);
		//double check
		if(ds == null) {
			synchronized(this) {
				ds = dataSourceMap.get(currentOrgCode);
				if(ds == null) {
					HikariConfig config = new HikariConfig();
					config.setDriverClassName("com.mysql.jdbc.Driver");
					config.setJdbcUrl(tenantDatasourceAttributes.getUrl());
					config.setUsername(tenantDatasourceAttributes.getUserName());
					config.setPassword(tenantDatasourceAttributes.getPassword());
					config.setMaximumPoolSize(tenantDatasourceAttributes.getMaxPoolSize());
					config.setMinimumIdle(tenantDatasourceAttributes.getMinIdle());
					ds =  new HikariDataSource(config);
					dataSourceMap.put(currentOrgCode, ds);
				}
			}
		}
		return ds.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
}
