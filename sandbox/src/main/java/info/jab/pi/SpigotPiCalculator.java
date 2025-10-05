package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;

/**
 * Pi calculation using Spigot algorithm
 * This algorithm can compute individual digits of π without computing the preceding ones
 */
public class SpigotPiCalculator implements HighPrecisionPiCalculator {
    
    @Override
    public double calculatePi() {
        // Simple spigot algorithm for double precision
        int[] digits = new int[100];
        int len = 10 * 100 / 3;
        int[] A = new int[len];
        Arrays.fill(A, 2);
        
        int nines = 0;
        int predigit = 0;
        StringBuilder result = new StringBuilder();
        
        for (int j = 1; j <= 15; j++) {
            int q = 0;
            for (int i = len - 1; i >= 0; i--) {
                int x = 10 * A[i] + q * (i + 1);
                A[i] = x % (2 * i + 1);
                q = x / (2 * i + 1);
            }
            A[0] = q % 10;
            q = q / 10;
            
            if (q == 9) {
                nines++;
            } else if (q == 10) {
                result.append(predigit + 1);
                for (int k = 0; k < nines; k++) {
                    result.append(0);
                }
                predigit = 0;
                nines = 0;
            } else {
                if (j > 1) result.append(predigit);
                predigit = q;
                if (nines != 0) {
                    for (int k = 0; k < nines; k++) {
                        result.append(9);
                    }
                    nines = 0;
                }
            }
        }
        result.append(predigit);
        
        String piStr = result.toString();
        if (piStr.length() > 1) {
            piStr = piStr.charAt(0) + "." + piStr.substring(1);
        }
        
        return Double.parseDouble(piStr);
    }
    
    @Override
    public String calculatePiHighPrecision(int precision) {
        int len = 10 * precision / 3;
        int[] A = new int[len];
        Arrays.fill(A, 2);
        
        int nines = 0;
        int predigit = 0;
        StringBuilder result = new StringBuilder();
        
        for (int j = 1; j <= precision + 10; j++) {
            int q = 0;
            for (int i = len - 1; i >= 0; i--) {
                int x = 10 * A[i] + q * (i + 1);
                A[i] = x % (2 * i + 1);
                q = x / (2 * i + 1);
            }
            A[0] = q % 10;
            q = q / 10;
            
            if (q == 9) {
                nines++;
            } else if (q == 10) {
                result.append(predigit + 1);
                for (int k = 0; k < nines; k++) {
                    result.append(0);
                }
                predigit = 0;
                nines = 0;
            } else {
                if (j > 1) result.append(predigit);
                predigit = q;
                if (nines != 0) {
                    for (int k = 0; k < nines; k++) {
                        result.append(9);
                    }
                    nines = 0;
                }
            }
        }
        result.append(predigit);
        
        String piStr = result.toString();
        if (piStr.length() > precision + 2) {
            piStr = piStr.substring(0, precision + 2);
        }
        
        if (piStr.length() > 1) {
            piStr = piStr.charAt(0) + "." + piStr.substring(1);
        }
        
        // Ensure we return exactly the requested precision
        if (piStr.contains(".") && piStr.length() > precision + 2) {
            piStr = piStr.substring(0, precision + 2);
        }
        
        return piStr;
    }
}