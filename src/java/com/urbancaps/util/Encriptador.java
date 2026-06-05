package com.urbancaps.util;

import org.mindrot.jbcrypt.BCrypt;

public class Encriptador {

    private static final int ROUNDS = 10;

    public static String cifrar(String passwordPlano) {

        if (passwordPlano == null || passwordPlano.isEmpty()) {
            System.out.println("✘ Error: La contraseña no puede estar vacía.");
            return null;
        }

        String hash = BCrypt.hashpw(passwordPlano, BCrypt.gensalt(ROUNDS));

        return hash;
    }

    public static boolean verificar(String passwordPlano, String hashGuardado) {

        if (passwordPlano == null || passwordPlano.isEmpty()) {
            System.out.println("✘ Error: La contraseña ingresada está vacía.");
            return false;
        }

        if (hashGuardado == null || hashGuardado.isEmpty()) {
            System.out.println("✘ Error: No hay contraseña registrada para este usuario.");
            return false;
        }

        return BCrypt.checkpw(passwordPlano, hashGuardado);
    }

    public static void main(String[] args) {

        System.out.println("===========================================");
        System.out.println("  PRUEBA DE ENCRIPTACIÓN - URBAN CAPS");
        System.out.println("===========================================");

        String passwordOriginal = "admin123";
        System.out.println("  Contraseña original : " + passwordOriginal);

        String hashGenerado = Encriptador.cifrar(passwordOriginal);
        System.out.println("  Hash generado       : " + hashGenerado);

        boolean esCorrecta = Encriptador.verificar(passwordOriginal, hashGenerado);
        System.out.println();
        System.out.println("  Verificando contraseña correcta (admin123):");
        System.out.println("  Resultado : " + (esCorrecta ? "✔ CORRECTA" : "✘ INCORRECTA"));

        boolean esIncorrecta = Encriptador.verificar("otraPassword", hashGenerado);
        System.out.println();
        System.out.println("  Verificando contraseña incorrecta (otraPassword):");
        System.out.println("  Resultado : " + (esIncorrecta ? "✔ CORRECTA" : "✘ INCORRECTA — correcto, no debe pasar"));

        System.out.println();
        System.out.println("===========================================");
        System.out.println("  El encriptador funciona correctamente.");
        System.out.println("===========================================");
    }
}
