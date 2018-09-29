package com.iotimc;


import com.iotimc.elsi.auth.agency.ElsiAuthAgencyApplication;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 *
 * @author ${author}
 *
 */
@SpringBootApplication
public class ElsiClientApplication extends ElsiAuthAgencyApplication {

	public static void main(String[] args) {
		if (StringUtils.isBlank(System.getProperty("LOGGER_HOME"))) {
			System.setProperty("LOGGER_HOME", ElsiClientApplication.class.getResource("/").getPath());
		}

		SpringApplication.run(ElsiClientApplication.class, args);
	}
}
