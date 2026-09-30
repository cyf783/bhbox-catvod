package com.github.catvod.utils;

import com.github.catvod.plugin.IManager;
import com.github.catvod.plugin.IRuntimePlugin;
import com.github.catvod.plugin.bean.PluginBean;

public class Plugin {
    private static class Loader {
        static volatile Plugin INSTANCE = new Plugin();
    }

    private static Plugin get() {
        return Plugin.Loader.INSTANCE;
    }

    private IManager manager;

    public static IManager setManager(IManager manager) {
        return get().manager =manager;
    }

    private static IManager getManager() {
        return get().manager;
    }

    public static PluginBean getPluginBeanById(String id){
        return getManager().getPluginBeanById(id);
    }

    public static IRuntimePlugin getRuntimePluginById(String id){
        return getManager().getRuntimePluginById(id);
    }

}
