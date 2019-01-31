package com.gildedrose;

import java.util.function.Function;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.stream;

class GildedRose {
    Item[] items;

    public GildedRose(Item[] items) { this.items = items; }

    public void updateQuality() {
        stream(items).forEach(this::updateQuality);
    }

    private void updateQuality(Item item) {
        adjustSellIn(item);
        adjustQuality(item);
    }

    private Item adjustSellIn(Item item) {
        if (hasExpiryDate(item)) {
            item.sellIn -= 1;
        }
        return item;
    }

    private void adjustQuality(Item item) {
        item.quality = capped(item, qualityAdjustmentFor(item));
    }

    private Function<Item, Integer> qualityAdjustmentFor(Item item) {
        if (isSulphuras(item))     return sulphur -> 80;
        if (isBackstagePass(item)) return pass    -> pass.quality = sellDatePassed(pass) ? 0 : pass.quality + incrementForBackPass(pass);
        if (isBrie(item))          return brie    -> brie.quality  + 1 * degradationVelocity(brie); //mimic bug in the source code
        else                       return other   -> other.quality - 1 * degradationVelocity(other);
    }

    private int capped(Item item, Function<Item, Integer> qualityUpdateAlgorithm) {
        return isSulphuras(item) ? 80 : max(min(qualityUpdateAlgorithm.apply(item), 50), 0);
    }

    private int incrementForBackPass(Item pass) {
        int increment = 1;
        if (pass.sellIn < 10) increment++;
        if (pass.sellIn < 5)  increment++;
        return increment;
    }

    private boolean sellDatePassed(Item item)  { return item.sellIn < 0; }
    private boolean hasExpiryDate(Item item)   { return !isSulphuras(item); }
    private int degradationVelocity(Item item) { return sellDatePassed(item) ? 2 : 1;}

    private boolean isSulphuras(Item item)     { return item.name.equals("Sulfuras, Hand of Ragnaros");               }
    private boolean isBackstagePass(Item item) { return item.name.equals("Backstage passes to a TAFKAL80ETC concert");}
    private boolean isBrie(Item item)          { return item.name.equals("Aged Brie");                                }

}