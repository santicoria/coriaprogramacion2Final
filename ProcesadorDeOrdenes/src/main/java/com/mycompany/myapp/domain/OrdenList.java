package com.mycompany.myapp.domain;

import java.util.ArrayList;
import java.util.List;

public class OrdenList {

    public List<Orden> getOrdenList() {
        return ordenList;
    }

    public void setOrdenList(List<Orden> ordenList) {
        this.ordenList = ordenList;
    }

    private List<Orden> ordenList = new ArrayList<Orden>();



}
