package com.interview.waiwingchoyProtfolioMonitor.util;

import com.interview.waiwingchoyProtfolioMonitor.UniversialCache;
import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityDefinition;
import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityStatic;
import com.interview.waiwingchoyProtfolioMonitor.calculator.CallOptionPriceCalculator;
import com.interview.waiwingchoyProtfolioMonitor.calculator.PriceCalculator;
import com.interview.waiwingchoyProtfolioMonitor.calculator.PutOptionPriceCalculator;
import com.interview.waiwingchoyProtfolioMonitor.calculator.SimplePriceCalculator;
import com.interview.waiwingchoyProtfolioMonitor.repository.SecurityDefinitionRepository;
import com.interview.waiwingchoyProtfolioMonitor.enums.SecurityType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SecuritiesUtils {
    public static List<SecurityStatic> loadSecurityStaticFromCVSAndDefinitionToList(String filePath, UniversialCache universialCache, SecurityDefinitionRepository securityDefinitionRepository) {
        BufferedReader reader;
        List<SecurityStatic> ret = new CopyOnWriteArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            line = reader.readLine();
            while (line != null) {
                String[] elements = line.split(",");
                if (elements.length == 2) {
                    String symbol = elements[0];
                    String positionSize = elements[1];
                    SecurityDefinition securityDefinition = getSecurityDefinition(symbol, securityDefinitionRepository);
                    if (securityDefinition==null) {
                        System.out.println("ERROR: " +symbol + " does not exist in db ...");
                        System.exit(1);
                    }
                    PriceCalculator priceCalculator = getPriceCalculator(securityDefinition);
                    SecurityStatic securityStatic = new SecurityStatic(symbol, Integer.parseInt(positionSize));
                    ret.add(securityStatic);
//                    universialCache.setSecurityStatic(symbol, securityStatic);
                    universialCache.setPriceCalculator(symbol, priceCalculator);
                    universialCache.setSecurityDefinition(symbol, securityDefinition);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("ERROR: SecuritiesUtil: file read error:" + filePath);
            e.printStackTrace();
        }
        return ret;
    }

    private static SecurityDefinition getSecurityDefinition(String symbol, SecurityDefinitionRepository securityDefinitionRepository) {
        return securityDefinitionRepository.findBySymbol(symbol);
    }

    private static PriceCalculator getPriceCalculator(SecurityDefinition securityDefinition) {
        PriceCalculator priceCalculator = null;
        if (securityDefinition.getType().equals(SecurityType.STOCK)) {
            System.out.println("Assign SimpleCal to "+securityDefinition.getSymbol());
            priceCalculator = new SimplePriceCalculator();
        } else if (securityDefinition.getType().equals(SecurityType.CALL)) {
            System.out.println("Assign CallCal to "+securityDefinition.getSymbol());
            priceCalculator = new CallOptionPriceCalculator();
        } else if (securityDefinition.getType().equals(SecurityType.PUT)) {
            System.out.println("Assign PutCal to "+securityDefinition.getSymbol());
            priceCalculator = new PutOptionPriceCalculator();
        }
        return priceCalculator;
    }
}
