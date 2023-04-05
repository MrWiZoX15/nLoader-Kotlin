package club.reaper.loader.main

import club.reaper.loader.Injection
import club.reaper.loader.downloader.FileDownloader
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.io.IOException
import java.security.*
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

class Loader : JavaPlugin() {

    val prefix = "mldr"
    val async = false

    companion object {
        lateinit var INSTANCE: Loader
    }

    var plugin: Injection? = null

    override fun onEnable() {
        INSTANCE = this
        loadPlugin()
    }

    override fun onDisable() {
        plugin?.disablePlugin()
    }

    fun reloadPlugin() {
        plugin?.disablePlugin()
        loadPlugin()
    }

    private fun loadPlugin() {
        if (async) {
            object : BukkitRunnable() {
                override fun run() {
                    try {
                        plugin = Injection(FileDownloader().download())
                        plugin?.enablePlugin(this@Loader)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: InvalidKeyException) {
                        e.printStackTrace()
                    } catch (e: NoSuchAlgorithmException) {
                        e.printStackTrace()
                    } catch (e: NoSuchPaddingException) {
                        e.printStackTrace()
                    } catch (e: IllegalBlockSizeException) {
                        e.printStackTrace()
                    } catch (e: BadPaddingException) {
                        e.printStackTrace()
                    } catch (e: InvalidAlgorithmParameterException) {
                        e.printStackTrace()
                    }
                }
            }.runTaskAsynchronously(this)
        } else {
            try {
                plugin = Injection(FileDownloader().download())
                plugin?.enablePlugin(this)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InvalidKeyException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: NoSuchPaddingException) {
                e.printStackTrace()
            } catch (e: IllegalBlockSizeException) {
                e.printStackTrace()
            } catch (e: BadPaddingException) {
                e.printStackTrace()
            } catch (e: InvalidAlgorithmParameterException) {
                e.printStackTrace()
            }
        }
    }
}
