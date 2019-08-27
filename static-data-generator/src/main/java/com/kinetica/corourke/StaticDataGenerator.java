package com.kinetica.corourke;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import net.andreinc.mockneat.MockNeat;
import net.andreinc.mockneat.types.enums.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StaticDataGenerator {

    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(StaticDataGenerator.class);
        MockNeat mock = MockNeat.threadLocal();

        ArrayList<Map> categories = new ArrayList<>();
        ArrayList<Double> category_probabilities = new ArrayList<Double>();

        load_categories_from_csv(logger, category_probabilities, categories);

        /*
         * Generate Item Master
         */
        try {
            Writer writer = new FileWriter("./src/main/resources/item_master.csv");
            StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(writer).build();
            List<ItemMasterBean> items = new ArrayList<>();

            for(int n=0; n<32000; n++) {
                ItemMasterBean itemMaster = new ItemMasterBean(categories, category_probabilities);
                items.add(itemMaster);
            }

            sbc.write(items);
            writer.close();
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
        }

    }

    private static void load_categories_from_csv(Logger logger, ArrayList<Double> category_probabilities, ArrayList<Map> categories) {
        try {
            Reader reader = new FileReader("./src/main/resources/product_categories.csv");
            CSVReaderHeaderAware csvReader = new CSVReaderHeaderAware(reader);
            Map line;

            // Read in each category from the CSV file
            Double sum_prob = 0D; // for the category_probabilities table
            while((line = csvReader.readMap()) != null) {
                categories.add(line);

                // Add _probability to category_probabilities lookup table
                Double prob = Double.valueOf(line.get("_probability").toString());
                sum_prob += prob;
                category_probabilities.add(sum_prob);
            }
            csvReader.close();
            reader.close();

        } catch (FileNotFoundException e) {
            logger.error("Can not find input CSV file. ", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
