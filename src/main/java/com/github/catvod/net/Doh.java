package com.github.catvod.net;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Doh {

    @SerializedName("name")
    private String name;
    @SerializedName("url")
    private String url;
    @SerializedName("ips")
    private List<String> ips;

    public Doh name(String name) {
        this.name = name;
        return this;
    }

    public Doh url(String url) {
        this.url = url;
        return this;
    }

    public String getName() {
        return TextUtils.isEmpty(name) ? "" : name;
    }

    public String getUrl() {
        return TextUtils.isEmpty(url) ? "" : url;
    }

    public List<String> getIps() {
        return ips == null ? Collections.emptyList() : ips;
    }

    public List<InetAddress> getHosts() {
        try {
            List<InetAddress> list = new ArrayList<>();
            for (String ip : getIps()) list.add(InetAddress.getByName(ip));
            return list.isEmpty() ? null : list;
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Doh)) return false;
        Doh it = (Doh) obj;
        return getUrl().equals(it.getUrl());
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
