package com.github.catvod.plugin.bean;


import java.io.Serializable;
import java.util.List;

public class ApkParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String type;
    private String value;
    private boolean required;
    private String hint;
    private int lines = 1;
    private List<Option> options;
    private transient Object tag;
    private transient Object labelTag;

    public String getId() {
        return id;
    }

    public ApkParam setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ApkParam setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public ApkParam setType(String type) {
        this.type = type;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ApkParam setValue(String value) {
        this.value = value;
        return this;
    }

    public boolean getRequired() {
        return required;
    }

    public ApkParam setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public String getHint() {
        return hint;
    }

    public ApkParam setHint(String hint) {
        this.hint = hint;
        return this;
    }

    public int getLines() {
        return lines;
    }

    public ApkParam setLines(int lines) {
        this.lines = lines;
        return this;
    }

    public List<Option> getOptions() {
        return options;
    }

    public ApkParam setOptions(List<Option> options) {
        this.options = options;
        return this;
    }

    public Object getTag() {
        return tag;
    }

    public ApkParam setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public Object getLabelTag() {
        return labelTag;
    }

    public ApkParam setLabelTag(Object labelTag) {
        this.labelTag = labelTag;
        return this;
    }

    public static class Option implements Serializable {

        private static final long serialVersionUID = 1L;

        private String name;
        private String value;

        public Option() {
        }

        public Option(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Option setName(String name) {
            this.name = name;
            return this;
        }

        public String getValue() {
            return value;
        }

        public Option setValue(String value) {
            this.value = value;
            return this;
        }
    }
}
