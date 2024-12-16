package com.example.users.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private RestTemplate restTemplate; // Utilisation de RestTemplate pour interagir avec Keycloak

    private static final String KEYCLOAK_USERS_URL = "http://localhost:8180/admin/realms/myRealm/users";
    private static final String KEYCLOAK_TOKEN_URL = "http://localhost:8180/realms/myRealm/protocol/openid-connect/token";

    // Fonction pour récupérer tous les utilisateurs
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erreur : Token d'accès manquant ou mal formé.");
        }

        String accessToken = authorizationHeader.substring(7);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List> response = restTemplate.exchange(KEYCLOAK_USERS_URL, HttpMethod.GET, entity, List.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body("Erreur lors de la récupération des utilisateurs. Code d'erreur: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body("Erreur lors de la communication avec l'API Keycloak: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la communication avec l'API Keycloak : " + e.getMessage());
        }
    }

    // Méthode pour obtenir le nom de l'utilisateur connecté
    @GetMapping("/username")
    public String getAccountName(Principal principal) {
        return principal.getName();
    }

    // Méthode pour retourner les informations d'authentification de l'utilisateur
    @GetMapping("/auth")
    public Principal authentification(Principal principal) {
        return principal;
    }


}
