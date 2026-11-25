/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Componentes;

/**
 *
 * @author jimen
 */
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;

public class Encriptacion {

    private static final String VARIABLE_CLAVE = "AURUMSYS_AES_KEY";
    private static final int TAMANO_NONCE = 12;
    private static final int TAMANO_TAG = 128;

    private static SecretKey obtenerClave() {

        String clave = System.getenv(VARIABLE_CLAVE);

        if (clave == null || clave.isBlank()) {
            throw new IllegalStateException(
                "No se encontró la clave de encriptación."
            );
        }

        byte[] bytes = Base64.getDecoder().decode(clave);

        if (bytes.length != 32) {
            throw new IllegalStateException(
                "La clave AES debe contener 32 bytes."
            );
        }

        return new SecretKeySpec(bytes, "AES");
    }

    public static String encriptar(BigDecimal monto) {

        if (monto == null) {
            throw new IllegalArgumentException(
                "El monto no puede ser nulo."
            );
        }

        try {
            byte[] nonce = new byte[TAMANO_NONCE];
            new SecureRandom().nextBytes(nonce);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            cipher.init(
                Cipher.ENCRYPT_MODE,
                obtenerClave(),
                new GCMParameterSpec(TAMANO_TAG, nonce)
            );

            byte[] contenido = cipher.doFinal(
                monto.toPlainString().getBytes(StandardCharsets.UTF_8)
            );

            byte[] resultado = new byte[nonce.length + contenido.length];

            System.arraycopy(nonce, 0, resultado, 0, nonce.length);
            System.arraycopy(
                contenido, 0,
                resultado, nonce.length,
                contenido.length
            );

            return Base64.getEncoder().encodeToString(resultado);

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                "No se pudo encriptar el monto.", e
            );
        }
    }

    public static BigDecimal desencriptar(String montoCifrado) {

        if (montoCifrado == null || montoCifrado.isBlank()) {
            throw new IllegalArgumentException(
                "El monto encriptado está vacío."
            );
        }

        try {
            byte[] datos = Base64.getDecoder().decode(montoCifrado);

            if (datos.length < TAMANO_NONCE + 16) {
                throw new IllegalArgumentException(
                    "El contenido encriptado no es válido."
                );
            }

            byte[] nonce = Arrays.copyOfRange(
                datos, 0, TAMANO_NONCE
            );

            byte[] contenido = Arrays.copyOfRange(
                datos, TAMANO_NONCE, datos.length
            );

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            cipher.init(
                Cipher.DECRYPT_MODE,
                obtenerClave(),
                new GCMParameterSpec(TAMANO_TAG, nonce)
            );

            byte[] original = cipher.doFinal(contenido);

            return new BigDecimal(
                new String(original, StandardCharsets.UTF_8)
            );

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                "No se pudo desencriptar el monto.", e
            );
        }
    }
}
