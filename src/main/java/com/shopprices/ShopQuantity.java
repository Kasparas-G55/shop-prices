package com.shopprices;

import lombok.Getter;

public enum ShopQuantity {
    VALUE(0,"Value", 1),
    BUY1(1, "Buy 1", 1),
    BUY5(2,"Buy 5", 5),
    BUY10(3,"Buy 10", 10),
    BUY50(4,"Buy 50", 50);

    @Getter
    private final int id;

    @Getter
    private final String option;

    @Getter
    private final int amount;

    ShopQuantity(int id, String option, int amount) {
        this.id = id;
        this.option = option;
        this.amount = amount;
    }

    private static final ShopQuantity[] VALUES = values();

    public static ShopQuantity getById(int id) {
        for (ShopQuantity quantity : VALUES) {
            if (quantity.id == id) {
                return quantity;
            }
        }

        return ShopQuantity.VALUE;
    }
}
