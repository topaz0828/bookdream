package win.hellobro.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
//@EnableDiscoveryClient
public class ReviewDreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewDreamApplication.class, args);
	}
}
