package pax.tecs.pip.rpt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ComponentScan(basePackages = "pax.tecs.pip.rpt")
public class AppConfig {

	@Bean
	public S3Client s3Client() {
		return  S3Client.builder().build();
	}

}