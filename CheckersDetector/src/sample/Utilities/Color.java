package sample.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Color {

    public Color(Integer B1, Integer G1, Integer R1, Integer B2, Integer G2, Integer R2) {
        minValues = Arrays.asList(B1,G1,R1);
        maxValues = Arrays.asList(B2,G2,R2);
    }

    private List<Integer> minValues;
    private List<Integer> maxValues;

    public List<Integer> getMinValues() {
        return minValues;
    }

    public List<Integer> getMaxValues() {
        return maxValues;
    }
}
