package win.hellobro.recovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
@EnableDiscoveryClient
public class RecoveryServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecoveryServerApplication.class, args);
	}
}
