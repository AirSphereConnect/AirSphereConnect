import jenkins.model.*
import hudson.model.UpdateCenter
import java.util.logging.Logger

def logger = Logger.getLogger("")
def instance = Jenkins.getInstance()
def pm = instance.getPluginManager()
def uc = instance.getUpdateCenter()
def plugins = new File("/usr/share/jenkins/ref/plugins.txt").readLines()

plugins.each { pluginName ->
    if (!pm.getPlugin(pluginName)) {
        logger.info("Installing Plugin: ${pluginName}")
        def plugin = uc.getPlugin(pluginName)
        if (plugin) {
            plugin.deploy()
        }
    } else {
        logger.info("Plugin ${pluginName} is already installed")
    }
}

instance.save()