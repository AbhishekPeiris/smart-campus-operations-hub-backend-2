package com.smartcampus.operationshub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.smartcampus.operationshub.auth.config.GoogleOAuthProperties;
import com.smartcampus.operationshub.storage.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({ FileStorageProperties.class, GoogleOAuthProperties.class })
public class OperationshubApplication {

	public static void main(String[] args) {
		SpringApplication.run(OperationshubApplication.class, args);
	}

}
