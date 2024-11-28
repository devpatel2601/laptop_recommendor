package com.example.laptoprecommendationsystem.util;

import com.example.laptoprecommendationsystem.model.Laptop;

import java.util.List;

public class LaptopQuickSort {

    /**
     * Sorts a list of laptops by price using QuickSort.
     *
     * @param laptops The list of laptops to sort.
     * @param order   The sorting order ("asc" for ascending, "desc" for descending).
     */
    public static void quickSortByPrice(List<Laptop> laptops, String order) {
        boolean isAscending = order.equalsIgnoreCase("asc");
        quickSort(laptops, isAscending, 0, laptops.size() - 1);
    }

    private static void quickSort(List<Laptop> laptops, boolean isAscending, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(laptops, isAscending, low, high);
            quickSort(laptops, isAscending, low, pivotIndex - 1);
            quickSort(laptops, isAscending, pivotIndex + 1, high);
        }
    }

    private static int partition(List<Laptop> laptops, boolean isAscending, int low, int high) {
        Laptop pivot = laptops.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (comparePrice(laptops.get(j), pivot, isAscending) <= 0) {
                i++;
                swap(laptops, i, j);
            }
        }

        swap(laptops, i + 1, high);
        return i + 1;
    }

    private static void swap(List<Laptop> laptops, int i, int j) {
        Laptop temp = laptops.get(i);
        laptops.set(i, laptops.get(j));
        laptops.set(j, temp);
    }

    private static int comparePrice(Laptop a, Laptop b, boolean isAscending) {
        double priceA = a.getPrice(); // Assuming `getPrice()` returns a `double`
        double priceB = b.getPrice();
        int comparisonResult = Double.compare(priceA, priceB);
        return isAscending ? comparisonResult : -comparisonResult;
    }
}
