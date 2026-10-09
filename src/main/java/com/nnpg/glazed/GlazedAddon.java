package com.nnpg.glazed;

import com.nnpg.glazed.modules.esp.BedrockVoidESP;
import com.nnpg.glazed.modules.esp.RegionMap;
import com.nnpg.glazed.modules.main.GlazedFreecam;
import com.nnpg.glazed.modules.main.GodTrident;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class GlazedAddon extends MeteorAddon {
    public static final Category CATEGORY = new Category("DFiz Addons");

    // Same category: modules that use GlazedAddon.esp still work.
    public static final Category esp = CATEGORY;

    @Override
    public void onInitialize() {
        Modules.get().add(new GlazedFreecam());
        Modules.get().add(new GodTrident());
        Modules.get().add(new BedrockVoidESP());
        Modules.get().add(new RegionMap());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.nnpg.glazed";
    }
}
