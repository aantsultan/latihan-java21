package id.latihan.java21.restfulapi.service;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class VectorServiceImpl implements VectorService {

    private static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

    @Override
    public void loadData() {
        int[] a = {1, 2, 3};
        int[] b = {4, 3, 2};
        int[] result = new int[3];

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            VectorMask<Integer> mask = SPECIES.indexInRange(i, a.length);
            IntVector intVectorA = IntVector.fromArray(SPECIES, a, i, mask);
            IntVector intVectorB = IntVector.fromArray(SPECIES, b, i, mask);
            IntVector sum = intVectorA.add(intVectorB);
            sum.intoArray(result, i, mask);
        }

        for (int x : result) {
            System.out.println(x + " ");
        }

    }

    @Override
    public String get() {
        int[] a = {1, 2, 3};
        int[] b = {4, 5, 6};
        int[] result = new int[3];

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            VectorMask<Integer> mask = SPECIES.indexInRange(i, a.length);
            IntVector intVectorA = IntVector.fromArray(SPECIES, a, i, mask);
            IntVector intVectorB = IntVector.fromArray(SPECIES, b, i, mask);
            IntVector sum = intVectorA.add(intVectorB);
            sum.intoArray(result, i, mask);
        }
        List<String> list = Arrays.stream(result).boxed().map(Object::toString).toList();
        return String.join(" ", list);
    }

    @Override
    public String dot() {
        int[] u = {2, 6};
        int[] v = {-1, 5};
        int x = 0;
        int length = u.length;

        for (int i = 0; i < length; i += SPECIES.length()) {
            VectorMask<Integer> mask = SPECIES.indexInRange(i, length);
            IntVector intVectorU = IntVector.fromArray(SPECIES, u, i, mask);
            IntVector intVectorV = IntVector.fromArray(SPECIES, v, i, mask);
            x += intVectorU.mul(intVectorV).reduceLanes(VectorOperators.ADD);
        }
        return String.format("%d", x);
    }

    @Override
    public String cross() {
        return null;
    }

    @Override
    public String multiplySkalar() {
        int[] a = {1, 2, 3};
        int[] result = new int[3];

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            VectorMask<Integer> mask = SPECIES.indexInRange(i, a.length);
            IntVector intVectorA = IntVector.fromArray(SPECIES, a, i, mask);
            IntVector sum = intVectorA.mul(5);
            sum.intoArray(result, i, mask);
        }
        List<String> list = Arrays.stream(result).boxed().map(Object::toString).toList();
        return String.join(" ", list);
    }
}
