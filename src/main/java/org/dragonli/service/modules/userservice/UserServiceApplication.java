package org.dragonli.service.modules.userservice;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.log4j.Logger;
import org.dragonli.service.dubbosupport.DubboApplicationBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},scanBasePackages={"org.dragonli"})
@DubboComponentScan(basePackages = "org.dragonli.service.modules.userservice.service")
public class UserServiceApplication extends DubboApplicationBase {
	public UserServiceApplication(
			@Value("${service.micro-service.user-service.application-name}") String applicationName,
			@Value("${service.micro-service.common.registry-address}") String registryAddr,
			@Value("${service.micro-service.user-service.protocol-name}") String protocolName,
			@Value("${service.micro-service.user-service.protocol-port}") Integer protocolPort,
			@Value("${service.micro-service.user-service.scan}") String registryId,
			@Value("${service.micro-service.user-service.group}") String group,
			@Value("${service.micro-service.user-service.http-port}") int port
		)
	{
		//super(applicationName, registryAddr, protocolName, protocolPort, registryId, port);
//		super("dubbo-netty", registryAddr, protocolName, 20900, "com.itranswarp.crypto.serviceInterface", 1);
		super(applicationName, registryAddr, protocolName, protocolPort, registryId, port,null
				,group != null && !"".equals(group.trim()) ? group.trim() : null);
	}

	@SuppressWarnings(value = "unused")
	final Logger logger = Logger.getLogger(getClass());

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
}
