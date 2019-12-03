package com.github.corourke;

import com.opencsv.bean.CsvBindByName;
import net.andreinc.mockneat.MockNeat;
import net.andreinc.mockneat.types.enums.StringType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class ItemMaster {
    private MockNeat mock = MockNeat.threadLocal();;

    private static Integer item_seq = 12345;

    private static ArrayList<Map> categories;
    private static Probabilities category_probabilities;

    @CsvBindByName
    private String item_upc;

    @CsvBindByName
    private Integer item_id;

    @CsvBindByName
    private Integer category_code;

    @CsvBindByName
    private BigDecimal item_price;

    @CsvBindByName
    private Integer repl_qty;

    @CsvBindByName(column = "_frequency")
    private Integer frequency;

    ItemMaster(ArrayList<Map> categories, Probabilities p) {
        ItemMaster.categories = categories;
        ItemMaster.category_probabilities = p;
        generate();
    }

    public String getItem_upc() {
        return item_upc;
    }

    public Integer getItem_id() {
        return item_id;
    }

    public Integer getCategory_code() {
        return category_code;
    }

    public BigDecimal getItem_price() {
        return item_price;
    }

    public Integer getRepl_qty() {
        return repl_qty;
    }

    public Integer getFrequency() {
        return frequency;
    }

    // Create all values
    private void generate() {
        // UPC Code -- 11 digits
        item_upc = mock.strings()
                .size(10)
                .type(StringType.NUMBERS)
                .prepend("1")
                .get();

        // Item ID
        item_id = item_seq++;

        // Category Code based on probability table
        Map category = categories.get(category_probabilities.get_weighted_index());
        category_code = Integer.parseInt(category.get("category_code").toString());

        // Item Price - vary by 10% up/down
        Double avg_price =  Double.valueOf(category.get("_avg_price").toString());
        Double variance = new Random().nextGaussian();
        item_price = new BigDecimal(avg_price + (avg_price * .1 * variance)).setScale(2, BigDecimal.ROUND_HALF_DOWN);

        // Replenishment Quantity -- should probably be based on _avg_price
        repl_qty = mock.ints().from(new int[]{1, 12, 18, 24}).get();

        // Frequency -- how often item is sold
        frequency = mock.probabilites(Integer.class)
                .add(0.5, 1)
                .add(0.25, 2)
                .add(0.15, 3)
                .add(.10, 4)
                .get();
    }

}
