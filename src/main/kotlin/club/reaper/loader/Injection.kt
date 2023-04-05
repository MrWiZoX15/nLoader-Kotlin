package club.reaper.loader

import club.reaper.loader.accessor.FieldAccess
import club.reaper.loader.accessor.Instance
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.InvalidPluginException
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.lang.reflect.Field
import java.net.URLClassLoader
import java.util.*


class Injection(private val pluginFile: File) {
    private lateinit var root: File
    private lateinit var handle: Plugin

    init {
        pluginFile.parentFile.listFiles()?.forEach { file ->
            val name = file.name
            if (name.endsWith(".jar") && !file.equals(pluginFile)) {
                file.delete()
            }
        }
    }

    fun enablePlugin(parent: Plugin) {
        try {
            handle = parent.pluginLoader.loadPlugin(pluginFile)
        } catch (e: InvalidPluginException) {
            e.printStackTrace()
        }
        fixPluginDir(parent)
        fixConfig()
        checkFields(parent)
        parent.server.pluginManager.enablePlugin(handle)
    }

    fun disablePlugin() {
        unload()
        if (pluginFile.exists()) {
            pluginFile.delete()
        }
    }

    private fun unload() {
        val name = handle.name
        val pluginManager = Bukkit.getPluginManager()
        pluginManager.disablePlugin(handle)
        val plugins: MutableList<Plugin> = FieldAccess(pluginManager.javaClass, "plugins").read(pluginManager)
        val names: MutableMap<String, Plugin> = FieldAccess(pluginManager.javaClass, "lookupNames").read(pluginManager)
        val commandMap: SimpleCommandMap = FieldAccess(pluginManager.javaClass, "commandMap").read(pluginManager)
        val commands: MutableMap<String, Command> = FieldAccess(
            SimpleCommandMap::class.java, "knownCommands"
        ).read(commandMap)
        pluginManager.disablePlugin(handle)
        if (plugins != null) plugins.remove(handle)
        if (names != null) names.remove(name)
        if (commandMap != null) {
            val it: MutableIterator<Map.Entry<String, Command>> = commands.entries.iterator()
            while (it.hasNext()) {
                val (_, value) = it.next()
                if (value is PluginCommand) {
                    val c = value
                    if (c.plugin === handle) {
                        c.unregister(commandMap)
                        it.remove()
                    }
                }
            }
        }
        val cl = handle.javaClass.classLoader
        if (cl is URLClassLoader) {
            FieldAccess(cl.javaClass, "plugin").set(cl, null)
            FieldAccess(cl.javaClass, "pluginInit").set(cl, null)
            closeLoader(cl)
        }
    }

    private fun checkFields(parent: Plugin) {
        Arrays.stream(handle.javaClass.fields).filter { field: Field ->
            field.isAnnotationPresent(
                Instance::class.java
            )
        }.forEach { field: Field ->
            try {
                field.isAccessible = true
                field[handle] = parent
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        Arrays.stream(handle.javaClass.declaredFields).filter { field: Field ->
            field.isAnnotationPresent(
                Instance::class.java
            )
        }.forEach { field: Field ->
            try {
                field.isAccessible = true
                field[handle] = parent
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }


    private fun fixConfig() {
        var file: File?
        FieldAccess(JavaPlugin::class.java, "configFile").set(handle, File(root, "config.yml").also { file = it })
        FieldAccess(JavaPlugin::class.java, "newConfig").set(handle, YamlConfiguration.loadConfiguration(file))
    }


    private fun fixPluginDir(parent: Plugin) {
        FieldAccess(JavaPlugin::class.java, "dataFolder").set(handle,
            File(parent.dataFolder.parentFile, handle.description.name.replace(" ".toRegex(), "_")).also {
                root = it
            })
    }

    private fun closeLoader(classLoader: URLClassLoader) {
        try {
            classLoader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}

