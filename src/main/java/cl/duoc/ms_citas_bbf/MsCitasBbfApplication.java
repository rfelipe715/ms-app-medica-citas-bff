package cl.duoc.ms_citas_bbf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsCitasBbfApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCitasBbfApplication.class, args);
	}

}
