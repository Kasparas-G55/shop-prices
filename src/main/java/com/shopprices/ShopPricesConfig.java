package com.shopprices;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.Color;

@ConfigGroup("shopprices")
public interface ShopPricesConfig extends Config {
    @ConfigItem(
        position = 0,
        keyName = "displayOverlay",
        name = "Display prices below Items",
        description = "Shows a item value below each item in the shop."
    )
    default boolean displayOverlay() {
        return true;
    }

    @ConfigItem(
        position = 1,
        keyName = "displayTooltip",
        name = "Display prices in Tooltip",
        description = "Shows a small tooltip with items total price when hovering over an item."
    )
    default boolean displayTooltip() {
        return true;
    }

    @ConfigItem(
        position = 2,
        keyName = "overlayValueColor",
        name = "Shop price color",
        description = "Sets the default item value color."
    )
    @Alpha default Color defaultOverlayColor() {
        return Color.WHITE;
    }

    @ConfigItem(
        position = 3,
        keyName = "enablePriceThreshold",
        name = "Enable price threshold",
        description = "Allows you to set a threshold for when a price reaches a certain percentage."
    )
    default boolean priceThresholdEnabled() {
        return false;
    }

    @ConfigItem(
        position = 4,
        keyName = "priceThresholdPercentage",
        name = "Threshold Percentage",
        description = "Sets the threshold percentage."
    )
    default int priceThreshold() {
        return 15;
    }

    @ConfigItem(
        position = 5,
        keyName = "priceThresholdColor",
        name = "Price threshold color",
        description = "Sets the threshold item value color."
    )
    @Alpha default Color thresholdOverlayColor() {
        return Color.RED;
    }
}
