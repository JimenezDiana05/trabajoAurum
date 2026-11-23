/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Componentes;

/**
 *
 * @author jimen
 */

import org.mindrot.jbcrypt.BCrypt;

public class Password {

    public static String encriptar(String contrasena) {

        if (contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña no puede estar vacía."
            );
        }

        return BCrypt.hashpw(
                contrasena,
                BCrypt.gensalt(12)
        );
    }

    public static boolean verificar(String contrasena, String hash) {

        if (contrasena == null || hash == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(contrasena, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

