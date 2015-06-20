package br.feevale.distributeddatabase.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="distributed.database")
@Getter @Setter
public class DistributedDatabaseConfiguration {

	private String folder;

	private String server;

}
