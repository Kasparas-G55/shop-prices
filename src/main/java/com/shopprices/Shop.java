package com.shopprices;

import net.runelite.api.ItemComposition;

import java.text.DecimalFormat;
import java.util.Map;

public final class Shop {
    public static final String SHOP_KEY_PATTERN = "[^a-zA-Z ]+";
    public static final float MIN_SELL_MULTIPLIER = 30.0f;

    /** Shops default sell multiplier for every item */
    int sellMultiplier;

    /** A price percentage change per shops stock surplus/deficit based on default stock. */
    float shopDelta;

    /**
     * A map of items that contains their default shop stock.
     * Items stock defaulted to 0 on null.
     */
    Map<String, Integer> itemStocks;

    /**
     * Gets the map key by formatting the shops name.
     *
     * @param shopName Shops name displayed in Shop frame widget.
     * @return         Shop key for accessing a structured map.
     */
    public static String formatShopName(String shopName) {
        return String.join("_", shopName.replaceAll(SHOP_KEY_PATTERN, "").toUpperCase().split(" "));
    }

    /**
     * Gets the formatted string price value.
     *
     * @param priceValue Items calculated value.
     * @return           Exact price value formatted with commas.
     */
    public static String getExactPriceValue(int priceValue) {
        DecimalFormat formatter = new DecimalFormat("#,###,###,### gp");
        return formatter.format(priceValue);
    }

    /**
     * Gets the items selling multiplier based on its current stock.
     *
     * @param itemComposition       Items compositions.
     * @param multiplierThreshold   Items percentage multiplier threshold.
     * @param currentStock          Items current stock in the shop.
     * @return                      True when multipliers threshold is greater than or equal to an items current multiplier.
     */
    public boolean isPriceAtThreshold(ItemComposition itemComposition, int multiplierThreshold, int currentStock) {
        return this.sellMultiplier + multiplierThreshold <= this.getSellMultiplier(itemComposition, currentStock);
    }

    /**
     * Gets the items selling multiplier based on its current stock.
     *
     * @param itemComposition   Items compositions.
     * @param currentStock      Items current stock in the shop.
     * @return                  A percentage sell multiplier.
     */
    public float getSellMultiplier(ItemComposition itemComposition, int currentStock) {
        Integer defaultStock = this.itemStocks.get(itemComposition.getName());

        if (defaultStock == null) {
            defaultStock = 0;
        }

        int stockDelta = defaultStock - currentStock;
        return sellMultiplier + (this.shopDelta * stockDelta);
    }

    /**
     * Gets the items selling price based on its current stock.
     *
     * @param itemComposition   Items compositions.
     * @param currentStock      Items current stock in the shop.
     * @return                  Items price value when buying a single quantity.
     */
    public int getSellPrice(ItemComposition itemComposition, int currentStock) {
        int itemValue = itemComposition.getPrice();

        // TODO: Look over this equation again and confirm its validity with tests.
        return (int) Math.max(
            itemValue * this.getSellMultiplier(itemComposition, currentStock) / 100,
            Math.max(MIN_SELL_MULTIPLIER * itemValue / 100, 1)
        );
    }

    /**
     * Gets the items selling price based on its current stock and amount being bought.
     *
     * @param itemComposition   Items compositions.
     * @param currentStock      Items current stock in the shop.
     * @param buyAmount         Amount of items being bought.
     * @return                  Items total price value when buying X quantity.
     */
    public int getSellPriceTotal(ItemComposition itemComposition, int currentStock, int buyAmount) {
        int totalCost = 0;

        for (int amount = 0; amount < buyAmount; amount++) {
            int stockDelta = currentStock - amount;
            totalCost += this.getSellPrice(itemComposition, stockDelta);
        }

        return totalCost;
    }
}