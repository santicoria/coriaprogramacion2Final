package com.mycompany.myapp.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CleanerService {

    private final OrdenService ordenService;

    public CleanerService(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    public void cleanDb(List<Long> idList){

        for (int i=0; i<idList.size(); i++){
            ordenService.delete(idList.get(i)).subscribe();
        }
    }
}
