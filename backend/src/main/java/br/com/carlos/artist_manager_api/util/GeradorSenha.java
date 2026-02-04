package br.com.carlos.artist_manager_api.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeradorSenha {

    public static void main(String[] args) {
        String senhaTextoPuro = "123456";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senhaCriptografada = encoder.encode(senhaTextoPuro);

        System.out.println("=== Gerador de Hash BCrypt ===");
        System.out.println("Senha Original: " + senhaTextoPuro);
        System.out.println("Senha Hash (Copie para o Banco):");
        System.out.println(senhaCriptografada);

        boolean bateu = encoder.matches(senhaTextoPuro, senhaCriptografada);
        System.out.println("\nValidação: " + (bateu ? "Sucesso! A senha bate com o hash." : "Falha!"));
    }
}