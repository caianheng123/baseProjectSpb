package com.faw;

import com.faw.dataSource.DynamicDataSourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;



/*@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableAsync
@EnableScheduling
@Import({DynamicDataSourceConfig.class})
public class QualityApplication {

	public static void main(String[] args) {
		SpringApplication.run(QualityApplication.class, args);
	}

}*/

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableAsync            //开启多线程异步任务支持
@ServletComponentScan   //开启对监听器@WebListener注解支持
@Import({DynamicDataSourceConfig.class})
public class QualityApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(QualityApplication.class, args);

	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(QualityApplication.class);
	}
}

