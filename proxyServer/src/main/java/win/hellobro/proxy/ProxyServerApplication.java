package win.hellobro.proxy;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Configuration;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixThreadPoolProperties;

@EnableEurekaClient
@EnableZuulProxy
@SpringBootApplication
@Configuration
public class ProxyServerApplication {

	@Value ("${custom.hystrix.circuit-breaker.enabled:true}")
	boolean hystrixCircuitBreakerEnable;
	
	@Value ("${custom.hystrix.timeout.enabled:true}")
	boolean hystrixTimeOutEnable;

	@Value ("${custom.hystrix.interrupt.timeout.enabled:true}")
	boolean hystrixInterruptTimeOutEnable;
	
	@Value("${custom.hystrix.thread-timeout:3000}")
	private int hystrixTimeOut;
	
	@Value("${custom.hystrix.read-timeout:60000}")
	private int ribbonReadTimeout;
	
	@Value("${custom.hystrix.connect-timeout:60000}")
	private int ribbonConnectTimeout;
	
	@Value("${custom.hystrix.stream.enpoint.enabled:true}")
	boolean hystrixEndPonitEnable;

	@Value("${custom.hystrix.threadpool.default.coreSize:10}")
	private int coreSize;
	
	@Value("${custom.hystrix.threadpool.default.maximumSize:10}")
	private int maximumSize;
	
	@Value("${custom.hystrix.threadpool.default.maxQueueSize:-1}")
	private int maxQueueSize;
	
	public static void main(String[] args) {
		SpringApplication.run(ProxyServerApplication.class, args);
	}
	
	@PostConstruct
	void setPrep() {
		System.out.println("");
		HystrixThreadPoolProperties.Setter().withMaxQueueSize(20).withAllowMaximumSizeToDivergeFromCoreSize(true);
		
		ConfigurationManager.getConfigInstance().setProperty("ribbon.ReadTimeout", ribbonReadTimeout);
		ConfigurationManager.getConfigInstance().setProperty("ribbon.ConnectTimeout", ribbonConnectTimeout);
		
		ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.circuitBreaker.enabled", hystrixCircuitBreakerEnable);
		ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.execution.timeout.enabled",hystrixTimeOutEnable);

		ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.execution.isolation.thread.interruptOnTimeout", hystrixInterruptTimeOutEnable);
		ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds", hystrixTimeOut);

	    System.setProperty("hystrix.stream.endpoint.enabled", hystrixEndPonitEnable?"true":"false"); //disable hystrix.stream

	    ConfigurationManager.getConfigInstance().setProperty("hystrix.threadpool.default.coreSize", coreSize);
		ConfigurationManager.getConfigInstance().setProperty("hystrix.threadpool.default.maximumSize", maximumSize);
		ConfigurationManager.getConfigInstance().setProperty("hystrix.threadpool.default.maxQueueSize", maxQueueSize);
	}
	
}
