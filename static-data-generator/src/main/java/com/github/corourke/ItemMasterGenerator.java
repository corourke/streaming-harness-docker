package com.github.corourke;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemMasterGenerator {

    public static void main(String[] args) {
        Integer SCALE = 32000; // TODO: Take from parameters

        final Logger logger = LoggerFactory.getLogger(ItemMasterGenerator.class);

        ArrayList<Map> categories = new ArrayList<>();
        Probabilities category_probabilities = new Probabilities();

        load_categories_from_csv(logger, categories, category_probabilities);

        // Generate item master
        try {
            // TODO: Take input file from command line
            Writer writer = new FileWriter("./src/main/resources/item_master.csv");
            StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(writer).build();
            List<ItemMaster> items = new ArrayList<>();

            for(int n = 0; n< SCALE; n++) {
                ItemMaster itemMaster = new ItemMaster(categories, category_probabilities);
                items.add(itemMaster);
            }

            sbc.write(items);
            writer.close();
            logger.info("Wrote {} ItemMaster rows.", items.size());
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
        }

    }

    private static void load_categories_from_csv(Logger logger, ArrayList<Map> categories, Probabilities category_probabilities) {
        try {
            // TODO: Take output file from command line
            Reader reader = new FileReader("./src/main/resources/product_categories.csv");
            CSVReaderHeaderAware csvReader = new CSVReaderHeaderAware(reader);
            Map csvLine;

            // Read in each category from the CSV file
            while((csvLine = csvReader.readMap()) != null) {
                categories.add(csvLine);

                // Add _probability to category_probabilities lookup table
                category_probabilities.add(Double.valueOf(csvLine.get("_probability").toString()));
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
