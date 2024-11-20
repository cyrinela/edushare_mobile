package iset.project.Gs_User;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@CrossOrigin(origins = "*")
@SpringBootApplication
public class GsUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(GsUserApplication.class, args);
	}
	@Configuration
	public class CorsConfig implements WebMvcConfigurer {
		@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/**")
					.allowedOrigins("*") // Change '*' à des origines spécifiques pour plus de sécurité
					.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
		}
	}

}
