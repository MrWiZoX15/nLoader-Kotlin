package club.reaper.loader.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.InvalidKeyException
import java.security.Key
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES"

    fun encrypt(key: String, inputFile: File, outputFile: File) {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile)
    }

    fun decrypt(key: String, inputFile: File, outputFile: File) {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile)
    }

    private fun doCrypto(cipherMode: Int, key: String, inputFile: File, outputFile: File) {
        try {
            val secretKey: Key = SecretKeySpec(key.toByteArray(), ALGORITHM)
            val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(cipherMode, secretKey)

            val inputStream = FileInputStream(inputFile)
            val inputBytes = ByteArray(inputFile.length().toInt())
            inputStream.read(inputBytes)

            val outputBytes = cipher.doFinal(inputBytes)

            val outputStream = FileOutputStream(outputFile)
            outputStream.write(outputBytes)

            inputStream.close()
            outputStream.close()

        } catch (ex: NoSuchPaddingException) {
        } catch (ex: NoSuchAlgorithmException) {
        } catch (ex: InvalidKeyException) {
        } catch (ex: BadPaddingException) {
        } catch (ex: IllegalBlockSizeException) {
        } catch (ex: Exception) {
        }
    }
}
