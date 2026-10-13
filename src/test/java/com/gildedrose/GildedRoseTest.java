package com.gildedrose;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GildedRoseTest {

    private static final String VEST = "+5 Dexterity Vest";
    private static final String BRIE = "Aged Brie";
    private static final String BACKSTAGE = "Backstage passes to a TAFKAL80ETC concert";
    private static final String SULFURAS = "Sulfuras, Hand of Ragnaros";

    private void updateOneDay(Item... items) {
        ItemRepository repository = new InMemoryItemRepository(items);
        new GildedRose(repository).updateQuality();
    }

    private static void assertItem(Item item, int expectedSellIn, int expectedQuality) {
        assertEquals(expectedSellIn, item.sellIn, "sellIn");
        assertEquals(expectedQuality, item.quality, "quality");
    }

    // ---------- 일반 아이템 ----------

    @Test
    @DisplayName("일반 아이템은 유통기한 전에는 품질이 1 감소한다")
    void normalItem_qualityDecreasesByOne_beforeSellByDate() {
        Item vest = new Item(VEST, 10, 20);

        updateOneDay(vest);

        assertItem(vest, 9, 19);
    }

    @Test
    @DisplayName("일반 아이템은 유통기한이 1이어도 품질이 1 감소한다")
    void normalItem_qualityDecreasesByOne_whenSellInIsOne() {
        Item vest = new Item(VEST, 1, 20);

        updateOneDay(vest);

        assertItem(vest, 0, 19);
    }

    @Test
    @DisplayName("일반 아이템은 유통기한이 0이면 품질이 2 감소한다")
    void normalItem_qualityDecreasesByTwo_whenSellInIsZero() {
        Item vest = new Item(VEST, 0, 20);

        updateOneDay(vest);

        assertItem(vest, -1, 18);
    }

    @Test
    @DisplayName("일반 아이템은 유통기한이 지난 이후에도 품질이 2 감소한다")
    void normalItem_qualityDecreasesByTwo_afterSellByDate() {
        Item vest = new Item(VEST, -1, 20);

        updateOneDay(vest);

        assertItem(vest, -2, 18);
    }

    @Test
    @DisplayName("일반 아이템의 품질은 0 아래로 감소하지 않는다")
    void normalItem_qualityNeverBecomesNegative() {
        Item vest = new Item(VEST, 5, 0);

        updateOneDay(vest);

        assertItem(vest, 4, 0);
    }

    @Test
    @DisplayName("일반 아이템은 품질이 1일 때 유통기한이 지나도 0까지만 감소한다")
    void normalItem_qualityStopsAtZero_whenExpiredWithQualityOne() {
        Item vest = new Item(VEST, 0, 1);

        updateOneDay(vest);

        assertItem(vest, -1, 0);
    }

    @Test
    @DisplayName("일반 아이템은 유통기한이 지나도 품질이 0이면 0을 유지한다")
    void normalItem_qualityStaysZero_whenExpiredWithQualityZero() {
        Item vest = new Item(VEST, 0, 0);

        updateOneDay(vest);

        assertItem(vest, -1, 0);
    }

    // ---------- Aged Brie ----------

    @Test
    @DisplayName("Aged Brie는 유통기한 전에는 품질이 1 증가한다")
    void agedBrie_qualityIncreasesByOne_beforeSellByDate() {
        Item brie = new Item(BRIE, 5, 10);

        updateOneDay(brie);

        assertItem(brie, 4, 11);
    }

    @Test
    @DisplayName("Aged Brie는 유통기한이 1이어도 품질이 1 증가한다")
    void agedBrie_qualityIncreasesByOne_whenSellInIsOne() {
        Item brie = new Item(BRIE, 1, 10);

        updateOneDay(brie);

        assertItem(brie, 0, 11);
    }

    @Test
    @DisplayName("Aged Brie는 유통기한이 0이면 품질이 2 증가한다")
    void agedBrie_qualityIncreasesByTwo_whenSellInIsZero() {
        Item brie = new Item(BRIE, 0, 10);

        updateOneDay(brie);

        assertItem(brie, -1, 12);
    }

    @Test
    @DisplayName("Aged Brie는 유통기한이 지난 이후에도 품질이 2 증가한다")
    void agedBrie_qualityIncreasesByTwo_afterSellByDate() {
        Item brie = new Item(BRIE, -1, 10);

        updateOneDay(brie);

        assertItem(brie, -2, 12);
    }

    @Test
    @DisplayName("Aged Brie의 품질은 50을 초과하지 않는다")
    void agedBrie_qualityNeverExceedsFifty() {
        Item brie = new Item(BRIE, 5, 50);

        updateOneDay(brie);

        assertItem(brie, 4, 50);
    }

    @Test
    @DisplayName("Aged Brie는 품질이 49일 때 유통기한이 지나도 50까지만 증가한다")
    void agedBrie_qualityStopsAtFifty_whenExpiredWithQualityFortyNine() {
        Item brie = new Item(BRIE, 0, 49);

        updateOneDay(brie);

        assertItem(brie, -1, 50);
    }

    @Test
    @DisplayName("Aged Brie는 유통기한이 지나도 품질이 50이면 50을 유지한다")
    void agedBrie_qualityStaysFifty_whenExpiredWithQualityFifty() {
        Item brie = new Item(BRIE, 0, 50);

        updateOneDay(brie);

        assertItem(brie, -1, 50);
    }

    // ---------- Backstage passes ----------

    @Test
    @DisplayName("Backstage passes는 공연까지 11일 이상 남으면 품질이 1 증가한다")
    void backstagePasses_qualityIncreasesByOne_whenElevenDaysLeft() {
        Item pass = new Item(BACKSTAGE, 11, 20);

        updateOneDay(pass);

        assertItem(pass, 10, 21);
    }

    @Test
    @DisplayName("Backstage passes는 공연까지 10일 남으면 품질이 2 증가한다")
    void backstagePasses_qualityIncreasesByTwo_whenTenDaysLeft() {
        Item pass = new Item(BACKSTAGE, 10, 20);

        updateOneDay(pass);

        assertItem(pass, 9, 22);
    }

    @Test
    @DisplayName("Backstage passes는 공연까지 6일 남아도 품질이 2 증가한다")
    void backstagePasses_qualityIncreasesByTwo_whenSixDaysLeft() {
        Item pass = new Item(BACKSTAGE, 6, 20);

        updateOneDay(pass);

        assertItem(pass, 5, 22);
    }

    @Test
    @DisplayName("Backstage passes는 공연까지 5일 남으면 품질이 3 증가한다")
    void backstagePasses_qualityIncreasesByThree_whenFiveDaysLeft() {
        Item pass = new Item(BACKSTAGE, 5, 20);

        updateOneDay(pass);

        assertItem(pass, 4, 23);
    }

    @Test
    @DisplayName("Backstage passes는 공연까지 1일 남아도 품질이 3 증가한다")
    void backstagePasses_qualityIncreasesByThree_whenOneDayLeft() {
        Item pass = new Item(BACKSTAGE, 1, 20);

        updateOneDay(pass);

        assertItem(pass, 0, 23);
    }

    @Test
    @DisplayName("Backstage passes는 공연이 끝나면 품질이 0이 된다")
    void backstagePasses_qualityDropsToZero_afterConcert() {
        Item pass = new Item(BACKSTAGE, 0, 20);

        updateOneDay(pass);

        assertItem(pass, -1, 0);
    }

    @Test
    @DisplayName("Backstage passes는 품질이 49일 때 2 증가해야 해도 50을 초과하지 않는다")
    void backstagePasses_qualityStopsAtFifty_whenTenDaysLeftWithQualityFortyNine() {
        Item pass = new Item(BACKSTAGE, 10, 49);

        updateOneDay(pass);

        assertItem(pass, 9, 50);
    }

    @Test
    @DisplayName("Backstage passes는 품질이 48일 때 3 증가해야 해도 50을 초과하지 않는다")
    void backstagePasses_qualityStopsAtFifty_whenFiveDaysLeftWithQualityFortyEight() {
        Item pass = new Item(BACKSTAGE, 5, 48);

        updateOneDay(pass);

        assertItem(pass, 4, 50);
    }

    @Test
    @DisplayName("Backstage passes는 품질이 이미 50이면 증가하지 않는다")
    void backstagePasses_qualityDoesNotIncrease_whenAlreadyFifty() {
        Item pass = new Item(BACKSTAGE, 5, 50);

        updateOneDay(pass);

        assertItem(pass, 4, 50);
    }

    // ---------- Sulfuras ----------

    @Test
    @DisplayName("Sulfuras는 유통기한과 품질이 변하지 않는다")
    void sulfuras_neverChanges() {
        Item sulfuras = new Item(SULFURAS, 10, 80);

        updateOneDay(sulfuras);

        assertItem(sulfuras, 10, 80);
    }

    @Test
    @DisplayName("Sulfuras는 유통기한이 지난 상태에서도 변하지 않는다")
    void sulfuras_neverChanges_afterSellByDate() {
        Item sulfuras = new Item(SULFURAS, -1, 80);

        updateOneDay(sulfuras);

        assertItem(sulfuras, -1, 80);
    }

    // ---------- 공통 동작 ----------

    @Test
    @DisplayName("여러 아이템이 있으면 각각의 규칙에 따라 갱신한다")
    void multipleItems_eachUpdatedByItsOwnRule() {
        Item vest = new Item(VEST, 10, 20);
        Item brie = new Item(BRIE, 5, 10);

        updateOneDay(vest, brie);

        assertItem(vest, 9, 19);
        assertItem(brie, 4, 11);
    }
}
