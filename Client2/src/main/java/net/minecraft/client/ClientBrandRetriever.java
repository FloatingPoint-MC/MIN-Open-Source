package net.minecraft.client;

import cn.floatingpoint.min.management.Managers;

public class ClientBrandRetriever {
    public static String getClientModName() {
        return Managers.clientManager.hardMode < 2 ? "fml,forge" : "vanilla";
    }
}
