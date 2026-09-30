package com.github.catvod.plugin;

import com.github.catvod.plugin.bean.PluginBean;

public interface IManager {

    PluginBean getPluginBeanById(String id);

    IRuntimePlugin getRuntimePluginById(String id);

}