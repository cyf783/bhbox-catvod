package com.github.catvod.plugin.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 详情页集数列表（宿主 Movie.UrlBean 的插件侧镜像，字段对齐，避免 catvod 依赖宿主）。
 * parse 入参：宿主填好的完整结构；返回：同构同序的完整结构（未处理的组原样放回），宿主按序对齐回填。
 */
public class UrlBean implements Serializable {

    private static final long serialVersionUID = 1L;

    public List<UrlInfo> infoList;

    public static class UrlInfo implements Serializable {

        /** 集组名（如 "HD"、"正片"） */
        public String flag;
        public List<InfoBean> beanList;
    }

    public static class InfoBean implements Serializable {

        public String name;
        public String url;

        public InfoBean(String name, String url) {
            this.name = name;
            this.url = url;
        }
    }
}
