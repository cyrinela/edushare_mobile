package Gs_Data.project.com;

import Gs_Data.project.com.Entities.Categorie;
import Gs_Data.project.com.Entities.Ressource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

@SpringBootApplication
public class GsDataApplication implements CommandLineRunner {
	@Autowired
private RepositoryRestConfiguration repositoryRestConfiguration;
	public static void main(String[] args) {
		SpringApplication.run(GsDataApplication.class, args);
	}


@Override
public void run(String... args) throws Exception {
		repositoryRestConfiguration.exposeIdsFor(Ressource.class, Categorie.class);
	}

}