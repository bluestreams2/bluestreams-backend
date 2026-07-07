package com.spmf;

import io.smallrye.config.ConfigMapping;

import java.util.List;

//@ApplicationScoped
@ConfigMapping(prefix = "iptv")
public interface IPTVConfig {

    List<Provider> providers();

    interface Provider {
        String name();
        String url();
    }

}