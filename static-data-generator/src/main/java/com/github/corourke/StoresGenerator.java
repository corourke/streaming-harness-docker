package com.github.corourke;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

// Generate stores
public class StoresGenerator {

    public static void main(String[] args) {

        int SCALE = 30;
        Logger logger = LoggerFactory.getLogger(StoresGenerator.class);


        // TODO: Take input file from command line
        Writer writer = null;
        try {
            writer = new FileWriter("./src/main/resources/stores.csv");
            StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(writer).build();
            List<Store> stores = new ArrayList<>();

            for (int n = 0; n < SCALE; n++) {
                Store store = new Store();
                stores.add(store);
            }

            sbc.write(stores);
            writer.close();
            logger.info("Wrote {} Store rows.", stores.size());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        } catch (CsvDataTypeMismatchException e) {
            e.printStackTrace();
        }


    }

}
