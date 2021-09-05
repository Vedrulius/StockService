package com.mihey.stock.model;

import java.util.Comparator;

public class CompanyDataVolumeComparator implements Comparator<CompanyData> {
    @Override
    public int compare(CompanyData cd1, CompanyData cd2) {
        String name1 = cd1.getCompanyName();
        String name2 = cd2.getCompanyName();
        long vol1 = cd1.getVolume();
        long vol2 = cd2.getVolume();
        if (vol1 == 0) {
            vol1 = cd1.getPreviousVolume();
        }
        if (vol2 == 0) {
            vol2 = cd2.getPreviousVolume();
        }
        if (vol1 != vol2) {
            return (int) (vol2 - vol1);
        }
        return name1.compareTo(name2);
    }

}
